<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { Icon } from '@xcan-angus/vue-ui';

import { ResourceInfo } from '../../PropsType';

type Props = {
  dataSource: ResourceInfo;
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined
});

const total = ref<string>('0');
const overduetTtal = ref<string>('0');
const pendingTotal = ref<string>('0');
const passedTotal = ref<string>('0');
const noPassedTotal = ref<string>('0');
const blockedTotal = ref<string>('0');
const canceledTotal = ref<string>('0');

onMounted(() => {
  watch(() => props.dataSource, (newValue) => {
    if (newValue === undefined) {
      return;
    }

    total.value = newValue.allCase;
    overduetTtal.value = newValue.caseByOverdue;
    pendingTotal.value = newValue.caseByTestResult?.PENDING;
    passedTotal.value = newValue.caseByTestResult?.PASSED;
    noPassedTotal.value = newValue.caseByTestResult?.NOT_PASSED;
    blockedTotal.value = newValue.caseByTestResult?.BLOCKED;
    canceledTotal.value = newValue.caseByTestResult?.CANCELED;
  }, { immediate: true });
});
</script>

<template>
  <div class="rounded border border-solid border-theme-text-box pt-3.5 pb-2">
    <div class="font-semibold mb-2 px-4">
      <span class="mr-2">总用例</span>
      <span class="text-4">{{ total }}</span>
    </div>

    <div class="flex-1 flex items-center justify-center flex-wrap content-center transform-gpu translate-y-2">
      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-daiceshi" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">待测试<span class="placeholder nth-1"></span></div>
          <div class="text-4">{{ pendingTotal }}</div>
        </div>
      </div>

      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-daiqueren" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">测试通过<span class="placeholder nth-2"></span></div>
          <div class="text-4">{{ passedTotal }}</div>
        </div>
      </div>

      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-yiyuqi1" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">逾期<span class="placeholder nth-3"></span></div>
          <div class="text-4">{{ overduetTtal }}</div>
        </div>
      </div>

      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-tianjiaxuqiu" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">测试未通过<span class="placeholder nth-4"></span></div>
          <div class="text-4">{{ noPassedTotal }}</div>
        </div>
      </div>

      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-zusaizhong" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">阻塞<span class="placeholder nth-5"></span></div>
          <div class="text-4">{{ blockedTotal }}</div>
        </div>
      </div>

      <div class="item-container flex items-center space-x-2.5 justify-center mb-4">
        <Icon icon="icon-yiquxiao" class="text-10 flex-shrink-0" />
        <div class="whitespace-nowrap space-y-1">
          <div class="text-theme-sub-content">已忽略<span class="placeholder nth-6"></span></div>
          <div class="text-4">{{ canceledTotal }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.placeholder {
  display: inline-block;
}

@media screen and (max-width: 1450px) {
  .item-container {
    width: 50%;
  }

  .placeholder.nth-2,
  .placeholder.nth-3,
  .placeholder.nth-5 {
    width: 1em;
  }

  .placeholder.nth-6 {
    width: 2em;
  }
}

@media screen and (min-width: 1451px) {
  .item-container {
    width: 33.33%;
  }

  .placeholder.nth-3 {
    width: 1em;
  }

  .placeholder.nth-1,
  .placeholder.nth-5 {
    width: 2em;
  }
}
</style>
