import { ref, computed, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { ScriptType, SearchCriteria } from '@xcan-angus/infra';
import { cloneDeep } from 'lodash-es';
import { CombinedTargetType } from '@/enums/enums';
import {
  createAuditOptions, createTimeOptions, createEnumTypeConfig, type QuickSearchConfig
} from '@/components/quickSearch';

/**
 * Composable for managing search functionality in script home
 * @param userId - The ID of the current user
 */
export function useScriptSearch (userId: string) {
  const { t } = useI18n();

  // Search panel reference
  const searchPanelRef = ref();
  // Quick search options reference
  const quickSearchOptionsRef = ref();

  // Filters
  const filters = ref<SearchCriteria[]>([]);
  const serviceIdFilter = ref<SearchCriteria>({
    key: 'serviceId',
    op: SearchCriteria.OpEnum.Equal,
    value: undefined
  });
  const sourceIdFilter = ref<SearchCriteria>({
    key: 'sourceId',
    op: SearchCriteria.OpEnum.Equal,
    value: undefined
  });

  // Flag to prevent circular updates
  let isUpdating = false;

  // Quick search configuration
  const quickSearchConfig = computed<QuickSearchConfig>(() => ({
    title: t('quickSearch.title'),
    auditOptions: createAuditOptions([
      { key: 'createdBy', name: t('quickSearch.createdByMe'), fieldKey: 'createdBy' },
      { key: 'lastModifiedBy', name: t('quickSearch.modifiedByMe'), fieldKey: 'lastModifiedBy' }
    ], userId),
    enumType: createEnumTypeConfig(ScriptType, 'type', [ScriptType.MOCK_APIS]),
    timeOptions: createTimeOptions([
      { key: 'last1Day', name: t('quickSearch.last1Day'), timeRange: 'last1Day' },
      { key: 'last3Days', name: t('quickSearch.last3Days'), timeRange: 'last3Days' },
      { key: 'last7Days', name: t('quickSearch.last7Days'), timeRange: 'last7Days' }
    ], 'createdDate'),
    externalClearFunction: () => {
      // Clear search panel
      if (typeof searchPanelRef.value?.clear === 'function') {
        searchPanelRef.value.clear();
      }
      // Clear serviceId and sourceId
      serviceIdFilter.value.value = undefined;
      sourceIdFilter.value.value = undefined;
    }
  }));

  /**
   * Handle quick search changes
   */
  const handleQuickSearchChange = (_selectedKeys: string[], searchCriteria: SearchCriteria[]) => {
    if (isUpdating) return;

    isUpdating = true;
    // Merge quick search criteria with existing filters
    const quickSearchFields = ['createdBy', 'lastModifiedBy', 'type', 'createdDate'];
    const otherFilters = filters.value.filter(f => f.key && !quickSearchFields.includes(f.key));
    if (_selectedKeys.includes('createdBy')) {
      if (!filters.value.find(f => f.key === 'createdBy')) {
        if (typeof searchPanelRef.value?.setConfigs === 'function') {
          searchPanelRef.value.setConfigs([{
            valueKey: 'createdBy',
            value: userId,
          }]);
        }
      }
    } else {
      if (filters.value.find(f => f.key === 'createdBy')?.value === userId) {
        if (typeof searchPanelRef.value?.setConfigs === 'function') {
          searchPanelRef.value.setConfigs([{
            valueKey: 'createdBy',
            value: undefined,
          }]);
        }
      }
    }
    if (_selectedKeys.includes('lastModifiedBy')) {
      if (!filters.value.find(f => f.key === 'lastModifiedBy')) {
        if (typeof searchPanelRef.value?.setConfigs === 'function') {
          searchPanelRef.value.setConfigs([{
            valueKey: 'lastModifiedBy',
            value: userId,
          }]);
        }
      }
    } else {
      if (filters.value.find(f => f.key === 'lastModifiedBy')?.value === userId) {
        if (typeof searchPanelRef.value?.setConfigs === 'function') {
          searchPanelRef.value.setConfigs([{
            valueKey: 'lastModifiedBy',
            value: undefined,
          }]);
        }
      }
    }
    const newFilters = [...otherFilters, ...searchCriteria];

    // Always update filters to ensure watch triggers
    filters.value = newFilters;
    isUpdating = false;
  };

  /**
   * Handle search panel change events
   */
  const handleSearchPanelChange = (
    _filters: SearchCriteria[],
    _headers?: { [key: string]: string },
    key?: string
  ) => {
    if (isUpdating) return;

    isUpdating = true;
    const quickSearchFields = ['type', 'createdDate'];
    const commonFields = ['createdBy', 'lastModifiedBy'];
    let quickSearchFilters = filters.value.filter(f => f.key && [...quickSearchFields, ...commonFields].includes(f.key));

    if (key && commonFields.includes(key)) {
      quickSearchFilters = quickSearchFilters.filter(f => f.key !== key);
    }

    const searchCriteriaKeys = quickSearchOptionsRef.value.getSearchCriteria().map(f => f.key);
    if (searchCriteriaKeys.includes('createdBy') && key === 'createdBy' && _filters.find(f => f.key === 'createdBy')?.value !== userId) {
      quickSearchOptionsRef.value.handleOptionClick({
        key: 'createdBy',
        name: t('quickSearch.createdByMe')
      });
    }
    if (searchCriteriaKeys.includes('lastModifiedBy') && key === 'lastModifiedBy' && _filters.find(f => f.key === 'lastModifiedBy')?.value !== userId) {
      quickSearchOptionsRef.value.handleOptionClick({
        key: 'lastModifiedBy',
        name: t('quickSearch.createdByMe')
      });
    }
  
    
    const newFilters = [...quickSearchFilters, ..._filters];

    // Check if filters actually changed
    if (JSON.stringify(filters.value) !== JSON.stringify(newFilters)) {
      filters.value = newFilters;
    }

    if (key === 'source') {
      // Reset service, API, scenario
      serviceIdFilter.value.value = undefined;
      sourceIdFilter.value.value = undefined;
    }
    console.log(filters.value, 'filters');

    isUpdating = false;
  };

  /**
   * Handle source ID change
   */
  const handleSourceIdChange = (value: string | undefined) => {
    sourceIdFilter.value = { key: 'sourceId', op: SearchCriteria.OpEnum.Equal, value };
  };

  /**
   * Handle service ID change
   */
  const handleServiceIdChange = (value: string | undefined) => {
    serviceIdFilter.value = { key: 'serviceId', op: SearchCriteria.OpEnum.Equal, value };
  };

  /**
   * Get search data for API requests
   */
  const getData = () => {
    // Package data
    const _filters: SearchCriteria[] = cloneDeep(filters.value);
    if (serviceIdFilter.value.value) {
      _filters.push({ ...(serviceIdFilter.value) });
    }

    if (sourceIdFilter.value.value) {
      _filters.push({ ...(sourceIdFilter.value) });
    }
    return _filters;
  };

  /**
   * Reset search data
   */
  const resetData = () => {
    filters.value = [];
    serviceIdFilter.value = { key: 'serviceId', op: SearchCriteria.OpEnum.Equal, value: undefined };
    sourceIdFilter.value = { key: 'sourceId', op: SearchCriteria.OpEnum.Equal, value: undefined };
  };

  /**
   * Reset search panel
   */
  const resetSearchPanel = () => {
    if (typeof searchPanelRef.value?.setConfigs === 'function') {
      const configs = searchOptions.map(item => {
        return {
          valueKey: item.valueKey,
          value: undefined
        };
      });

      searchPanelRef.value.setConfigs(configs);
    }
  };

  // Search options configuration
  const searchOptions = [
    {
      valueKey: 'name',
      placeholder: t('common.placeholders.searchKeyword'),
      type: 'input',
      maxlength: 100
    },
    {
      valueKey: 'source',
      placeholder: t('common.placeholders.selectSource'),
      type: 'select-enum',
      enumKey: 'ScriptSource',
      excludes: (data: { value: CombinedTargetType; message: string }) => {
        return [CombinedTargetType.TASK].includes(data.value);
      }
    },
    {
      valueKey: 'tag',
      placeholder: t('common.placeholders.searchTag'),
      type: 'input',
      op: SearchCriteria.OpEnum.Equal,
      maxlength: 100
    },
    {
      type: 'select',
      valueKey: 'serviceId',
      placeholder: t('common.placeholders.selectService'),
      noDefaultSlot: true
    },
    {
      type: 'select',
      valueKey: 'sourceId',
      noDefaultSlot: true
    },
    {
      type: 'select-user',
      valueKey: 'createdBy',
      placeholder: t('common.placeholders.selectCreator')
    },
    {
      type: 'select-user',
      valueKey: 'lastModifiedBy',
      placeholder: t('common.placeholders.selectModifier')
    },
    {
      type: 'date-range',
      valueKey: 'createdDate',
      placeholder: [
        t('common.placeholders.selectCreatedDateRange.0'),
        t('common.placeholders.selectCreatedDateRange.1')
      ],
      showTime: true
    },
    {
      type: 'date-range',
      valueKey: 'lastModifiedDate',
      placeholder: [
        t('common.placeholders.selectModifiedDateRange.0'),
        t('common.placeholders.selectModifiedDateRange.1')
      ],
      showTime: true
    }
  ] as any;

  const source = computed(() => {
    return filters.value.find(item => item.key === 'source')?.value;
  });

  const isAPISource = computed(() => {
    return source.value === CombinedTargetType.API;
  });

  const isScenarioSource = computed(() => {
    return source.value === CombinedTargetType.SCENARIO;
  });

  const apiParams = computed(() => {
    return {
      serviceId: serviceIdFilter.value.value
    };
  });

  return {
    // Reactive data
    searchPanelRef,
    quickSearchOptionsRef,
    filters,
    serviceIdFilter,
    sourceIdFilter,

    // Quick search configuration
    quickSearchConfig,
    handleQuickSearchChange,

    // Methods
    handleSearchPanelChange,
    handleSourceIdChange,
    handleServiceIdChange,
    getData,
    resetData,
    resetSearchPanel,

    // Computed properties
    searchOptions,
    isAPISource,
    isScenarioSource,
    apiParams
  };
}
