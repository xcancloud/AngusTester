<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { ActivityInfo, Scroll } from '@xcan-angus/vue-ui';
import { TESTER } from '@xcan-angus/infra';

import { ActivityItem } from './PropsType';
import { CaseInfo } from '../PropsType';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  dataSource: CaseInfo;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined
});

const dataList = ref<ActivityItem[]>([]);
const params = ref<{
  mainTargetId:string;
  filters:[{ key: 'targetType', value: 'FUNC_CASE', op: 'EQUAL' }]
}>();

const change = (data: ActivityItem[]) => {
  dataList.value = data;
};

onMounted(() => {
  watch(() => targetId.value, (newValue, oldValue) => {
    if (newValue === oldValue) {
      return;
    }

    params.value = {
      mainTargetId: targetId.value,
      filters: [{ key: 'targetType', value: 'FUNC_CASE', op: 'EQUAL' }]
    };
  }, { immediate: true });
});

const targetId = computed(() => {
  return props.dataSource?.id;
});
</script>
<template>
  <div class="h-full text-3 leading-5 pl-5">
    <div class="text-theme-title mb-2.5 font-semibold">活动</div>

    <Scroll
      v-if="targetId"
      :action="`${TESTER}/activity`"
      :hideNoData="!!dataList.length"
      :params="params"
      :lineHeight="32"
      transition
      style="height:calc(100% - 30px);"
      @change="change">
      <ActivityInfo :dataSource="dataList" infoKey="description" />
    </Scroll>
  </div>
</template>
