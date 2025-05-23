<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import * as echarts from 'echarts';

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

const statusColorSet = {
  4: 'rgba(200, 202, 208, 1)',
  3: '#52C41A',
  2: '#FFB925',
  1: '#FF8100',
  0: '#7F91FF'
};

const methodColorSet = {
  get: 'rgba(30, 136, 229, 1)',
  head: '#67D7FF',
  post: 'rgba(51, 183, 130, 1)',
  put: 'rgba(255, 167, 38, 1)',
  patch: 'rgba(171, 71, 188, 1)',
  delete: 'rgba(255, 82, 82, 1)',
  options: 'rgba(0, 150, 136, 1)',
  trace: '#7F91FF'
};

const taskStatusRef = ref();
const priorityRef = ref();

const taskStatusOption = {
  title: {
    text: '',
    textStyle: {
      fontSize: 12
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  legend: {},
  grid: {
    top: '8%',
    left: '3%',
    right: '8%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    splitLine: { show: false }
  },
  yAxis: {
    type: 'category',
    axisTick: { show: false },
    splitLine: { show: false },
    axisLine: { show: false },
    data: ['已发布', '开发完成', '开发中', '设计中', '未知']
  },
  series: [
    {
      type: 'bar',
      showBackground: true,
      barMaxWidth: 20,
      label: {
        show: true,
        position: 'right'
      },
      data: [
        {
          value: 0,
          itemStyle: {
            color: '#F7F8FB'
          }
        },
        {
          value: 0,
          itemStyle: {
            color: '#FF8100'
          }
        },
        {
          value: 0,
          itemStyle: {
            color: '#FFB925'
          }
        },
        {
          value: 0,
          itemStyle: {
            color: '#52C41A'
          }
        },
        {
          value: 0,
          itemStyle: {
            color: '#2D8EFF'
          }
        }
      ]
    }
  ]
};

const priorityOptions = {
  title: {
    text: 0,
    subtext: '总数',
    left: '29.5%',
    top: '40%',
    padding: 2,
    textAlign: 'center',
    textStyle: {
      fontSize: 14,
      width: 120,
      fontWeight: 'bolder'
    },
    subtextStyle: {
      fontSize: 12
    }
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: 'middle',
    right: '0',
    orient: 'vertical',
    itemHeight: 14,
    itemWidth: 14,
    itemGap: 4
  },
  series: [
    {
      name: '',
      type: 'pie',
      radius: ['40%', '55%'],
      center: ['30%', '50%'],
      avoidLabelOverlap: true,
      label: {
        show: true,
        formatter: '{c}'
      },
      itemStyle: {
        borderRadius: 5,
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        label: {
          show: true
        }
      },
      labelLine: {
        show: true
      },
      data: [
        {
          name: 'PUT',
          value: 0,
          itemStyle: {
            color: methodColorSet.put
          }
        },
        {
          name: 'POST',
          value: 0,
          itemStyle: {
            color: methodColorSet.post
          }
        },
        {
          name: 'HEAD',
          value: 0,
          itemStyle: {
            color: methodColorSet.head
          }
        },
        {
          name: 'GET',
          value: 0,
          itemStyle: {
            color: methodColorSet.get
          }
        },
        {
          name: 'DELETE',
          value: 0,
          itemStyle: {
            color: methodColorSet.delete
          }
        },
        {
          name: 'PATCH',
          value: 0,
          itemStyle: {
            color: methodColorSet.patch
          }
        },
        {
          name: 'OPTIONS',
          value: 0,
          itemStyle: {
            color: methodColorSet.options
          }
        },
        {
          name: 'TRACE',
          value: 0,
          itemStyle: {
            color: methodColorSet.trace
          }
        }
      ]
    }
  ]
};

let statusEchart;
let priorityEchart;

onMounted(() => {
  watch(() => props.dataSource, newValue => {
    const apisByStatus = newValue?.content?.apis?.apisByStatus;
    if (apisByStatus) {
      const {
        IN_DEV,
        UNKNOWN,
        RELEASED,
        IN_DESIGN,
        DEV_COMPLETED
      } = apisByStatus;
      taskStatusOption.series[0].data = [UNKNOWN, IN_DESIGN, IN_DEV, DEV_COMPLETED, RELEASED].map((value, idx) => {
        return {
          value,
          itemStyle: {
            color: statusColorSet[idx]
          }
        };
      });
    }

    const apisByMethod = newValue?.content?.apis?.apisByMethod;
    if (apisByMethod) {
      const {
        GET = 0,
        PUT = 0,
        HEAD = 0,
        POST = 0,
        PATCH = 0,
        TRACE = 0,
        DELETE = 0,
        OPTIONS = 0
      } = apisByMethod;
      const methodData = [
        PUT,
        POST,
        HEAD,
        GET,
        DELETE,
        PATCH,
        OPTIONS,
        TRACE
      ];
      priorityOptions.series[0].data.forEach((item, idx) => {
        item.value = methodData[idx];
      });
    } else {
      priorityOptions.series[0].data.forEach(item => {
        item.value = 0;
      });
    }

    statusEchart = echarts.init(taskStatusRef.value);
    priorityEchart = echarts.init(priorityRef.value);
    statusEchart.setOption(taskStatusOption);
    priorityEchart.setOption(priorityOptions);
  }, {
    immediate: true
  });
});

</script>

<template>
  <h1 class="text-theme-title font-medium mb-3">
    <span class="text-3 text-theme-title font-medium">按状态分组</span>
  </h1>
  <div ref="taskStatusRef" class="flex-1 h-50 w-120 mb-7">
  </div>

  <h1 class="text-theme-title font-medium mb-3">
    <span class="text-3 text-theme-title font-medium">按请求方法分组</span>
  </h1>
  <div ref="priorityRef" class="flex-1 h-50  w-120">
  </div>
</template>

<style scoped>
.content-text-container {
  text-indent: 2em;
}
</style>
