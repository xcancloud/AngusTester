<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import * as eCharts from 'echarts';

interface Props {
  // title0: string;
  // title1: string;
  // value0: {name: string, value: string|number}[];
  // value1: {name: string, value: string|number}[];

  overdueAssessmentData: Record<string, any>;

  chart1Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
  chart2Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
}
const props = withDefaults(defineProps<Props>(), {
  overdueAssessmentData: () => ({}),
  chart1Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  }),
  chart2Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  })
});

const completedWorkloadRef = ref();
const savingWorkloadRef = ref();

let completedWorkloadEchart;
let savingWorkloadEchart;

const completedWorkloadEchartConfig = {
  title: {
    text: '0%',
    left: '35%',
    top: '40%',
    padding: 2,
    subtext: '完成工作量占比',
    // left: '25%',
    // top: '40%',
    itemGap: 50,
    textAlign: 'center',
    textStyle: {
      fontSize: 12,
      fontWeight: 'bolder'
    },
    subtextStyle: {
      fontSize: 12,
      color: '#000'
    }
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: 'middle',
    right: '10',
    orient: 'vertical',
    itemHeight: 14,
    itemWidth: 14,
    itemGap: 2
  },
  series: [
    {
      name: '',
      type: 'pie',
      // radius: ['50%', '70%'],
      radius: '70%',
      center: ['35%', '45%'],
      avoidLabelOverlap: true,
      label: {
        show: true,
        formatter: '{c}'
      },
      itemStyle: {
        borderRadius: 2,
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        label: {
          show: true
        }
      },
      labelLine: {
        show: true,
        length: 5
      },
      data: [
        {
          name: '未完成',
          value: 0,
          itemStyle: {
            color: 'rgba(217, 217, 217, 1)'
          }
        },
        {
          name: '已完成',
          value: 0,
          itemStyle: {
            color: 'rgba(245, 34, 45, 1)'
          }
        }
      ]
    }
  ]
};

const savingWorkloadEchartConfig = JSON.parse(JSON.stringify({
  ...completedWorkloadEchartConfig,
  title: {
    ...completedWorkloadEchartConfig.title,
    subtext: '节省工作量占比'
  }
}));

onMounted(() => {
  completedWorkloadEchart = eCharts.init(completedWorkloadRef.value);

  savingWorkloadEchart = eCharts.init(savingWorkloadRef.value);

  watch([() => props.chart0Value, () => props.chart1Value, () => props.chart2Value], () => {
    completedWorkloadEchartConfig.series[0].data[0] = {
      ...completedWorkloadEchartConfig.series[0].data[0],
      ...props.chart1Value.value[0]
    };
    completedWorkloadEchartConfig.series[0].data[1] = {
      ...completedWorkloadEchartConfig.series[0].data[1],
      ...props.chart1Value.value[1]
    };
    completedWorkloadEchartConfig.title.text = props.chart1Value.title;

    savingWorkloadEchartConfig.series[0].data[0] = {
      ...savingWorkloadEchartConfig.series[0].data[0],
      ...props.chart2Value.value[0]
    };
    savingWorkloadEchartConfig.series[0].data[1] = {
      ...savingWorkloadEchartConfig.series[0].data[1],
      ...props.chart2Value.value[1]
    };
    savingWorkloadEchartConfig.title.text = props.chart2Value.title;
    completedWorkloadEchart.setOption(completedWorkloadEchartConfig);
    savingWorkloadEchart.setOption(savingWorkloadEchartConfig);
  }, {
    immediate: true,
    deep: true
  });
});

defineExpose({
  resize: () => {
    completedWorkloadEchart.resize();
    savingWorkloadEchart.resize();
  }
});

</script>
<template>
  <div class="flex">
    <div class="px-3 w-100">
      <div class="flex justify-around">
        <div class="text-center flex-1">
          <div class="font-semibold text-5 text-status-error">{{ props.overdueAssessmentData.overdueNum || 0 }}</div>
          <div>
            逾期数
          </div>
        </div>
        <div class="text-center flex-1">
          <div :class="`risk-level-${props.overdueAssessmentData?.riskLevel?.value}`" class="font-semibold text-5">{{ overdueAssessmentData?.riskLevel?.message }}</div>
          <div>逾期风险</div>
        </div>
      </div>
      <div class="flex justify-around mt-3">
        <div class="text-center">
          <div class="font-semibold text-5  text-status-error">{{ props.overdueAssessmentData.overdueTime || 0 }}小时</div>
          <div>
            逾期时长
          </div>
        </div>

        <div class="text-center">
          <div class="font-semibold text-5">{{ props.overdueAssessmentData.dailyProcessedWorkload || 0 }}</div>
          <div>
            每天平均处理工作量
          </div>
        </div>

        <div class="text-center">
          <div class="font-semibold text-5">{{ props.overdueAssessmentData.overdueWorkloadProcessingTime || 0 }}小时</div>
          <div>
            逾期工作量预计处理时长
          </div>
        </div>
      </div>
    </div>
    <div ref="completedWorkloadRef" class="flex-1 h-35"></div>
    <div ref="savingWorkloadRef" class="flex-1 h-35"></div>
  </div>
</template>
<style scoped>
.risk-level-LOW {
  color: 'gold'
}

.risk-level-HIGH {
  color: 'red'
}

.risk-level-NONE {
  color: '#52C41A'
}
</style>
