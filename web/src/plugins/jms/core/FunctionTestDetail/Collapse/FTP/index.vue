<script setup lang="ts">
import { inject, computed, defineAsyncComponent, ref } from 'vue';
import { Alert, Button, Collapse, CollapsePanel, Tabs, TabPane } from 'ant-design-vue';
import { Arrow, Colon, Icon, HttpMethodText } from '@xcan-angus/vue-ui';
import { utils } from '@xcan-angus/infra';

import StatusTag from '../StatusTag/index.vue';
import { HTTPInfo } from './PropsType';
import { ExecContent } from '../../PropsType';

export interface Props {
  value: HTTPInfo;
  content: ExecContent[];
  ignoreAssertions: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  content: undefined,
  ignoreAssertions: undefined
});

const RequestHeaders = defineAsyncComponent(() => import('./RequestHeaders/index.vue'));
const RequestBody = defineAsyncComponent(() => import('./RequestBody/index.vue'));
const ResponseBody = defineAsyncComponent(() => import('./ResponseBody/index.vue'));
const TimeLine = defineAsyncComponent(() => import('./TimeLine/index.vue'));
const AssertionsResult = defineAsyncComponent(() => import('./AssertionsResult/index.vue'));

const setGlobalTabActiveKey = inject<(key:'executeConfig')=>void>('setGlobalTabActiveKey');

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

const caseId = computed(() => {
  return props.value?.caseId;
});

const apisId = computed(() => {
  return props.value?.apisId;
});

const httpContent = computed(() => {
  const linkName = props.value?.linkName;
  if (!linkName) {
    return undefined;
  }

  return props.content?.find(item => linkName === item.name);
});

const method = computed(() => {
  return httpContent.value?.content?.request0?.method;
});

const url = computed(() => {
  return httpContent.value?.content?.request0?.url;
  // if (!caseId.value && !apisId.value) {
  //   return props.value?.request?.url;
  // }

  // if (!props.value?.request?.server) {
  //   return '';
  // }

  // const endpoint = props.value.request.endpoint;
  // const { url, variables } = props.value.request.server;
  // const variableReg = /\{[a-zA-Z0-9_]+\}/g;
  // const replaced = url.replace(variableReg, match => {
  //   const key = match.replace('{', '').replace('}', '');
  //   return variables?.[key]?.defaultValue || '';
  // });

  // return replaced + endpoint;
});

const httpStatus = computed(() => {
  return httpContent.value?.content?.response?.status || '--';
});

const bodySize = computed(() => {
  const size = httpContent.value?.content?.response?.bodySize;
  if (size === undefined || size === null) {
    return '--';
  }

  return utils.formatBytes(+size);
});

const runtime = computed(() => {
  const timeStamp = httpContent.value?.content?.response?.timeline?.total;
  if (timeStamp === undefined || timeStamp === null) {
    return '--';
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
          <template v-if="caseId">
            <Icon class="flex-shrink-0 text-4 mr-3" icon="icon-cese" />
          </template>
          <template v-else>
            <Icon
              v-if="apisId"
              class="flex-shrink-0 text-4 mr-3"
              icon="icon-yinyonghttp" />
            <Icon
              v-else
              class="flex-shrink-0 text-4 mr-3"
              icon="icon-httpcanshu" />
          </template>
          <div :title="props.value?.name" class="truncate min-w-55 max-w-100 mr-5 name">{{ props.value?.name }}</div>
          <HttpMethodText
            class="mr-5"
            :value="method"
            style="min-width: 0;" />
          <div :title="url" class="flex-1 truncate mr-5">{{ url }}</div>
          <div class="flex-1 justify-end flex items-center mr-3">
            <template v-if="showBasicInfo">
              <div class="mr-5">
                <span class="mr-0.5">状态码<Colon /></span>
                <span class="text-theme-sub-content">{{ httpStatus }}</span>
              </div>
              <div class="mr-5">
                <span class="mr-0.5">耗时<Colon /></span>
                <span class="text-theme-sub-content"> {{ runtime }}</span>
              </div>
              <div class="mr-5">
                <span class="mr-0.5">大小<Colon /></span>
                <span class="text-theme-sub-content"> {{ bodySize }}</span>
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
        class="mt-3" />
      <Tabs
        type="card"
        size="small"
        class="mt-3 card-tabs">
        <TabPane key="general" tab="基本">
          <RequestHeaders :value="httpContent" class="py-3" />
        </TabPane>
        <TabPane key="requestBody" tab="请求体">
          <RequestBody :value="httpContent" class="py-3" />
        </TabPane>
        <TabPane key="response" tab="响应">
          <ResponseBody
            :url="httpContent?.content?.request0?.url"
            :value="httpContent?.content?.response"
            class="py-3" />
        </TabPane>
        <TabPane key="timeline" tab="时间线">
          <TimeLine :value="httpContent?.content?.response?.timeline" />
        </TabPane>
        <TabPane key="assertions" tab="断言结果">
          <Alert
            v-if="props.ignoreAssertions===true"
            closable
            type="info"
            style="border-radius: 4px;"
            class="mt-3">
            <template #message>
              <div class="leading-5 text-3 flex items-center whitespace-pre-line flex-wrap">
                您已开启忽略断言，本次不会执行断言。如果需要启用断言，请在<Button
                  size="small"
                  class="mx-1 px-0 h-5 leading-5"
                  type="link"
                  @click="setGlobalTabActiveKey('executeConfig')">
                  执行配置 > 插件配置
                </Button>中关闭忽略断言选项。
              </div>
            </template>
          </Alert>
          <AssertionsResult
            :value="httpContent"
            :ignoreAssertions="props.ignoreAssertions"
            class="py-3" />
        </TabPane>
      </Tabs>
    </CollapsePanel>
  </Collapse>
</template>

<style scoped>
.ant-collapse > :deep(.ant-collapse-item) > .ant-collapse-header {
  padding: 0;
  border-color: var(--border-divider);
  line-height: 20px;
}

.ant-collapse > :deep(.ant-collapse-item) .ant-collapse-content-box {
  padding: 0 12px;
  line-height: 20px;
}

.embed.ant-collapse > :deep(.ant-collapse-item) > .ant-collapse-header {
  border: none;
  border-bottom: 1px dashed  var(--border-text-box);
}

.embed.ant-collapse,
.embed :deep(.ant-collapse-content),
.embed.ant-collapse > .ant-collapse-item {
  border: none;
}

.embed.ant-collapse > .ant-collapse-item.ant-collapse-item-active {
  border-bottom: 1px dashed var(--border-text-box);
}

.embed .name {
  min-width: 208px;
}

.card-tabs.ant-tabs-top > :deep(.ant-tabs-nav) {
  margin: 0;
}

.card-tabs.ant-tabs-card.ant-tabs-small > :deep(.ant-tabs-nav) .ant-tabs-tab {
  padding: 3px 10px;
  line-height: 20px;
}
</style>
