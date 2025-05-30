<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import * as eCharts from 'echarts';

interface Props {
  // title0: string;
  // title1: string;
  // value0: {name: string, value: string|number}[];
  // value1: {name: string, value: string|number}[];
  chart0Value: {
    yData0: number[],
    yData1: number[]
  };
  chart1Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
  chart2Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
  chart3Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
  chart4Value: {
    title: string;
    value: {name: string, value: string|number}[];
  }
}
const props = withDefaults(defineProps<Props>(), {
  chart0Value: () => ({
    yData0: [0, 0, 0, 0],
    yData1: [0, 0, 0, 0]
  }),
  chart1Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  }),
  chart2Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  }),
  chart3Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  }),
  chart4Value: () => ({
    title: '',
    value: [{ name: '', vaue: 0 }, { name: '', vaue: 0 }]
  })
});

const completedRef = ref();
const completedWorkloadRef = ref();
const coreRef = ref();
const completedOverduedRef = ref();
const completedBugRef = ref();

let coreChart;
let completedEchart;
let completedWorkloadEchart;
let completedOverduedEchart;
let completedBugEchart;

// 核心指标
const coreEchartConfig = {
  title: {
    text: '核心指标',
    bottom: 0,
    left: 'center',
    textStyle: {
      fontSize: 12
    }
  },
  grid: {
    left: '40',
    right: '30',
    bottom: '50',
    top: '40'
  },
  xAxis: {
    type: 'category',
    data: ['任务数', '工作量', '逾期数', '缺陷数'],
    axisLabel: {
      interval: 0,
      overflow: 'break'
    }
  },
  yAxis: [{
    type: 'value'
  }],
  tooltip: {
    show: true
  },
  legend: {
    show: true,
    data: ['完成量', '总量'],
    top: 0
  },
  series: [
    {
      name: '完成量',
      itemStyle: {
        color: 'rgba(45, 142, 255, 1)',
        borderRadius: [5, 5, 0, 0]
      },
      barGap: 0,
      data: [0, 0, 0],
      type: 'bar',
      barMaxWidth: '20',
      label: {
        show: true,
        position: 'top'
      }
    },
    {
      name: '总量',
      itemStyle: {
        color: 'rgba(136, 185, 242, 0.8)',
        borderRadius: [5, 5, 0, 0]
      },
      data: [0, 0, 0],
      type: 'bar',
      barMaxWidth: '20',
      label: {
        show: true,
        position: 'top'
      }
    }
  ]
};
// 完成任务占比
const completedEchartConfig = {
  title: {
    text: '0%',
    left: '35%',
    top: '45%',
    padding: 2,
    subtext: '完成任务占比',
    // left: '25%',
    // top: '40%',
    itemGap: 40,
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
      radius: '65%',
      // radius: ['50%', '65%'],
      center: ['35%', '50%'],
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
          name: '',
          value: 0,
          itemStyle: {
            color: 'rgba(255, 165, 43, 1)'
          }
        },
        {
          name: '',
          value: 0,
          itemStyle: {
            color: 'rgba(45, 142, 255, 1)'
          }
        }

      ]
    }
  ]
};

// 完成工作量占比
const completedWorkloadEchartConfig = JSON.parse(JSON.stringify({
  ...completedEchartConfig,
  title: {
    ...completedEchartConfig.title,
    subtext: '一次完成任务占比',
    itemGap: 40
  }
}));

// 逾期逾期数占比
const completedOverduedEchartConfig = JSON.parse(JSON.stringify({
  ...completedWorkloadEchartConfig,
  title: {
    ...completedWorkloadEchartConfig.title,
    subtext: '逾期逾期数占比'
  }
}));

// 完成缺陷占比
const completedBugEchartConfig = JSON.parse(JSON.stringify({
  ...completedWorkloadEchartConfig,
  title: {
    ...completedWorkloadEchartConfig.title,
    subtext: '完成缺陷占比'
  }
}));

onMounted(() => {
  completedEchart = eCharts.init(completedRef.value);

  completedWorkloadEchart = eCharts.init(completedWorkloadRef.value);

  coreChart = eCharts.init(coreRef.value);

  completedOverduedEchart = eCharts.init(completedOverduedRef.value);

  completedBugEchart = eCharts.init(completedBugRef.value);

  watch([() => props.chart0Value, () => props.chart1Value, () => props.chart2Value, () => props.chart3Value, () => props.chart4Value], () => {
    coreEchartConfig.series[0].data = props.chart0Value.yData0;
    coreEchartConfig.series[1].data = props.chart0Value.yData1;

    completedEchartConfig.series[0].data[0] = {
      ...completedEchartConfig.series[0].data[0],
      ...props.chart1Value.value[0]
    };
    completedEchartConfig.series[0].data[1] = {
      ...completedEchartConfig.series[0].data[1],
      ...props.chart1Value.value[1]
    };
    completedEchartConfig.title.text = props.chart1Value.title;

    completedWorkloadEchartConfig.series[0].data[0] = {
      ...completedWorkloadEchartConfig.series[0].data[0],
      ...props.chart2Value.value[0]
    };
    completedWorkloadEchartConfig.series[0].data[1] = {
      ...completedWorkloadEchartConfig.series[0].data[1],
      ...props.chart2Value.value[1]
    };
    completedWorkloadEchartConfig.title.text = props.chart2Value.title;

    completedOverduedEchartConfig.series[0].data[0] = {
      ...completedOverduedEchartConfig.series[0].data[0],
      ...props.chart3Value.value[0]
    };
    completedOverduedEchartConfig.series[0].data[1] = {
      ...completedOverduedEchartConfig.series[0].data[1],
      ...props.chart3Value.value[1]
    };
    completedOverduedEchartConfig.title.text = props.chart3Value.title;

    completedBugEchartConfig.series[0].data[0] = {
      ...completedBugEchartConfig.series[0].data[0],
      ...props.chart4Value.value[0]
    };
    completedBugEchartConfig.series[0].data[1] = {
      ...completedBugEchartConfig.series[0].data[1],
      ...props.chart4Value.value[1]
    };
    completedBugEchartConfig.title.text = props.chart4Value.title;

    completedEchart.setOption(completedEchartConfig);
    completedWorkloadEchart.setOption(completedWorkloadEchartConfig);
    coreChart.setOption(coreEchartConfig);
    completedOverduedEchart.setOption(completedOverduedEchartConfig);
    completedBugEchart.setOption(completedBugEchartConfig);
  }, {
    immediate: true,
    deep: true
  });
});

defineExpose({
  resize: () => {
    completedEchart.resize();
    completedWorkloadEchart.resize();
    coreChart.resize();
    completedOverduedEchart.resize();
    completedBugEchart.resize();
  }
});

</script>
<template>
  <div class="flex">
    <div ref="coreRef" class="h-40 w-100"></div>
    <div ref="completedRef" class="flex-1 h-30"></div>
    <div ref="completedWorkloadRef" class="flex-1 h-30"></div>
    <div ref="completedOverduedRef" class="flex-1 h-30"></div>
    <div ref="completedBugRef" class="flex-1 h-30"></div>
  </div>
</template>
