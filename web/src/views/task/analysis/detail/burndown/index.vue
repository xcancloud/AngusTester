<script lang="ts" setup>
import { defineAsyncComponent, onMounted, ref, watch } from 'vue';
import { getDateArr } from '@/utils/utils';

interface Props {
  analysisInfo?: Record<string, any>;
}

const props = withDefaults(defineProps<Props>(), {
  analysisInfo: undefined
});

const Echart = defineAsyncComponent(() => import('./echart.vue'));

const getChartData = (data, target = 'NUM') => {
  const res = {};
  if (data) {
    const xData = (data[target]?.expected || []).map(i => i.timeSeries);
    const expectedYData = (data[target]?.expected || []).map(i => i.value);
    const remainingYData = (data[target]?.remaining || []).map(i => i.value);
    res.xData = xData;
    res.yData = [remainingYData, expectedYData];
  } else {
    res.xData = [];
    res.yData = [[], []];
    // burnDownEchartsConfig.series[0].data = [];
    // burnDownEchartsConfig.series[1].data = [];
  }
  if (res.xData.length === 0) {
    res.xData = getDateArr();
    res.yData = [[0, 0, 0, 0, 0, 0, 0], []];
  }
  return res;
};

const totalValue = ref({

});

const personValues = ref([]);

onMounted(() => {
  watch(() => props.analysisInfo, (newValue) => {
    if (newValue) {
      const sourceData = newValue.data?.totalBurnDownCharts || {};
      totalValue.value.chart0Data = getChartData(sourceData, 'NUM');
      totalValue.value.chart1Data = getChartData(sourceData, 'WORKLOAD');
      // const {completedNum = 0, completedRate = 0, completedWorkload = 0, completedWorkloadRate = 0, evalWorkload = 0, totalNum = 0} = sourceData;
      // totalValue.value.value0 = [{name: '未完成', value: totalNum - completedNum}, {name: '已完成', value: completedNum }];
      // totalValue.value.value1 = [{name: '未完成', value: evalWorkload - completedWorkload}, {name: '已完成', value: completedWorkload }];

      // totalValue.value.title0 = completedRate + '%';
      // totalValue.value.title1 = completedWorkloadRate + '%';

      if (newValue?.containsUserAnalysis) {
        const sourceData = newValue.data?.assigneesBurnDownCharts || {};
        const assignees = newValue.data?.assignees || [];
        Object.keys(sourceData).forEach(userId => {
          const viewData = sourceData[userId] || {};

          const chartData = {
            chart0Data: getChartData(viewData, 'NUM'),
            chart1Data: getChartData(viewData, 'WORKLOAD')
          };

          personValues.value.push({
            userName: assignees[userId]?.fullName,
            chartData,
            id: userId
          });
        });
      }
    } else {
      totalValue.value = {};
      personValues.value = [];
    }
  }, {
    immediate: true,
    deep: true
  });
});

const totalChartRef = ref();
const chartListRef = [];
defineExpose({
  resize: () => {
    totalChartRef.value.resize();
    chartListRef.forEach(item => {
      item.resize();
    });
  }
});

</script>
<template>
  <div>
    <div class="font-semibold pl-3">总共</div>
    <Echart
      ref="totalChartRef"
      v-bind="totalValue"
      class="ml-10" />
  </div>

  <div
    v-for="item in personValues"
    :key="item.id"
    class="mt-5">
    <div class="font-semibold pl-3">{{ item.userName }}</div>
    <Echart
      ref="chartListRef"
      v-bind="item.chartData"
      class="ml-10" />
  </div>
</template>
