<script lang="ts" setup>
import { onMounted, ref, watch, computed, nextTick } from 'vue';
import { DatePicker, Hints, Input, Modal, Colon, Select, Icon, HttpMethodText, notification } from '@xcan-angus/vue-ui';
import { Checkbox, Form, FormItem, Textarea, RadioGroup, Button } from 'ant-design-vue';
import { toClipboard, TESTER, enumUtils, DomainManager, AppOrServiceRoute, EnumMessage } from '@xcan-angus/infra';
import { ApisShareScope } from '@/enums/enums';
import { apis } from '@/api/tester';

interface Props {
  visible: boolean;
  shareId?: string;
  projectId: string;
  servicesId?: string; // 分享固定服务或接口
  apisIds?: string[]; // 分享固定接口
  shareScope?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  shareId: undefined,
  projectId: '',
  servicesId: undefined,
  apisIds: () => [],
  shareScope: ApisShareScope.SERVICES
});

const emits = defineEmits<{(e: 'cancel'):void; (e: 'ok'):void; (e: 'update:visible', value: boolean):void}>();

const origin = ref('');
const baseUrl = computed(() => {
  return `${origin.value}/apis/share`;
});

const formState = ref({
  name: undefined,
  remark: undefined,
  expiredDate: undefined,
  displayOptions: { includeServiceInfo: true, allowDebug: false, schemaStyle: 'TABLE' },
  shareScope: 'SERVICES',
  servicesId: props.servicesId || undefined,
  apisIds: props.apisIds as string[]
});

const apisShareScope = ref<EnumMessage<ApisShareScope>[]>([]);
const loadApisShareScopeOpt = () => {
  apisShareScope.value = enumUtils.enumToMessages(ApisShareScope);
};

const loading = ref(false);
const formRef = ref();

const loadData = async (id: string) => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, res] = await apis.getShareDetail(id);
  loading.value = false;
  if (error) {
    return;
  }

  const data = res?.data || {};
  if (!data) {
    return;
  }
  const { name, remark, expiredDate, displayOptions, shareScope, servicesId, apisIds } = data;
  formState.value = {
    name, remark, expiredDate, displayOptions, shareScope: shareScope.value, servicesId, apisIds
  };
  if (apisIds?.length) {
    const [error, { data }] = await apis.getShareList({
      projectId: props.projectId,
      filters: [{ value: apisIds, op: 'IN', key: 'id' }],
      pageSize: 1000
    });
    if (error) {
      return;
    }
    selectApis.value = data.list || [];
  }
};

// 范围变更
const handleScopeChange = () => {
  if (formState.value.shareScope === 'SERVICES') {
    formState.value.apisIds = [];
    selectApis.value = [];
  } else if (formState.value.shareScope === 'SINGLE_APIS' && !!formState.value.apisIds.length) {
    formState.value.apisIds = [formState.value.apisIds[0]];
    selectApis.value = selectApis.value.filter(i => i.id === formState.value.apisIds[0]);
  }
};

// 服务变更
const handleServiceChange = () => {
  formState.value.apisIds = [];
};

const handleSigngeApiChange = (apisId: string) => {
  formState.value.apisIds = [apisId];
};

const selectApiId = ref();
// const selectApi = ref();
const selectApis = ref<any[]>([]);
// const handleMultipleApiChange = (_apisId: string, option) => {
//   selectApi.value = option || undefined;
// };

const addMultipleApis = (apisId: string, option) => {
  selectApis.value.push(option);
  formState.value.apisIds.push(apisId);
  // selectApis.value.push(selectApi.value);
  // formState.value.apisIds.push(selectApiId.value);
  nextTick(() => {
    selectApiId.value = undefined;
  });
};

const delApis = (api, idx) => {
  selectApis.value.splice(idx, 1);
  formState.value.apisIds = formState.value.apisIds.filter(id => api.id !== id);
};

const cancel = () => {
  formRef.value.resetFields();
  emits('update:visible', false);
  emits('cancel');
};

const ok = async () => {
  formRef.value.validate().then(async () => {
    if (!props.shareId) {
      await addOk();
    } else {
      await editOk();
    }
  });
};

const addOk = async () => {
  loading.value = true;
  const [error, { data }] = await apis.addShare({
    ...formState.value,
    baseUrl: baseUrl.value,
    projectId: props.projectId
  });
  if (error) {
    loading.value = false;
    return;
  }
  const [_error1, resp] = await apis.getShareDetail(data.id);
  loading.value = false;
  if (resp.data) {
    toClipboard(`分享“${resp.data.name}”，访问地址：${resp.data.url}`).then(() => {
      notification.success('分享信息成功复制到剪贴板');
    });
  } else {
    notification.success('分享成功');
  }
  emits('ok');
  emits('update:visible', false);
};

const editOk = async () => {
  loading.value = true;
  const [error] = await apis.patchShared({
    ...formState.value,
    id: props.shareId
  });
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('分享成功');
  emits('ok');
  emits('update:visible', false);
};

onMounted(async () => {
  loadApisShareScopeOpt();
  origin.value = DomainManager.getInstance().getAppDomain(AppOrServiceRoute.tester);
  watch(() => props.visible, async (newValue) => {
    if (newValue) {
      if (props.shareId) {
        loadData(props.shareId);
      } else {
        formState.value = {
          name: undefined,
          remark: undefined,
          expiredDate: undefined,
          displayOptions: { includeServiceInfo: true, allowDebug: false, schemaStyle: 'TABLE' },
          shareScope: props.shareScope || 'SERVICES',
          servicesId: props.servicesId,
          apisIds: props.apisIds || []
        };
        selectApis.value = [];
        selectApiId.value = undefined;
      }
    }
  }, { immediate: true });
});

const schemaStyleOpt = [
  {
    value: 'TABLE',
    label: '表格'
  },
  {
    value: 'TREE',
    label: '树'
  }
];

</script>
<template>
  <Modal
    :title="props.shareId ? '编辑分享' : '添加分享'"
    :visible="props.visible"
    :width="680"
    :okButtonProps="{
      loading
    }"
    @cancel="cancel"
    @ok="ok">
    <Form
      ref="formRef"
      :model="formState"
      size="small"
      :labelCol="{ style: { width: '90px' } }"
      class="max-w-242.5"
      layout="horizontal">
      <FormItem
        required
        name="name"
        label="分享名称">
        <Input
          v-model:value="formState.name"
          :maxlength="100"
          placeholder="输入分享名称，最多可输入100字符" />
      </FormItem>
      <FormItem
        label="分享备注"
        class="!mb-5"
        name="remark">
        <Textarea
          v-model:value="formState.remark"
          :maxlength="400"
          placeholder="请输入分享备注，会展示在分享信息中，最多支持400字符">
        </Textarea>
      </FormItem>
      <FormItem
        name="expiredDate"
        label="到期日期"
        class="min-w-0">
        <div class="flex items-center space-x-1">
          <DatePicker
            v-model:value="formState.expiredDate"
            showToday
            showTime
            class="flex-1 min-w-0" />
          <Hints text="不指定时永久有效。" />
        </div>
      </FormItem>
      <FormItem
        label="展示选项"
        class="!mb-5"
        name="displayOptions">
        <div class="flex items-center">
          <Checkbox
            v-model:checked="formState.displayOptions.includeServiceInfo">
            展示服务说明
          </Checkbox>
          <Checkbox
            v-model:checked="formState.displayOptions.allowDebug">
            允许调试
          </Checkbox>
          <div class="inline-flex items-center text-3 ml-2">
            <span>字段样式</span>
            <Colon />
            <Select
              v-model:value="formState.displayOptions.schemaStyle"
              class="flex-1 ml-2"
              :options="schemaStyleOpt">
            </Select>
          </div>
        </div>
      </FormItem>

      <template v-if="!props.servicesId">
        <FormItem
          label="分享范围"
          name="shareScope"
          required>
          <RadioGroup
            v-model:value="formState.shareScope"
            :options="apisShareScope"
            @change="handleScopeChange">
          </RadioGroup>
        </FormItem>
        <FormItem
          label="选择服务"
          name="servicesId"
          class="flex-1 min-w-0"
          required>
          <Select
            v-model:value="formState.servicesId"
            :action="`${TESTER}/services?projectId=${props.projectId}&fullTextSearch=true`"
            placeholder="选择服务"
            :fieldNames="{value: 'id', label: 'name'}"
            @change="handleServiceChange" />
        </FormItem>
        <FormItem
          v-if="['PARTIAL_APIS', 'SINGLE_APIS'].includes(formState.shareScope)"
          label="接口"
          name="apisIds"
          class="flex-1 min-w-0"
          required>
          <Select
            v-if="formState.shareScope === 'SINGLE_APIS'"
            :value="formState.apisIds[0]"
            :disabled="!formState.servicesId"
            placeholder="选择接口"
            :action="`${TESTER}/services/${formState.servicesId}/apis`"
            :fieldNames="{value: 'id', label: 'summary'}"
            @change="handleSigngeApiChange" />

          <div v-if="formState.shareScope === 'PARTIAL_APIS'" class="flex items-center space-x-2 text-3">
            <Select
              v-model:value="selectApiId"
              :disabled="!formState.servicesId"
              class="flex-1 min-w-0"
              placeholder="选择接口"
              :action="`${TESTER}/services/${formState.servicesId}/apis`"
              :disabledList="formState.apisIds"
              :fieldNames="{value: 'id', label: 'summary'}"
              @change="addMultipleApis" />
            <!-- <Button
                :disabled="!selectApi"
                size="small"
                type="primary"
                @click="addMultipleApis">
                <Icon icon="icon-jia" />
              </Button> -->

            <span>已选择{{ formState.apisIds?.length || 0 }}个接口</span>
          </div>
        </FormItem>

        <FormItem v-if="formState.shareScope === 'PARTIAL_APIS'" label="">
          <div class="max-h-50 overflow-y-auto pl-22 ">
            <div
              v-for="(item, idx) in selectApis"
              :key="item.id"
              class="px-1 flex h-6 items-center ">
              <HttpMethodText :value="item.method" />
              <span class="min-w-0 truncate flex-1" :title="item.endpoint">{{ item.endpoint }}</span>
              <span class="min-w-0 truncate flex-1" :title="item.apisName || item.caseName || item.summary">{{ item.apisName || item.caseName || item.summary }}</span>
              <Button
                type="link"
                size="small"
                class="ml-2"
                @click="delApis(item, idx)">
                <Icon icon="icon-qingchu" />
              </Button>
            </div>
          </div>
        </FormItem>
      </template>
    </Form>
  </Modal>
</template>
