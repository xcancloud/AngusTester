export const enumNamespaceMap = new Map<any, string>([
  [NodeSource, 'xcm.enum.NodeSource'],
  [CountScriptType, 'xcm.enum.CountScriptType'],
  [TaskTargetType, 'xcm.enum.TaskTargetType'],
  [ApisTargetType, 'xcm.enum.ApisTargetType'],
  [CombinedTargetType, 'xcm.enum.CombinedTargetType'],
  [AuthorizationType, 'xcm.enum.AuthorizationType'],
  [ApiSource, 'xcm.enum.ApiSource'],
  [ApiStatus, 'xcm.enum.ApiStatus'],
  [ActionWhenDuplicate, 'xcm.enum.ActionWhenDuplicate'],
  [TestType, 'xcm.enum.TestType'],
  [CountTestType, 'xcm.enum.CountTestType'],
  [TaskType, 'xcm.enum.TaskType'],
  [TaskStatus, 'xcm.enum.TaskStatus'],
  [SoftwareVersionStatus, 'xcm.enum.SoftwareVersionStatus'],
  [BugLevel, 'xcm.enum.BugLevel'],
  [OverdueRiskLevel, 'xcm.enum.OverdueRiskLevel'],
  [AssociateUserType, 'xcm.enum.AssociateUserType'],
  [ServicesPermission, 'xcm.enum.ServicesPermission'],
  [ApiPermission, 'xcm.enum.ApiPermission'],
  [StrategyWhenDuplicated, 'xcm.enum.StrategyWhenDuplicated'],
  [ScriptPermission, 'xcm.enum.ScriptPermission'],
  [MockServiceStartStatus, 'xcm.enum.MockServiceStartStatus'],
  [ExecStatus, 'xcm.enum.ExecStatus'],
  [TestResultStatus, 'xcm.enum.TestResultStatus'],
  [ProjectType, 'xcm.enum.ProjectType'],
  [ServicesExportScope, 'xcm.enum.ServicesExportScope'],
  [ApisShareScope, 'xcm.enum.ApisShareScope'],
  [IndicatorType, 'xcm.enum.IndicatorType'],
  [TaskSprintPermission, 'xcm.enum.TaskSprintPermission'],
  [ScenarioPermission, 'xcm.enum.ScenarioPermission'],
  [MockServicePermission, 'xcm.enum.MockServicePermission'],
  [MockServiceStatus, 'xcm.enum.MockServiceStatus'],
  [MockServiceSource, 'xcm.enum.MockServiceSource'],
  [MockApisSource, 'xcm.enum.MockApisSource'],
  [MockDataSourceStatus, 'xcm.enum.MockDataSourceStatus'],
  [BinaryContentType, 'xcm.enum.BinaryContentType'],
  [MockAuthorizationType, 'xcm.enum.MockAuthorizationType'],
  [MockDataTargetType, 'xcm.enum.MockDataTargetType'],
  [ServicesCompType, 'xcm.enum.ServicesCompType'],
  [TaskSprintStatus, 'xcm.enum.TaskSprintStatus'],
  [TaskMeetingType, 'xcm.enum.TaskMeetingType'],
  [ApisDesignSource, 'xcm.enum.ApisDesignSource'],
  [DnsLine, 'xcm.enum.DnsLine'],
  [InstanceChargeType, 'xcm.enum.InstanceChargeType'],
  [FuncTargetType, 'xcm.enum.FuncTargetType'],
  [CaseTestResult, 'xcm.enum.CaseTestResult'],
  [CaseStepView, 'xcm.enum.CaseStepView'],
  [FuncPlanPermission, 'xcm.enum.FuncPlanPermission'],
  [FuncPlanStatus, 'xcm.enum.FuncPlanStatus'],
  [ScenarioMonitorStatus, 'xcm.enum.ScenarioMonitorStatus'],
  [DataAssetsCategory, 'xcm.enum.DataAssetsCategory'],
  [ParameterizationDataSource, 'xcm.enum.ParameterizationDataSource'],
  [EfficiencyResourceType, 'xcm.enum.EfficiencyResourceType'],
  [BurnDownResourceType, 'xcm.enum.BurnDownResourceType'],
  [AnalysisTimeRange, 'xcm.enum.AnalysisTimeRange'],
  [AnalysisDataSource, 'xcm.enum.AnalysisDataSource'],
  [AnalysisTaskObject, 'xcm.enum.AnalysisTaskObject'],
  [AnalysisTaskTemplate, 'xcm.enum.AnalysisTaskTemplate'],
  [AnalysisTaskTemplateDesc, 'xcm.enum.AnalysisTaskTemplateDesc'],
  [AnalysisCaseObject, 'xcm.enum.AnalysisCaseObject'],
  [AnalysisCaseTemplate, 'xcm.enum.AnalysisCaseTemplate'],
  [AnalysisCaseTemplateDesc, 'xcm.enum.AnalysisCaseTemplateDesc'],
  [ReportPermission, 'xcm.enum.ReportPermission'],
  [ReportStatus, 'xcm.enum.ReportStatus'],
  [ReportCategory, 'xcm.enum.ReportCategory'],
  [ReportTemplate, 'xcm.enum.ReportTemplate'],
  [ExampleDataType, 'xcm.enum.ExampleDataType'],
  [AssociateTargetType, 'xcm.enum.AssociateTargetType']
]);

export enum BinaryContentType {
  FILE = 'FILE',
  BASE64_TEXT = 'BASE64_TEXT',
  URL = 'URL'
}

export enum MockAuthorizationType {
  HTTP_BASIC = 'HTTP_BASIC',
  REQUEST_HEADER_MATCH = 'REQUEST_HEADER_MATCH'
}

export enum MockDataTargetType {
  FILE = 'FILE',
  DATASOURCE = 'DATASOURCE'
}

export enum ServicesCompType {
  schemas = 'schemas',
  responses = 'responses',
  parameters = 'parameters',
  examples = 'examples',
  requestBodies = 'requestBodies',
  headers = 'headers',
  securitySchemes = 'securitySchemes',
  links = 'links',
  callbacks = 'callbacks',
  extensions = 'extensions',
  pathItems = 'pathItems'
}

export enum TaskSprintStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  BLOCKED = 'BLOCKED'
}

export enum TaskMeetingType {
  DAILY_STANDUP = 'DAILY_STANDUP',
  PLANNING = 'PLANNING',
  REVIEW = 'REVIEW',
  RETROSPECTIVE = 'RETROSPECTIVE',
  BACKLOG_REFINEMENT = 'BACKLOG_REFINEMENT',
  OTHER = 'OTHER'
}

export enum ApisDesignSource {
  SYNCHRONOUS_SERVICE = 'SYNCHRONOUS_SERVICE',
  FILE_IMPORT = 'FILE_IMPORT',
  MANUAL_CREATED = 'MANUAL_CREATED'
}

export enum DnsLine {
  DEFAULT = 'DEFAULT',
  TELECOM = 'TELECOM',
  UNICOM = 'UNICOM',
  MOBILE = 'MOBILE',
  OVERSEA = 'OVERSEA'
}

export enum InstanceChargeType {
  PrePaid = 'PrePaid',
  PostPaid = 'PostPaid'
}

export enum FuncTargetType {
  PLAN = 'PLAN',
  CASE = 'CASE'
}

export enum CaseTestResult {
  PENDING = 'PENDING',
  PASSED = 'PASSED',
  NOT_PASSED = 'NOT_PASSED',
  BLOCKED = 'BLOCKED',
  CANCELED = 'CANCELED'
}

export enum CaseStepView {
  TABLE = 'TABLE',
  TEXT = 'TEXT'
}

export enum FuncPlanPermission {
  MODIFY_PLAN = 'MODIFY_PLAN',
  DELETE_PLAN = 'DELETE_PLAN',
  ADD_CASE = 'ADD_CASE',
  MODIFY_CASE = 'MODIFY_CASE',
  DELETE_CASE = 'DELETE_CASE',
  EXPORT_CASE = 'EXPORT_CASE',
  REVIEW = 'REVIEW',
  RESET_REVIEW_RESULT = 'RESET_REVIEW_RESULT',
  TEST = 'TEST',
  RESET_TEST_RESULT = 'RESET_TEST_RESULT',
  ESTABLISH_BASELINE = 'ESTABLISH_BASELINE',
  GRANT = 'GRANT'
}

export enum FuncPlanStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  BLOCKED = 'BLOCKED'
}

export enum ScenarioMonitorStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE'
}

export enum DataAssetsCategory {
  FUNC = 'FUNC',
  APIS = 'APIS',
  SCENARIO = 'SCENARIO',
  TASK = 'TASK',
  SCRIPT = 'SCRIPT',
  MOCK = 'MOCK',
  DATA = 'DATA'
}

export enum ParameterizationDataSource {
  STATIC_VALUE = 'STATIC_VALUE',
  EXTRACT_VALUE = 'EXTRACT_VALUE',
  EXTRACT_FILE = 'EXTRACT_FILE',
  EXTRACT_HTTP = 'EXTRACT_HTTP',
  EXTRACT_HTTP_SAMPLING = 'EXTRACT_HTTP_SAMPLING',
  EXTRACT_JDBC = 'EXTRACT_JDBC'
}

export enum EfficiencyResourceType {
  TASK = 'TASK',
  CASE = 'CASE'
}

export enum BurnDownResourceType {
  NUM = 'NUM',
  WORKLOAD = 'WORKLOAD'
}

export enum AnalysisTimeRange {
  ALL_TIME = 'ALL_TIME',
  THIS_WEEK = 'THIS_WEEK',
  LAST_WEEK = 'LAST_WEEK',
  THIS_MONTH = 'THIS_MONTH',
  LAST_MONTH = 'LAST_MONTH',
  THIS_YEAR = 'THIS_YEAR',
  CUSTOM_TIME = 'CUSTOM_TIME'
}

export enum AnalysisDataSource {
  REAL_TIME_DATA = 'REAL_TIME_DATA',
  SNAPSHOT_DATA = 'SNAPSHOT_DATA'
}

export enum AnalysisTaskObject {
  CURRENT_PROJECT = 'CURRENT_PROJECT',
  SPRINT = 'SPRINT',
  ASSIGNEE_ORG = 'ASSIGNEE_ORG'
}

export enum AnalysisTaskTemplate {
  PROGRESS = 'PROGRESS',
  BURNDOWN = 'BURNDOWN',
  WORKLOAD = 'WORKLOAD',
  OVERDUE_ASSESSMENT = 'OVERDUE_ASSESSMENT',
  BUGS = 'BUGS',
  HANDLING_EFFICIENCY = 'HANDLING_EFFICIENCY',
  CORE_KPI = 'CORE_KPI',
  FAILURES = 'FAILURES',
  BACKLOG_TASKS = 'BACKLOG_TASKS',
  RECENT_DELIVERY = 'RECENT_DELIVERY',
  LEAD_TIME = 'LEAD_TIME',
  UNPLANNED_TASKS = 'UNPLANNED_TASKS',
  TASK_GROWTH_TREND = 'TASK_GROWTH_TREND',
  RESOURCE_CREATION = 'RESOURCE_CREATION',
  CUSTOM_DEFINITION = 'CUSTOM_DEFINITION'
}

export enum AnalysisTaskTemplateDesc {
  PROGRESS = 'PROGRESS',
  BURNDOWN = 'BURNDOWN',
  WORKLOAD = 'WORKLOAD',
  OVERDUE_ASSESSMENT = 'OVERDUE_ASSESSMENT',
  BUGS = 'BUGS',
  HANDLING_EFFICIENCY = 'HANDLING_EFFICIENCY',
  CORE_KPI = 'CORE_KPI',
  FAILURES = 'FAILURES',
  BACKLOG_TASKS = 'BACKLOG_TASKS',
  RECENT_DELIVERY = 'RECENT_DELIVERY',
  LEAD_TIME = 'LEAD_TIME',
  UNPLANNED_TASKS = 'UNPLANNED_TASKS',
  TASK_GROWTH_TREND = 'TASK_GROWTH_TREND',
  RESOURCE_CREATION = 'RESOURCE_CREATION',
  CUSTOM_DEFINITION = 'CUSTOM_DEFINITION'
}

export enum AnalysisCaseObject {
  CURRENT_PROJECT = 'CURRENT_PROJECT',
  PLAN = 'PLAN',
  TESTER_ORG = 'TESTER_ORG'
}

export enum AnalysisCaseTemplate {
  PROGRESS = 'PROGRESS',
  BURNDOWN = 'BURNDOWN',
  WORKLOAD = 'WORKLOAD',
  OVERDUE_ASSESSMENT = 'OVERDUE_ASSESSMENT',
  SUBMITTED_BUGS = 'SUBMITTED_BUGS',
  TESTING_EFFICIENCY = 'TESTING_EFFICIENCY',
  CORE_KPI = 'CORE_KPI',
  REVIEW_EFFICIENCY = 'REVIEW_EFFICIENCY',
  BACKLOG_CASES = 'BACKLOG_CASES',
  RECENT_DELIVERY = 'RECENT_DELIVERY',
  LEAD_TIME = 'LEAD_TIME',
  UNPLANNED_CASES = 'UNPLANNED_CASES',
  CASE_GROWTH_TREND = 'CASE_GROWTH_TREND',
  RESOURCE_CREATION = 'RESOURCE_CREATION',
  CUSTOM_DEFINITION = 'CUSTOM_DEFINITION'
}

export enum AnalysisCaseTemplateDesc {
  PROGRESS = 'PROGRESS',
  BURNDOWN = 'BURNDOWN',
  WORKLOAD = 'WORKLOAD',
  OVERDUE_ASSESSMENT = 'OVERDUE_ASSESSMENT',
  SUBMITTED_BUGS = 'SUBMITTED_BUGS',
  TESTING_EFFICIENCY = 'TESTING_EFFICIENCY',
  CORE_KPI = 'CORE_KPI',
  REVIEW_EFFICIENCY = 'REVIEW_EFFICIENCY',
  BACKLOG_CASES = 'BACKLOG_CASES',
  RECENT_DELIVERY = 'RECENT_DELIVERY',
  LEAD_TIME = 'LEAD_TIME',
  UNPLANNED_CASES = 'UNPLANNED_CASES',
  CASE_GROWTH_TREND = 'CASE_GROWTH_TREND',
  RESOURCE_CREATION = 'RESOURCE_CREATION',
  CUSTOM_DEFINITION = 'CUSTOM_DEFINITION'
}

export enum ReportPermission {
  VIEW = 'VIEW',
  MODIFY = 'MODIFY',
  GENERATE = 'GENERATE',
  DELETE = 'DELETE',
  GRANT = 'GRANT',
  EXPORT = 'EXPORT'
}

export enum ReportStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE'
}

export enum ReportCategory {
  PROJECT = 'PROJECT',
  TASK = 'TASK',
  FUNCTIONAL = 'FUNCTIONAL',
  APIS = 'APIS',
  SCENARIO = 'SCENARIO',
  EXECUTION = 'EXECUTION'
}

export enum ReportTemplate {
  PROJECT_PROGRESS = 'PROJECT_PROGRESS',
  PROJECT_DATA_ASSETS = 'PROJECT_DATA_ASSETS',
  PROJECT_EFFICIENCY = 'PROJECT_EFFICIENCY',
  PROJECT_ACTIVITY = 'PROJECT_ACTIVITY',
  TASK_SPRINT = 'TASK_SPRINT',
  TASK = 'TASK',
  FUNC_TESTING_PLAN = 'FUNC_TESTING_PLAN',
  FUNC_TESTING_CASE = 'FUNC_TESTING_CASE',
  SERVICES_TESTING_RESULT = 'SERVICES_TESTING_RESULT',
  APIS_TESTING_RESULT = 'APIS_TESTING_RESULT',
  APIS_TESTING = 'APIS_TESTING',
  SCENARIO_TESTING_RESULT = 'SCENARIO_TESTING_RESULT',
  SCENARIO_TESTING = 'SCENARIO_TESTING',
  EXEC_FUNCTIONAL_RESULT = 'EXEC_FUNCTIONAL_RESULT',
  EXEC_PERFORMANCE_RESULT = 'EXEC_PERFORMANCE_RESULT',
  EXEC_STABILITY_RESULT = 'EXEC_STABILITY_RESULT',
  EXEC_CUSTOMIZATION_RESULT = 'EXEC_CUSTOMIZATION_RESULT'
}

export enum ExampleDataType {
  TAG = 'TAG',
  MODULE = 'MODULE',
  TASK = 'TASK',
  FUNC = 'FUNC',
  SERVICES = 'SERVICES',
  SCENARIO = 'SCENARIO',
  SCRIPT = 'SCRIPT',
  MOCK = 'MOCK',
  VARIABLE = 'VARIABLE',
  DATASET = 'DATASET',
  EXECUTION = 'EXECUTION',
  REPORT = 'REPORT'
}

export enum AssociateTargetType {
  EXEC = 'EXEC',
  REPORTER = 'REPORTER'
}
