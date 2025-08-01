<script lang="ts" setup>
import { computed, inject, nextTick, onMounted, ref, watch } from 'vue';
import { Icon, Input, notification, Select, SelectEnum, Spin } from '@xcan-angus/vue-ui';
import { Button, DatePicker, Form, FormItem, Popover } from 'ant-design-vue';
import { EnumMessage, EvalWorkloadMethod, utils, TESTER, enumUtils } from '@xcan-angus/infra';
import dayjs from 'dayjs';
import { task, project } from '@/api/tester';

import RichEditor from '@/components/richEditor/index.vue';

import { FormState } from './PropsType';
import { MeetingInfo } from '../PropsType';

type Props = {
  projectId: string;
  userInfo: { id: string; };
  appInfo: { id: string; };
  data: {
    _id: string;
    id: string | undefined;
  }
}

const props = withDefaults(defineProps<Props>(), {
  projectId: undefined,
  userInfo: undefined,
  appInfo: undefined,
  data: undefined
});

const updateTabPane = inject<(data: { [key: string]: any }) => void>('updateTabPane', () => ({}));
const deleteTabPane = inject<(keys: string[]) => void>('deleteTabPane', () => ({}));
const replaceTabPane = inject<(id: string, data: { [key: string]: any }) => void>('replaceTabPane', () => ({}));

const formRef = ref();

const evalWorkloadMethodOptions = ref<EnumMessage<EvalWorkloadMethod>[]>([]);
const dataSource = ref<MeetingInfo>();
const formState = ref<FormState>({
  content: '',
  date: '',
  location: '',
  moderator: undefined,
  participants: [],
  projectId: '',
  sprintId: undefined,
  subject: '',
  timeEnd: '',
  timeStart: '',
  type: 'DAILY_STANDUP'
});

const loading = ref(false);

const getParams = () => {
  const { content, date, timeEnd, timeStart, type, location, moderator, subject, participants, sprintId } = formState.value;
  return {
    content,
    date,
    time: `${timeStart}~${timeEnd}`,
    location,
    moderator: members.value.find(i => i.id === moderator),
    participants: participants.map(id => members.value.find(i => i.id === id)),
    sprintId,
    subject,
    type,
    id: props.data?.id || undefined,
    projectId: props.projectId
  };
  // return params;
};

const refreshList = () => {
  nextTick(() => {
    updateTabPane({ _id: 'meetingList', notify: utils.uuid() });
  });
};

const editOk = async () => {
  const params = getParams();
  loading.value = true;
  const [error] = await task.updateMeeting(params);
  loading.value = false;
  if (error) {
    return;
  }

  notification.success('修改成功');

  const id = params.id;
  const name = params.subject;
  updateTabPane({ _id: id, name });
  // 更新数据名称
  if (dataSource.value) {
    dataSource.value.subject = name;
  }
};

const addOk = async () => {
  const params = getParams();
  loading.value = true;
  const [error, res] = await task.addMeeting(params);
  loading.value = false;
  if (error) {
    return;
  }

  notification.success('添加成功');

  const _id = props.data?._id;
  const newId = res?.data?.id;
  const name = params.subject;
  replaceTabPane(_id, { _id: newId, uiKey: newId, name, data: { _id: newId, id: newId } });
};

const ok = async () => {
  formRef.value.validate().then(async () => {
    if (!editFlag.value) {
      await addOk();
    } else {
      await editOk();
    }
    refreshList();
  });
};

const cancel = () => {
  deleteTabPane([props.data._id]);
};

const loadEnums = async () => {
  evalWorkloadMethodOptions.value = enumUtils.enumToMessages(EvalWorkloadMethod);
};

const loadData = async (id: string) => {
  if (loading.value) {
    return;
  }

  loading.value = true;
  const [error, res] = await task.getMeetingDetail(id);
  loading.value = false;
  if (error) {
    return;
  }

  const data = res?.data as MeetingInfo;
  if (!data) {
    return;
  }

  dataSource.value = data;
  setFormData(data);

  const name = data.subject;
  if (name && typeof updateTabPane === 'function') {
    updateTabPane({ name, _id: id });
  }
};

const setFormData = (data: MeetingInfo) => {
  if (!data) {
    formState.value = {
      content: '',
      date: '',
      location: '',
      moderator: undefined,
      participants: [],
      projectId: '',
      sprintId: undefined,
      subject: '',
      timeEnd: '',
      timeStart: '',
      type: 'DAILY_STANDUP'
    };
    return;
  }

  const {
    type,
    date,
    time,
    moderator,
    participants
  } = data;
  const [startTime = '', endTime = ''] = (time || '').split('~');
  if (!members.value.find(item => item.id === moderator.id)) {
    members.value.push(moderator);
  }
  formState.value = {
    ...data,
    type: type?.value || type,
    date: dayjs(date),
    timeStart: dayjs(startTime),
    timeEnd: dayjs(endTime),
    moderator: moderator?.id,
    participants: (participants || []).map(i => {
      if (i.fullName && i.id && members.value.find(item => item.id === i.id)) {
        members.value.push({
          ...i
        });
      }
      return i?.id;
    }).filter(Boolean)
  };
};

const members = ref<{ fullName: string, id: string; }[]>([]);

const loadMembers = async () => {
  const [error, { data }] = await project.getProjectMember(props.projectId);
  if (error) {
    return;
  }
  members.value = (data || []).map(i => {
    return {
      ...i
    };
  });
};

onMounted(async () => {
  loadEnums();
  await loadMembers();

  watch(() => props.data, async (newValue, oldValue) => {
    const id = newValue?.id;
    if (!id) {
      return;
    }

    const oldId = oldValue?.id;
    if (id === oldId) {
      return;
    }

    // await loadPermissions(id);
    await loadData(id);
  }, { immediate: true });
});

// const validateDate = async (_rule: Rule, value: string) => {
//   if (!value) {
//     return Promise.reject(new error('请选择会议时间'));
//   } else if (!value[0]) {
//     return Promise.reject(new error('请选择会议开始时间'));
//   } else if (!value[1]) {
//     return Promise.reject(new error('请选择会议截止时间'));
//   } else {
//     return Promise.resolve();
//   }
// };

const contentRichRef = ref();
const validateDescRequired = async () => {
  if (!formState.value.content) {
    return Promise.reject(new Error('请输入会议内容'));
  }
  const values = contentRichRef.value.getData();
  if (!values) {
    return Promise.reject(new Error('请输入会议内容'));
  }
  const valuesObj = JSON.parse(values);

  if (valuesObj.length < 2) {
    if (valuesObj.length === 1) {
      const inserValue = valuesObj[0].insert.replaceAll('\n', '');
      if (!inserValue) {
        return Promise.reject(new Error('请输入会议内容'));
      }
    }
  }
  return Promise.resolve();
};

const validateTime = async () => {
  if (!formState.value.timeEnd || !formState.value.timeStart) {
    return Promise.reject(new Error('请选择会议时间'));
  }
  return Promise.resolve();
};

const editFlag = computed(() => {
  return !!props.data?.id;
});

const fieldNames = {
  label: 'fullName',
  value: 'id'
};

</script>
<template>
  <Spin :spinning="loading" class="h-full text-3 leading-5 px-5 py-5 overflow-auto">
    <div class="flex items-center space-x-2.5 mb-5">
      <Button
        type="primary"
        size="small"
        class="flex items-center space-x-1"
        @click="ok">
        <Icon icon="icon-dangqianxuanzhong" class="text-3.5" />
        <span>保存</span>
      </Button>
      <Button
        size="small"
        class="flex items-center space-x-1"
        @click="cancel">
        <span>取消</span>
      </Button>
    </div>
    <Form
      ref="formRef"
      :model="formState"
      size="small"
      :labelCol="{ style: { width: '75px' } }"
      class="max-w-242.5"
      layout="horizontal">
      <FormItem
        required
        name="subject"
        label="会议主题">
        <Input
          v-model:value="formState.subject"
          :maxlength="200"
          placeholder="最多支持200个字符" />
      </FormItem>
      <div class="flex space-x-2">
        <FormItem
          required
          label="会议类型"
          name="type"
          class="flex-1 min-w-0">
          <div class="flex items-center space-x-1">
            <SelectEnum
              v-model:value="formState.type"
              :lazy="false"
              class="flex-1 min-w-0"
              enumKey="TaskMeetingType" />
            <Popover placement="right" content="指定会议类型，以便识别和管理。">
              <Icon icon="icon-tishi1" class="text-tips text-3.5 cursor-pointer" />
            </Popover>
          </div>
        </FormItem>
        <FormItem
          label="迭代"
          class="flex-1 min-w-0"
          name="sprintId">
          <div class="flex items-center space-x-1">
            <Select
              v-model:value="formState.sprintId"
              placeholder="选择所属迭代"
              :fieldNames="{
                value: 'id',
                label: 'name'
              }"
              :action="`${TESTER}/task/sprint?projectId=${props.projectId}&fullTextSearch=true`">
            </Select>
            <!-- <Icon icon="" class="text-tips text-3.5 cursor-pointer" /> -->
          </div>
        </FormItem>
      </div>
      <div class="flex space-x-2">
        <FormItem
          required
          label="会议日期"
          class="flex-1 min-w-0"
          name="date">
          <div class="flex items-center space-x-1">
            <DatePicker
              v-model:value="formState.date"
              format="YYYY-MM-DD"
              showToday
              class="flex-1 min-w-0" />
            <Icon icon="" class="text-tips text-3.5 cursor-pointer" />
          </div>
        </FormItem>
        <FormItem
          label="会议时间"
          class="flex-1 min-w-0"
          name="time"
          :rules="{validator: validateTime, message: '请选择会议时间', required: true}">
          <div class="w-full flex items-center space-x-1">
            <DatePicker
              v-model:value="formState.timeStart"
              mode="time"
              picker="time"
              placeholder="开始时间"
              class="flex-1" />
            <span>-</span>
            <DatePicker
              v-model:value="formState.timeEnd"
              mode="time"
              picker="time"
              placeholder="结束时间"
              class="flex-1" />
              <!-- <Icon icon="" class="text-tips text-3.5 cursor-pointer" /> -->
          </div>
        </FormItem>
      </div>
      <div class="flex space-x-2">
        <FormItem label="会议地点" class="flex-1 min-w-0">
          <div class="flex items-center space-x-1">
            <Input
              v-model:value="formState.location"
              placeholder="记录会议地点或在线会议的链接，最多支持100个字符"
              :maxlength="100"
              class="flex-1 min-w-0" />
            <Icon icon="" class="text-tips text-3.5 cursor-pointer" />
          </div>
        </FormItem>
        <FormItem
          required
          label="会议主持"
          class="flex-1 min-w-0"
          name="moderator">
          <div class="flex items-center space-x-1">
            <Select
              v-model:value="formState.moderator"
              placeholder="选择会议主持人"
              :options="members"
              :fieldNames="fieldNames"
              class="flex-1 min-w-0" />
            <!-- <Icon icon="" class="text-tips text-3.5 cursor-pointer" /> -->
          </div>
        </FormItem>
      </div>
      <FormItem
        required
        label="参会人员"
        class="min-w-0"
        name="participants">
        <div class="flex items-center space-x-1">
          <Select
            v-model:value="formState.participants"
            placeholder="选择参会人员"
            :maxTags="200"
            :options="members"
            :fieldNames="fieldNames"
            mode="multiple"
            class="flex-1 min-w-0" />
          <!-- <Icon icon="" class="text-tips text-3.5 cursor-pointer" /> -->
        </div>
      </FormItem>
      <FormItem
        label="会议内容"
        class="flex-1 !mb-5"
        name="content"
        :rules="{required: true, validator: validateDescRequired}">
        <RichEditor
          ref="contentRichRef"
          v-model:value="formState.content"
          class="review-description" />
      </FormItem>
    </Form>
  </Spin>
</template>

<style scoped>
:deep(.ant-form-item-label>label::after) {
  margin-right: 10px;
}

.ant-tabs-small>:deep(.ant-tabs-nav) .ant-tabs-tab {
  padding-top: 0;
}
</style>
