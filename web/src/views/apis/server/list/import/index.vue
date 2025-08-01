<script setup lang="ts">
import { computed, ref } from 'vue';
import { Radio, RadioGroup, TypographyParagraph, UploadDragger, UploadFile } from 'ant-design-vue';
import { Icon, Modal, Spin } from '@xcan-angus/vue-ui';
import { variable } from '@/api/tester';

import { formatBytes } from '@/utils/common';

interface Props {
  visible: boolean;
  projectId: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  projectId: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (event: 'update:visible', value: boolean): void;
  (event: 'ok'): void;
}>();

const MAX_SIZE = 20;

const loading = ref(false);

const originFile = ref<UploadFile>();
const strategyWhenDuplicated = ref<'COVER' | 'IGNORE'>('COVER');
const uploadErrorMsg = ref<string>();

const uploadChange = async ({ file }: { file: UploadFile }) => {
  if (!file) {
    originFile.value = undefined;
    uploadErrorMsg.value = undefined;
    return;
  }

  if (file?.size && file.size > maxFileSize.value) {
    uploadErrorMsg.value = `文件大小不能超过${MAX_SIZE}M`;
    originFile.value = undefined;
    return;
  }

  originFile.value = file;
};

const customRequest = () => {
  return false;
};

const deleteFile = () => {
  originFile.value = undefined;
  uploadErrorMsg.value = undefined;
};

const cancel = () => {
  reset();
  emit('update:visible', false);
};

const ok = async () => {
  const formData = new FormData();
  const file = originFile.value?.originFileObj as File;
  formData.append('file', file);
  formData.append('strategyWhenDuplicated', strategyWhenDuplicated.value);
  formData.append('projectId', props.projectId);
  loading.value = true;
  const [error] = await variable.importVariables(formData);
  loading.value = false;
  if (error) {
    uploadErrorMsg.value = error.message;
    return;
  }

  reset();
  emit('update:visible', false);
  emit('ok');
};

const reset = () => {
  originFile.value = undefined;
  uploadErrorMsg.value = undefined;
  strategyWhenDuplicated.value = 'COVER';
};

const okButtonProps = computed(() => {
  return {
    disabled: !originFile.value
  };
});

const maxFileSize = computed(() => {
  return 1024 * 1024 * MAX_SIZE;
});

const ellipsis = computed(() => {
  return {
    rows: 5,
    expandable: false,
    tooltip: uploadErrorMsg.value
  };
});
</script>
<template>
  <Modal
    :visible="props.visible"
    :width="600"
    :okButtonProps="okButtonProps"
    :confirmLoading="loading"
    title="上传服务器"
    @cancel="cancel"
    @ok="ok">
    <Spin :spinning="loading" class="mb-5">
      <UploadDragger
        v-show="!originFile"
        :class="{ 'error-border': !!uploadErrorMsg }"
        :multiple="false"
        :showUploadList="false"
        :customRequest="customRequest"
        accept=".zip,.rar,.7z,.gz,.tar,.bz2,.xz,.lzma,.json,.yaml,.yml"
        @change="uploadChange">
        <div class="flex flex-col items-center justify-center text-3 leading-5">
          <Icon icon="icon-shangchuan" class="text-5 text-text-link" />
          <div class="mt-1 mb-1.5 text-text-link">选择文件</div>
          <div class="text-theme-sub-content">
            可直接将文件拖入此区域直接上传，文件大小不超过{{ MAX_SIZE }}M，支持.json,.yaml,.yml类型文件。
          </div>
        </div>
      </UploadDragger>

      <div
        v-show="!!originFile"
        :class="{ 'border-status-error': !!uploadErrorMsg }"
        class="px-3.5 border rounded">
        <div class="flex py-2">
          <div :title="originFile?.name" class="flex-2 truncate">{{ originFile?.name }}</div>
          <div class="flex-1 text-right">{{ formatBytes(+(originFile?.size || 0)) }}</div>
          <div class="flex-1 text-right">
            <Icon
              icon="icon-qingchu"
              class="cursor-pointer ml-2 text-3.5 text-theme-text-hover"
              @click="deleteFile" />
          </div>
        </div>
      </div>

      <TypographyParagraph
        :content="uploadErrorMsg"
        :ellipsis="ellipsis"
        class="text-status-error mt-1" />
    </Spin>

    <div class="space-y-0.5 leading-5 text-3">
      <div>遇到重复时的处理策略</div>
      <RadioGroup v-model:value="strategyWhenDuplicated">
        <Radio value="COVER">覆盖</Radio>
        <Radio value="IGNORE">忽略</Radio>
      </RadioGroup>
    </div>
  </Modal>
</template>
<style scoped>
:deep(.ant-upload.ant-upload-drag.error-border) {
  @apply border-border-error;
}

.flex-2 {
  flex: 2 1 0%;
}
</style>
