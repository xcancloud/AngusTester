<script setup lang="ts">
import { Checkbox } from 'ant-design-vue';

interface Props {
    options: { label: string; value: string }[];
    disabled: boolean;
    value: string[];
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
  disabled: false,
  value: () => []
});

const emit = defineEmits<{(e: 'change', value: string[]) }>();

const change = (event: { target: { checked: boolean; } }, value: string) => {
  const checked = event.target.checked;

  // 取消勾选修改，需要同步取消发布
  if (!checked) {
    const excludes = [value];
    if (value === 'MODIFY') {
      excludes.push('RELEASE');
    }
    const temp = value !== 'VIEW' ? props.value.filter(item => !excludes.includes(item)) : [];
    emit('change', temp);

    return;
  }

  const newValues = value !== 'VIEW' && !props.value.includes('VIEW') ? [value, 'VIEW'] : [value];
  if (value === 'RELEASE') {
    newValues.push('MODIFY');
  }

  const temp = new Set(props.value.concat(...newValues));
  emit('change', Array.from(temp));
};
</script>
<template>
  <div class="ant-checkbox-group">
    <Checkbox
      v-for="item in props.options"
      :key="item.value"
      :disabled="props.disabled"
      :checked="props.value.includes(item.value)"
      @change="change($event, item.value)">
      {{ item.label }}
    </Checkbox>
  </div>
</template>
<style scoped>
.ant-checkbox-wrapper + .ant-checkbox-wrapper {
  margin-left: 6px;
}

.ant-checkbox-group .ant-checkbox-wrapper:first-child {
  margin-left: 6px;
}
</style>
