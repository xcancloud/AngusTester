<script setup lang="ts">
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';

// eslint-disable-next-line import/no-absolute-path
// eslint-disable-next-line import/no-absolute-path
import { MockXml } from '@/plugins/gendata/xmlIndex';

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

const userInfo = inject('tenantInfo', ref());
const router = useRouter();

const onFormatChange = (data) => {
  emit('formatChange', data);
};

const cancel = () => {
  router.push('/data');
};
</script>
<template>
  <MockXml
    class="px-5 py-2 h-full overflow-y-auto"
    :params="props.params"
    :userInfo="userInfo"
    :cancel="cancel"
    @formatChange="onFormatChange" />
</template>
