import { inject, onMounted, ref, watch } from 'vue';
import { utils } from '@xcan-angus/infra';
import * as echarts from 'echarts/core';
import { useI18n } from 'vue-i18n';

import { ResourceInfo, ChartDataItem, PieChartOption } from '../types';
import { useChartConfig } from './useChartConfig';
import { useECharts } from './useECharts';

/**
 * Composable for managing pie chart functionality
 * Handles chart initialization, data updates, and resize events
 */
export function usePieChart(dataSource: ResourceInfo) {
  const { t } = useI18n();
  const { createPieChartOption, transformResourceToChartData } = useChartConfig();
  const { initializeECharts, createChartInstance, setChartOptions, resizeChart } = useECharts();
  
  // Window resize notification from parent component
  const windowResizeNotify = inject('windowResizeNotify', ref<string>());
  
  // Chart container reference
  const containerRef = ref<HTMLElement>();
  
  // Unique DOM ID for chart container
  const domId = utils.uuid('pie');
  
  // ECharts instance
  let echartInstance: echarts.ECharts | null = null;
  
  // Chart configuration options
  const chartOption: PieChartOption = createPieChartOption();

  /**
   * Initialize ECharts library and create chart instance
   */
  const initializeChart = () => {
    initializeECharts();
    echartInstance = createChartInstance(document.getElementById(domId));
    if (echartInstance) {
      setChartOptions(echartInstance, chartOption);
    }
  };

  /**
   * Update chart data based on resource information
   * @param resourceInfo - Resource information containing script counts
   */
  const updateChartData = (resourceInfo: ResourceInfo) => {
    if (!resourceInfo) return;

    // Transform resource data to chart data
    const chartData = transformResourceToChartData(resourceInfo);
    chartOption.series![0].data = chartData;
  };

  /**
   * Render or update the chart with current data
   */
  const renderChart = () => {
    if (!echartInstance) {
      initializeChart();
      return;
    }
    
    // Update chart with new data
    setChartOptions(echartInstance, chartOption);
  };

  /**
   * Handle window resize events
   */
  const handleResize = () => {
    resizeChart(echartInstance);
  };

  /**
   * Watch for data source changes and update chart
   */
  const watchDataSource = () => {
    watch(() => dataSource, (newValue) => {
      if (!newValue) return;
      
      updateChartData(newValue);
      renderChart();
    }, { immediate: true });
  };

  /**
   * Watch for window resize notifications
   */
  const watchResizeEvents = () => {
    watch(() => windowResizeNotify.value, (newValue) => {
      if (!newValue) return;
      
      handleResize();
    }, { immediate: true });
  };

  /**
   * Initialize chart functionality
   */
  const initialize = () => {
    watchDataSource();
    watchResizeEvents();
  };

  return {
    containerRef,
    domId,
    chartOption,
    initialize,
    renderChart,
    handleResize
  };
}
