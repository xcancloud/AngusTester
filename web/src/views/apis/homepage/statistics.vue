
<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { utils } from '@xcan-angus/infra';
import * as echarts from 'echarts';
import elementResizeDetector from 'element-resize-detector';

import { analysis } from 'src/api/tester';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  notify: string;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  notify: undefined
});

const { t } = useI18n();
const erd = elementResizeDetector({ strategy: 'scroll' });

const loading = ref(false);
const state = reactive({
  statistic: {
    allService: '0',
    serviceByLastWeek: '0',
    serviceByLastMonth: '0',
    allApis: '0',
    apisByLastWeek: '0',
    apisByLastMonth: '0',
    allUnarchivedApis: '0',
    unarchivedApisByLastWeek: '0',
    unarchivedApisByLastMonth: '0'
  }
});

const statusColorSet = {
  0: 'rgba(200, 202, 208, 1)',
  1: '#52C41A',
  2: '#FFB925',
  3: '#FF8100',
  4: '#7F91FF'
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

const loadMyStatistics = async (): Promise<void> => {
  loading.value = true;
  const params = {
    creatorObjectType: 'USER',
    creatorObjectId: props.userInfo?.id,
    projectId: props.projectId
  };
  const [error, res] = await analysis.getApisStatistics(params);
  loading.value = false;
  if (error || utils._typeof(res?.data) !== 'object') {
    return;
  }

  state.statistic = res.data;
};

const allApis = ref();
const allService = ref();
const loadAllStatistics = async (): Promise<void> => {
  loading.value = true;
  const params = {
    projectId: props.projectId
  };
  const [error, res] = await analysis.getApisStatistics(params);
  loading.value = false;
  if (error || utils._typeof(res?.data) !== 'object') {
    return;
  }
  const { data } = res;
  allApis.value = data.allApis;
  allService.value = data.allService;
  if (res.data.servicesByStatus) {
    const { UNKNOWN, IN_DESIGN, IN_DEV, DEV_COMPLETED, RELEASED } = res.data.servicesByStatus;
    serviceOption.series[0].data = [UNKNOWN, IN_DESIGN, IN_DEV, DEV_COMPLETED, RELEASED].map((value, idx) => {
      return {
        value,
        itemStyle: {
          color: statusColorSet[idx]
        }
      };
    });
  }

  if (res.data.apisByStatus) {
    const { UNKNOWN, IN_DESIGN, IN_DEV, DEV_COMPLETED, RELEASED } = res.data.apisByStatus;

    apiOption.series[0].data = [UNKNOWN, IN_DESIGN, IN_DEV, DEV_COMPLETED, RELEASED].map((value, idx) => {
      return {
        value,
        itemStyle: {
          color: statusColorSet[idx]
        }
      };
    });
  }

  methodOptions.series[0].data = Object.keys(res.data.apisByMethod).map(method => {
    return {
      name: method,
      value: res.data.apisByMethod[method],
      itemStyle: {
        color: methodColorSet[method.toLowerCase()]
      }
    };
  });
  serviceEchart.setOption(serviceOption, true);
  apiEchart.setOption(apiOption);
  methodEchart.setOption(methodOptions);
};

const statisticConfig = [
  {
    topClass: 'huang-top',
    bottomClass: 'huang-bottom',
    total: 'allService',
    week: 'serviceByLastWeek',
    month: 'serviceByLastMonth',
    name: '服务'
  },
  {
    topClass: 'hong-top',
    bottomClass: 'hong-bottom',
    total: 'allApis',
    week: 'apisByLastWeek',
    month: 'apisByLastMonth',
    name: '接口'
  },
  {
    topClass: 'lan-top',
    bottomClass: 'lan-bottom',
    total: 'allUnarchivedApis',
    week: 'unarchivedApisByLastWeek',
    month: 'unarchivedApisByLastMonth',
    name: '未归档接口'
  }
];

const echartsWrapRef = ref();
const serviceRef = ref();
const apiRef = ref();
const methodRef = ref();

let serviceEchart;
let apiEchart;
let methodEchart;
const serviceOption = {
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
    right: '6%',
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
    data: ['未知', '设计中', '开发中', '开发完成', '已发布']
  },
  series: [
    {
      type: 'bar',
      showBackground: true,
      barMaxWidth: 20,
      data: [
        {
          value: 0,
          itemStyle: {
            color: '#F7F8FB'
          }
        }, {
          value: 0,
          itemStyle: {
            color: '#FF8100'
          }
        }, {
          value: 0,
          itemStyle: {
            color: '#FFB925'
          }
        }, {
          value: 0,
          itemStyle: {
            color: '#52C41A'
          }
        }, {
          value: 0,
          itemStyle: {
            color: '#2D8EFF'
          }
        }]
    }
  ]
};

const apiOption = {
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
    right: '6%',
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
    data: ['未知', '设计中', '开发中', '开发完成', '已发布']
  },
  series: [
    {
      type: 'bar',
      showBackground: true,
      barMaxWidth: 20,
      data: [{
        value: 0,
        itemStyle: {
          color: '#F7F8FB'
        }
      }, {
        value: 0,
        itemStyle: {
          color: '#FF8100'
        }
      }, {
        value: 0,
        itemStyle: {
          color: '#FFB925'
        }
      }, {
        value: 0,
        itemStyle: {
          color: '#52C41A'
        }
      }, {
        value: 0,
        itemStyle: {
          color: '#2D8EFF'
        }
      }]
    }
  ]
};

const methodOptions = {
  title: {
    text: '',
    textStyle: {
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
    itemWidth: 14
  },
  series: [
    {
      name: '',
      type: 'pie',
      radius: ['30%', '50%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: true,
      label: {
        show: true,
        formatter: '{c}'
      },
      emphasis: {
        label: {
          show: true
        }
      },
      labelLine: {
        show: true
      },
      data: []
    }
  ]
};

const resizeHandler = () => {
  const warpWith = echartsWrapRef.value.clientWidth;
  if (warpWith < 1100) {
    if (!methodEchart.legend?.formatter) {
      methodOptions.legend.formatter = (name) => {
        const data = methodOptions?.series?.[0].data;
        for (let i = 0; i < data.length; i++) {
          if (data[i].name === name) {
            name += ' ' + data[i].value;
            break;
          }
        }
        return name;
      };
      methodOptions.series[0].label.show = false;
      methodEchart.setOption(methodOptions);
    }
  } else {
    if (methodOptions.legend.formatter) {
      methodOptions.legend.formatter = undefined;
      methodOptions.series[0].label.show = true;
      methodEchart.setOption(methodOptions);
    }
  }
  serviceEchart.resize();
  apiEchart.resize();
  methodEchart.resize();
};

onMounted(() => {
  serviceEchart = echarts.init(serviceRef.value);
  apiEchart = echarts.init(apiRef.value);
  methodEchart = echarts.init(methodRef.value);
  serviceEchart.setOption(serviceOption);
  apiEchart.setOption(apiOption);
  methodEchart.setOption(methodOptions);

  watch(() => props.projectId, () => {
    loadMyStatistics();
    loadAllStatistics();
  }, { immediate: true });

  watch(() => props.notify, (newValue) => {
    if (newValue === undefined || newValue === null || newValue === '') {
      return;
    }

    loadMyStatistics();
    loadAllStatistics();
  }, { immediate: true });

  erd.listenTo(echartsWrapRef.value, resizeHandler);
});

onBeforeUnmount(() => {
  erd.removeListener(echartsWrapRef.value, resizeHandler);
});

</script>
<template>
  <div class="mb-7.5 text-3 leading-5">
    <div class="text-3.5 font-semibold mb-3">我添加的</div>
    <div class="flex space-x-3.75 text-content">
      <div
        v-for="item,index in statisticConfig"
        :key="index"
        class="rounded w-1/3 relative">
        <div class="vertical-layout-top" :class="item.topClass"><div>{{ item.name }}</div><div>{{ state.statistic[item.total] }}</div></div>
        <div class="vertical-layout-bottom" :class="item.bottomClass">
          <div>
            <div>{{ t('近7天') }}</div><div>{{ state.statistic[item.week] }}</div>
          </div>
          <div>
            <div>{{ t('近30天') }}</div>
            <div>{{ state.statistic[item.month] }}</div>
          </div>
        </div>
        <template v-if="index ===0">
          <img src="./images/icon-service.png" class="w-15 absolute right-0 top-0" />
        </template>
        <template v-if="index ===1">
          <img src="./images/icon-api.png" class="w-15 absolute right-0 top-0" />
        </template>
        <template v-if="index ===2">
          <img src="./images/weiuidangicon.png" class="w-15 absolute right-0 top-0" />
        </template>
      </div>
    </div>
  </div>

  <div class="text-3 leading-5">
    <div class="text-3.5 font-semibold mb-3">资源统计</div>
    <div ref="echartsWrapRef" class="flex space-x-3.75">
      <div class="border border-theme-text-box w-1/3 p-2 rounded">
        <div class="font-semibold flex items-center px-2">总服务 <span class="text-4 ml-2">{{ allService }}</span></div>
        <div ref="serviceRef" class="w-full h-65"></div>
      </div>
      <div class="border border-theme-text-box w-1/3 p-2 rounded">
        <div class="font-semibold flex items-center px-2">总接口 <span class="text-4 ml-2">{{ allApis }}</span></div>
        <div ref="apiRef" class="w-full h-65"></div>
      </div>
      <div class="border border-theme-text-box w-1/3 p-2 rounded">
        <div class="font-semibold">请求方法</div>
        <div ref="methodRef" class="w-full h-65"></div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.vertical-layout-top{
  @apply p-3.5 rounded-t;
}

.vertical-layout-bottom{
 @apply  p-3.5 rounded-b;
}

.vertical-layout-top > div:first-child{
 margin-bottom: 4px
}

.vertical-layout-bottom > div:first-child{
  margin-bottom: 4px
}

.vertical-layout-bottom > div > div:first-child{
  margin-bottom: 4px
}

@media (min-width: 1400px) {
  .vertical-layout-top {
        display: flex;
        align-items: center;
   }

  .vertical-layout-bottom > div {
    display: flex;
    align-items: center;
  }

  .vertical-layout-top > div:first-child{
    margin-right: 10px;
    margin-bottom: 0;
  }

  .vertical-layout-bottom > div:first-child{
    margin-bottom: 4px
  }

  .vertical-layout-bottom > div > div:first-child{
    margin-right: 10px;
    margin-bottom: 0;
  }
}

@media (min-width: 1600px) {
  .vertical-layout-top {
      display: flex;
      align-items: center;
   }

  .vertical-layout-bottom {
    display: flex;
    align-items: center;
  }

  .vertical-layout-bottom > div {
      display: flex;
      align-items: center;
      width: 50%
   }

  .vertical-layout-top > div:first-child{
    margin-right: 10px;
    margin-bottom: 0;
  }

  .vertical-layout-bottom > div:first-child{
    margin-bottom: 0
  }

  .vertical-layout-bottom > div > div:first-child{
    margin-right: 10px;
    margin-bottom: 0;
  }
}

.zi-top{
  background: linear-gradient(to right, #cab9ff, #f3f0ff);
}

.zi-bottom {
  background-color: #f3f0ff;
}

.huang-top{
  background: linear-gradient(to right, #f7cb71, #fef7df);
}

.huang-bottom {
  background-color: #fef7df;
}

.hong-top{
  background: linear-gradient(to right, #ffb99f, #fff0e8);
}

.hong-bottom {
  background-color: #fff0e8;
}

.lan-top{
  background: linear-gradient(to right, #d1e7ff, #eff6ff);
}

.lan-bottom {
  background-color: #eff6ff;
}

.qin-top{
  background: linear-gradient(to right, #87d7ee, #e2f7fd);
}

.qin-bottom {
  background-color: #e2f7fd;
}
</style>
