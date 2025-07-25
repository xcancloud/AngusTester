<script lang="ts" setup>
import { defineAsyncComponent, onMounted, ref, watch } from 'vue';
import { ExecInfo, ReportInfo } from '../../PropsType';
import { exec } from 'src/api/ctrl';

const Performance = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/index.vue'));

type Props = {
  projectInfo: { [key: string]: any };
  userInfo: { [key: string]: any };
  appInfo: { [key: string]: any };
  reportInfo: ReportInfo;
  execInfo: ExecInfo;
}

const props = withDefaults(defineProps<Props>(), {
  projectInfo: undefined,
  userInfo: undefined,
  appInfo: undefined,
  reportInfo: undefined,
  execInfo: undefined
});

const loading = ref(false);
const detail = ref();
const exception = ref<{ [key: string]: any }>();

const emit = defineEmits<{(e: 'change', value: { [key: string]: any }): void }>();

const getDetail = async () => {
  loading.value = true;
  const [error, { data }] = await exec.getExecDetail(props.execInfo.id);

  if (error) {
    loading.value = false;
    return;
  }

  detail.value = data;

  if (detail.value?.scriptType.value === 'MOCK_DATA') {
    detail.value.batchRows = detail.value.task?.mockData?.settings.batchRows || '1';
  }
};

const getInfo = (data) => {
  if (!data) {
    return;
  }
  const keys = Object.keys(data);
  if (!keys.length || !detail.value) {
    return;
  }

  for (const key in data) {
    detail.value[key] = data[key];
  }

  setException();
};

const setException = () => {
  const lastSchedulingResult = detail.value?.lastSchedulingResult;
  const meterMessage = detail.value?.meterMessage;
  if (lastSchedulingResult?.length) {
    const foundItem = lastSchedulingResult.find(f => !f.success);
    if (foundItem) {
      exception.value = {
        code: foundItem.exitCode,
        message: foundItem.message,
        codeName: '退出码',
        messageName: '失败原因'
      };
      return;
    }

    if (meterMessage) {
      exception.value = {
        code: detail.value?.meterStatus || '',
        message: meterMessage,
        codeName: '采样状态',
        messageName: '失败原因'
      };
      return;
    }
  }

  if (meterMessage) {
    exception.value = {
      code: detail.value?.meterStatus || '',
      message: meterMessage,
      codeName: '采样状态',
      messageName: '失败原因'
    };
    return;
  }

  exception.value = undefined;
};

onMounted(() => {
  watch(() => props.execInfo, async () => {
    if (props.execInfo) {
      await getDetail();
      setException();
    }
  }, {
    immediate: true
  });
});
</script>
<template>
  <div>
    <h1 class="text-theme-title font-medium mb-5">
      <span id="a5" class="text-4 text-theme-title font-medium">三、<em class="inline-block w-0.25"></em>采样指标</span>
    </h1>
    <Performance
      v-if="detail"
      :detail="detail"
      :exception="exception"
      @loaded="getInfo"
      @change="emit('change', $event)" />
  </div>
</template>
