<script lang="ts" setup>
import { defineAsyncComponent } from 'vue';

import { ReportContent } from '../../PropsType';

type Props = {
  dataSource: ReportContent['content']['tasks']['burnDownCharts'];
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined
});

const Chart = defineAsyncComponent(() => import('./Chart.vue'));

</script>
<template>
  <div class="space-y-4">
    <Chart :dataSource="props.dataSource?.NUM" title="任务数" />
    <Chart :dataSource="props.dataSource?.WORKLOAD" title="工作量" />
  </div>
</template>
