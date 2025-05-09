<script lang="ts" setup>
import { defineAsyncComponent } from 'vue';

type Props = {
  execInfo: Record<string, any>;
  delayInSeconds:number;
  apiDimensionObj: {[key:string]:{ [key:string]: string[]}};
  indexDimensionObj: {[key:string]:{ [key:string]: string[]}};
  apiNames:string[];
  timestampData:string[];
  isLoaded:boolean;
  brpsUnit:'KB' | 'MB';
  minBrpsUnit: 'KB' | 'MB';
  maxBrpsUnit:'KB' | 'MB';
  meanBrpsUnit:'KB' | 'MB';
  bwpsUnit:'KB' | 'MB';
  minBwpsUnit: 'KB' | 'MB';
  maxBwpsUnit:'KB' | 'MB';
  meanBwpsUnit:'KB' | 'MB';
  statusCodeData:Record<string, any>;
  errCountList: Record<string, any>[];
  sampleList: Record<string, any>[];
  exception?:{
    codeName:string;
    messageName:string;
    code:string;
    message:string;
  };
  infoMaxQps: number | string;
  infoMaxTps: number | string;
}

const props = withDefaults(defineProps<Props>(), {
  delayInSeconds: 3000,
  exception: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'loadingChange', value: boolean): void;
}>();

const Throughput = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/throughput/index.vue'));
const Concurrency = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/concurrency/index.vue'));
const ResponseTime = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/responseTime/index.vue'));
const Error = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/error/index.vue'));
const StatusCode = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/statusCode/index.vue'));
const NodeResource = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/nodeResource/index.vue'));
const OverlayAnalysis = defineAsyncComponent(() => import('@/views/report/preview/execPerf/sampling/perf/testDetail/overlayAnalysis/index.vue'));

const loadingChange = (value:boolean) => {
  emit('loadingChange', value);
};
</script>
<template>
  <div>
    <div class="mb-7">
      <h2 class="text-3.5 mb-3.5 text-theme-title">
        <span id="a6">3.1<em class="inline-block w-3.5"></em>吞吐量</span>
      </h2>
      <Throughput
        v-if="timestampData.length > 0"
        :dataMap="apiDimensionObj"
        :timestampList="timestampData"
        :names="apiNames" />
      <div v-else class="content-text-container">无</div>
    </div>

    <div class="mb-7">
      <h2 class="text-3.5 mb-3.5 text-theme-title">
        <span id="a7">3.2<em class="inline-block w-3.5"></em>并发数</span>
      </h2>
      <Concurrency
        v-if="timestampData.length > 0"
        :dataMap="apiDimensionObj"
        :timestampList="timestampData"
        :names="apiNames" />
      <div v-else class="content-text-container">无</div>
    </div>

    <div class="mb-7">
      <h2 class="text-3.5 mb-3.5 text-theme-title">
        <span id="a8">3.3<em class="inline-block w-3.5"></em>响应时间</span>
      </h2>
      <ResponseTime
        v-if="timestampData.length > 0"
        :dataMap="apiDimensionObj"
        :timestampList="timestampData"
        :names="apiNames" />
      <div v-else class="content-text-container">无</div>
    </div>

    <div class="mb-7">
      <h2 class="text-3.5 mb-3.5 text-theme-title">
        <span id="a9">3.4<em class="inline-block w-3.5"></em>错误</span>
      </h2>
      <Error
        v-if="timestampData.length > 0"
        :singleHttp="props.execInfo?.singleTargetPipeline"
        :dataMap="apiDimensionObj"
        :timestampList="timestampData"
        :names="apiNames" />
      <div v-else class="content-text-container">无</div>
    </div>
    <template v-if="props.execInfo?.plugin === 'Http'">
      <div class="mb-7">
        <h2 class="text-3.5 mb-3.5 text-theme-title">
          <span id="a10">3.5<em class="inline-block w-3.5"></em>状态码</span>
        </h2>
        <StatusCode
          v-if="timestampData.length > 0"
          :statusCodeData="props.statusCodeData" />
        <div v-else class="content-text-container">无</div>
      </div>

      <div class="mb-7">
        <h2 class="text-3.5 mb-3.5 text-theme-title">
          <span id="a11">3.6<em class="inline-block w-3.5"></em>节点资源</span>
        </h2>
        <NodeResource
          :delayInSeconds="props.delayInSeconds"
          :reportInterval="props.execInfo?.reportInterval"
          :startTime="props.execInfo?.actualStartDate || props.execInfo?.createdDate"
          :endTime="props.execInfo?.endDate"
          :appNodes="props.execInfo?.appNodes"
          :execNodes="props.execInfo?.execNodes"
          :status="props.execInfo?.status.value"
          @loadingChange="loadingChange" />
      </div>

      <div>
        <h2 class="text-3.5 mb-3.5 text-theme-title">
          <span id="a12">3.7<em class="inline-block w-3.5"></em>叠加分析</span>
        </h2>
        <OverlayAnalysis
          v-if="timestampData.length > 0"
          :dataMap="apiDimensionObj"
          :timestampList="timestampData"
          :names="apiNames" />
        <div v-else class="content-text-container">无</div>
      </div>
    </template>

    <!-- <div class="mb-7">
      <h2 class="flex items-center space-x-2.5 text-3.5 mb-3.5 text-theme-title">
        <span id="a10">3.5<em class="inline-block w-3.5"></em>状态码</span>
      </h2>
      <statusCode
        v-if="timestampData.length > 0"
        :statusCodeData="props.statusCodeData" />
      <div v-else class="content-text-container">无</div>
    </div> -->

    <template v-else>
      <div class="mb-7">
        <h2 class="text-3.5 mb-3.5 text-theme-title">
          <span id="a11">3.5<em class="inline-block w-3.5"></em>节点资源</span>
        </h2>
        <NodeResource
          :delayInSeconds="props.delayInSeconds"
          :reportInterval="props.execInfo?.reportInterval"
          :startTime="props.execInfo?.actualStartDate || props.execInfo?.createdDate"
          :endTime="props.execInfo?.endDate"
          :appNodes="props.execInfo?.appNodes"
          :execNodes="props.execInfo?.execNodes"
          :status="props.execInfo?.status.value"
          @loadingChange="loadingChange" />
      </div>

      <div>
        <h2 class="text-3.5 mb-3.5 text-theme-title">
          <span id="a12">3.6<em class="inline-block w-3.5"></em>叠加分析</span>
        </h2>
        <OverlayAnalysis
          v-if="timestampData.length > 0"
          :dataMap="apiDimensionObj"
          :timestampList="timestampData"
          :names="apiNames" />
        <div v-else class="content-text-container">无</div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.content-text-container{
  text-indent: 2em;
}
</style>
