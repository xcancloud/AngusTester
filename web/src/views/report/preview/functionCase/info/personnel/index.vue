<script setup lang="ts">
import { computed } from 'vue';

import { ReportContent } from '../../PropsType';

type Props = {
  projectInfo: { [key: string]: any };
  userInfo: { [key: string]: any };
  appInfo: { [key: string]: any };
  dataSource: ReportContent;
}

const props = withDefaults(defineProps<Props>(), {
  projectInfo: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined
});

const caseInfo = computed(() => {
  return props.dataSource?.content?.cases;
});
</script>

<template>
  <div>
    <h1 class="text-theme-title font-medium mb-3.5">
      <span id="a2" class="text-4 text-theme-title font-medium">二、<em class="inline-block w-0.25"></em>人员信息</span>
    </h1>

    <div class="border border-solid border-border-input">
      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          测试人
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ caseInfo?.testerName }}
        </div>
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          添加人
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ caseInfo?.createdByName }}
        </div>
      </div>

      <div class="flex">
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          评审人
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ caseInfo?.reviewerName }}
        </div>
        <div
          class="w-27 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          最后修改人
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ caseInfo?.lastModifiedByName }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-text-container{
  text-indent: 2em;
}
</style>
