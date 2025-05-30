<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue';
import { Button } from 'ant-design-vue';
import { AsyncComponent, Icon, NoData, Toggle } from '@xcan-angus/vue-ui';
import { task } from 'src/api/tester';

import { TaskInfo } from '../../../../../../../PropsType';

type Props = {
  taskId: string;
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  dataSource: TaskInfo;
  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  taskId: undefined,
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined,
  loading: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (event: 'loadingChange', value: boolean): void;
  (event: 'change', value: Partial<TaskInfo>): void;
}>();

const RichEditor = defineAsyncComponent(() => import('@/components/richEditor/index.vue'));

const richEditorRef = ref();

const openFlag = ref(true);
const editFlag = ref(false);
const content = ref<string>('');

const toEdit = () => {
  openFlag.value = true;
  editFlag.value = true;
  content.value = props?.dataSource?.description || '';
};

const editorChange = (value: string) => {
  content.value = value;
};

const cancel = () => {
  editFlag.value = false;
  content.value = props.dataSource?.description || '';
};

const validateErr = ref(false);
const ok = async () => {
  if (isError()) {
    validateErr.value = true;
    return;
  }
  validateErr.value = false;

  const params = { description: content.value };
  emit('loadingChange', true);
  const [error] = await task.editTaskDescription(props.taskId, params);
  emit('loadingChange', false);
  if (error) {
    return;
  }

  editFlag.value = false;
  emit('change', { id: props.taskId, description: content.value });
};

onMounted(() => {
  watch(() => props.dataSource, (newValue) => {
    content.value = newValue?.description || '';
  }, { immediate: true });
});

const isError = () => {
  if (!content.value) {
    return false;
  }
  const length = richEditorRef.value.getLength();
  return length > 6000;
};

</script>

<template>
  <Toggle v-model:open="openFlag">
    <template #title>
      <div class="flex items-center text-3">
        <span>描述</span>
        <template v-if="editFlag">
          <Button
            size="small"
            type="link"
            @click="cancel">
            取消
          </Button>
          <Button
            size="small"
            type="link"
            @click="ok">
            确定
          </Button>
        </template>
        <Button
          v-else
          type="link"
          class="flex-shrink-0 ml-2 p-0 h-3.5 leading-3.5 border-none"
          @click="toEdit">
          <Icon icon="icon-shuxie" class="text-3.5" />
        </Button>
      </div>
    </template>

    <template #default>
      <template v-if="editFlag">
        <div class="mb-2.5 mt-2.5 ml-5.5">
          <RichEditor
            ref="richEditorRef"
            :value="content"
            :options="{placeholder: '添加详细说明，最大支持6000个字符'}"
            placeholder="描述最大支持6000个字符"
            @change="editorChange" />
          <div v-show="validateErr" class="text-status-error text-3">描述最大支持6000个字符</div>
        </div>

        <!-- <div class="space-x-2.5 w-full flex items-center justify-end">
            <Button size="small" @click="cancel">取消</Button>
            <Button
              size="small"
              type="primary"
              @click="ok">
              确定
            </Button>
          </div> -->
      </template>

      <div v-if="!editFlag" class="browser-container">
        <RichEditor :value="props.dataSource?.description" mode="view" />
      </div>

      <NoData
        v-if="!editFlag&&!content?.length"
        size="small"
        style="min-height: 160px;" />
    </template>
  </Toggle>
</template>

<style scoped>
.border-none {
  border: none;
}

.browser-container  {
  padding-left: 21px;
  transform: translateY(1px);
}
</style>
