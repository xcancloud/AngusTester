<script setup lang="ts">
import { ref } from 'vue';
// eslint-disable-next-line import/no-absolute-path
import ExecHttpPerfExecDetail from '@/plugins/http/index';
// eslint-disable-next-line import/no-absolute-path

interface Props {
  execInfo: Record<string, any>;
  exception:{
    codeName:string;
    messageName:string;
    code:string;
    message:string;
  };
  delayInSeconds:number;
  apiDimensionObj: {[key:string]:{ [key:string]: number[]}};
  indexDimensionObj: {[key:string]:{ [key:string]: number[]}};
  apiNames:string[];
  timestampData:string[];
  isLoaded:boolean;
  brpsUnit:'KB' | 'MB';
  bwpsUnit: 'KB' | 'MB';
  stutasCodeData:Record<string, any>;
  errCountList: Record<string, any>[];
  sampleList: Record<string, any>[];
}

const props = withDefaults(defineProps<Props>(), {
  exception: undefined,
  delayInSeconds: 3000
});

const emit = defineEmits<{(e:'setCountTabKey', value:string):void;}>();
const setCountTabKey = (value:string) => {
  emit('setCountTabKey', value);
};

const detailRef = ref();
defineExpose({
  restartNode: () => detailRef.value?.restartNode()
});
</script>
<template>
  <ExecHttpPerfExecDetail
    ref="detailRef"
    type="detail"
    :execInfo="props.execInfo"
    :delayInSeconds="props.delayInSeconds"
    :apiDimensionObj="props.apiDimensionObj"
    :indexDimensionObj="props.indexDimensionObj"
    :apiNames="props.apiNames"
    :timestampData="props.timestampData"
    :errCountList="props.errCountList"
    :sampleList="props.sampleList"
    :stutasCodeData="props.stutasCodeData"
    :isLoaded="props.isLoaded"
    :brpsUnit="props.brpsUnit"
    :bwpsUnit="props.bwpsUnit"
    :exception="props.exception"
    @setCountTabKey="setCountTabKey" />
</template>
<style scoped>
.header-tabs > :deep(.ant-tabs-content-holder) {
  @apply overflow-y-auto px-5;
}

.header-tabs > :deep(.ant-tabs-nav ){
  @apply h-10 pr-5 font-medium;

  background-image: linear-gradient(to bottom, rgb(255, 255, 255) 13%, rgb(245, 251, 255) 100%);
}

.header-tabs > :deep(.ant-tabs-nav > .ant-tabs-nav-wrap) {
  @apply pl-5;
}

.node-action-btn {
  @apply rounded mr-2 text-text-content text-3 border-0  px-2 shadow-none inline-flex items-center;
}

.node-action-btn :deep(span) {
  @apply ml-1;
}

.node-action-btn[disabled],
.node-action-btn:not([disabled]),
.node-action-btn:focus {
  @apply bg-transparent !important;
}

.node-action-btn[disabled] {
  @apply opacity-50;
}

.node-action-btn:not([disabled]):hover {
  @apply text-text-link;
}

.node-action-btn::after {
  @apply hidden;
}
</style>
