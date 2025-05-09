export default {
  execute: '执行',
  name: '名称',
  type: '类型',
  scenario: '脚本',
  configure: '配置',
  executParam: '执行参数',
  advancedParam: '高级参数',
  httpParam: 'HTTP参数',
  performanceTest: '性能测试',
  automation: '接口自动化',
  status: '状态',
  protocol: '协议',
  founder: '添加人',
  executionType: '脚本类型',
  creationDate: '添加时间',
  numbering: '编号',
  concurrency: '并发数',
  startingDate: '开始时间',
  endDate: '结束时间',
  executionTime: '执行时长（秒）',
  iterations: '迭代次数',
  errorRate: '错误率',
  scriptName: '脚本名称',
  priority: '优先级',
  executionWay: '执行方式',
  overtimeTime: '超时时间',
  terminateExecution: '终止执行',
  again: '重新执行',
  failure: '失败原因',
  viewReport: '查看报告',
  second: '秒',
  minute: '分钟',
  hour: '小时',
  day: '天',
  millisecond: '毫秒',
  createExecution: '添加执行',
  info: '执行详情',
  config: '执行配置',
  placeholder: {
    ps: '请选择',
    p1: '查询执行名称',
    p2: '请输入名称',
    p3: '选择脚本',
    p4: '选择项目/服务',
    p5: '查询接口名称'
  },
  timeType: {
    t1: '时间 ( 毫秒：ms )',
    t2: '时间 ( 秒：s )',
    t3: '时间 ( 分钟：m )',
    t4: '时间 ( 小时：h )',
    t5: '时间 ( 天：d )'
  },
  yName: '执\n行\n并\n发\n数',
  mh: '：',
  dh: '，',
  pageShowTotal: '共{total}条记录,第{pageNo} / {totalPage}页',
  infoCard: {
    c1: '并发数(VU)',
    c2: '每秒查询数 (QPS)',
    c3: '每秒事务数 (TPS)',
    c4: '平均响应时间(AVG RT)',
    c5: '错误率',
    c6: '已执行时间',
    c7: '已迭代次数',
    c8: '百分位P90',
    c9: '上传',
    c10: '下载'
  },
  leftTabs: {
    t1: '响应时间 (RT)',
    t2: '吞吐量 (TPS/QPS)',
    t3: '并发数 (用户)',
    t4: '节点资源',
    t5: '错误 (ERROR)',
    t6: '汇总结果',
    t7: '当前',
    t8: '当前TPS',
    t9: '当前QPS',
    t10: 'CPU',
    t11: '内存',
    t12: '磁盘',
    t13: '错误',
    t14: '总请求',
    t15: '网络'
  },
  columns: {
    c1: '平均/ms',
    c2: '最小/ms',
    c3: '最大/ms',
    c4: 'P50',
    c5: 'P90',
    c6: 'P95',
    c7: 'P99',
    c8: '最大',
    c9: '最小'
  },
  errAsk: '错误请求',
  errNum: '错误数',
  errInfo: '错误信息',
  check: '查看',
  aggregateResults: '聚合结果',
  resColumns: {
    c1: 'URL/脚本总体',
    c2: '平均响应',
    c3: '最小',
    c4: '最大',
    c5: '上传'
  },
  params: {
    p1: '并发线程数 (用户) ',
    p2: '迭代次数',
    p3: '执行时长',
    p4: '初始增压',
    p5: '结束减压',
    p6: '测试机选择策略',
    p7: '测试机数量',
    p8: '脚本名称'
  },
  basicInfo: '基本信息',
  executionInfo: '执行信息',
  passingRate: '通过率',
  failureRate: '失效率',
  IterationProcess: '第{n}次迭代',
  executor: '执行人',
  interfaceNum: '接口数量',
  executionStart: '执行开始时间',
  executionEnd: '执行结束时间',
  project: '项目/服务',
  interface: '接口',
  selected: '已选',
  selectedInterface: '{n}个接口',
  strategy: '测试机选择策略',
  tips: {
    t1: '当前排序为接口默认执行顺序，鼠标拖拽位置可改变执行顺序',
    t2: '当前最大迭代次数支持 100,000,000,000',
    t3: '优先级高的最先被执行，值范围：0~2147483647，值越大优先级越高',
    t4: '开启后执行达到超时时间将会终止执行',
    t5: '当前最大并发支持',
    t6: '当前最大迭代次数支持',
    t7: '若同时指定了迭代次数和执行时长，只要有一个先完成则结束执行',
    t8: '"迭代次数" 和“执行时长” 至少需要输入1项',
    t9: '每1万并发需要1个测试节点',
    t10: '增压并发不可超出并发数',
    t11: '减压并发不可超出并发数',
    t12: '优先级高的最先被执行，取值范围：{m}-{n}，越大越靠前',
    t13: '开启后执行达到超时时间将会终止执行',
    t14: 'Html格式不可取消勾选',
    t15: '每隔{n}秒采样1次',
    t16: '最大收集错误数',
    t17: '减压并发不可超出并发数',
    t18: '减压并发不可超出并发数'
  },
  sort: {
    s1: '按添加时间',
    s2: '按优先级',
    s3: '按添加人'
  },
  immediately: '立即执行',
  timing: '定时执行',
  cancel: '取消',
  specifiedTime: '指点时间',
  cron: 'Cron表达式',
  priority1: '执行优先级',
  timeOut: '执行超时时间',
  error: '遇到错误',
  continue: '继续',
  stop: '停止',
  termination: '立即终止',
  directPressure: '直压',
  increment: '逐渐加压',
  straightDown: '直减',
  decrease: '逐渐减压',
  everyOther: '每隔',
  addTo: '增加',
  concurrency1: '并发',
  reduce: '缩减',
  whenErr: '遇到错误时',
  reportFormat: '报告格式',
  reportTemplate: '报告模版',
  samplingInterval: '采样时间间隔',
  wrongSamples: '错误采样数',
  sampleData: '采样数据',
  time: '时间',
  throughput: '吞吐量',
  thread: '线程',
  polymerization: '聚合',
  count: '计数',
  error1: '错误',
  percentile: '百分位',
  system: '系统',
  testNode: '测试节点',
  appNode: '应用节点',
  executeScript: '执行脚本',
  timedOut1: '连接超时',
  timedOut2: '请求超时',
  timedOut3: '读取超时',
  redirects: '最大重定向次数',
  maxRetries: '失败时最大重试次数',
  enableSll: '开启SLL'
};
