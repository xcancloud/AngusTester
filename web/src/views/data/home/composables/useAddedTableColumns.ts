import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

import { DataType } from '@/views/data/home/types';

/**
 * <p>
 * Table columns configuration composable
 * </p>
 * <p>
 * Dynamically generates table columns based on data type with proper internationalization
 * </p>
 */
export function useAddedTableColumns (type: DataType) {
  const { t } = useI18n();

  /**
   * <p>
   * Generate table columns configuration based on data type
   * </p>
   */
  const columns = computed(() => {
    const baseColumns = [
      {
        title: t('dataHome.summaryTable.columns.id'),
        dataIndex: 'id'
      },
      {
        title: t('dataHome.summaryTable.columns.name'),
        dataIndex: 'name',
        ellipsis: true,
        sorter: true
      }
    ];

    // Add type-specific columns
    const typeSpecificColumns = [];

    if (['space', 'dataSource'].includes(type)) {
      typeSpecificColumns.push(
        {
          title: t('dataHome.summaryTable.columns.createdBy'),
          dataIndex: 'createdByName'
        },
        {
          title: t('dataHome.summaryTable.columns.createdDate'),
          dataIndex: 'createdDate'
        }
      );
    }

    if (['variable', 'dataSet'].includes(type)) {
      typeSpecificColumns.push(
        {
          title: t('dataHome.summaryTable.columns.lastModifiedBy'),
          dataIndex: 'lastModifiedByName',
          ellipsis: true
        },
        {
          title: t('dataHome.summaryTable.columns.lastModifiedDate'),
          dataIndex: 'lastModifiedDate'
        }
      );
    }

    // Add action column
    const actionColumn = {
      title: t('dataHome.summaryTable.columns.action'),
      dataIndex: 'action',
      width: 50
    };

    return [...baseColumns, ...typeSpecificColumns, actionColumn];
  });

  return {
    columns
  };
}
