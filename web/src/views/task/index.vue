<script lang="ts" setup>
import { computed, defineAsyncComponent, inject, onMounted, ref, Ref, watch } from 'vue';
import { utils, appContext } from '@xcan-angus/infra';

import LeftMenu from '@/components/layout/leftMenu/index.vue';

type MenuKey = 'homepage' | 'sprint' | 'task' | 'backlog' | 'trash' | 'meeting' | 'analysis' | 'versions';

const Homepage = defineAsyncComponent(() => import('@/views/task/homepage/index.vue'));
const Backlog = defineAsyncComponent(() => import('@/views/task/backlog/index.vue'));
const Sprint = defineAsyncComponent(() => import('@/views/task/sprint/index.vue'));
const Task = defineAsyncComponent(() => import('@/views/task/task/index.vue'));
const Version = defineAsyncComponent(() => import('@/views/task/version/index.vue'));
const Meeting = defineAsyncComponent(() => import('@/views/task/meeting/index.vue'));
const Analysis = defineAsyncComponent(() => import('@/views/task/analysis/index.vue'));
const Trash = defineAsyncComponent(() => import('@/views/task/trash/index.vue'));

const userInfo = inject<Ref<{ id: string }>>('tenantInfo', ref({ id: '' }));
const projectInfo = inject<Ref<{ id: string; avatar: string; name: string; }>>('projectInfo', ref({ id: '', avatar: '', name: '' }));
const appInfo = inject<Ref<{ id: string; name: string; }>>('appInfo', ref({ id: '', name: '' }));
const proTypeShowMap = inject<Ref<{[key: string]: boolean}>>('proTypeShowMap', ref({ showTask: true, showBackLog: true, showMeeting: true, showSprint: true, showTasStatistics: true }));

const activeKey = ref<MenuKey>('');
const editionType = ref();

const menus = computed(() => {
  return [
    { name: '主页', icon: 'icon-zhuye', key: 'homepage' },
    proTypeShowMap.value.showBackLog && { name: 'Backlog', icon: 'icon-backlog', key: 'backlog' },
    proTypeShowMap.value.showSprint && { name: '迭代', icon: 'icon-diedai', key: 'sprint' },
    { name: '任务', icon: 'icon-renwu2', key: 'task' },
    { name: '版本', icon: 'icon-banben1', key: 'version' },
    proTypeShowMap.value.showMeeting && { name: '会议', icon: 'icon-RT', key: 'meeting' },
    editionType.value !== 'COMMUNITY' && { name: '分析', icon: 'icon-fenxi', key: 'analysis' },
    { name: '回收站', icon: 'icon-qingchu', key: 'trash' }
  ].filter(Boolean);
});

const homepageRefreshNotify = ref<string>('');
const trashRefreshNotify = ref<string>('');
const sprintsRefreshNotify = ref<string>('');
const tasksRefreshNotify = ref<string>('');

let homepageRefreshNotifyFlag = false;
let trashRefreshNotifyFlag = false;
let sprintsRefreshNotifyFlag = false;
let tasksRefreshNotifyFlag = false;

const projectId = computed(() => {
  return projectInfo.value?.id;
});

onMounted(async () => {
  editionType.value = appContext.getEditionType();
  watch(() => activeKey.value, (newValue) => {
    if (newValue === 'homepage') {
      if (homepageRefreshNotifyFlag) {
        homepageRefreshNotify.value = utils.uuid();
      }

      homepageRefreshNotifyFlag = true;
      return;
    }

    if (newValue === 'trash') {
      if (trashRefreshNotifyFlag) {
        trashRefreshNotify.value = utils.uuid();
      }

      trashRefreshNotifyFlag = true;
      return;
    }

    if (newValue === 'sprint') {
      if (sprintsRefreshNotifyFlag) {
        sprintsRefreshNotify.value = utils.uuid();
      }

      sprintsRefreshNotifyFlag = true;
      return;
    }

    if (newValue === 'tasks') {
      if (tasksRefreshNotifyFlag) {
        tasksRefreshNotify.value = utils.uuid();
      }

      tasksRefreshNotifyFlag = true;
    }
  }, { immediate: true });
});
</script>
<template>
  <LeftMenu v-model:activeKey="activeKey" :menuItems="menus">
    <template #homepage>
      <Homepage
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="homepageRefreshNotify" />
    </template>
    <template #backlog>
      <Backlog
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="tasksRefreshNotify" />
    </template>
    <template #sprint>
      <Sprint
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="sprintsRefreshNotify" />
    </template>
    <template #task>
      <Task
        :projectId="projectId"
        :projectName="projectInfo?.name"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="tasksRefreshNotify" />
    </template>
    <template #version>
      <Version
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo" />
    </template>
    <template #meeting>
      <Meeting
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="sprintsRefreshNotify" />
    </template>
    <template #analysis>
      <Analysis
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo" />
    </template>
    <template #trash>
      <Trash
        :projectId="projectId"
        :userInfo="userInfo"
        :appInfo="appInfo"
        :refreshNotify="trashRefreshNotify" />
    </template>
  </LeftMenu>
</template>
