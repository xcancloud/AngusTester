import { useI18n } from 'vue-i18n';
import { computed, ref, watch, onMounted } from 'vue';
import { utils, duration } from '@xcan-angus/infra';
import { debounce } from 'throttle-debounce';

/**
 * Composable for managing parameter name input logic in dataset components
 * Handles validation, duplicate checking, and data management for parameter names
 */
export function useParameterNameInput (props: { defaultValue: { name: string }[]; columnIndex: string }, emit: (event: 'change', value: { name: string }[]) => void) {
  const { t } = useI18n();

  // Reactive references for component state
  const idList = ref<string[]>([]);
  const dataMap = ref<{ [key: string]: { name: string } }>({});
  const errorMessage = ref<Map<string, string>>(new Map());
  const nameErrorSet = ref<Set<string>>(new Set());

  /**
   * Handle name change event with debouncing
   * Validates input and checks for duplicates
   */
  const nameChange = debounce(duration.delay, (event: { target: { value: string; } }, id: string, index: number) => {
    nameErrorSet.value.delete(id);
    errorMessage.value.delete(id);

    // Ensure the last item is empty
    if (index === idList.value.length - 1 && event.target.value) {
      addNewItem();
    }

    // Check for duplicate names
    const duplicates: string[] = [];
    const uniqueNames = new Set();
    const names = Object.values(dataMap.value).map(item => item.name).filter(item => item);
    for (let i = 0, len = names.length; i < len; i++) {
      const name = names[i];
      if (uniqueNames.has(name)) {
        duplicates.push(name);
      } else {
        uniqueNames.add(name);
      }
    }

    const ids = idList.value;
    const data = dataMap.value;
    for (let i = 0, len = ids.length; i < len; i++) {
      const _id = ids[i];
      if (duplicates.includes(data[_id].name)) {
        nameErrorSet.value.add(_id);
        errorMessage.value.set(_id, t('dataset.detail.parameterNameInput.errors.duplicate'));
      } else {
        nameErrorSet.value.delete(_id);
        errorMessage.value.delete(_id);
      }
    }

    emitChange();
  });

  /**
   * Handle blur event for name input
   * Validates the input format
   */
  const nameBlur = (event: { target: { value: string; } }, id: string) => {
    const name = event.target.value;
    if (!name) {
      return;
    }

    // Validate name format - only allow alphanumeric and specific special characters
    const rex = /[^a-zA-Z0-9!$%^&*_\-+=./]/;
    if (rex.test(name)) {
      nameErrorSet.value.add(id);
      errorMessage.value.set(id, t('dataset.detail.parameterNameInput.errors.invalid'));
    }
  };

  /**
   * Emit change event with current data
   */
  const emitChange = () => {
    const data = getData();
    emit('change', data);
  };

  /**
   * Add a new empty item to the list
   */
  const addNewItem = () => {
    const id = utils.uuid();
    const data = { name: '', value: '' };
    idList.value.push(id);
    dataMap.value[id] = data;
  };

  /**
   * Delete an item from the list
   */
  const deleteHandler = (id: string, index: number): void => {
    const length = idList.value.length - 1;
    idList.value.splice(index, 1);

    delete dataMap.value[id];
    errorMessage.value.delete(id);
    nameErrorSet.value.delete(id);
    emitChange();

    // If the deleted item was the last one, add a new empty item
    if (index === length) {
      addNewItem();
    }
  };

  /**
   * Reset all state
   */
  const reset = () => {
    idList.value = [];
    dataMap.value = {};
    errorMessage.value.clear();
    nameErrorSet.value.clear();
  };

  /**
   * Computed property for mapping values based on column index
   */
  const valueMap = computed(() => {
    const num = props.columnIndex ? +props.columnIndex : 0;
    const len = idList.value.length - 1;
    return idList.value.reduce((prev, cur, index) => {
      if (index < len) {
        prev[cur] = num + index;
      }
      return prev;
    }, {} as { [key: string]: number });
  });

  /**
   * Validate all inputs
   * @returns boolean indicating if all inputs are valid
   */
  const isValid = (): boolean => {
    errorMessage.value.clear();
    nameErrorSet.value.clear();
    const ids = idList.value;
    const data = dataMap.value;

    // Check for duplicate names
    const duplicates: string[] = [];
    const uniqueNames = new Set();
    const names = Object.values(dataMap.value).map(item => item.name).filter(item => item);
    for (let i = 0, len = names.length; i < len; i++) {
      const name = names[i];
      if (uniqueNames.has(name)) {
        duplicates.push(name);
      } else {
        uniqueNames.add(name);
      }
    }

    // Validate all items except the last empty one
    for (let i = 0, len = ids.length - 1; i < len; i++) {
      const id = ids[i];
      const { name } = data[id];
      if (!name) {
        nameErrorSet.value.add(id);
      } else {
        if (duplicates.includes(name)) {
          nameErrorSet.value.add(id);
          errorMessage.value.set(id, '名称重复');
        }

        // Validate name format
        const rex = /[a-zA-Z0-9!$%^&*_\-+=./]+/;
        if (!rex.test(name)) {
          nameErrorSet.value.add(id);
          errorMessage.value.set(id, t('dataset.detail.parameterNameInput.errors.invalid'));
        }
      }
    }

    return !(nameErrorSet.value.size);
  };

  /**
   * Get current data
   * @returns Array of parameter name objects
   */
  const getData = () => {
    const ids = idList.value;
    const _dataMap = dataMap.value;
    const dataList: { name: string; }[] = [];

    // Exclude the last empty item
    for (let i = 0, len = ids.length - 1; i < len; i++) {
      dataList.push({ ..._dataMap[ids[i]] });
    }

    return dataList;
  };

  /**
   * Initialize component with default values
   */
  const initialize = () => {
    reset();
    if (props.defaultValue?.length) {
      const dataList = props.defaultValue;
      for (let i = 0, len = dataList.length; i < len; i++) {
        const id = utils.uuid();
        idList.value.push(id);
        dataMap.value[id] = dataList[i];
      }
    }

    addNewItem();
  };

  // Watch for changes in default value and reinitialize
  onMounted(() => {
    watch(() => props.defaultValue, () => {
      initialize();
    }, { immediate: true });
  });

  return {
    // State
    idList,
    dataMap,
    errorMessage,
    nameErrorSet,
    valueMap,

    // Methods
    nameChange,
    nameBlur,
    deleteHandler,
    isValid,
    getData,
    initialize
  };
}
