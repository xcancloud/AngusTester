import { useI18n } from 'vue-i18n';
import type { TableColumn } from '../types';

/**
 * Composable function for managing table columns
 *
 * @returns Object containing table column configurations
 */
export function useTableColumns () {
  const { t } = useI18n();

  /**
   * Table column definitions
   */
  const columns: TableColumn[] = [
    {
      title: t('common.name'),
      dataIndex: 'name',
      ellipsis: true,
      width: '16%',
      sorter: true
    },
    {
      title: t('common.value'),
      dataIndex: 'value',
      ellipsis: true,
      width: '20%'
    },
    {
      title: t('common.description'),
      dataIndex: 'description',
      ellipsis: true,
      width: '26%'
    },
    {
      title: t('common.password'),
      dataIndex: 'passwordValue',
      ellipsis: true,
      width: '6%',
      customRender: ({ text }: { text: any }) => text ? t('dataVariable.list.isPassword') : t('dataVariable.list.notPassword')
    },
    {
      title: t('common.source'),
      dataIndex: 'dataSource',
      ellipsis: true,
      width: '12%',
      customRender: ({ text }: { text: any }) => text?.message
    },
    {
      title: t('common.createdBy'),
      dataIndex: 'createdBy',
      ellipsis: true,
      width: '12%',
      sorter: true,
      groupName: 'person',
      customRender: ({ record }: { record: any }) => record.createdByName
    },
    {
      title: t('common.lastModifiedBy'),
      dataIndex: 'lastModifiedBy',
      ellipsis: true,
      width: '12%',
      sorter: true,
      groupName: 'person',
      hide: true,
      customRender: ({ record }: { record: any }) => record.lastModifiedByName
    },
    {
      title: t('common.createdDate'),
      dataIndex: 'createdDate',
      ellipsis: true,
      width: '12%',
      sorter: true,
      groupName: 'date'
    },
    {
      title: t('common.lastModifiedDate'),
      dataIndex: 'lastModifiedDate',
      ellipsis: true,
      width: '12%',
      sorter: true,
      groupName: 'date',
      hide: true
    },
    {
      title: t('common.actions'),
      dataIndex: 'action',
      width: '180px'
    }
  ];

  return {
    columns
  };
}
