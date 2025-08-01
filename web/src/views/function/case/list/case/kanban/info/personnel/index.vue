<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { Button } from 'ant-design-vue';
import { AsyncComponent, Colon, Icon, SelectUser } from '@xcan-angus/vue-ui';
import { TESTER } from '@xcan-angus/infra';
import { funcCase } from '@/api/tester';

import { CaseInfo } from '../../PropsType';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  dataSource: CaseInfo;
  canEdit: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  dataSource: undefined,
  canEdit: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (event: 'loadingChange', value: boolean): void;
  (event: 'change', value: CaseInfo): void;
  (event: 'update:dataSource', value: CaseInfo): void;
}>();

const testerRef = ref();
const testerEditFlag = ref(false);
const testerMessage = ref<string>();
const testerIdValue = ref<string>();

const toEditAssignee = () => {
  testerIdValue.value = testerId.value;
  testerEditFlag.value = true;

  nextTick(() => {
    setTimeout(() => {
      if (typeof testerRef.value?.focus === 'function') {
        testerRef.value?.focus();
      }
    }, 100);
  });
};

const assignToMe = () => {
  testerIdValue.value = userId.value;
  testerMessage.value = userName.value;
  testerBlur();
};

const testerChange = async (_event: { target: { value: string; } }, option: { id: string; fullName: string; }) => {
  testerMessage.value = option.fullName;
};

const testerBlur = async () => {
  const value = testerIdValue.value;
  if (value === testerId.value) {
    testerEditFlag.value = false;
    return;
  }

  loadingChange(true);
  const [error] = await funcCase.updateCase([{
    id: props.dataSource?.id,
    testerId: value
  }]);
  loadingChange(false);
  if (error) {
    if (typeof testerRef.value?.focus === 'function') {
      testerRef.value?.focus();
    }

    return;
  }

  testerEditFlag.value = false;
  change();
};

const loadingChange = (value:boolean) => {
  emit('loadingChange', value);
};

const change = async () => {
  const id = props.dataSource?.id;
  if (!id) {
    return;
  }

  loadingChange(true);
  const [error, res] = await funcCase.getCaseDetail(id);
  loadingChange(false);
  if (error) {
    return;
  }

  const data = res?.data || {};
  emit('change', data);
  emit('update:dataSource', data);
};

const developerName = computed(() => props.dataSource?.developerName);
const reviewerName = computed(() => props.dataSource?.reviewerName);
const createdByName = computed(() => props.dataSource?.createdByName);
const lastModifiedByName = computed(() => props.dataSource?.lastModifiedByName);

const testerId = computed(() => props.dataSource?.testerId);
const testerName = computed(() => props.dataSource?.testerName);
const testerDefaultOptions = computed(() => {
  const id = testerId.value;
  if (!id) {
    return undefined;
  }

  return {
    [id]: {
      id: id,
      fullName: testerName.value
    }
  };
});

const userId = computed(() => {
  return props.userInfo?.id;
});

const userName = computed(() => {
  return props.userInfo?.fullName;
});
</script>

<template>
  <div class="h-full text-3 leading-5 pl-5 overflow-auto">
    <div class="text-theme-title mb-2.5 font-semibold">人员</div>

    <div class="space-y-2.5">
      <div class="flex items-start">
        <div class="w-18.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>测试人</span>
          <Colon class="w-1" />
        </div>

        <div
          v-if="props.canEdit"
          v-show="!testerEditFlag"
          class="flex items-start whitespace-pre-wrap break-words break-all">
          <div>{{ testerName }}</div>
          <Button
            type="link"
            class="flex-shrink-0 ml-2 p-0 h-3.5 leading-3.5 border-none transform-gpu translate-y-0.75"
            @click="toEditAssignee">
            <Icon icon="icon-shuxie" class="text-3.5" />
          </Button>
          <Button
            v-if="!testerId||testerId!==userId"
            size="small"
            type="link"
            class="p-0 h-5 leading-5 ml-1"
            @click="assignToMe">
            指派给我
          </Button>
        </div>

        <AsyncComponent :visible="testerEditFlag">
          <SelectUser
            v-show="testerEditFlag"
            ref="testerRef"
            v-model:value="testerIdValue"
            placeholder="选择测试人"
            allowClear
            :defaultOptions="testerDefaultOptions"
            :action="`${TESTER}/project/${props.projectId}/member/user`"
            :maxlength="80"
            class="edit-container"
            @change="testerChange"
            @blur="testerBlur" />
        </AsyncComponent>
      </div>

      <div class="flex items-start">
        <div class="w-18.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>开发人</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ developerName }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-18.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>评审人</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ reviewerName || '--' }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-18.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>添加人</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ createdByName }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-18.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>最后修改人</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ lastModifiedByName }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.border-none {
  border: none;
}

.edit-container {
  width: 100%;
  transform: translateY(-5px);
}
</style>
