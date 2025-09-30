import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { modal, notification } from '@xcan-angus/vue-ui';
import { toClipboard, utils } from '@xcan-angus/infra';
import { variable } from '@/api/tester';
import { VariableItem } from '../../types';
import { BasicProps } from '@/types/types';

/**
 * Composable for managing variable detail page logic
 * Provides data loading, CRUD operations, and tab management for variable details
 */
export function useVariableDetail (
  props: BasicProps,
  tabActions: {
    updateTabPane: (data: { [key: string]: any }) => void;
    deleteTabPane: (keys: string[]) => void;
    replaceTabPane: (id: string, data: { [key: string]: any }) => void;
    refreshList: () => void;
  }
) {
  const { t } = useI18n();

  // State management
  const loading = ref(false);
  const loaded = ref(false);
  const dataSource = ref<VariableItem>();
  const exportModalVisible = ref(false);
  const exportId = ref<string>();

  /**
   * Load variable detail data from API
   * Fetches variable details by ID and updates tab information
   *
   * @param id - Variable ID to load
   */
  const loadData = async (id: string) => {
    if (loading.value) {
      return;
    }

    loading.value = true;
    const [error, res] = await variable.getVariableDetail(id);
    loading.value = false;
    loaded.value = true;
    if (error) {
      notification.error(error.message);
      tabActions.deleteTabPane([id]);
      return;
    }

    const data = res?.data as VariableItem;
    if (!data) {
      return;
    }

    dataSource.value = data;
    const name = data.name;
    if (name && typeof tabActions.updateTabPane === 'function') {
      tabActions.updateTabPane({ name, _id: id });
    }
  };

  /**
   * Handle save/ok action
   * Updates tab information after saving a variable
   *
   * @param data - Variable data that was saved
   * @param isEdit - Flag indicating if this is an edit operation
   */
  const handleOk = (data: VariableItem, isEdit = false) => {
    const { id, name } = data;
    if (!isEdit) {
      const _id = props.data?._id;
      tabActions.replaceTabPane(_id, { _id: id, uiKey: id, name, data: { _id: id, id } });
    } else {
      tabActions.updateTabPane({ _id: id, name, data: { id } });

      // Update data name
      if (dataSource.value) {
        dataSource.value.name = data.name;
      }
    }

    Promise.resolve().then(() => {
      tabActions.updateTabPane({ _id: 'variableList', notify: utils.uuid() });
    });
    tabActions.refreshList();
  };

  /**
   * Handle cancel action
   *
   * @param data - Variable data
   */
  const handleCancel = () => {
    const id = props?.data?._id;
    if (!id) {
      return;
    }
    tabActions.deleteTabPane([id]);
  };

  /**
   * Handle delete action
   * Shows confirmation dialog and deletes variable
   */
  const handleDelete = () => {
    const data = dataSource.value;
    if (!data) {
      return;
    }

    modal.confirm({
      content: t('actions.tips.confirmDelete', { name: data.name }),
      async onOk () {
        const id = data.id;
        const [error] = await variable.deleteVariables([id]);
        if (error) {
          return;
        }

        notification.success(t('actions.tips.deleteSuccess'));
        tabActions.deleteTabPane([id]);

        Promise.resolve().then(() => {
          tabActions.updateTabPane({ _id: 'variableList', notify: utils.uuid() });
          tabActions.refreshList();
        });
      }
    });
  };

  /**
   * Handle export action
   * Opens export modal for the variable
   *
   * @param id - Variable ID to export
   */
  const handleExport = (id: string) => {
    exportModalVisible.value = true;
    exportId.value = id;
  };

  /**
   * Handle clone action
   * Clones the current variable
   */
  const handleClone = async () => {
    const data = dataSource.value;
    if (!data) {
      return;
    }

    loading.value = true;
    const [error] = await variable.cloneVariable([data.id]);
    loading.value = false;
    if (error) {
      return;
    }

    notification.success(t('actions.tips.cloneSuccess'));
    Promise.resolve().then(() => {
      tabActions.updateTabPane({ _id: 'variableList', notify: utils.uuid() });
      tabActions.refreshList();
    });
  };

  /**
   * Handle copy link action
   * Copies the variable URL to clipboard
   *
   * @param id - Variable ID to copy link for
   */
  const handleCopyLink = (id: string) => {
    toClipboard(window.location.origin + `/data#variables?id=${id}`).then(() => {
      notification.success(t('actions.tips.copyLinkSuccess'));
    }).catch(() => {
      notification.error(t('actions.tips.copyLinkFail'));
    });
  };

  /**
   * Handle refresh action
   * Reloads variable data
   *
   * @param id - Variable ID to refresh
   */
  const handleRefresh = (id: string) => {
    loadData(id);
  };

  return {
    // State
    loading,
    loaded,
    dataSource,
    exportModalVisible,
    exportId,

    // Methods
    loadData,
    handleOk,
    handleCancel,
    handleDelete,
    handleExport,
    handleClone,
    handleCopyLink,
    handleRefresh
  };
}
