<script setup lang="ts">
import { computed, ref, onMounted, nextTick } from 'vue';
import { Badge, Descriptions, DescriptionsItem, TypographyParagraph } from 'ant-design-vue';
import { AsyncComponent, Icon, Tooltip, Modal, Spin, MonacoEditor } from '@xcan-angus/vue-ui';
import { utils } from '@xcan-angus/infra';
import beautify from 'js-beautify';

import { ExecContent } from '../../../../PropsType';

export interface Props {
  value: ExecContent['content']['assertions'][number];
  execContent:ExecContent['content'];
  status: 'Disabled' | 'Ignored' | 'Success' | 'Failed';
  ignoreAssertions:boolean;
  loading:boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  status: undefined,
  execContent: undefined,
  ignoreAssertions: false,
  loading: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e:'update:loading', value:boolean):void;
}>();

const editorRef = ref();
const editorValue = ref('');
const editorLoading = ref(true);
const language = ref<'json' | 'xml' | 'text' | 'yaml' | 'typescript'>('text');
const modalVisible = ref(false);

onMounted(() => {
  nextTick(() => {
    emit('update:loading', false);
  });
});

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const openModal = (key: 'real' | 'expected') => {
  if (key === 'real') {
    editorValue.value = editorRealValue.value;
    language.value = realLanguage.value;
  } else {
    editorValue.value = editorExpectedValue.value;
    language.value = 'json';
  }
  modalVisible.value = true;
  setTimeout(() => {
    if (typeof editorRef.value?.format === 'function') {
      editorRef.value.format();
    }
  }, 16.67);
};

const conditionMessage = computed(() => {
  if (!props.value || props.ignoreAssertions) {
    return '';
  }

  const { actualCondition, condition } = props.value;
  if (!condition) {
    return '';
  }

  if (actualCondition === condition) {
    return `没有定义${conditionName.value}变量，直接执行该条件表达式`;
  }

  return '';
});

const conditionExpression = computed(() => {
  return props.value?.condition || '--';
});

/**
 * @description 提取变量，不满足变量规则，则返回输入值。变量的格式为${变量名}
 * @param str 要提取变量的字符串
 * @returns 提取后的变量值或输入值
 */
const extractVar = (str: string): string => {
  const regex = /^\{([^}][a-zA-Z0-9!@$%^&*()_\-+=./]+)\}$/;
  const match = str.match(regex);
  return match ? match[1] : str;
};

/**
 * @description 把字符串根据指定运算符拆分为[左操作数,运算符,右操作数]
 * @param str 要拆分的字符
 * @returns [左操作数,运算符,右操作数] 或 undefined
 */
const splitCondition = (condition: string, replaceFlag = true): [string, string, string] | null => {
  if (!condition) {
    return null;
  }

  const COMPARISON_OPERATORS: Array<'='| '!='| '>='| '<='| '>'| '<'> = ['=', '!=', '>=', '<=', '>', '<'];
  const operatorRegex = new RegExp(`^([^\\s${COMPARISON_OPERATORS.join('')}]+)\\s*(${COMPARISON_OPERATORS.join('|')})\\s*([^${COMPARISON_OPERATORS.join('')}]+)$`);
  const match = condition.trim().match(operatorRegex);
  if (match && match.length === 4) {
    const varStr = replaceFlag ? extractVar(match[1].trim()) : match[1].trim();
    return [varStr, match[2].trim(), match[3].trim()];
  } else {
    // 输入字符串非法，包含多个运算符或不包含运算符或左变量和右边值包含指定的运算符
    return null;
  }
};

const conditionName = computed(() => {
  return splitCondition(props.value?.condition)?.[0] || '变量';
});

const conditionValue = computed(() => {
  return splitCondition(props.value?.condition, false)?.[0] || '--';
});

const assertionIgnored = computed(() => {
  return props.ignoreAssertions || conditionFailure.value || !props.value?.enabled;
});

const conditionFailure = computed(() => {
  if (props.ignoreAssertions) {
    return false;
  }

  return props.value.ignore;
});

const conditionResultMessage = computed(() => {
  if (!props.value) {
    return '';
  }

  if (!props.value.condition) {
    return '表达式为空，执行该条断言';
  }

  if (props.value?.ignore) {
    return '运算结果不成立，忽略该条断言';
  }

  return '运算结果成立，执行该条断言';
});

const assertLabel = computed(() => {
  if (!assertionType.value) {
    return '';
  }

  if (assertionType.value === 'HEADER') {
    const parameterName = props.value.parameterName;
    if (['REG_MATCH', 'XPATH_MATCH', 'JSON_PATH_MATCH'].includes(assertionCondition.value)) {
      return TYPE_MAP[assertionType.value] + (parameterName || '') + CONDITION_MAP[assertionCondition.value] + '值';
    }

    return TYPE_MAP[assertionType.value] + (parameterName || '') + '值';
  }

  if (['REG_MATCH', 'XPATH_MATCH', 'JSON_PATH_MATCH'].includes(assertionCondition.value)) {
    return TYPE_MAP[assertionType.value] + CONDITION_MAP[assertionCondition.value] + '值';
  }

  return TYPE_MAP[assertionType.value];
});

const assertionType = computed(() => {
  return props.value?.type?.value;
});

const headers = computed(() => {
  if (!props.execContent?.response?.headerArray?.length) {
    return {};
  }

  const result = {};
  const headerArray = props.execContent.response.headerArray;
  for (let i = 0, len = headerArray.length; i < len; i++) {
    if (i % 2 === 0) {
      result[headerArray[i].toLowerCase()] = '';
    } else {
      result[headerArray[i - 1].toLowerCase()] = headerArray[i];
    }
  }

  return result;
});

const headerName = computed(() => {
  return props.value?.parameterName?.toLowerCase() || '';
});

const realValue = computed(() => {
  if (!assertionType.value) {
    return '';
  }

  if (assertionType.value === 'BODY') {
    const body = props.execContent?.response;
    return body?.rawContent || '';
  }

  if (assertionType.value === 'HEADER') {
    return headers.value?.[headerName.value] || '';
  }

  if (assertionType.value === 'STATUS') {
    return props.execContent?.response?.status;
  }

  if (assertionType.value === 'BODY_SIZE') {
    return utils.formatBytes(+props.execContent?.response?.bodySize);
  }

  if (assertionType.value === 'SIZE') {
    return utils.formatBytes(+props.execContent?.response?.size);
  }

  if (assertionType.value === 'DURATION') {
    return utils.formatTime(+props.execContent?.response?.timeline?.total);
  }

  return '';
});

const showRealValue = computed(() => {
  try {
    return JSON.stringify(JSON.parse(realValue.value));
  } catch (error) {
    return realValue.value;
  }
});

const realLanguage = computed(() => {
  const contentType = headers.value?.['content-type'];
  if (contentType?.includes('application/json')) {
    return 'json';
  }

  if (contentType?.includes('text/html')) {
    return 'html';
  }

  if (contentType?.includes('text/xml')) {
    return 'xml';
  }

  if (contentType?.includes('application/javascript ')) {
    return 'typescript';
  }

  if (contentType?.includes('application/yaml ')) {
    return 'yaml';
  }

  return 'text';
});

const editorRealValue = computed(() => {
  if (!realValue.value) {
    return '';
  }

  if (assertionType.value === 'BODY') {
    if (realLanguage.value === 'json' || realLanguage.value === 'typescript') {
      return beautify.js(realValue.value);
    }

    if (realLanguage.value === 'xml' || realLanguage.value === 'html') {
      return beautify.html(realValue.value);
    }
  }

  return realValue.value;
});

const assertionCondition = computed(() => {
  return props.value?.assertionCondition?.value;
});

const expectedLabel = computed(() => {
  return props.value?.extraction ? '提取值' : '期望值';
});

const expectedValue = computed(() => {
  const actualExpected = props.value?.actualExpected;
  if (!actualExpected) {
    return '--';
  }

  if (['BODY_SIZE', 'SIZE'].includes(assertionType.value)) {
    return utils.formatBytes(+actualExpected);
  }

  if (assertionType.value === 'DURATION') {
    return utils.formatTime(+actualExpected);
  }

  return actualExpected;
});

const showExpectedValue = computed(() => {
  try {
    return JSON.stringify(JSON.parse(expectedValue.value));
  } catch (error) {
    return expectedValue.value;
  }
});

const editorExpectedValue = computed(() => {
  try {
    return JSON.stringify(JSON.parse(expectedValue.value), null, 2);
  } catch (error) {
    return expectedValue.value;
  }
});

const resultMessage = computed(() => {
  return props.value?.result?.message;
});

const resultFailure = computed(() => {
  return !!props.value?.result?.failure;
});

// eslint-disable-next-line @typescript-eslint/no-empty-function
const onEllipsis = () => { };// 删除该方法不会触发省略
const ellipsis = { rows: 1, expandable: false, onEllipsis };

const TYPE_MAP = {
  STATUS: '响应状态码',
  HEADER: '响应头',
  BODY: '响应体',
  BODY_SIZE: '响应体大小',
  SIZE: '响应大小',
  DURATION: '耗时'
};

// 断言条件
const CONDITION_MAP = {
  CONTAIN: '包含',
  EQUAL: '等于',
  GREATER_THAN: '大于',
  GREATER_THAN_EQUAL: '大于等于',
  IS_EMPTY: '为空(空字符串或null值)',
  IS_NULL: '为null',
  LESS_THAN: '小于',
  LESS_THAN_EQUAL: '小于等于',
  NOT_CONTAIN: '不包含',
  NOT_EMPTY: '不为空(非空字符串或null值)',
  NOT_EQUAL: '不等于',
  NOT_NULL: '不为null',
  REG_MATCH: '正则匹配',
  XPATH_MATCH: 'XPath匹配',
  JSON_PATH_MATCH: 'JSONPath匹配'
};

const EMPTY_LIST = ['IS_EMPTY', 'IS_NULL', 'NOT_EMPTY', 'NOT_NULL'];
</script>
<template>
  <Descriptions
    class="px-3 pt-2 pb-3"
    size="small"
    bordered>
    <DescriptionsItem>
      <template #label>
        <div class="flex items-center">
          <div class="mr-1.5">执行条件表达式</div>
          <template v-if="conditionMessage">
            <Tooltip>
              <template #title>{{ conditionMessage }}</template>
              <Icon class="text-status-warn cursor-pointer" icon="icon-jinggao" />
            </Tooltip>
          </template>
        </div>
      </template>
      <div :title="conditionExpression">{{ conditionExpression }}</div>
    </DescriptionsItem>

    <DescriptionsItem>
      <template #label>
        <div class="flex items-center">
          <div class="mr-1.5">{{ conditionName }}值</div>
        </div>
      </template>
      <div :title="conditionValue">{{ conditionValue }}</div>
    </DescriptionsItem>

    <DescriptionsItem label="执行条件结果">
      <template v-if="props.ignoreAssertions||!props.value?.enabled">
        <Badge status="default" text="忽略" />
      </template>
      <template v-else-if="conditionFailure">
        <Badge status="error" :text="conditionResultMessage" />
      </template>
      <template v-else>
        <Badge status="success" :text="conditionResultMessage" />
      </template>
    </DescriptionsItem>

    <DescriptionsItem :label="assertLabel">
      <template v-if="(assertionType === 'BODY'||assertionType === 'HEADER') && showRealValue">
        <div class="flex items-center justify-between flex-nowrap">
          <TypographyParagraph
            style="word-wrap: unset;word-break: break-all;white-space: break-spaces;"
            :ellipsis="ellipsis"
            :content="showRealValue" />
          <div class="flex-shrink-0 text-text-link cursor-pointer" @click="openModal('real')">查看</div>
        </div>
      </template>

      <template v-else>
        <div :title="showRealValue">{{ showRealValue }}</div>
      </template>
    </DescriptionsItem>

    <DescriptionsItem label="断言条件">
      {{ CONDITION_MAP[assertionCondition] }}
    </DescriptionsItem>

    <DescriptionsItem>
      <template #label>
        <div class="flex items-center">
          <div class="mr-1.5">{{ expectedLabel }}</div>
        </div>
      </template>

      <template v-if="!EMPTY_LIST.includes(assertionCondition) && (assertionType === 'BODY' || assertionType === 'HEADER') && showExpectedValue">
        <div class="flex items-center justify-between flex-nowrap">
          <TypographyParagraph
            style="word-wrap: unset;word-break: break-all;white-space: break-spaces;"
            :ellipsis="ellipsis"
            :content="showExpectedValue" />
          <div class="flex-shrink-0 text-text-link cursor-pointer" @click="openModal('expected')">查看</div>
        </div>
      </template>

      <template v-else>
        <div :title="showExpectedValue">{{ showExpectedValue }}</div>
      </template>
    </DescriptionsItem>

    <template v-if="assertionIgnored">
      <DescriptionsItem label="断言结果" :span="3">
        <Badge status="default" text="忽略" />
      </DescriptionsItem>
    </template>

    <template v-else>
      <template v-if="resultFailure">
        <DescriptionsItem label="断言结果" :span="!!resultMessage ? 1 : 3">
          <Badge
            class="flex-shrink-0"
            status="error"
            text="失败" />
        </DescriptionsItem>

        <DescriptionsItem
          v-if="!!resultMessage"
          label="失败原因"
          :span="2">
          <div :title="resultMessage">{{ resultMessage }}</div>
        </DescriptionsItem>
      </template>

      <template v-else>
        <DescriptionsItem label="断言结果" :span="3">
          <Badge status="success" text="通过" />
        </DescriptionsItem>
      </template>
    </template>
  </Descriptions>

  <AsyncComponent :visible="modalVisible">
    <Modal
      v-model:visible="modalVisible"
      wrapClassName="assert-modal"
      width="95%"
      :footer="false">
      <Spin
        class="w-full h-full py-3"
        :spinning="editorLoading"
        :mask="false">
        <MonacoEditor
          ref="editorRef"
          v-model:loading="editorLoading"
          readOnly
          class="h-full"
          :value="editorValue"
          :language="language" />
      </Spin>
    </Modal>
  </AsyncComponent>
</template>

<style>
.assert-modal .ant-modal {
  top: 0;
  max-width: 100%;
  margin: 0;
  padding-bottom: 0;
}

.assert-modal .ant-modal-content {
  display: flex;
  flex-direction: column;
  height: calc(92vh);
}

.assert-modal .ant-modal-body {
  flex: 1;
  height: calc(100% - 44px);
  padding: 0;
}
</style>

<style scoped>
.ant-descriptions-bordered.ant-descriptions-small :deep(.ant-descriptions-item-label),
.ant-descriptions-bordered.ant-descriptions-small :deep(.ant-descriptions-item-content) {
  width: 16.67%;
}
</style>
