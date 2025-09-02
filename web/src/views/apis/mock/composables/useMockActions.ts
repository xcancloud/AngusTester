import { ref } from 'vue';
import { modal, notification } from '@xcan-angus/vue-ui';
import { MockServiceObj } from '../types';
import { mock } from '@/api/tester';
import { useMockData } from './useMockData';

/**
 * Composable for managing mock service actions and operations
 * Handles start, stop, delete, export and other service operations
 */
export function useMockActions (mockData: ReturnType<typeof useMockData>, projectId: any) {
  const { loading, tableData, fetchMockServiceList, updateTableData, getCurrentPageAfterDeletion } = mockData;

  // Reactive state for actions
  const rouSelection = ref<any | null>(null);
  const lastBatchType = ref<'del' | 'start' | undefined>();
  const timersMap = ref<{[key: string]: {count: number; timer?: NodeJS.Timeout}}>({});

  /**
   * Handle row selection change in table
   */
  const handleSelectChange = (_selectedRowKeys: string[]) => {
    if (!rouSelection.value) {
      return;
    }
    rouSelection.value.selectedRowKeys = _selectedRowKeys;
  };

  /**
   * Update service status by ID with polling
   */
  const updateStatusById = (id: string) => {
    if (!timersMap.value[id]?.count) {
      timersMap.value[id] = { count: 0, timer: undefined };
    }

    timersMap.value[id].count++;
    timersMap.value[id].timer = setTimeout(async () => {
      await getServiceById([id]);
    }, 2000);
  };

  /**
   * Get service by ID from API
   */
  const getServiceById = async (ids: string | string[]) => {
    const idArray = Array.isArray(ids) ? ids : [ids];
    const [error, { data = { list: [], total: 0 } }] = await mock.getServiceList({
      filters: [{ key: 'id', value: idArray, op: 'IN' }],
      projectId: projectId.value
    });

    if (error) {
      return;
    }

    const newData = data.list[0];
    const id = Array.isArray(ids) ? ids[0] : ids;

    if (newData.status.value === 'RUNNING' || timersMap.value[id].count === 16) {
      clearTimeout(timersMap.value[newData.id].timer);
      delete timersMap.value[newData.id];
      updateTableData(newData);

      if (newData.status.value === 'RUNNING') {
        notification.success('mock.startSuccess');
      } else {
        notification.warning('mock.startFail');
      }
      return;
    }

    updateStatusById(id);
  };

  /**
   * Batch start selected services
   */
  const batchStart = async () => {
    if (!rouSelection.value) {
      rouSelection.value = {
        onChange: handleSelectChange,
        getCheckboxProps: (record: MockServiceObj) => ({
          disabled: (record.auth && !record.currentAuthsValue.includes('RUN')) || record.status?.value !== 'NOT_STARTED'
        })
      };
      lastBatchType.value = 'start';
    } else {
      if (lastBatchType.value === 'start') {
        if (!rouSelection.value.selectedRowKeys?.length) {
          rouSelection.value = null;
          return;
        }
      } else {
        rouSelection.value = {
          onChange: handleSelectChange,
          getCheckboxProps: (record: MockServiceObj) => ({
            disabled: (record.auth && !record.currentAuthsValue.includes('RUN')) || record.status?.value !== 'NOT_STARTED'
          })
        };
        lastBatchType.value = 'start';
        if (rouSelection.value.selectedRowKeys) {
          rouSelection.value.selectedRowKeys = [];
        }
      }
    }

    const _selectKey = rouSelection.value?.selectedRowKeys;

    if (loading.value || !_selectKey?.length) {
      return;
    }

    loading.value = true;
    const [error, { data }] = await mock.startService(_selectKey);
    loading.value = false;

    if (error) {
      return;
    }

    const failList = data.filter(item => !item.success);
    if (failList.length) {
      const _failIds: string[] = [];
      for (let i = 0; i < failList.length; i++) {
        const failItem = failList[i];
        _failIds.push(failItem.serviceId);
        const matchingItem = tableData.value.find(dataItem => dataItem.id === failItem.serviceId);
        if (matchingItem) {
          matchingItem.failTips = failItem;
        }
      }

      if (failList.length !== data.length) {
        if (rouSelection.value?.selectedRowKeys) {
          rouSelection.value.selectedRowKeys = rouSelection.value.selectedRowKeys.filter((f: string) => !_failIds.includes(f));
        }
      } else {
        notification.success('mock.batchStartFail');
        return;
      }
    }

    notification.success('mock.starting');
    for (let i = 0; i < tableData.value.length; i++) {
      const _data = tableData.value[i];
      if (rouSelection.value?.selectedRowKeys?.includes(_data.id)) {
        _data.status = { value: 'STARTING', message: 'mock.startPending' };
        _data.currentAuthsValue = ['VIEW'];
      }
    }

    const _ids = JSON.parse(JSON.stringify(rouSelection.value?.selectedRowKeys || []));
    if (rouSelection.value) {
      rouSelection.value.selectedRowKeys = [];
      rouSelection.value = null;
    }
    updateStatusByIds(_ids);
  };

  /**
   * Update status for multiple services by IDs with polling
   */
  const updateStatusByIds = (ids: string[]) => {
    const key = ids.join('');
    if (!timersMap.value[key]?.count) {
      timersMap.value[key] = { count: 0, timer: undefined };
    }

    timersMap.value[key].count++;
    timersMap.value[key].timer = setTimeout(async () => {
      await getServicesByIds(ids);
    }, 2000);
  };

  /**
   * Get services by IDs from API
   */
  const getServicesByIds = async (ids: string[]) => {
    const [error, { data = { list: [], total: 0 } }] = await mock.getServiceList({
      filters: [{ key: 'id', value: ids, op: 'IN' }],
      projectId: projectId.value
    });

    if (error) {
      return;
    }

    let _ids = JSON.parse(JSON.stringify(ids));
    const newDataList = data.list;

    for (let i = 0; i < newDataList.length; i++) {
      const newData = newDataList[i];
      updateTableData(newData);

      if (newData.status.value === 'RUNNING') {
        notification.success('mock.serviceStartSuccess');
        _ids = _ids.filter((id: string) => id !== newData.id);
        const key = ids.join('');

        if (timersMap.value[key]) {
          clearTimeout(timersMap.value[key].timer);
          delete timersMap.value[key];
        }
      }
    }

    if (_ids.length) {
      const key = ids.join('');
      if (_ids.length === ids.length && timersMap.value[key] && timersMap.value[key].count === 16) {
        clearTimeout(timersMap.value[key].timer);
        delete timersMap.value[key];

        for (let i = 0; i < newDataList.length; i++) {
          const newData = newDataList[i];
          updateTableData(newData);
        }

        notification.warning('mock.startFail');
      } else {
        updateStatusByIds(_ids);
      }
    }
  };

  /**
   * Handle delete service(s)
   */
  const handleDelete = async (ids: string[]) => {
    modal.confirm({
      centered: true,
      content: 'mock.deleteTip',
      async onOk () {
        loading.value = true;
        const [error] = await mock.deleteService(ids);
        loading.value = false;

        if (error) {
          return;
        }

        notification.success('mock.deleteSuccess');
        getCurrentPageAfterDeletion();
        fetchMockServiceList();
      }
    });
  };

  /**
   * Force delete a service
   */
  const forceDelete = (record: MockServiceObj) => {
    modal.confirm({
      centered: true,
      content: 'mock.deleteTip',
      async onOk () {
        loading.value = true;
        const [error] = await mock.deleteServiceByForce([record.id]);
        loading.value = false;

        if (error) {
          return;
        }

        notification.success('mock.deleteSuccess');
        getCurrentPageAfterDeletion();
        fetchMockServiceList();
      }
    });
  };

  /**
   * Batch delete selected services
   */
  const batchDelete = async () => {
    if (!rouSelection.value) {
      rouSelection.value = {
        onChange: handleSelectChange,
        getCheckboxProps: (record: MockServiceObj) => ({
          disabled: (record.auth && !record.currentAuthsValue.includes('DELETE')) || record.status?.value !== 'NOT_STARTED'
        })
      };
      lastBatchType.value = 'del';
    } else {
      if (lastBatchType.value === 'del') {
        if (!rouSelection.value.selectedRowKeys?.length) {
          rouSelection.value = null;
          return;
        }
      } else {
        rouSelection.value = {
          onChange: handleSelectChange,
          getCheckboxProps: (record: MockServiceObj) => ({
            disabled: (record.auth && !record.currentAuthsValue.includes('DELETE')) || record.status?.value !== 'NOT_STARTED'
          })
        };
        lastBatchType.value = 'del';
        if (rouSelection.value.selectedRowKeys) {
          rouSelection.value.selectedRowKeys = [];
        }
      }
    }

    const _selectKey = rouSelection.value?.selectedRowKeys;

    if (loading.value || !_selectKey?.length) {
      return;
    }

    modal.confirm({
      centered: true,
      content: 'mock.deleteTip',
      async onOk () {
        loading.value = true;
        const [error] = await mock.deleteService(_selectKey);
        loading.value = false;

        if (error) {
          return;
        }

        notification.success('mock.batchDelSuccess');
        getCurrentPageAfterDeletion();
        fetchMockServiceList();

        if (!rouSelection.value) {
          return;
        }

        rouSelection.value.selectedRowKeys = [];
        rouSelection.value = null;
        lastBatchType.value = 'del';
      }
    });
  };

  /**
   * Handle refresh service status
   */
  const handleRefresh = () => {
    fetchMockServiceList();
  };

  /**
   * Handle update service status (start/stop)
   */
  const handleUpdateStatus = async (item: MockServiceObj) => {
    if (loading.value) {
      return;
    }

    loading.value = true;
    const [error, { data }] = item.status?.value === 'RUNNING'
      ? await mock.stopService([item.id])
      : await mock.startService([item.id]);

    loading.value = false;

    if (error) {
      item.failTips = { exitCode: (error as any).code, message: (error as any).msg };
      return;
    }

    if (!data[0].success) {
      item.failTips = data[0];
      item.status?.value === 'RUNNING'
        ? notification.success('mock.stop_fail')
        : notification.success('mock.start_fail');
      return;
    }

    if (item.status?.value === 'RUNNING') {
      notification.success('mock.stop_success');
      getStopResById(item.id);
    } else {
      notification.warning('mock.in_starting');
      if (item.status?.value === 'NOT_STARTED') {
        item.status = { value: 'STARTING', message: 'mock.startPending' };
        item.currentAuthsValue = ['VIEW'];
      }
      updateStatusById(item.id);
    }
  };

  /**
   * Get stopped service result by ID
   */
  const getStopResById = async (id: string) => {
    const [error, { data = { list: [], total: 0 } }] = await mock.getServiceList({
      filters: [{ key: 'id', value: id, op: 'EQUAL' }],
      projectId: projectId.value
    });

    if (error) {
      return;
    }

    const newData = data.list[0];
    updateTableData(newData);
  };

  /**
   * Clear all timers on component unmount
   */
  const clearAllTimers = () => {
    const keys = Object.keys(timersMap.value);
    if (!keys.length) {
      return;
    }

    for (let i = 0; i < keys.length; i++) {
      const item = keys[i];
      if (timersMap.value[item]?.timer) {
        clearTimeout(timersMap.value[item].timer);
      }
    }

    timersMap.value = {};
  };

  return {
    // State
    rouSelection,
    lastBatchType,
    timersMap,

    // Methods
    handleSelectChange,
    updateStatusById,
    getServiceById,
    batchStart,
    updateStatusByIds,
    getServicesByIds,
    handleDelete,
    forceDelete,
    batchDelete,
    handleRefresh,
    handleUpdateStatus,
    getStopResById,
    clearAllTimers
  };
}
