<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue';
import { Button } from 'ant-design-vue';
import { Input, IconRequired, IconCopy } from '@xcan-angus/vue-ui';

export interface Props {
  value: {
    id: string;
    name: string;
    description: string;
    scriptId?: string;
    scriptName?: string;
  };

  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: undefined,
  loading: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'save', value: { name: string; description: string; }): void;
  (e: 'canecel'): void;
}>();

const name = ref<string>();
const nameError = ref(false);
const description = ref<string>();

const nameChange = (event: { target: { value: string; } }) => {
  const value = event.target.value;
  name.value = value;
  nameError.value = false;
};

const descriptionChange = (event: { target: { value: string; } }) => {
  const value = event.target.value;
  description.value = value;
};

const isValid = (): boolean => {
  nameError.value = false;
  let errorNum = 0;
  if (!name.value) {
    errorNum++;
    nameError.value = true;
  }

  return !errorNum;
};

const getData = () => {
  return { description: description.value, name: name.value };
};

const save = () => {
  if (!isValid()) {
    return;
  }

  emit('save', getData());
};

const cancel = () => {
  emit('canecel');
  init();
};

const init = () => {
  nameError.value = false;
  const data = props.value;
  if (!data) {
    return;
  }

  const { name: _name, description: _description } = data;
  name.value = _name;
  description.value = _description;
};

onMounted(() => {
  watch(() => props.value, () => {
    init();
  }, { immediate: true });
});

const scriptId = computed(() => {
  return props.value?.scriptId || '';
});

const scriptName = computed(() => {
  return props.value?.scriptName || '';
});

const id = computed(() => {
  if (!scriptId.value) {
    return '';
  }

  return props.value?.id;
});

defineExpose({ isValid, getData });
</script>

<template>
  <div class="space-y-4 leading-5">
    <div v-if="id" class="space-y-0">
      <div class="flex items-center">
        <span>ID</span>
      </div>
      <div class="flex-1 flex items-center space-x-2">
        <span :title="id" class="truncate">{{ id }}</span>
        <IconCopy class="flex-shrink-0" :copyText="id" />
      </div>
    </div>
    <div class="space-y-0.5">
      <div class="flex items-center">
        <IconRequired />
        <span>名称</span>
      </div>
      <Input
        :maxlength="200"
        :value="name"
        :error="nameError"
        placeholder="最长200个字符"
        trim
        @change="nameChange" />
    </div>

    <div v-if="scriptName" class="space-y-0">
      <div class="flex items-center">
        <span>脚本名称</span>
      </div>
      <div class="flex-1 flex items-center space-x-2">
        <span :title="scriptName" class="truncate">{{ scriptName }}</span>
        <IconCopy class="flex-shrink-0" :copyText="scriptName" />
      </div>
    </div>

    <div v-if="scriptId" class="space-y-0">
      <div class="flex items-center">
        <span>脚本ID</span>
      </div>
      <div class="flex-1 flex items-center space-x-2">
        <span :title="scriptId" class="truncate">{{ scriptId }}</span>
        <IconCopy class="flex-shrink-0" :copyText="scriptId" />
      </div>
    </div>
    <div class="space-y-0.5">
      <div class="flex items-center">描述</div>
      <Input
        :maxlength="800"
        :value="description"
        :autoSize="{ minRows: 5, maxRows: 5 }"
        showCount
        type="textarea"
        placeholder="最长800个字符"
        trim
        @change="descriptionChange" />
    </div>
    <div class="flex items-center space-x-2 !mt-5">
      <Button
        type="primary"
        size="small"
        :loading="props.loading"
        @click="save">
        确定
      </Button>
      <Button
        type="default"
        size="small"
        @click="cancel">
        取消
      </Button>
    </div>
  </div>
</template>
