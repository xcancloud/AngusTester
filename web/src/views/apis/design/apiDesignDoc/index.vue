<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { Tag, Button } from 'ant-design-vue';
import { apis } from '@/api/tester';
import OpenApiDesign from 'open-api-designer';
import { notification } from "@xcan-angus/vue-ui";

interface Props {
  designId: string
}

const props = withDefaults(defineProps<Props>(), {
  designId: ''
});

const openApiDesignRef = ref();
const designInfo = ref<{[key: string]: string}>({});
const designContent = ref();
let openAPIDesignInstance;

const getDesignContent = async () => {
  const [error, resp] = await apis.exportDesign({format: 'json', id: props.designId});
  if (error) {
    return
  }
  designContent.value = JSON.stringify(resp?.data || {});
}

const getDesignInfo = async () => {
  const [error, resp] = await apis.getDesignInfo(props.designId);
  if (error) {
    return
  }
  designInfo.value = resp?.data || {};
};

const updateContent = async () => {
  console.log(updateContent)
  const content = (openAPIDesignInstance && typeof openAPIDesignInstance.getDocApi === 'function')
    ? openAPIDesignInstance.getDocApi === 'function'
    : designInfo.value;
  const [error] = await apis.putDesignContent({id: props.designId, openapi: JSON.stringify(content)});
  if (error) {
    return;
  }
  notification.success('保存成功');
};

const releaseDesign = async ()  => {
  console.log(updateContent)
  const [error] = await apis.releaseDesign(props.designId);
  if (error) {
    return;
  }
  notification.success('发布成功');
}

onMounted(async () => {
  await getDesignContent();
  await getDesignInfo();
  openAPIDesignInstance = new OpenApiDesign();
})
</script>
<template>
<div ref="openApiDesignRef" class="h-full">
  <component is="open-api-design" :open-api-doc="designContent">
    <div slot="docTitle" class="flex justify-center items-center space-x-2">
      <Tag color="green" class="text-3.5 rounded-full">{{designInfo.openapiSpecVersion}}</Tag>
      <div class="text-5 font-medium">{{designInfo.name}}</div>
      <div>{{designInfo.lastModifiedByName}}最后修改于{{designInfo.lastModifiedDate}}</div>
      <div class="relative left-20 space-x-2">
        <Button
          type="primary"
          size="small"
          @click="updateContent">
          保存草稿
        </Button>
        <Button
          size="small"
          @click="releaseDesign">
          发布设计
        </Button>
      </div>
    </div>
  </component>

</div>
</template>
<style scoped>
</style>
