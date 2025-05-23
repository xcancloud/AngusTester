<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue';

import { TaskInfo } from '../../../../../../PropsType';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  dataSource: TaskInfo;
  largePageLayout: boolean;
  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined,
  largePageLayout: undefined,
  loading: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (event: 'update:loading', value: boolean): void;
  (event: 'change', value: Partial<TaskInfo>): void;
}>();

const APIBasicInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/apis/index.vue'));
const SceneBasicInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/scenario/index.vue'));
const BasicInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/basic/index.vue'));
const Description = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/description/index.vue'));
const PersonnelInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/personnel/index.vue'));
const DateInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/date/index.vue'));
const AttachmentInfo = defineAsyncComponent(() => import('@/views/task/task/list/task/detail/view/info/attachment/index.vue'));
// const refTasks = defineAsyncComponent(() => import('./refTasks/index.vue'));
// const refCases = defineAsyncComponent(() => import('./refCases/index.vue'));

const change = (data: Partial<TaskInfo>) => {
  emit('change', data);
};

const loadingChange = (value: boolean) => {
  emit('update:loading', value);
};

const taskId = computed(() => {
  return props.dataSource?.id;
});

const taskType = computed(() => {
  return props.dataSource?.taskType?.value;
});

const className = computed(() => {
  if (props.largePageLayout === true) {
    return 'large-page-layout';
  }

  if (props.largePageLayout === false) {
    return 'small-page-layout';
  }

  return '';
});
</script>

<template>
  <div :class="className" class="h-full pr-5 overflow-auto">
    <div class="flex-1 space-y-4">
      <APIBasicInfo
        v-if="taskType === 'API_TEST'"
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />
      <SceneBasicInfo
        v-else-if="taskType === 'SCENARIO_TEST'"
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />
      <BasicInfo
        v-else
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />

      <Description
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />
    </div>

    <div class="flex-shrink-0 w-75 space-y-4">
      <PersonnelInfo
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />

      <DateInfo
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />

      <!-- <refTasks
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" /> -->

      <!-- <refCases
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" /> -->

      <AttachmentInfo
        :dataSource="props.dataSource"
        :projectId="props.projectId"
        :userInfo="props.userInfo"
        :appInfo="props.appInfo"
        :taskId="taskId"
        @change="change"
        @loadingChange="loadingChange" />
    </div>
  </div>
</template>

<style scoped>
.large-page-layout {
  display: flex;
  align-items: flex-start;
}

.large-page-layout>div+div {
  margin-left: 20px;
}

.small-page-layout>div+div {
  margin-top: 16px;
}
</style>
