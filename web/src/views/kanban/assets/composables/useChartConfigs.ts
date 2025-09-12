// import { useI18n } from 'vue-i18n';
import { i18n } from '@xcan-angus/infra';

const I18nInstance = i18n.getI18n();
const t = I18nInstance?.global?.t || ((value: string): string => value);

import { ChartConfig, ChartSeriesColorConfig, MethodColorConfig, TargetDataCategory, RankIconConfig } from '../types';

// const { t } = useI18n();

/**
 * <p>
 * Chart configuration management composable for data assets dashboard
 * </p>
 * <p>
 * Provides factory functions for creating various chart configurations
 * </p>
 *
 * @returns Object containing chart configuration factory functions
 */
export function useChartConfigs () {
  /** Chart series color configuration */
  const chartSeriesColorConfig: ChartSeriesColorConfig = {
    0: '84,112,198',
    1: '145,204,117',
    2: '250,200,88',
    3: '238,102,102',
    4: '115,192,222',
    5: '59,162,114',
    6: '252,132,82',
    7: '154,96,180',
    8: '234,124,204'
  };

  /** HTTP method color configuration */
  const methodColorConfig: MethodColorConfig = {
    GET: 'rgba(30, 136, 229, 1)',
    HEAD: '#67D7FF',
    POST: 'rgba(51, 183, 130, 1)',
    PUT: 'rgba(255, 167, 38, 1)',
    PATCH: 'rgba(171, 71, 188, 1)',
    DELETE: 'rgba(255, 82, 82, 1)',
    OPTIONS: 'rgba(0, 150, 136, 1)',
    TRACE: '#7F91FF'
  };

  /** Target data category mapping */
  const targetDataCategory: TargetDataCategory = {
    TEST_CUSTOMIZATION: t('kanban.dataAssets.categories.testCustomization'),
    TEST_FUNCTIONALITY: t('kanban.dataAssets.categories.testFunctionality'),
    TEST_PERFORMANCE: t('kanban.dataAssets.categories.testPerformance'),
    TEST_STABILITY: t('kanban.dataAssets.categories.testStability'),
    SERVICES: t('kanban.dataAssets.categories.services'),
    APIS: t('kanban.dataAssets.categories.apis'),
    CASES: t('kanban.dataAssets.categories.cases'),
    PLAN: t('kanban.dataAssets.categories.plan'),
    SPRINT: t('kanban.dataAssets.categories.sprint'),
    TASK_SPRINT: t('kanban.dataAssets.categories.taskSprint'),
    TASK: t('kanban.dataAssets.categories.task'),
    MOCK_APIS: t('kanban.dataAssets.categories.mockApis'),
    MOCK_PUSHBACK: t('kanban.dataAssets.categories.mockPushback'),
    MOCK_RESPONSE: t('kanban.dataAssets.categories.mockResponse'),
    MOCK_SERVICE: t('kanban.dataAssets.categories.mockService'),
    DATA_DATASET: t('kanban.dataAssets.categories.dataDataset'),
    DATA_DATASOURCE: t('kanban.dataAssets.categories.dataDatasource'),
    DATA_VARIABLE: t('kanban.dataAssets.categories.dataVariable'),
    TOTAL: t('kanban.dataAssets.categories.total'),
    REPORT: t('kanban.dataAssets.categories.report'),
    REPORT_RECORD: t('kanban.dataAssets.categories.reportRecord')
  };

  /** Rank icon configuration */
  const rankIconConfig: RankIconConfig = {
    0: 'icon-diyiming',
    1: 'icon-dierming',
    2: 'icon-disanming'
  };

  /**
   * <p>
   * Creates growth trend chart configuration
   * </p>
   * <p>
   * Base configuration for line charts showing growth trends over time
   * </p>
   */
  const createGrowthTrendConfig = (): ChartConfig => ({
    grid: {
      left: '5%',
      right: '20',
      bottom: '60',
      top: 20
    },
    legend: {
      show: true,
      bottom: 0,
      type: 'plain'
    },
    tooltip: {
      show: true,
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value',
      min: function (value: any) {
        if (value.max < 1) {
          return 10;
        } else {
          return undefined;
        }
      }
    },
    series: [
      {
        name: 'api',
        data: [],
        type: 'line',
        smooth: true
      }
    ]
  });

  /**
   * <p>
   * Creates case bar chart configuration
   * </p>
   * <p>
   * Configuration for horizontal bar chart showing case status distribution
   * </p>
   */
  const createCaseBarConfig = (): ChartConfig => ({
    title: {
      text: '',
      textStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {},
    grid: {
      top: '8%',
      left: '3%',
      right: '6%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      splitLine: { show: false }
    },
    yAxis: {
      type: 'category',
      axisTick: { show: false },
      splitLine: { show: false },
      axisLine: { show: false },
      data: [
        t('kanban.dataAssets.status.cancelled'),
        t('kanban.dataAssets.status.blocked'),
        t('kanban.dataAssets.status.testFailed'),
        t('kanban.dataAssets.status.testPassed'),
        t('kanban.dataAssets.status.pendingTest')
      ]
    },
    series: [
      {
        type: 'bar',
        showBackground: true,
        barMaxWidth: '16',
        data: [
          {
            value: 0,
            itemStyle: { color: 'rgba(217, 217, 217, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: 'rgba(255, 165, 43, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: 'rgba(245, 34, 45, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: 'rgba(45, 142, 255, 1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates case pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing case review status distribution
   * </p>
   */
  const createCasePieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: t('kanban.dataAssets.total'),
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14
    },
    series: [
      {
        name: t('kanban.dataAssets.total'),
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        itemStyle: {
          borderRadius: [5],
          color: 'rgba(136, 185, 242, 1)'
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        data: [
          {
            name: t('kanban.dataAssets.reviewStatus.pendingReview'),
            value: 0,
            itemStyle: { color: 'rgba(201, 119, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.reviewStatus.reviewPassed'),
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          },
          {
            name: t('kanban.dataAssets.reviewStatus.reviewFailed'),
            value: 0,
            itemStyle: { color: 'rgba(245, 34, 45, 1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates API bar chart configuration
   * </p>
   * <p>
   * Configuration for horizontal bar chart showing API status distribution
   * </p>
   */
  const createApiBarConfig = (): ChartConfig => ({
    title: {
      text: '',
      textStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {},
    grid: {
      top: '8%',
      left: '3%',
      right: '6%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      splitLine: { show: false }
    },
    yAxis: {
      type: 'category',
      axisTick: { show: false },
      splitLine: { show: false },
      axisLine: { show: false },
      data: [
        t('kanban.dataAssets.status.unknown'),
        t('kanban.dataAssets.status.designing'),
        t('kanban.dataAssets.status.developing'),
        t('kanban.dataAssets.status.developmentCompleted'),
        t('kanban.dataAssets.status.published')
      ]
    },
    series: [
      {
        type: 'bar',
        showBackground: true,
        barMaxWidth: '16',
        data: [
          {
            value: 0,
            itemStyle: { color: 'rgba(200, 202, 208, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: '#52C41A' }
          },
          {
            value: 0,
            itemStyle: { color: '#FFB925' }
          },
          {
            value: 0,
            itemStyle: { color: '#FF8100' }
          },
          {
            value: 0,
            itemStyle: { color: '#7F91FF' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates API pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing API method distribution
   * </p>
   */
  const createApiPieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: '总数',
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14,
      itemGap: 14
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        itemStyle: {
          borderRadius: [5],
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        data: [
          {
            name: 'PUT',
            value: 0,
            itemStyle: { color: methodColorConfig.PUT }
          },
          {
            name: 'POST',
            value: 0,
            itemStyle: { color: methodColorConfig.POST }
          },
          {
            name: 'HEAD',
            value: 0,
            itemStyle: { color: methodColorConfig.HEAD }
          },
          {
            name: 'GET',
            value: 0,
            itemStyle: { color: methodColorConfig.GET }
          },
          {
            name: 'DELETE',
            value: 0,
            itemStyle: { color: methodColorConfig.DELETE }
          },
          {
            name: 'PATCH',
            value: 0,
            itemStyle: { color: methodColorConfig.PATCH }
          },
          {
            name: 'OPTIONS',
            value: 0,
            itemStyle: { color: methodColorConfig.OPTIONS }
          },
          {
            name: 'TRACE',
            value: 0,
            itemStyle: { color: methodColorConfig.TRACE }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates task bar chart configuration
   * </p>
   * <p>
   * Configuration for horizontal bar chart showing task status distribution
   * </p>
   */
  const createTaskBarConfig = (): ChartConfig => ({
    title: {
      text: '',
      textStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {},
    grid: {
      top: '8%',
      left: '3%',
      right: '6%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      splitLine: { show: false }
    },
    yAxis: {
      type: 'category',
      axisTick: { show: false },
      splitLine: { show: false },
      axisLine: { show: false },
      data: [
        t('kanban.dataAssets.status.cancelled'),
        t('kanban.dataAssets.status.completed'),
        t('kanban.dataAssets.status.pendingConfirmation'),
        t('kanban.dataAssets.status.inProgress'),
        t('kanban.dataAssets.status.pending')
      ]
    },
    series: [
      {
        type: 'bar',
        showBackground: true,
        barMaxWidth: '16',
        data: [
          {
            value: 0,
            itemStyle: { color: 'rgba(200, 202, 208, 1)' }
          },
          {
            value: 0,
            itemStyle: { color: '#52C41A' }
          },
          {
            value: 0,
            itemStyle: { color: '#FFB925' }
          },
          {
            value: 0,
            itemStyle: { color: '#FF8100' }
          },
          {
            value: 0,
            itemStyle: { color: '#7F91FF' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates task pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing task type distribution
   * </p>
   */
  const createTaskPieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: t('kanban.dataAssets.total'),
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14,
      itemGap: 6
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        itemStyle: {
          borderRadius: [5],
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        data: [
          {
            name: t('kanban.dataAssets.taskTypes.story'),
            value: 0,
            itemStyle: { color: 'rgba(136, 185, 242, 1)' }
          },
          {
            name: t('kanban.dataAssets.taskTypes.requirement'),
            value: 0,
            itemStyle: { color: 'rgba(201, 119, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.taskTypes.task'),
            value: 0,
            itemStyle: { color: 'rgba(255, 165, 43, 1)' }
          },
          {
            name: t('kanban.dataAssets.taskTypes.defect'),
            value: 0,
            itemStyle: { color: 'rgba(245, 34, 45, 1)' }
          },
          {
            name: t('kanban.dataAssets.taskTypes.apiTest'),
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          },
          {
            name: t('kanban.dataAssets.taskTypes.scenarioTest'),
            value: 0,
            itemStyle: { color: 'rgba(0,119,255,1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates plan pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing plan status distribution
   * </p>
   */
  const createPlanPieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: t('kanban.dataAssets.total'),
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14,
      itemGap: 12
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        itemStyle: {
          borderRadius: [5],
          borderColor: '#fff',
          borderWidth: 1
        },
        data: [
          {
            name: t('kanban.dataAssets.status.pendingStart'),
            value: 0,
            itemStyle: { color: 'rgba(45, 142, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.inProgress'),
            value: 0,
            itemStyle: { color: 'rgba(103, 215, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.completed'),
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.blockedStatus'),
            value: 0,
            itemStyle: { color: 'rgba(245, 34, 45, 1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates sprint pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing sprint status distribution
   * </p>
   */
  const createSprintPieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: t('kanban.dataAssets.total'),
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14,
      itemGap: 12
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        itemStyle: {
          borderRadius: [5],
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        data: [
          {
            name: t('kanban.dataAssets.status.pendingStart'),
            value: 0,
            itemStyle: { color: 'rgba(45, 142, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.inProgress'),
            value: 0,
            itemStyle: { color: 'rgba(103, 215, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.completed'),
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          },
          {
            name: t('kanban.dataAssets.status.blockedStatus'),
            value: 0,
            itemStyle: { color: 'rgba(245, 34, 45, 1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates scenario pie chart configuration
   * </p>
   * <p>
   * Configuration for pie chart showing scenario script type distribution
   * </p>
   */
  const createScenarioPieConfig = (): ChartConfig => ({
    title: {
      text: 0,
      subtext: t('kanban.dataAssets.total'),
      left: '29.5%',
      top: '35%',
      padding: 2,
      textAlign: 'center',
      textStyle: {
        fontSize: 14,
        width: 120,
        fontWeight: 'bolder'
      },
      subtextStyle: {
        fontSize: 12
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: 'middle',
      left: '65%',
      orient: 'vertical',
      itemHeight: 14,
      itemWidth: 14,
      itemGap: 12
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        label: {
          show: true,
          formatter: '{c}'
        },
        itemStyle: {
          borderRadius: [5],
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            show: true
          }
        },
        labelLine: {
          show: true
        },
        data: [
          {
            name: t('kanban.dataAssets.categories.testPerformance'),
            value: 0,
            itemStyle: { color: 'rgba(45, 142, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.categories.testStability'),
            value: 0,
            itemStyle: { color: 'rgba(201, 119, 255, 1)' }
          },
          {
            name: t('kanban.dataAssets.categories.testFunctionality'),
            value: 0,
            itemStyle: { color: 'rgba(255, 102, 0, 1)' }
          },
          {
            name: t('kanban.dataAssets.categories.testCustomization'),
            value: 0,
            itemStyle: { color: 'rgba(82, 196, 26, 1)' }
          }
        ]
      }
    ]
  });

  /**
   * <p>
   * Creates script bar chart configuration
   * </p>
   * <p>
   * Configuration for vertical bar chart showing script distribution by plugin
   * </p>
   */
  const createScriptBarConfig = (): ChartConfig => ({
    grid: {
      left: '60',
      right: '30',
      bottom: '60',
      top: '20'
    },
    xAxis: {
      type: 'category',
      data: ['http', 'websocket', 'jdbc', 'tcp'],
      axisLabel: {
        interval: 0,
        rotate: -45,
        overflow: 'break'
      }
    },
    yAxis: {
      type: 'value'
    },
    tooltip: {
      show: true
    },
    series: [
      {
        itemStyle: {
          color: 'rgba(45, 142, 255, 1)',
          borderRadius: [5, 5, 0, 0]
        },
        data: [0, 0, 0, 0],
        type: 'bar',
        barMaxWidth: '20',
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  });

  return {
    // Configuration objects
    chartSeriesColorConfig,
    methodColorConfig,
    targetDataCategory,
    rankIconConfig,

    // Factory functions
    createGrowthTrendConfig,
    createCaseBarConfig,
    createCasePieConfig,
    createApiBarConfig,
    createApiPieConfig,
    createTaskBarConfig,
    createTaskPieConfig,
    createPlanPieConfig,
    createSprintPieConfig,
    createScenarioPieConfig,
    createScriptBarConfig
  };
}
