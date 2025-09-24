import { useI18n } from 'vue-i18n';
import type { DropdownMenuItem } from '../types';

/**
 * Composable function for managing dropdown menu configurations
 *
 * @returns Object containing dropdown menu configurations
 */
export function useDropdownMenus () {
  const { t } = useI18n();

  /**
   * Button dropdown menu items for different extraction types
   */
  const buttonDropdownMenuItems: DropdownMenuItem[] = [
    {
      name: t('dataVariable.list.dropdown.fileExtract'),
      key: 'file',
      noAuth: true
    },
    {
      name: t('dataVariable.list.dropdown.httpExtract'),
      key: 'http',
      noAuth: true
    },
    {
      name: t('dataVariable.list.dropdown.jdbcExtract'),
      key: 'jdbc',
      noAuth: true
    }
  ];

  /**
   * Table row action dropdown menu items
   */
  const tableDropdownMenuItems: DropdownMenuItem[] = [
    {
      name: t('actions.export'),
      key: 'export',
      icon: 'icon-daochu1'
    },
    {
      name: t('actions.clone'),
      key: 'clone',
      icon: 'icon-fuzhi'
    }
  ];

  /**
   * Search panel button dropdown menu items
   */
  const searchPanelButtonDropdownMenuItems: DropdownMenuItem[] = [
    {
      name: t('dataVariable.list.searchPanel.dropdown.fileExtract'),
      key: 'file',
      noAuth: true
    },
    {
      name: t('dataVariable.list.searchPanel.dropdown.httpExtract'),
      key: 'http',
      noAuth: true
    },
    {
      name: t('dataVariable.list.searchPanel.dropdown.jdbcExtract'),
      key: 'jdbc',
      noAuth: true
    }
  ];

  return {
    buttonDropdownMenuItems,
    tableDropdownMenuItems,
    searchPanelButtonDropdownMenuItems
  };
}
