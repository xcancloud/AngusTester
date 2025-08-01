<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { Colon, DatePicker, Hints, IconRequired, Select } from '@xcan-angus/vue-ui';
import { Tree } from 'ant-design-vue';
import { TESTER, GM } from '@xcan-angus/infra';
import { contentTreeData } from './config';

interface Props {
  projectId: string;
  contentSetting: {
    creatorObjectType?: string;
    creatorObjectId?: string;
    createdDateStart?: string;
    createdDateEnd?: string;
    targetId?: string;
  };
  disabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: '',
  disabled: false
});

const targetTypeOpt = [
  {
    value: 'USER',
    label: '用户'
  },
  {
    value: 'DEPT',
    label: '部门'
  },
  {
    value: 'GROUP',
    label: '组'
  }
];

const creatorObjectType = ref('USER');
const creatorObjectId = ref();

const dateRange = ref();
const planId = ref();

const handleTypeChange = () => {
  creatorObjectId.value = undefined;
};

const checked = ref<string[]>([]);
contentTreeData.forEach(item => {
  checked.value.push(item.key);
  if (item.children) {
    checked.value.push(...item.children.map(i => i.key));
  }
});

onMounted(() => {
  watch(() => props.contentSetting, newValue => {
    if (newValue) {
      if (newValue.creatorObjectType) {
        creatorObjectType.value = newValue.creatorObjectType;
      }
      if (newValue.creatorObjectId) {
        creatorObjectId.value = newValue.creatorObjectId;
      }

      const { createdDateStart, createdDateEnd, targetId } = newValue;
      if (createdDateStart && createdDateEnd) {
        dateRange.value = [createdDateStart, createdDateEnd];
      }
      if (targetId) {
        planId.value = targetId;
      }
    }
  });
});

const isValid = ref(false);
const validate = () => {
  isValid.value = true;
  if (!planId.value) {
    return false;
  }
  return true;
};

defineExpose({
  validate,
  getData: () => {
    isValid.value = false;
    return {
      targetId: planId.value,
      targetType: 'FUNC_PLAN',
      planOrSprintId: planId.value,
      creatorObjectType: creatorObjectId.value ? creatorObjectType.value : undefined,
      creatorObjectId: creatorObjectId.value,
      createdDateStart: dateRange.value?.[0] || undefined,
      createdDateEnd: dateRange.value?.[1] || undefined
    };
  }
});
</script>
<template>
  <div class="flex items-center space-x-1">
    <span class="h-4 w-1.5 bg-blue-border1"></span>
    <span>过滤</span>
  </div>
  <div class="mt-2  pl-2 space-y-2">
    <div class="flex flex-1 items-center space-x-2">
      <div class="w-16 text-right">
        <IconRequired class="mr-1" />
        测试计划
      </div>
      <Colon />
      <Select
        v-model:value="planId"
        :showSearch="true"
        :error="isValid && !planId"
        :disabled="!props.projectId || props.disabled"
        :defaultActiveFirstOption="true"
        allowClear
        class="w-72"
        :lazy="false"
        placeholder="选择计划"
        :action="`${TESTER}/func/plan?projectId=${props.projectId}&fullTextSearch=true`"
        :fieldNames="{ label: 'name', value: 'id' }">
      </Select>
    </div>
    <div class="flex flex-1 items-center space-x-2">
      <span class="w-16 text-right">组织人员</span><Colon />
      <Select
        v-model:value="creatorObjectType"
        :options="targetTypeOpt"
        class="w-20"
        @change="handleTypeChange" />
      <Select
        v-if="creatorObjectType === 'USER'"
        v-model:value="creatorObjectId"
        :showSearch="true"
        allowClear
        class="w-50"
        placeholder="选择用户"
        :action="`${GM}/user?fullTextSearch=true`"
        :fieldNames="{ label: 'fullName', value: 'id' }">
      </Select>

      <Select
        v-if="creatorObjectType === 'DEPT'"
        v-model:value="creatorObjectId"
        placeholder="选择部门"
        class="w-50"
        allowClear
        :showSearch="true"
        :action="`${GM}/dept?fullTextSearch=true`"
        :fieldNames="{ label: 'name', value: 'id' }">
      </Select>

      <Select
        v-if="creatorObjectType === 'GROUP'"
        v-model:value="creatorObjectId"
        placeholder="选择组"
        class="w-50"
        allowClear
        :showSearch="true"
        :action="`${GM}/group?fullTextSearch=true`"
        :fieldNames="{ label: 'name', value: 'id' }">
      </Select>
      <Hints text="用于输出指定组织或人员项目报告。" />
    </div>
    <div class="flex flex-1 items-center space-x-2">
      <span class="w-16 text-right">时间</span><Colon />
      <DatePicker
        v-model:value="dateRange"
        showTime
        class="w-72 flex-shrink-0"
        type="date-range" />
      <Hints text="用于输出指定时间范围项目报告。" />
    </div>
  </div>
  <div class="flex items-center space-x-2">
  </div>
  <div class="flex items-center space-x-1 mt-4">
    <span class="h-4 w-1.5 bg-blue-border1"></span>
    <span>内容</span>
    <Hints text="以下是报告输出内容目录信息。" />
  </div>
  <Tree
    v-model:checkedKeys="checked"
    class="mt-2 text-3"
    disabled
    :treeData="contentTreeData"
    :defaultExpandAll="true"
    :selectable="false"
    :checkable="true">
    <template #title="{title}">
      <span style="color: rgb(82, 90, 101);">{{ title }}</span>
    </template>
  </Tree>
</template>
