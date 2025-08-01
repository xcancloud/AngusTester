<!-- eslint-disable @typescript-eslint/no-empty-function -->
<!-- eslint-disable @typescript-eslint/ban-types -->
<script lang="ts" setup>
import { inject, onMounted, reactive, ref } from 'vue';
import { Button, Form, FormItem } from 'ant-design-vue';
import { Input, notification, Select, SelectEnum, SelectUser, TreeSelect, IconText } from '@xcan-angus/vue-ui';
import { TESTER } from '@xcan-angus/infra';

import { apis, services } from 'src/api/tester';

interface Props {
  getParameter: any;
  id: string;
  summary: string;
  operationId: string;
  serviceId: string;
  description: string;
  status: string;
  ownerId: string;
  deprecated: boolean;
  tabKey: string;
  packageParams: ()=> Record<string, any>;
}

// eslint-disable-next-line @typescript-eslint/no-empty-function
const replaceTabPane = inject<(key:string, data: any) => void>('replaceTabPane', () => { });
const close = inject('close', () => ({}));
const auths = inject('auths', ref<string[]>([]));
const userInfo = inject('tenantInfo', ref());
const isUnarchived = inject('isUnarchived', { value: false });
const projectInfo = inject('projectInfo', ref({ id: '' }));

// 更新左侧未归档列表
// eslint-disable-next-line @typescript-eslint/no-empty-function
const refreshUnarchived = inject('refreshUnarchived', () => {});

// eslint-disable-next-line @typescript-eslint/no-empty-function
const updateTabPane = inject<(data: any) => void>('updateTabPane', () => { });

const defaultProject = ref();

const props = withDefaults(defineProps<Props>(), {
  getParameter: () => ({})
});

const emits = defineEmits<{(e: 'ok'): void}>();

const form = reactive({
  summary: '',
  operationId: '',
  ownerId: '',
  serviceId: '',
  projectName: '',
  description: '',
  status: 'UNKNOWN',
  deprecated: false,
  tags: []
});

const formRef = ref();
const rules = {
  summary: [{
    required: true, message: '请输入接口名称，100字符以内', trigger: 'blur'
  }],
  ownerId: [{
    required: true, message: '请选择接口负责人', trigger: 'change'
  }],
  serviceId: [{
    required: true, message: '请选择所属服务', trigger: 'change'
  }],
  status: [{
    required: true, message: '请选择状态', trigger: 'change'
  }]
};
const ownerOpt = ref([]);

const isLoading = ref(false);

const handleChanegProject = (value, name) => {
  form.serviceId = value;
  form.projectName = name?.[0];
};

const tagsOpt = ref<{value: string}[]>([]);
const loadTagfromProject = async () => {
  if (!form.serviceId || tagsOpt.value.length) {
    return;
  }
  const [error, resp] = await services.getTags(form.serviceId);
  if (error) {
    return;
  }
  tagsOpt.value = (resp.data || []).map(i => ({ value: i.name, label: i.name }));
};

const save = () => {
  formRef.value.validate().then(async () => {
    const params = form;
    const apiInfo = await props.packageParams();
    if (apiInfo.protocol !== 'ws' && apiInfo.protocol !== 'wss') {
      notification.warning('WebSocket协议必须以ws://或wss://开头');
      return;
    }
    const [error, resp] = isUnarchived.value && props.id
      ? await apis.addApi([{ ...apiInfo, ...params, unarchivedId: props.id }])
      : props.id
        ? await apis.updateApi([{ ...apiInfo, ...params, id: props.id }])
        : await apis.putApi([{ ...apiInfo, ...params }]);
    if (error) {
      return;
    }
    if (isUnarchived.value || !props.id) {
      notification.success('添加接口成功');
      refreshUnarchived();
    } else {
      notification.success('更新接口成功');
    }
    const id = props.id || resp.data[0]?.id;
    if (props.id) {
      updateTabPane({
        _id: id + 'socket',
        pid: props.tabKey,
        id: id,
        name: params.summary,
        unarchived: false,
        value: 'socket'
      });
    } else {
      replaceTabPane(props.tabKey, {
        _id: id + 'socket',
        pid: id + 'socket',
        name: params.summary,
        id: id,
        unarchived: false,
        value: 'socket'
      });
    }
    handleClose();
    emits('ok');
  });
};

const handleClose = () => {
  close();
};

onMounted(async () => {
  const apiInfo = await props.packageParams();
  form.summary = props.summary;
  form.operationId = props.operationId;
  form.ownerId = props.ownerId || userInfo.value.id;
  form.serviceId = apiInfo.serviceId || undefined;
  form.projectName = apiInfo.projectName || undefined;
  form.description = props.description;
  form.status = props.status || 'UNKNOWN';
  form.deprecated = props.deprecated;
  defaultProject.value = {
    name: form.projectName,
    id: form.serviceId,
    targetType: apiInfo.projectType
  };
});
</script>
<template>
  <Form
    ref="formRef"
    layout="vertical"
    :model="form"
    :rules="rules">
    <FormItem label="接口名称" name="summary">
      <Input
        v-model:value="form.summary"
        :maxlength="40"
        :allowClear="false"
        :disabled="!auths.includes('MODIFY')"
        class="rounded"
        placeholder="请输入接口名称，40字符以内" />
    </FormItem>
    <FormItem label="编码">
      <Input
        v-model:value="form.operationId"
        :maxlength="40"
        dataType="mixin-en"
        includes=":_-."
        :allowClear="false"
        :disabled="!auths.includes('MODIFY')"
        class="rounded"
        placeholder="请输入编码，40字符以内" />
    </FormItem>
    <FormItem label="接口负责人" name="ownerId">
      <SelectUser
        v-model:value="form.ownerId"
        class="rounded-border"
        :options="ownerOpt"
        :disabled="!auths.includes('MODIFY')"
        placeholder="请选择接口负责人"
        :allowClear="false" />
    </FormItem>
    <FormItem label="所属服务" name="serviceId">
      <TreeSelect
        v-model:defaultValue="defaultProject"
        :action="`${TESTER}/services?projectId=${projectInfo.id}&hasPermission=ADD&fullTextSearch=true`"
        :allowClear="false"
        :disabled="!auths.includes('MODIFY')||!isUnarchived"
        :fieldNames="{children:'children', label:'name', value: 'id'}"
        placeholder="请选择所属服务"
        :virtual="false"
        size="small"
        @change="handleChanegProject">
        <template #title="{name, targetType}">
          <div
            class="flex items-center"
            :title="name">
            <IconText
              text="S"
              class="bg-blue-badge-s mr-2 text-3"
              style="width: 16px; height: 16px;" />
            <span class="truncate flex-1">{{ name }}</span>
          </div>
        </template>
      </TreeSelect>
    </FormItem>
    <FormItem name="tags">
      <template #label>
        <div>
          <span>标签</span>
          <Tooltip placement="left">
            <Icon icon="icon-tishi1" class="text-blue-tips ml-0.5 text-3.5" />
            <template #title> 标签作为API附加元信息用于对其分组。 </template>
          </Tooltip>
        </div>
      </template>
      <Select
        v-model:value="form.tags"
        mode="tags"
        placeholder="请输入或选择标签，输入需按Enter确认"
        :disabled="!auths.includes('MODIFY')"
        :options="tagsOpt"
        @dropdownVisibleChange="loadTagfromProject">
      </Select>
    </FormItem>
    <FormItem label="状态" name="status">
      <SelectEnum
        v-model:value="form.status"
        :disabled="!auths.includes('MODIFY')"
        enumKey="ApiStatus">
      </SelectEnum>
    </FormItem>
    <FormItem label="是否弃用" name="deprecated">
      <Select
        v-model:value="form.deprecated"
        :disabled="!auths.includes('MODIFY')"
        :options="[{label: '正常', value: false}, {label: '已弃用', value: true}]">
      </Select>
    </FormItem>
    <FormItem label="描述">
      <Input
        v-model:value="form.description"
        type="textarea"
        showCount
        :rows="4"
        :allowClear="false"
        :disabled="!auths.includes('MODIFY')"
        class="rounded-border"
        placeholder="限制输入200字符以内" />
    </FormItem>
    <FormItem>
      <Button
        type="primary"
        class="mr-2.5 rounded"
        size="small"
        :loading="isLoading"
        :disabled="!auths.includes('MODIFY')"
        @click="save">
        保存
      </Button>
      <Button
        class="rounded"
        size="small"
        @click="handleClose">
        取消
      </Button>
      <p v-if="form.status === 'RELEASED'" class="text-3 text-status-orange mt-1">已发布接口不允许修改</p>
    </FormItem>
  </Form>
</template>
