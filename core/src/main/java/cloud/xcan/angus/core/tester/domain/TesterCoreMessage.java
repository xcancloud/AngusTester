package cloud.xcan.angus.core.tester.domain;

import cloud.xcan.angus.api.commonlink.TesterApisMessage;
import java.util.List;

/**
 * ATS001 ~ ATS999
 */
public interface TesterCoreMessage extends TesterApisMessage {

  /*<******************Project(ATS030 ~ ATS049)******************>*/
  String PROJECT_NAME_REPEATED_T = "xcm.angustester.project.name.repeated.t";
  String PROJECT_NOT_MEMBER_USER_CODE = "ATS030";
  String PROJECT_NOT_MEMBER_USER = "xcm.angustester.project.not.member.user";
  String PROJECT_NOT_MODIFY_PERMISSION_CODE = "ATS031";
  String PROJECT_NOT_MODIFY_PERMISSION_MODIFY = "xcm.angustester.project.no.modify.permission";
  String PROJECT_NOT_DELETE_PERMISSION_CODE = "ATS032";
  String PROJECT_NOT_DELETE_PERMISSION = "xcm.angustester.project.no.delete.permission";
  String PROJECT_NOT_EDIT_TAG_PERMISSION_CODE = "ATS033";
  String PROJECT_NOT_EDIT_TAG_PERMISSION = "xcm.angustester.project.no.edit.tag.permission";
  String PROJECT_NOT_EDIT_MODULE_PERMISSION_CODE = "ATS034";
  String PROJECT_NOT_EDIT_MODULE_PERMISSION = "xcm.angustester.project.no.edit.module.permission";

  /*<******************Trash(ATS050 ~ ATS069)******************>*/
  // @see#TesterApisMessage
  // String TRASH_NO_BACK_PERMISSION_CODE = "ATS050";
  // String TRASH_NO_BACK_PERMISSION = "xcm.angustester.trash.no.back.permission";
  // String TRASH_NO_CLEAR_PERMISSION_CODE = "ATS051";
  // String TRASH_NO_CLEAR_PERMISSION = "xcm.angustester.trash.no.clear.permission";
  // String TRASH_NO_VIEW_PERMISSION_CODE = "ATS052";
  // String TRASH_NO_VIEW_PERMISSION = "xcm.angustester.trash.no.view.permission";

  /*******************Indicator(ATS070 ~ ATS099)******************>*/
  String INDICATOR_NO_AUDITING_CODE = "ATS070";
  String INDICATOR_NO_AUDITING = "xcm.angustester.indicator.no.auditing";
  String INDICATOR_GET_PLATFORM_FAIL_CODE = "ATS071";
  String INDICATOR_GET_PLATFORM_FAIL = "xcm.angustester.indicator.get.platform.fail";
  String INDICATOR_IS_AUDITING_CODE = "ATS072";
  String INDICATOR_IS_AUDITING = "xcm.angustester.indicator.is.auditing";

  /*<******************Variables(ATS100 ~ ATS149)******************>*/
  String VARIABLE_NAME_REPEATED_T = "xcm.angustester.variable.name.repeated.t";

  //  String VARIABLE_TENANT_OVER_LIMIT_CODE = "ATS100";
  //  String VARIABLE_TENANT_OVER_LIMIT_T = "xcm.angustester.variable.tenant.over.limit.t";
  //  String VARIABLE_TARGET_OVER_LIMIT_CODE = "ATS101";
  //  String VARIABLE_TARGET_OVER_LIMIT_T = "xcm.angustester.variable.target.over.limit.t";

  /*<******************Script(ATS150 ~ ATS199)******************>*/
  String SCRIPT_EXPORT_ERROR_CODE = "ATS121";
  String SCRIPT_EXPORT_ERROR = "xcm.angustester.script.file.export.error";
  String SCRIPT_PROPERTIES_CONSTRAINT_ERROR = "xcm.angustester.script.properties.constraint.error";
  String SCRIPT_CONTENT_PARSE_ERROR = "xcm.angustester.script.content.parse.error";

  /*<******************Apis(ATS200 ~ ATS249)******************>*/
  String APIS_OPERATION_EXISTED = "xcm.angustester.apis.operation.existed.t";

  String APIS_UNARCHIVED_NO_PERMISSION_CODE = "ATS200";
  String APIS_UNARCHIVED_NO_PERMISSION_T = "xcm.angustester.apis.unarchived.no.permission.t";

  String APIS_PUBLISHED_CANNOT_MODIFY_CODE = "ATS201";
  String APIS_PUBLISHED_CANNOT_MODIFY_T = "xcm.angustester.apis.released.cannot.modify.t";

  String APIS_OAS_DESIGN_NOT_FOUND = "xcm.angustester.apis.oas.not.found";

  //  String APIS_TENANT_OVER_LIMIT_CODE = "ATS206";
  //  String APIS_TENANT_OVER_LIMIT_T = "xcm.angustester.apis.tenant.over.limit.t";

  /*<******************Apis#Auth(ATS250 ~ ATS259)******************>*/
  String APIS_NO_AUTH_CODE = "ATS250";
  String APIS_NO_AUTH = "xcm.angustester.apis.no.auth.t";
  String APIS_NO_TARGET_AUTH_CODE = "ATS251";
  String APIS_NO_TARGET_AUTH = "xcm.angustester.apis.no.target.auth.t";
  /*<******************Apis#Favourite(ATS260 ~ ATS269)******************>*/
  String APIS_FAVOURITE_REPEATED_T = "xcm.angustester.apis.favorite.repeated.t";
  /*<******************Apis#Follow(ATS270 ~ ATS279)******************>*/
  String APIS_FOLLOW_REPEATED_T = "xcm.angustester.apis.follow.repeated.t";


  /*<******************Apis#Unarchived(ATS280 ~ ATS299)******************>*/

  /*******************Apis#case(ATS300 ~ ATS349)******************>*/
  String APIS_CASE_NAME_REPEATED_T = "xcm.angustester.apis.case.name.existed.t";

  String APIS_CASE_OVER_LIMIT_CODE = "ATS301";
  String APIS_CASE_OVER_LIMIT_T = "xcm.angustester.apis.case.quota.over.limit.t";

  String APIS_CASE_SMOKE_NAME = "xcm.angustester.apis.case.smoke.name";
  String APIS_CASE_SECURITY_NAME = "xcm.angustester.apis.case.security.name";
  String APIS_CASE_DEFAULT_NAME = "xcm.angustester.apis.case.default.name";

  String APIS_CASE_ASSERT_SERVER_IS_RESPONSIVE = "xcm.angustester.apis.case.assert.server.is.responsive";
  String APIS_CASE_ASSERT_STATUS_GE_200 = "xcm.angustester.apis.case.assert.status.greaterOrEqual.200";
  String APIS_CASE_ASSERT_STATUS_LESS_300 = "xcm.angustester.apis.case.assert.status.less.300";
  String APIS_CASE_ASSERT_STATUS_IS_OK = "xcm.angustester.apis.case.assert.status.is.ok";

  String APIS_CASE_ASSERT_STATUS_IS_NOT_401_403 = "xcm.angustester.apis.case.assert.status.is.not.401.403";
  String APIS_CASE_ASSERT_STATUS_IS_401_403 = "xcm.angustester.apis.case.assert.status.is.401.403";

  /*<******************Services(ATS350 ~ ATS379)******************>*/
  String SERVICE_IMPORT_NEW_NAME_EMPTY = "xcm.angustester.service.import.name.is.empty";

  String SERVICE_NAME_REPEATED_T = "xcm.angustester.service.name.repeated.error.t";

  String SERVICE_PUBLISHED_CANNOT_MODIFY_CODE = "ATS358";
  String SERVICE_PUBLISHED_CANNOT_MODIFY_T = "xcm.angustester.service.released.cannot.modify.t";

  String SERVICE_APIS_NOT_BELONG_T = "xcm.angustester.service.apis.not.belongs.t";

  String SERVICE_SMOKE_CASE_NOT_FOUND = "xcm.angustester.service.smoke.case.not.found";
  String SERVICE_SECURITY_CASE_NOT_FOUND = "xcm.angustester.service.security.case.not.found";

  String SERVICE_NO_IMPORT_APIS_PLUGIN_CODE = "ATS359";
  String SERVICE_NO_IMPORT_APIS_PLUGIN_T = "xcm.angustester.service.no.importApis.plugin.error.t";

  /*<******************Services#Auth(ATS380 ~ ATS394)******************>*/
  String SERVICE_NO_AUTH_CODE = "ATS380";
  String SERVICE_NO_AUTH = "xcm.angustester.service.no.auth.t";
  String SERVICE_NO_TARGET_AUTH_CODE = "ATS381";
  String SERVICE_NO_TARGET_AUTH = "xcm.angustester.service.no.target.auth.t";

  /*<******************Services#Config(ATS395 ~ ATS399)******************>*/
  String SERVICE_SYNC_NUM_OVER_LIMIT_CODE = "ATS395";
  String SERVICE_SYNC_NUM_OVER_LIMIT_T = "xcm.angustester.service.sync.num.over.limit.t";
  String SERVICE_SYNC_NAME_EXISTED_T = "xcm.angustester.service.sync.name.existed.t";
  String SERVICE_SYNC_TEST_URL_INVALID = "xcm.angustester.service.sync.test.url.invalid";
  String SERVICE_SYNC_TEST_CONN_FAILED_T = "xcm.angustester.service.sync.test.connection.failed.t";
  String SERVICE_SYNC_CONFIG_NOT_FOUND = "xcm.angustester.service.sync.config.not.found";

  String SERVICE_DOC_CHANGE_REMINDER = "xcm.angustester.service.doc.change.reminder";

  String SERVICE_SHARE_TOKEN_ERROR_T = "xcm.angustester.service.share.tpt.error";
  String SERVICE_SHARE_EXPIRED = "xcm.angustester.service.share.expired";

  /*<******************Node(ATS400 ~ ATS429)******************>*/
  String NODE_IP_EXISTED_T = "xcm.angustester.node.ip.existed.t";
  String NODE_IP_NOT_AVAILABLE_T = "xcm.angustester.node.ip.not.available.t";
  String NODE_CONN_ERROR_T = "xcm.angustester.node.conn.error.t";
  String NODE_SPECS_NO_CHANGE = "xcm.angustester.node.specs.no.change";
  String NODE_PURCHASE_CHANGE_CONFLICTS_T = "xcm.angustester.node.purchase.change.conflicts.t";

  String NODE_OVER_LIMIT_CODE = "ATS400";
  String NODE_OVER_LIMIT_T = "xcm.angustester.node.over.limit.t";
  String NODE_DELETE_FAILED_CODE = "ATS401";
  String NODE_DELETE_FAILED = "xcm.angustester.node.delete.failed";
  String NODE_PURCHASE_NO_RES_CODE = "ATS402";
  String NODE_PURCHASE_NO_RES = "xcm.angustester.node.purchase.no.res";
  String NODE_INSTALL_AGENT_FAILED_CODE = "ATS403";
  String NODE_INSTALL_AGENT_FAILED = "xcm.angustester.node.install.agent.failed";
  String NODE_AGENT_IS_INSTALLED_CODE = "ATS404";
  String NODE_AGENT_IS_INSTALLED = "xcm.angustester.node.agent.is.installed";
  String NODE_AGENT_NOT_ONLINE_CODE = "ATS405";
  String NODE_AGENT_NOT_ONLINE_T = "xcm.angustester.node.agent.not.online.t";
  String NODE_PURCHASE_UPDATE_ERROR_CODE = "ATS406";
  String NODE_PURCHASE_UPDATE_ERROR_T = "xcm.angustester.node.update.purchase.error.t";
  String NODE_PURCHASE_BY_ORDER_REPEATED_CODE = "ATS407";
  String NODE_PURCHASE_BY_ORDER_REPEATED_T = "xcm.angustester.node.purchase.by.order.repeated.t";
  String NODE_CHANGE_OWN_NOT_ALLOW_CODE = "ATS408";
  String NODE_CHANGE_OWN_NOT_ALLOW = "xcm.angustester.node.change.own.not.allowed";
  String NODE_CLOUD_RES_NOT_AVAILABLE_CODE = "ATS409";
  String NODE_CLOUD_RES_NOT_AVAILABLE = "xcm.angustester.node.cloud.res.not.available";
  String NODE_CLOUD_RES_QUERY_FAIL_CODE = "ATS410";
  String NODE_CLOUD_RES_QUERY_FAIL = "xcm.angustester.node.cloud.res.query.fail";
  String NODE_REDUCE_DISK_NOT_ALLOW_CODE = "ATS411";
  String NODE_REDUCE_DISK_NOT_ALLOW = "xcm.angustester.node.reduce.disk.not.allowed";
  String NODE_PURCHASE_EXCEPTION_CODE = "ATS412";
  String NODE_PURCHASE_EXCEPTION = "xcm.angustester.node.purchase.change.exception";
  String NODE_NOT_CONFIGURED_ROLE_CODE = "ATS413";
  String NODE_NOT_CONFIGURED_ROLE_T = "xcm.angustester.mock.service.not.configured.role.t";
  String NODE_NOT_INSTALLED_AGENT_CODE = "ATS414";
  String NODE_NOT_INSTALLED_AGENT_T = "xcm.angustester.node.not.installed.agent.t";
  String NODE_DELETE_NOT_ALLOW_CODE = "ATS415";
  String NODE_DELETE_NOT_ALLOW = "xcm.angustester.node.delete.not.allow";
  String DOMAIN_NAME_REPEATED_T = "xcm.angustester.dns.domain.name.repeated.t";
  String DOMAIN_DNS_NAME_REPEATED_T = "xcm.angustester.dns.domain.dns.name.repeated.t";

  /*<******************Scenario(ATS430 ~ ATS449)******************>*/
  String SCE_NAME_REPEATED_T = "xcm.angustester.sce.name.repeated.t";

  /*<******************Scenario#Favourite(ATS450 ~ ATS454)******************>*/
  String SCE_FAVOURITE_REPEATED = "xcm.angustester.sce.favorite.repeated";
  /*<******************Scenario#Follow(ATS455 ~ ATS459)******************>*/
  String SCE_FOLLOW_REPEATED = "xcm.angustester.sce.follow.repeated";

  /*<******************Scenario#Auth(ATS460 ~ ATS479)******************>*/
  String SCE_NO_AUTH_CODE = "ATS460";
  String SCE_NO_AUTH_T = "xcm.angustester.sce.no.auth.t";
  String SCE_NO_TARGET_AUTH_CODE = "ATS461";
  String SCE_NO_TARGET_AUTH = "xcm.angustester.sce.no.target.auth.t";

  /*<******************Script#Auth(ATS520 ~ ATS524)******************>*/
  String SCRIPT_NO_AUTH_CODE = TesterApisMessage.SCRIPT_NO_AUTH_CODE;
  String SCRIPT_NO_AUTH_T = TesterApisMessage.SCRIPT_NO_AUTH_T;
  String SCRIPT_NO_TARGET_AUTH_CODE = TesterApisMessage.SCRIPT_NO_TARGET_AUTH_CODE;
  String SCRIPT_NO_TARGET_AUTH = TesterApisMessage.SCRIPT_NO_TARGET_AUTH;

  /*<******************Datasource(ATS600 ~ ATS609)******************>*/
  String MOCK_DS_NAME_EXISTED_T = "xcm.angustester.mock.ds.name.existed.t";

  /*<******************Datasource#Auth(ATS610 ~ ATS619)******************>*/

  /*<******************Script(ATS620 ~ ATS649)******************>*/

  /*<******************Datasource(ATS650 ~ ATS679)******************>*/
  String MOCK_SERVICE_NAME_EXISTED_T = "xcm.angustester.mock.service.name.existed.t";
  String MOCK_SERVICE_OVER_LIMIT_IN_NODE = "xcm.angustester.mock.service.over.limit.in.node";
  String MOCK_SERVICE_TOP_LEVEL_DOMAIN_ERROR_T = "xcm.angustester.mock.service.top.level.cloud.domain.error.t";
  String MOCK_SERVICE_DOMAIN_IN_USE_T = "xcm.angustester.mock.service.domain.in.use.t";
  String MOCK_SERVICE_NODE_PORT_IN_USE_T = "xcm.angustester.mock.service.node.port.in.use.t";
  String MOCK_SERVICE_DOMAIN_PORT_IN_USE_T = "xcm.angustester.mock.service.domain.port.in.use.error.t";
  String MOCK_SERVICE_PORT_UNAVAILABLE_IN_AGENT_T = "xcm.angustester.mock.service.port.unavailable.in.agent.t";
  String MOCK_SERVICE_IMPORT_FILE_OR_TEXT_REQUIRED = "xcm.angustester.mock.service.import.file.or.text.required";
  String MOCK_SERVICE_ASSOC_SERVICE_EXISTED_T = "xcm.angustester.mock.service.assoc.project.existed.t";
  String MOCK_SERVICE_ASSOC_EXISTED_T = "xcm.angustester.mock.service.assoc.existed.t";

  /*<******************Datasource#Auth(ATS680 ~ ATS699)******************>*/
  String MOCK_SERVICE_NO_AUTH_CODE = "ATS610";
  String MOCK_SERVICE_NO_AUTH_T = "xcm.angustester.mock.service.no.auth.t";
  String MOCK_SERVICE_NO_TARGET_AUTH_CODE = "ATS611";
  String MOCK_SERVICE_NO_TARGET_AUTH = "xcm.angustester.mock.service.no.target.auth.t";

  /*<******************Datasource(ATS870 ~ ATS899)******************>*/
  String MOCK_APIS_NAME_EXISTED_T = "xcm.angustester.mock.apis.name.existed.t";
  String MOCK_APIS_OPERATION_EXISTED_T = "xcm.angustester.mock.apis.operation.existed.t";
  String MOCK_APIS_RESPONSE_ID_INCONSISTENT_T = "xcm.angustester.mock.apis.response.id.inconsistent.t";
  //String MOCK_APIS_RESPONSE_STATUS_ERROR_T = "xcm.angustester.mock.response.status.error.t";
  String MOCK_APIS_ASSOC_APIS_EXISTED_T = "xcm.angustester.mock.apis.assoc.apis.existed.t";
  String MOCK_APIS_ASSOC_EXISTED_T = "xcm.angustester.mock.apis.assoc.existed.t";

  /*<******************Task(ATS930 ~ ATS959)******************>*/
  String TASK_NAME_EXISTED_T = "xcm.angustester.task.name.existed.t";
  String TASK_APIS_NOT_EXISTED_T = "xcm.angustester.task.apis.not.existed.t";
  String TASK_SCE_NOT_EXISTED_T = "xcm.angustester.task.sce.not.existed.t";
  String TASK_APIS_EXISTED_T = "xcm.angustester.task.apis.existed.t";
  String TASK_SCE_EXISTED_T = "xcm.angustester.task.sce.existed.t";
  String TASK_PARENT_CIRCULAR_REF_BY_SELF = "xcm.angustester.task.parent.circular.ref.by.self";
  String TASK_PARENT_CIRCULAR_REF = "xcm.angustester.task.parent.circular.ref";
  String TASK_EVAL_WORKLOAD_NOT_SET = "xcm.angustester.task.eval.workload.not.set";
  String TASK_SUB_IS_NOT_COMPLETED_T  = "xcm.angustester.task.sub.is.not.completed.t";

  String TASK_START_NO_PENDING_CODE = "ATS930";
  String TASK_START_NO_PENDING = "xcm.angustester.task.start.no.pending";

  String TASK_NO_PROCESSING_CODE = "ATS931";
  String TASK_NO_PROCESSING = "xcm.angustester.task.no.processing";

  String TASK_NO_CONFIRMING_CODE = "ATS932";
  String TASK_NO_CONFIRMING = "xcm.angustester.task.no.confirming";

  String TASK_REOPEN_REPEATED_CODE = "ATS933";
  String TASK_REOPEN_REPEATED_T = "xcm.angustester.task.reopen.repeated.t";

  String TASK_REMARK_OVER_LIMIT_CODE = "ATS936";
  String TASK_REMARK_OVER_LIMIT_T = "xcm.angustester.task.remark.over.limit.t";

  String TASK_CLEAR_CONFIRMOR_ERROR_CODE = "ATS937";
  String TASK_CLEAR_CONFIRMOR_ERROR = "xcm.angustester.task.clear.confirmor.error.t";

  String TASK_ASSOC_TARGET_ID_REQUIRED = "xcm.angustester.task.assoc.test.target.id.required";
  String TASK_WEBSOCKET_NOT_SUPPORT_GEN_TASK = "xcm.angustester.task.websocket.not.support.gen.task.error";

  /*<******************Task#Services#Auth(ATS970 ~ ATS979)******************>*/
  String TASK_SPRINT_NO_AUTH_CODE = "ATS970";
  String TASK_SPRINT_NO_AUTH = "xcm.angustester.task.sprint.auth.t";

  /*<******************Task#Plan(ATS980 ~ ATS989)******************>*/
  String TASK_SPRINT_NAME_REPEATED_T = "xcm.angustester.task.sprint.name.repeated.t";
  String TASK_SPRINT_DATE_RANGE_ERROR_T = "xcm.angustester.task.sprint.date.range.error.t";
  String TASK_SPRINT_STATUS_MISMATCH_T = "xcm.angustester.task.sprint.status.mismatch.t";
  String TASK_SPRINT_NOT_STARTED_CODE = "ATS980";
  String TASK_SPRINT_NOT_STARTED = "xcm.angustester.task.sprint.not.started";
  String TASK_SPRINT_NOT_COMPLETED = "xcm.angustester.task.sprint.not.completed";

  /*<******************Task#Favourite******************>*/
  String TASK_FAVOURITE_REPEATED = "xcm.angustester.task.favorite.repeated";
  /*<******************Task#Follow******************>*/
  String TASK_FOLLOW_REPEATED = "xcm.angustester.task.follow.repeated";

  /*<******************Data#Variable******************>*/
  String VARIABLE_IS_NOT_VALID = "xcm.angustester.variable.is.not.valid";
  String VARIABLE_FILE_PARSING_ERROR_T = "xcm.angustester.variable.file.parsing.error.t";
  /*<******************Data#Dataset******************>*/
  String DATASET_IS_NOT_VALID = "xcm.angustester.dataset.is.not.valid";
  String DATASET_FILE_PARSING_ERROR_T = "xcm.angustester.dataset.file.parsing.error.t";

  /**********************Report********************/
  String REPORT_REPEATED_T = "xcm.angustester.report.repeated.t";
  String REPORT_RESOURCE_REPEATED_T = "xcm.angustester.report.resource.repeated.t";
  String REPORT_RECORD_NOT_FOUND = "xcm.angustester.report.record.not.found";
  String REPORT_SHARE_TOKEN_INVALID = "xcm.angustester.report.share.token.invalid";

  /**********************ExportHeaderName********************/
  //String EXPORT_APIS_RESOURCES_STATISTICS = "xcm.export.apisResourcesStatistics";
  String EXPORT_TASK_STATISTICS = "xcm.export.taskStatistics";
  String EXPORT_TASK_LIST = "xcm.export.taskListExport";
  //--------------------------ExportAnalysisTaskHeaders--------------
  String EXPORT_ANALYSIS_TASK_PROGRESS = "xcm.export.analysisTask.progress";
  String EXPORT_ANALYSIS_TASK_BURNDOWN = "xcm.export.analysisTask.burndown";
  String EXPORT_ANALYSIS_TASK_WORKLOAD = "xcm.export.analysisTask.workload";
  String EXPORT_ANALYSIS_TASK_OVERDUE_ASSESSMENT = "xcm.export.analysisTask.overdueAssessment";
  String EXPORT_ANALYSIS_TASK_BUGS = "xcm.export.analysisTask.bugs";
  String EXPORT_ANALYSIS_TASK_HANDLING_EFFICIENCY = "xcm.export.analysisTask.handlingEfficiency";
  String EXPORT_ANALYSIS_TASK_CORE_KPI = "xcm.export.analysisTask.coreKpi";
  String EXPORT_ANALYSIS_TASK_FAILURES = "xcm.export.analysisTask.failures";
  String EXPORT_ANALYSIS_TASK_BACKLOG_TASKS = "xcm.export.analysisTask.backlogTasks";
  String EXPORT_ANALYSIS_TASK_RECENT_DELIVERY = "xcm.export.analysisTask.recentDelivery";
  String EXPORT_ANALYSIS_TASK_LEAD_TIME = "xcm.export.analysisTask.leadTime";
  String EXPORT_ANALYSIS_TASK_UNPLANNED_TASKS = "xcm.export.analysisTask.unplannedTasks";
  String EXPORT_ANALYSIS_TASK_GROWTH_TREND = "xcm.export.analysisTask.taskGrowthTrend";
  String EXPORT_ANALYSIS_TASK_RESOURCE_CREATION = "xcm.export.analysisTask.resourceCreation";

  /**********************Others********************/
  String MESSAGE_TOTAL = "xcm.message.total";
  String MESSAGE_DATA_DETAIL = "xcm.message.dataDetail";
  String MESSAGE_MATCH_STATUS_RESPONSE_NAME = "xcm.message.match.status.response.name";
  String MESSAGE_MATCH_STATUS_AND_MEDIATYPE_RESPONSE_NAME = "xcm.message.match.statusAndMediaType.response.name";

  // Note: The order cannot be changed
  List<String> CASE_IMPORT_COLUMNS = List.of(
      "用例名称", "模块", "测试人", "开发人", "评审人", "优先级",
      "截止时间", "前置条件", "测试步骤", "描述", "评估工作量",
      "实际工作量", "测试结果", "评审状态", "测试处理时间", "评审时间",
      "关联标签", "关联任务", "关联用例", "创建人", "创建时间");
  List<String> CASE_IMPORT_REQUIRED_COLUMNS = List.of("用例名称", "测试人", "开发人", "截止时间");

  // Note: The order cannot be changed
  List<String> TASK_IMPORT_COLUMNS = List.of(
      "任务名称", "任务类型", "缺陷等级", "测试类型", "经办人", "确认人", "测试人", "是否漏测缺陷",
      "是否计划外任务", "优先级", "截止时间", "模块", "描述", "评估工作量", "实际工作量",
      "任务状态", "软件版本", "开始时间", "处理时间", "取消时间", "确认时间",
      "完成时间", "关联标签", "关联任务", "关联用例", "创建人",
      "创建时间"/*, "测试资源(接口或场景)ID"*/);
  List<String> TASK_IMPORT_REQUIRED_COLUMNS = List.of("任务名称", "任务类型", "经办人", "截止时间");

}
