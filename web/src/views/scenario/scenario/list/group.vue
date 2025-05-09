<script setup lang="ts">
import { computed, defineAsyncComponent, ref } from 'vue';
import { Collapse, CollapsePanel } from 'ant-design-vue';
import { Arrow, Image, ScriptTypeTag } from '@xcan-angus/vue-ui';

import { GroupedKey, SceneInfo } from './PropsType';

type Props = {
  dataSource: SceneInfo[];
  groupedKey:GroupedKey;
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  notify: string;
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined,
  groupedKey: 'none',
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  notify: undefined
});

const SceneList = defineAsyncComponent(() => import('./list.vue'));
const openKeys = ref<string[]>([]);

const collapseChange = (value:string[]) => {
  openKeys.value = value;
};

const arrowChange = (open:boolean, key:string) => {
  if (open) {
    openKeys.value.push(key);
    return;
  }

  openKeys.value = openKeys.value.filter(item => item !== key);
};

const groupedList = computed(() => {
  if (!props.dataSource?.length) {
    return [];
  }

  const key = props.groupedKey;
  const temp = props.dataSource.reduce((prev, cur) => {
    let _key = cur[key];
    if (Object.prototype.toString.call(_key) === '[object Object]') {
      _key = _key.value;
    }

    if (prev[_key]) {
      prev[_key].push(cur);
    } else {
      prev[_key] = [cur];
    }

    return prev;
  }, {} as {[key:string]:SceneInfo[]});

  return Object.entries(temp);
});
</script>

<template>
  <div class="flex-1 px-5 overflow-auto">
    <Collapse
      :activeKey="openKeys"
      :bordered="false"
      style="background-color: #fff;"
      class="space-y-3.5"
      @change="collapseChange">
      <CollapsePanel
        v-for="([key,dataList]) in groupedList"
        :key="key"
        :showArrow="false">
        <template #header>
          <div class="w-full flex justify-between items-center leading-5 text-3">
            <div v-if="props.groupedKey==='createdBy'" class="flex-1 flex items-center space-x-3">
              <Image
                :src="dataList[0].avatar"
                type="avatar"
                class="w-6 h-6 rounded-xl" />
              <div class="text-theme-content pt-0.5 font-bold">{{ dataList[0].createdByName }}</div>
            </div>

            <div v-else-if="props.groupedKey==='plugin'" class="flex-1 flex items-center">
              <div class="flex-shrink-0 leading-5 rounded px-2 text-white bg-tag">{{ dataList[0].plugin }}</div>
            </div>

            <div v-else-if="props.groupedKey==='scriptType'" class="flex-1 flex items-center">
              <ScriptTypeTag :value=" dataList[0].scriptType" />
            </div>

            <Arrow
              class="flex-shrink-0"
              :open="openKeys.includes(key)"
              @change="arrowChange($event,key)" />
          </div>
        </template>

        <SceneList
          :dataSource="dataList"
          :notify="props.notify"
          :userInfo="props.userInfo"
          :appInfo="props.appInfo"
          :projectId="props.projectId" />
      </CollapsePanel>
    </Collapse>
  </div>
</template>

<style scoped>
.ant-collapse > :deep(.ant-collapse-item) > .ant-collapse-header {
  display: flex;
  align-items: center;
  height: 40px;
  padding: 0 16px;
  border: 1px solid  var(--border-text-box);
  border-radius: 4px;
  background-color: var(--table-header-bg);
}

.ant-collapse-borderless > :deep(.ant-collapse-item) {
  border: none;
}

.ant-collapse-borderless > :deep(.ant-collapse-item) > .ant-collapse-content > .ant-collapse-content-box {
  padding: 14px 16px 4px;
}

.bg-tag{
  background-color: rgba(15 ,159, 255, 75%);
}

</style>
