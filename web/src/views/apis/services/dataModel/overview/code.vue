<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { MonacoEditor } from '@xcan-angus/vue-ui';
import YAML from 'yaml';

interface Props {
    dataSource: {[key: string]: any}; // openapi json
    selectStr?: string;
    startKey?: string;
}

const monacoEditorRef = ref();

const data = ref('');
const selectRange = ref({});

const props = withDefaults(defineProps<Props>(), {
  dataSource: () => ({}),
  selectStr: undefined,
  startKey: ''
});
onMounted(() => {
  watch(() => props.dataSource, newValue => {
    data.value = YAML.stringify(newValue);
  }, {
    immediate: true,
    deep: true
  });

  watch(() => props.selectStr, newValue => {
    if (monacoEditorRef.value) {
      monacoEditorRef.value.removeDecoration();
    }
    if (!newValue) {
      selectRange.value = {};
      return;
    }

    let lineNumber = 1;
    let hasKey = false;
    const targetStr = YAML.stringify(newValue);
    const targetStrs = targetStr.split('\n');
    const targetFirstLineStr = targetStrs[0];
    const allStrs = data.value.split('\n');
    for (let i = 0; i < allStrs.length; i++) {
      if (hasKey && allStrs[i].trim() === targetFirstLineStr.trim()) {
        lineNumber = 1 + i;
        break;
      } else {
        if (allStrs[i].trim() === (props.startKey.trim() + ':')) {
          hasKey = true;
        }
      }
    }
    setTimeout(() => {
      monacoEditorRef.value.decorations([lineNumber, 1, lineNumber + targetStrs.length - 2, 1]);
    }, 500);
  }, {
    immediate: true,
    deep: true
  });
});

</script>
<template>
  <div class="h-full pt-2">
    <MonacoEditor
      ref="monacoEditorRef"
      :value="data"
      :readOnly="true"
      language="yaml" />
  </div>
</template>
