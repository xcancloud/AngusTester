<script lang="ts" setup>
import { defineAsyncComponent, inject, ref, Ref } from 'vue';

import { HTTPConfig } from '../PropsType';

type Props = {
  variables: HTTPConfig['variables'];
  datasets: HTTPConfig['datasets'];
  actionOnEOF: 'RECYCLE' | 'STOP_THREAD';
  sharingMode: 'ALL_THREAD' | 'CURRENT_THREAD';
  errorNum: number;
}

const props = withDefaults(defineProps<Props>(), {
  variables: () => [],
  datasets: () => [],
  actionOnEOF: undefined,
  sharingMode: undefined,
  errorNum: 0
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'variablesChange', value: HTTPConfig['variables']): void;
  (e: 'datasetsChange', value: HTTPConfig['datasets']): void;
  (e: 'update:errorNum', value: number): void;
  (e: 'update:actionOnEOF', value:'RECYCLE' | 'STOP_THREAD'): void;
  (e: 'update:sharingMode', value: 'ALL_THREAD' | 'CURRENT_THREAD'): void;
}>();

const variablesRef = ref();

const Dataset = defineAsyncComponent(() => import('./Dataset/index.vue'));
const Variables = defineAsyncComponent(() => import('./Variables/index.vue'));

const userInfo = inject<Ref<{ id: string }>>('tenantInfo');
const projectInfo = inject<Ref<{ id: string; avatar: string; name: string; }>>('projectInfo', ref({ id: '', avatar: '', name: '' }));
const appInfo = inject<Ref<{ id: string; name: string; }>>('appInfo', ref({ id: '', name: '' }));

const targetInfoChange = (data: { actionOnEOF?: 'RECYCLE' | 'STOP_THREAD'; sharingMode?: 'ALL_THREAD' | 'CURRENT_THREAD'; }) => {
  const { actionOnEOF, sharingMode } = data;
  if (actionOnEOF) {
    emit('update:actionOnEOF', actionOnEOF);
  }

  if (sharingMode) {
    emit('update:sharingMode', sharingMode);
  }
};

const datasetsChange = (data:HTTPConfig['datasets']) => {
  emit('datasetsChange', data);
};

const variablesChange = (data:HTTPConfig['variables']) => {
  emit('variablesChange', data);
};

const errorChange = (value: number) => {
  emit('update:errorNum', value);
};

defineExpose({
  isValid: () => {
    if (typeof variablesRef.value?.isValid === 'function') {
      return variablesRef.value.isValid();
    }

    return true;
  }
});
</script>
<template>
  <div class="space-y-10">
    <Variables
      ref="variablesRef"
      :projectId="projectInfo?.id"
      :userInfo="userInfo"
      :appInfo="appInfo"
      :variables="props.variables"
      @change="variablesChange"
      @errorChange="errorChange" />
    <Dataset
      :projectId="projectInfo?.id"
      :userInfo="userInfo"
      :appInfo="appInfo"
      :datasets="props.datasets"
      :actionOnEOF="props.actionOnEOF"
      :sharingMode="props.sharingMode"
      @targetInfoChange="targetInfoChange"
      @change="datasetsChange">
    </dataset>
  </div>
</template>
