<script setup lang="ts">
import { computed } from 'vue';

import { ReportContent } from '../../PropsType';

type Props = {
  projectInfo: { [key: string]: any };
  userInfo: { [key: string]: any };
  appInfo: { [key: string]: any };
  dataSource: ReportContent;
  sequence: {
    big: string;
    small: number[]
  }
}

const props = withDefaults(defineProps<Props>(), {
  projectInfo: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined,
  sequence: () => ({
    big: '',
    small: []
  })
});

const content = computed(() => {
  return props.dataSource?.content;
});

const projectInfo = computed(() => {
  return content.value?.project;
});
</script>

<template>
  <div>
    <h1 class="text-theme-title font-medium mb-5">
      <span id="a1" class="text-4 text-theme-title font-medium">{{ sequence.big }}、<em class="inline-block w-0.25"></em>项目信息</span>
    </h1>

    <div class="flex-1 border border-solid border-border-input">
      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          名称
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ projectInfo?.name }}
        </div>
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          计划时间
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ projectInfo?.startDate }} ~ {{ projectInfo?.deadlineDate }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          负责人
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ projectInfo?.ownerName }}
        </div>
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          成员
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ projectInfo?.members?.USER?.map(item=>item.name).join('，') }}
        </div>
      </div>

      <div class="flex">
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          描述
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ projectInfo?.description }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ant-progress-circle :deep(.ant-progress-text) {
  font-size: 14px;
}
</style>
