<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue';

import { ReportContent } from '../PropsType';

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

const Progress = defineAsyncComponent(() => import('@/views/report/preview/components/progress/index.vue'));
const BurnDownChart = defineAsyncComponent(() => import('@/views/report/preview/projectProgress/funcCaseSummary/burndownChart/index.vue'));
const FuncCaseGrouped = defineAsyncComponent(() => import('@/views/report/preview/projectProgress/funcCaseSummary/group/index.vue'));

const totalOverview = computed(() => {
  return props.dataSource?.content?.cases?.totalOverview;
});

const overdueRate = computed(() => {
  return (totalOverview.value?.overdueRate?.replace(/(\d+\.\d{2})\d+/, '$1') || 0) + '%';
});

const oneTimePassedReviewRate = computed(() => {
  return (totalOverview.value?.oneTimePassedReviewRate?.replace(/(\d+\.\d{2})\d+/, '$1') || 0) + '%';
});

const oneTimePassedRate = computed(() => {
  return (totalOverview.value?.oneTimePassedRate?.replace(/(\d+\.\d{2})\d+/, '$1') || 0) + '%';
});

const savingWorkloadRate = computed(() => {
  return (totalOverview.value?.savingWorkloadRate?.replace(/(\d+\.\d{2})\d+/, '$1') || 0) + '%';
});

const progress = computed(() => {
  return +(totalOverview.value?.progress?.replace(/(\d+\.\d{2})\d+/, '$1') || 0);
});

const reviewProgress = computed(() => {
  return +(totalOverview.value?.reviewProgress?.replace(/(\d+\.\d{2})\d+/, '$1') || 0);
});

const burnDownCharts = computed(() => {
  return props.dataSource?.content?.cases?.burnDownCharts;
});
</script>

<template>
  <h1 class="text-theme-title font-medium mb-5">
    <span id="a6" class="text-4 text-theme-title font-medium">{{ props.sequence.big }}、<em class="inline-block w-0.25"></em>功能用例</span>
  </h1>

  <h2 class="flex items-center space-x-2.5 text-3.5 mb-3.5 text-theme-title">
    <span id="a7">{{ props.sequence.small[0] }}<em class="inline-block w-3.5"></em>用例测试汇总结果</span>
  </h2>
  <div class="flex items-center space-x-7 mb-7">
    <Progress
      :percent="progress"
      text="进度"
      class="ml-4" />
    <div class="flex-1 border border-solid border-border-input">
      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          用例总数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.totalCaseNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          有效用例数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.validCaseNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          待测试数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ totalOverview?.pendingTestNum || 0 }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          通过数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.passedTestNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          未通过数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.notPassedTestNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          阻塞数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ totalOverview?.blockedTestNum || 0 }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          取消数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.canceledTestNum }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          逾期数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.overdueNum }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          逾期率
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ overdueRate }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          一次性测试通过数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.oneTimePassedNum }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          一次性测试通过率
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ oneTimePassedRate }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          一次性评审通过率
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ oneTimePassedReviewRate }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          评估工作量
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.evalWorkload }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          实际工作量
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.actualWorkload }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          完成工作量
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ totalOverview?.completedWorkload }}
        </div>
      </div>

      <div class="flex">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          节省工作量
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.savingWorkload }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          工作量节省率
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ savingWorkloadRate }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
        </div>
      </div>
    </div>
  </div>

  <h2 class="flex items-center space-x-2.5 text-3.5 mb-3.5 text-theme-title">
    <span id="a8">{{ props.sequence.small[1] }}<em class="inline-block w-3.5"></em>用例评审汇总结果</span>
  </h2>
  <div class="flex items-center space-x-7 mb-7">
    <Progress
      :percent="reviewProgress"
      text="进度"
      class="ml-4" />
    <div class="flex-1 border border-solid border-border-input">
      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          总评审用例数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.totalReviewCaseNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          待评审数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.pendingReviewNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          评审通过数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ totalOverview?.passedReviewNum || 0 }}
        </div>
      </div>

      <div class="flex border-b border-solid border-border-input">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          评审未通过数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.failedReviewNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          已评审数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.totalReviewedCaseNum || 0 }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          未评审数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ totalOverview?.totalNotReviewCaseNum || 0 }}
        </div>
      </div>

      <div class="flex">
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          总评审次数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.totalReviewTimes }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          一次性通过评审数
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap border-r border-solid border-border-input">
          {{ totalOverview?.oneTimePassedReviewNum }}
        </div>
        <div
          class="w-30 flex-shrink-0 flex items-center bg-blue-table px-1.5 py-1.5 border-r border-solid border-border-input">
          一次性评审通过率
        </div>
        <div class="flex-1 px-1.5 py-1.5 break-all  whitespace-pre-wrap">
          {{ oneTimePassedReviewRate }}
        </div>
      </div>
    </div>
  </div>

  <h2 class="flex items-center space-x-2.5 text-3.5 mb-2.5 text-theme-title">
    <span id="a9">{{ props.sequence.small[2] }}<em class="inline-block w-3.5"></em>用例燃尽图</span>
  </h2>
  <BurnDownChart :dataSource="burnDownCharts" />
  <div class="mb-7"></div>

  <h2 class="flex items-center space-x-2.5 text-3.5 mb-2.5 text-theme-title">
    <span id="a10">{{ props.sequence.small[3] }}<em class="inline-block w-3.5"></em>用例分组统计</span>
  </h2>
  <FuncCaseGrouped
    :projectInfo="props.projectInfo"
    :userInfo="props.userInfo"
    :appInfo="props.appInfo"
    :dataSource="props.dataSource" />
  <div class="mb-7"></div>
</template>

<style scoped>
.content-text-container {
  text-indent: 2em;
}
</style>
