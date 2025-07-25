<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { Button } from 'ant-design-vue';
import { AsyncComponent, Colon, DatePicker, Icon, Tooltip } from '@xcan-angus/vue-ui';
import dayjs, { Dayjs } from 'dayjs';
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
  canEdit: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (event: 'loadingChange', value: boolean): void;
  (event: 'change', value: CaseInfo): void;
  (event: 'update:dataSource', value: CaseInfo): void;
}>();

const dateRef = ref();
const editFlag = ref(false);
const dateValue = ref<string>();

const dateError = ref();
const dateErrorMessage = ref<string>();

const toEdit = () => {
  dateValue.value = deadlineDate.value;
  editFlag.value = true;

  nextTick(() => {
    setTimeout(() => {
      if (typeof dateRef.value?.focus === 'function') {
        dateRef.value?.focus();
      }
    }, 100);
  });
};

const dateChange = (value:string) => {
  if (!value) {
    dateErrorMessage.value = '请选择截止时间';
    return;
  }

  if (dayjs(value).isBefore(dayjs(), 'minute')) {
    dateError.value = true;
    dateErrorMessage.value = '截止时间必须是一个未来时间';
    return;
  }

  dateError.value = false;
  dateErrorMessage.value = undefined;
};

const dateBlur = async () => {
  if (dateError.value) {
    if (typeof dateRef.value?.focus === 'function') {
      dateRef.value?.focus();
    }

    return;
  }

  const value = dateValue.value;
  if (!value || value === deadlineDate.value) {
    editFlag.value = false;
    return;
  }

  loadingChange(true);
  const [error] = await funcCase.putDeadline(caseId.value, value);
  loadingChange(false);
  if (error) {
    if (typeof dateRef.value?.focus === 'function') {
      dateRef.value?.focus();
    }

    return;
  }

  editFlag.value = false;
  change();
};

// 禁用日期组件当前时间之前的年月日
const disabledDate = (current: Dayjs) => {
  const today = dayjs().startOf('day');
  return current.isBefore(today, 'day');
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

const caseId = computed(() => props.dataSource?.id);
const createdDate = computed(() => props.dataSource?.createdDate);
const deadlineDate = computed(() => props.dataSource?.deadlineDate);
const testResultHandleDate = computed(() => props.dataSource?.testResultHandleDate);
const reviewDate = computed(() => props.dataSource?.reviewDate);
const lastModifiedDate = computed(() => props.dataSource?.lastModifiedDate);
</script>

<template>
  <div class="h-full text-3 leading-5 pl-5 overflow-auto">
    <div class="text-theme-title mb-2.5 font-semibold">日期</div>

    <div class="space-y-2.5">
      <div class="flex items-start">
        <div class="w-21.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>评审时间</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ reviewDate || '--' }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-21.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>评审完成时间</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ testResultHandleDate || '--' }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-21.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>添加时间</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ createdDate }}</div>
      </div>

      <div class="flex items-start">
        <div class="w-21.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>截止时间</span>
          <Colon class="w-1" />
        </div>

        <div v-show="!editFlag" class="flex items-start whitespace-pre-wrap break-words break-all">
          <div>{{ deadlineDate }}</div>
          <Button
            v-if="props.canEdit"
            type="link"
            class="flex-shrink-0 ml-2 p-0 h-3.5 leading-3.5 border-none transform-gpu translate-y-0.75"
            @click="toEdit">
            <Icon icon="icon-shuxie" class="text-3.5" />
          </Button>
        </div>

        <AsyncComponent :visible="editFlag">
          <Tooltip
            :visible="dateError"
            :title="dateErrorMessage"
            placement="left"
            arrowPointAtCenter>
            <DatePicker
              v-show="editFlag"
              ref="dateRef"
              v-model:value="dateValue"
              :error="dateError"
              :showNow="false"
              :disabledDate="disabledDate"
              :showTime="{ hideDisabledOptions: true, format: 'HH:mm:ss' }"
              type="date"
              size="small"
              class="edit-container"
              showToday
              @change="dateChange"
              @blur="dateBlur" />
          </Tooltip>
        </AsyncComponent>
      </div>

      <div class="flex items-start">
        <div class="w-21.5 flex items-center whitespace-nowrap flex-shrink-0">
          <span>最后修改时间</span>
          <Colon class="w-1" />
        </div>

        <div class="whitespace-pre-wrap break-words break-all">{{ lastModifiedDate || '--' }}</div>
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
