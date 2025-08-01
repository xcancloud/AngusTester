<script setup lang="ts">
import { computed, ref } from 'vue';
import { Alert, Collapse, CollapsePanel, Tabs, TabPane, Tag } from 'ant-design-vue';
import { Arrow, Colon, Icon } from '@xcan-angus/vue-ui';
import { utils } from '@xcan-angus/infra';

import StatusTag from '../StatusTag/index.vue';
import { TcpInfo } from './PropsType';
import { ExecContent } from '../../PropsType';

export interface Props {
  value: TcpInfo;
  content: ExecContent[];
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  content: undefined
});

const UUID = utils.uuid();
const collapseActiveKey = ref<string>();
const arrowOpen = ref(collapseActiveKey.value === UUID);

const arrowChange = (open: boolean) => {
  arrowOpen.value = open;
  if (open) {
    collapseActiveKey.value = UUID;
    return;
  }

  collapseActiveKey.value = undefined;
};

const httpContent = computed(() => {
  if (props.value?.enabled === true) {
    return props.content?.[0];
  }

  return undefined;
});

const requestData = computed(() => {
  return httpContent.value?.content?.request0?.data;
});

const requestDataSize = computed(() => {
  const size = httpContent.value?.content?.request0?.size;
  if (size === undefined || size === null) {
    return '0B';
  }

  return utils.formatBytes(+size);
});

const responseData = computed(() => {
  return httpContent.value?.content?.response?.data;
});

const responseSize = computed(() => {
  const size = httpContent.value?.content?.response?.size;
  if (size === undefined || size === null) {
    return '0B';
  }

  return utils.formatBytes(+size);
});

const runtime = computed(() => {
  const timeStamp = httpContent.value?.content?.response?.timeline?.total;
  if (timeStamp === undefined || timeStamp === null) {
    return '0毫秒';
  }

  return utils.formatTime(+timeStamp);
});

const status = computed(() => {
  if (!httpContent.value) {
    return 'block';
  }

  return httpContent.value.content?.success ? 'success' : 'fail';
});

const failMessage = computed(() => {
  return httpContent.value?.content?.failMessage;
});

const showBasicInfo = computed(() => {
  return !!httpContent.value;
});
</script>
<template>
  <Collapse
    :activeKey="collapseActiveKey"
    style="background-color: #fff;font-size: 12px;"
    class="timeline-collapse">
    <CollapsePanel
      :key="UUID"
      :showArrow="false"
      collapsible="disabled">
      <template #header>
        <div class="w-full flex items-center px-3 py-2.5 cursor-pointer" @click="arrowChange(!arrowOpen)">
          <Icon class="flex-shrink-0 text-4 mr-3" icon="icon-chajianpeizhi" />
          <div :title="props.value?.name" class="truncate min-w-55 max-w-100 mr-5 name">{{ props.value?.name }}</div>
          <Tag
            v-if="props.value?.setting?.eolByte"
            class="mr-3"
            style="line-height: 20px;">
            <span>基于EOL</span>
            <Colon />
            <span class="ml-1">{{ props.value?.setting?.eolByte }}</span>
          </Tag>
          <Tag
            v-if="props.value?.setting?.eomByte"
            class="mr-3"
            style="line-height: 20px;">
            <span>基于EOM</span>
            <Colon />
            <span class="ml-1">{{ props.value?.setting?.eomByte }}</span>
          </Tag>
          <Tag
            v-if="props.value?.setting?.binaryPrefixLength"
            class="mr-3"
            style="line-height: 20px;">
            <span>基于长度前缀</span>
            <Colon />
            <span class="ml-1">{{ props.value?.setting?.binaryPrefixLength }}</span>
          </Tag>
          <div class="flex-1 justify-end flex items-center mr-3">
            <template v-if="showBasicInfo">
              <div class="mr-5 truncate">
                <span class="mr-0.5">耗时<Colon /></span>
                <span class="text-theme-sub-content"> {{ runtime }}</span>
              </div>
            </template>
            <template v-if="!props.value?.enabled">
              <StatusTag />
            </template>
            <template v-else>
              <StatusTag :value="status" />
            </template>
          </div>
          <Arrow :open="arrowOpen" @change="arrowChange" />
        </div>
      </template>
      <Alert
        v-if="!!failMessage"
        :message="failMessage"
        type="error"
        style="margin-bottom: 12px;" />
      <Tabs
        type="card"
        size="small"
        class="card-tabs">
        <TabPane key="request">
          <template #tab>
            <div>
              <span>请求</span>
              <span class="ml-0.75">({{ requestDataSize }})</span>
            </div>
          </template>
          <div class="p-3 max-h-40 overflow-auto break-all whitespace-pre-wrap">{{ requestData }}</div>
        </TabPane>
        <TabPane key="response">
          <template #tab>
            <div>
              <span>响应</span>
              <span class="ml-0.75">({{ responseSize }})</span>
            </div>
          </template>
          <div class="p-3 max-h-40 overflow-auto break-all whitespace-pre-wrap">{{ responseData }}</div>
        </TabPane>
      </Tabs>
    </CollapsePanel>
  </Collapse>
</template>

<style scoped>
.ant-collapse> :deep(.ant-collapse-item)>.ant-collapse-header {
  padding: 0;
  border-color: var(--border-divider);
  line-height: 20px;
}

.ant-collapse> :deep(.ant-collapse-item) .ant-collapse-content-box {
  padding: 12px 12px 0;
  line-height: 20px;
}

.card-tabs.ant-tabs-top> :deep(.ant-tabs-nav) {
  margin: 0;
}

.card-tabs.ant-tabs-card.ant-tabs-small> :deep(.ant-tabs-nav) .ant-tabs-tab {
  padding: 3px 10px;
  line-height: 20px;
}
</style>
