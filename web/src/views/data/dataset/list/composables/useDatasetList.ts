import { ref, watch, onMounted } from 'vue';
import { dataSet } from '@/api/tester';
import { DataSetItem } from '../../types';
import { getCurrentPage } from '@/utils/utils';
import { notification } from '@xcan-angus/vue-ui';

/**
 * Dataset list management composable
 * Handles data loading, pagination, and dataset operations
 */
export function useDatasetList (props: {
  projectId: string;
  notify: string;
}, deleteTabPane: (keys: string[]) => void) {
  // Loading states
  const loaded = ref(false);
  const loading = ref(false);
  const searchedFlag = ref(false);

  // Pagination configuration
  const pagination = ref<{ current: number; pageSize: number; total: number; }>({
    current: 1,
    pageSize: 10,
    total: 0
  });

  // Search parameters
  const searchPanelParams = ref({
    orderBy: undefined as string | undefined,
    orderSort: undefined as 'ASC' | 'DESC' | undefined,
    filters: [] as {key: string; op: string; value: string | string[]}[]
  });

  // Table data
  const tableData = ref<DataSetItem[]>([]);

  /**
   * Load dataset list from API
   * Fetches datasets based on current pagination and search parameters
   */
  const loadData = async () => {
    const params: {
      projectId: string;
      pageNo: number;
      pageSize: number;
      orderBy?: string;
      orderSort?: 'ASC' | 'DESC';
      filters?: Array<Record<string, unknown>>
    } = {
      projectId: props.projectId,
      pageNo: pagination.value.current,
      pageSize: pagination.value.pageSize,
      ...searchPanelParams.value
    };

    loading.value = true;
    const [error, res] = await dataSet.getDataSetList(params);
    loaded.value = true;
    loading.value = false;

    if (params.filters?.length) {
      searchedFlag.value = true;
    } else {
      searchedFlag.value = false;
    }

    if (error) {
      pagination.value.total = 0;
      tableData.value = [];
      return;
    }

    const data = res?.data || { total: 0, list: [] };
    if (data) {
      pagination.value.total = +data.total;
      const _list = data.list as DataSetItem[];
      tableData.value = [..._list];
    }
  };

  /**
   * Handle table change events (pagination, sorting)
   * Updates pagination and sorting parameters, then reloads data
   */
  const handleTableChange = ({ current, pageSize }: { current: number; pageSize: number; }, _filters: { [key: string]: any }[], sorter: { orderBy: string; orderSort: 'ASC' | 'DESC' }) => {
    pagination.value.current = current;
    pagination.value.pageSize = pageSize;

    searchPanelParams.value.orderBy = sorter.orderBy;
    searchPanelParams.value.orderSort = sorter.orderSort;

    loadData();
  };

  /**
   * Handle search panel change events
   * Updates search filters and resets to first page, then reloads data
   */
  const handleSearchPanelChange = (data: { filters: {key: string; op: string; value: string | string[]}[] }) => {
    searchPanelParams.value.filters = data.filters;
    pagination.value.current = 1;
    loadData();
  };

  /**
   * Refresh the dataset list
   * Resets to first page and reloads data
   */
  const refresh = () => {
    pagination.value.current = 1;
    loadData();
  };

  /**
   * Delete a dataset
   * Performs deletion and updates the list accordingly
   */
  const deleteDataset = async (data: DataSetItem, t: (key: string, params?: any) => string) => {
    const id = data.id;
    const [error] = await dataSet.deleteDataSet([id]);
    if (error) {
      return;
    }

    notification.success(t('actions.tips.deleteSuccess'));
    pagination.value.current = getCurrentPage(
      pagination.value.current,
      pagination.value.pageSize,
      pagination.value.total
    );
    loadData();

    deleteTabPane([id]);
  };

  /**
   * Clone a dataset
   * Creates a copy of the dataset and refreshes the list
   */
  const cloneDataset = async (data: DataSetItem, t: (key: string) => string) => {
    loading.value = true;
    const [error] = await dataSet.cloneDataSet([data.id]);
    loading.value = false;
    if (error) {
      return;
    }

    notification.success(t('actions.tips.cloneSuccess'));
    loadData();
  };

  // Watch for project ID or notify changes to reload data
  onMounted(() => {
    watch(() => props.projectId, (newValue) => {
      if (!newValue) {
        return;
      }

      pagination.value.current = 1;
      loadData();
    }, { immediate: true });

    watch(() => props.notify, (newValue) => {
      if (!newValue) {
        return;
      }

      loadData();
    }, { immediate: true });
  });

  return {
    // Reactive states
    loaded,
    loading,
    searchedFlag,
    pagination,
    tableData,

    // Methods
    loadData,
    handleTableChange,
    handleSearchPanelChange,
    refresh,
    deleteDataset,
    cloneDataset
  };
}
