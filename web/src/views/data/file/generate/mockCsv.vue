<script setup lang="ts">
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';
import { appContext } from '@xcan-angus/infra';

// eslint-disable-next-line import/no-absolute-path
// eslint-disable-next-line import/no-absolute-path
import { MockCsv } from '@/plugins/gendata/csvIndex';

interface Props {
    params:{[key:string]:any};
}

const props = withDefaults(defineProps<Props>(), {
  params: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
    (e:'formatChange', value:{[key:string]:any}):void;
}>();

const userInfo = ref(appContext.getUser());
const router = useRouter();

const onFormatChange = (data) => {
  emit('formatChange', data);
};

const cancel = () => {
  router.push('/data');
};
</script>
<template>
  <MockCsv
    class="px-5 py-2 h-full overflow-y-auto"
    :params="props.params"
    :userInfo="userInfo"
    :cancel="cancel"
    @formatChange="onFormatChange" />
</template>
