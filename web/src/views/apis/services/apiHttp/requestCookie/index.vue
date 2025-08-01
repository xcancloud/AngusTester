<script setup lang="ts">
import { inject, reactive, ref, watch } from 'vue';
import { Button, Checkbox, Tooltip } from 'ant-design-vue';
import { Icon, Input, notification, Select, SelectSchema, ParamInput } from '@xcan-angus/vue-ui';
import SwaggerUI from '@xcan-angus/swagger-ui';

import { ParamsItem } from '@/views/apis/services/apiHttp/requestParam/interface';
import { getDefaultParams } from '../interface';
// import ParamsInput from '@/components/ParamInput/index.vue';
import { API_EXTENSION_KEY, variableNameReg } from '@/views/apis/utils';
import { deconstruct } from '@/utils/swagger';
import { services, variable as variableApi } from 'src/api/tester';
import { itemTypes } from './interface';
import { deepDelAttrFromObj, validateType } from '../utils';
import JsonContent from '../requestBody/json/index.vue';
import SimpleEditableSelect from '@/components/apis/editableSelector/index.vue';
import { toClipboard } from '@xcan-angus/infra';

const valueKey = API_EXTENSION_KEY.valueKey;
const enabledKey = API_EXTENSION_KEY.enabledKey;
// const exportVariableFlagKey = API_EXTENSION_KEY.exportVariableFlagKey;

interface Props {
  value: ParamsItem[];
}

const props = withDefaults(defineProps<Props>(), {});
const apiBaseInfo = inject('apiBaseInfo', ref());
const archivedId = inject('archivedId', ref());
const globalConfigs = inject('globalConfigs', { VITE_API_PARAMETER_NAME_LENGTH: 400, VITE_API_PARAMETER_VALUE_LENGTH: 4096 });

// eslint-disable-next-line func-call-spacing
const emits = defineEmits<{
  (e: 'change', data: ParamsItem[]): void,
  (e: 'del', index: number): void,
}>();

const state = reactive<{
  formData:ParamsItem[]
}>({
  formData: []
});

const jsContentRef = ref<any[]>([]);
const getKey = (index?: number): symbol => {
  return Symbol(index);
};

const enterHandle = (e: ChangeEvent): void => {
  if (e.key !== 'Enter') {
    return;
  }

  e.target.blur();
};

const handleValueBlur = (target:HTMLElement, index: number, data: ParamsItem):void => {
  let value = target.innerText.trim().replace('\n', '');
  if (['integer', 'number', 'boolean'].includes(data.schema?.type)) {
    try {
      if (value <= 9007199254740992) {
        value = JSON.parse(value);
      }
    } catch {}
  }
  if (value === data[valueKey]) {
    return;
  }

  const temp = { ...data, [valueKey]: value } as ParamsItem;
  changeEmit(index, temp);
};

const getModelData = async (ref) => {
  const [error, { data }] = await services.getRefInfo(apiBaseInfo.value?.serviceId, ref);
  if (error) {
    return {};
  }
  return deconstruct(data || {});
};

const selectModels = async (_value, option, index) => {
  if (option) {
    const model = await getModelData(option.ref);
    const schema = model.schema ? { ...model.schema, [valueKey]: model[valueKey] || model.schema?.[valueKey] } : {};
    const value = SwaggerUI.extension.sampleFromSchemaGeneric(schema);
    if (option.readonly) {
      model.$ref = option.ref;
    }
    if (!schema.type) {
      let type: string = typeof value;
      if (type === 'object') {
        if (Object.prototype.toString.call(value) === '[object Array]') {
          type = 'array';
        }
      }
      schema.type = type;
    }
    const temp = { ...model, schema, [enabledKey]: true, [valueKey]: value };
    changeEmit(index, temp);
  }
};

const handleBlur = (e: ChangeEvent, index: number, data: ParamsItem, key: string): void => {
  const value = e.target.value.trim();
  const temp = { ...data, [key]: value } as ParamsItem;
  changeEmit(index, temp);
};

// 启用 禁用
const handleChecked = (e:ChangeEvent, index:number, data: ParamsItem) => {
  const checked = e.target.checked;
  const temp = { ...data, [enabledKey]: checked } as ParamsItem;
  changeEmit(index, temp);
  if (!checked && validated.value) {
    jsContentRef.value[index] && jsContentRef.value[index].validate(false);
  }
};

const copyValue = async (data: ParamsItem) => {
  let text = data[valueKey];
  if (typeof text !== 'string') {
    text = JSON.stringify(text);
  }

  toClipboard(text).then(() => {
    notification.success('成功复制值到剪贴板');
  });
};

// 同步给父级
const emitChange = () => {
  emits('change', state.formData.filter(i => !!i.name));
};

const changeDataType = (value, index, item) => {
  const schema = item.schema || {};
  const temp = { ...item, schema: { ...schema, type: value } };
  if (value === 'object') {
    temp.deepObject = true;
    temp[valueKey] = { '': '' };
  } else {
    temp[valueKey] = undefined;
    if (value === 'array') {
      temp[valueKey] = [''];
      temp.schema.item = {
        type: 'string'
      };
    }
    delete temp.deepObject;
    delete temp.explode;
  }
  changeEmit(index, temp);
};

const handleDel = (index: number, data: ParamsItem): void => {
  const emptyList = state.formData.filter(item => !item.name);
  // 最少要有一条空数据
  if (!data.name && emptyList.length <= 1) {
    return;
  }
  state.formData.splice(index, 1);
  emitChange();
};

const addChild = (pItem, idx) => {
  jsContentRef.value[idx].addItem({ type: pItem.schema.type, id: -1, idLine: [-1], level: 0 });
};

const changeEmit = (index: number, data: ParamsItem): void => {
  state.formData[index] = data;
  if (state.formData.every(i => !!i.name)) {
    state.formData.push(getDefaultParams({ in: 'cookie' }) as ParamsItem);
  }
  emitChange();
};

const filterModels = (option) => {
  const model = option.model && JSON.parse(option.model);
  return !model || model.in !== 'cookie';
};

const validated = ref(false);
const validateContents = async (val = true) => {
  validated.value = val;
  for (const idx in jsContentRef.value) {
    if (state.formData[idx][enabledKey]) {
      jsContentRef.value[idx].validate(val);
    }
  }
};

const getErrValue = (item) => {
  if (!validated.value || !item.name || !item[enabledKey]) {
    return false;
  }
  const errors = validateType(item[valueKey], deepDelAttrFromObj(item.schema));
  return !!errors?.length;
};

const changeSchema = (schema, item, index) => {
  if (item.$ref) {
    return;
  }
  const temp = { ...item, schema };
  changeEmit(index, temp);
};

watch(() => props.value, (newValue) => {
  if (state.formData.length && state.formData.some(item => !!item.name || (!!item.schema?.type && item.schema?.type !== 'string') || !!item[valueKey])) {
    return;
  }
  state.formData = (newValue || []).map((i, idx) => {
    return { ...i, key: i.key || getKey(idx) };
  });
  if (state.formData.every(i => !!i.name)) {
    state.formData.push(getDefaultParams({ in: 'cookie', key: getKey() }) as ParamsItem);
  }
}, {
  deep: true,
  immediate: true
});

const updateComp = async () => {
  if (!apiBaseInfo.value?.serviceId) {
    return;
  }
  for (let i = 0; i < state.formData.length; i++) {
    if (state.formData[i].$ref) {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { $ref, ...content } = state.formData[i];
      await services.addComponent(apiBaseInfo.value?.serviceId, 'parameters', state.formData[i].name, { ...content, schema: { ...(content.schema || {}), [valueKey]: content[valueKey] } });
    }
    if (jsContentRef.value[i]) {
      await jsContentRef.value[i].updateComp();
    }
  }
};

const getModelResolve = () => {
  const models = {};
  state.formData.forEach((i, index) => {
    if (i.$ref) {
      models[i.$ref] = JSON.parse(JSON.stringify(i));
      delete models[i.$ref].schema.$ref;
      jsContentRef.value[index] && jsContentRef.value[index].getModelResolve(models);
    }
  });
  return models;
};

defineExpose({ updateComp, getModelResolve, validate: validateContents });
</script>
<template>
  <div class="space-y-3 min-w-220">
    <div
      v-for="(item, index) in state.formData"
      :key="item.key"
      class="space-y-2">
      <div
        class="flex flex-nowrap items-center mb-3 whitespace-nowrap space-x-2"
        :class="{'opacity-50': !item[enabledKey]}">
        <Checkbox
          :disabled="!item.name"
          :checked="item[enabledKey] && !!item.name"
          @change="handleChecked($event, index, item)" />
        <Tooltip placement="topLeft">
          <div class="flex flex-col w-100 flex-shrink-0" :class="{'border-l border-blue-1': item.$ref}">
            <SelectSchema
              v-if="apiBaseInfo?.serviceId"
              :id="apiBaseInfo?.serviceId"
              v-model:value="item.name"
              placeholder="参数名称"
              mode="pure"
              :maxLength="globalConfigs.VITE_API_PARAMETER_NAME_LENGTH"
              :params="{ ignoreModel: false, types: 'parameters'}"
              :type="['parameters']"
              :disabled="item.$ref"
              :excludes="opt => filterModels(opt)"
              @blur="handleBlur($event, index, state.formData[index], 'name')"
              @change="(...arg) => selectModels(...arg, index)" />
            <Input
              v-else
              placeholder="请输入参数名称"
              :value="item.name"
              :allowClear="false"
              :maxLength="globalConfigs.VITE_API_PARAMETER_NAME_LENGTH"
              size="small"
              @blur="handleBlur($event, index, item, 'name')"
              @keypress="enterHandle" />
          </div>
          <template v-if="item.$ref" #title>
            组件引用：{{ item.$ref }}
          </template>
        </Tooltip>
        <Select
          v-model:value="item.schema.type"
          :options="itemTypes"
          class="w-25 flex-shrink-0"
          @change="changeDataType($event, index, item)" />
        <div class="flex flex-col flex-1 flex-shrink-0">
          <SimpleEditableSelect
            v-if="item.schema?.enum"
            :placeholder="`请输入调试值，最大支持${globalConfigs.VITE_API_PARAMETER_VALUE_LENGTH}个字符`"
            :maxLength="globalConfigs.VITE_API_PARAMETER_VALUE_LENGTH"
            :options="item.schema.enum || item.schema?.[valueKey]"
            :value="item[valueKey]"
            @blur="handleValueBlur($event,index,item )"
            @select="changeEmit(index, { ...item, [valueKey]: $event, schema: {...item?.schema|| {}, [valueKey]: $event} })" />
          <ParamInput
            v-else-if="!['array', 'object'].includes(item.schema.type)"
            :placeholder="`请输入调试值，最大支持${globalConfigs.VITE_API_PARAMETER_VALUE_LENGTH}个字符`"
            :maxLength="globalConfigs.VITE_API_PARAMETER_VALUE_LENGTH"
            :value="item[valueKey]"
            :error="getErrValue(item)"
            @blur="handleValueBlur($event, index, item)" />
          <Input v-else disabled />
        </div>
        <Button
          type="primary"
          size="small"
          title="复制值"
          class="ml-2"
          @click="copyValue(item)">
          <Icon icon="icon-fuzhi" />
        </Button>
        <Button
          size="small"
          :disabled="!['array', 'object'].includes(item.schema.type) || (item.schema.type === 'object' && item.$ref)"
          @click="addChild(item, index)">
          <Icon icon="icon-jia" />
        </Button>
        <Button
          class="w-7 ml-3 p-0"
          type="default"
          size="small"
          :disabled="!item.name && !item[valueKey]"
          @click="handleDel(index, item)">
          <Icon icon="icon-shanchuguanbi" />
        </Button>
      </div>
      <JsonContent
        v-if="item.schema?.type === 'array' || item.schema?.type === 'object'"
        :ref="dom => jsContentRef[index] = dom"
        v-model:data="item[valueKey]"
        :schema="item.schema || {}"
        :disabled="!!item.$ref"
        :pType="item.schema?.type"
        @change="changeSchema($event, item, index)" />
    </div>
  </div>
</template>
../RequestParams/interface
