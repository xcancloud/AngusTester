<script setup lang="ts">
import { computed } from 'vue';
import { Grid, TestResult } from '@xcan-angus/vue-ui';

import { CaseInfo } from '../../PropsType';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  dataSource: CaseInfo;
  canEdit: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined,
  canEdit: false
});

const oneTestPass = computed(() => {
  if (props.dataSource?.testNum && Number(props.dataSource.testNum) > 0) {
    return props.dataSource?.testFailNum === '0' && props.dataSource?.testResult?.value === 'PASSED' ? '是' : '否';
  }
  return '--';
});

const testInfoColumns = [
  [
    {
      label: '测试次数',
      dataIndex: 'testNum'
    },
    {
      label: '失败次数',
      dataIndex: 'testFailNum'
    },
    {
      label: '是否一次性通过',
      dataIndex: 'oneTestPass'
    },
    {
      label: '结果备注',
      dataIndex: 'testRemark'
    }
  ]
];
</script>

<template>
  <div class="h-full text-3 leading-5 pl-5 overflow-auto">
    <div class="text-theme-title mb-2.5 font-semibold">测试信息</div>
    <Grid
      :columns="testInfoColumns"
      :dataSource="props.dataSource"
      :marginBottom="4"
      labelSpacing="10px"
      font-size="12px">
      <template #oneTestPass>
        {{ oneTestPass }}
      </template>
      <template #testResult="{text}">
        <TestResult :value="text" />
      </template>
    </Grid>
  </div>
</template>
