<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import * as eCharts from 'echarts';

interface Props {
  title0: string;
  title1: string;
  value0: {name: string, value: string|number}[];
  value1: {name: string, value: string|number}[];
}
const props = withDefaults(defineProps<Props>(), {
  value0: () => ([{ name: '', value: 0 }, { name: '', value: 0 }]),
  value1: () => ([{ name: '', value: 0 }, { name: '', value: 0 }]),
  title0: '',
  title1: ''
});

const progressRef = ref();
const workloadProcessRef = ref();

let progressEchart;
let workloadProcessEchart;

const progressEchartConfig = {
  title: {
    text: '0%',
    left: '35%',
    top: '38%',
    padding: 2,
    subtext: '完成用例进度',
    // left: '25%',
    // top: '40%',
    itemGap: 47,
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
            color: '#52C41A'
          }
        }
      ]
    }
  ]
};

const workloadProgressEchartConfig = JSON.parse(JSON.stringify({
  ...progressEchartConfig,
  title: {
    ...progressEchartConfig.title,
    subtext: '完成工作量进度'
  }
}));

onMounted(() => {
  progressEchart = eCharts.init(progressRef.value);

  workloadProcessEchart = eCharts.init(workloadProcessRef.value);

  watch([() => props.value0, () => props.value1], () => {
    progressEchartConfig.series[0].data[0] = {
      ...progressEchartConfig.series[0].data[0],
      ...props.value0[0]
    };
    progressEchartConfig.series[0].data[1] = {
      ...progressEchartConfig.series[0].data[1],
      ...props.value0[1]
    };
    progressEchartConfig.title.text = props.title0;

    workloadProgressEchartConfig.series[0].data[0] = {
      ...workloadProgressEchartConfig.series[0].data[0],
      ...props.value1[0]
    };
    workloadProgressEchartConfig.series[0].data[1] = {
      ...workloadProgressEchartConfig.series[0].data[1],
      ...props.value1[1]
    };
    workloadProgressEchartConfig.title.text = props.title1;
    progressEchart.setOption(progressEchartConfig);
    workloadProcessEchart.setOption(workloadProgressEchartConfig);
  }, {
    immediate: true,
    deep: true
  });
});

defineExpose({
  resize: () => {
    progressEchart.resize();
    workloadProcessEchart.resize();
  }
});

</script>
<template>
  <div class="flex">
    <div ref="progressRef" class="flex-1 h-30"></div>
    <div ref="workloadProcessRef" class="flex-1 h-30"></div>
  </div>
</template>
