package cloud.xcan.sdf.core.angustester.domain.activity;

import cloud.xcan.sdf.spec.ValueObject;

public enum ActivityType implements ValueObject<ActivityType> {
  CREATED, ARCHIVED, UPDATED, ENABLED, DISABLED, SUB_ENABLED, SUB_DISABLED, SCHEMA_UPDATED, NAME_UPDATED, DESCRIPTION_UPDATED, DESCRIPTION_CLEAR, ATTACHMENT_UPDATED,
  ATTACHMENT_CLEAR, REF_TASK_UPDATE, REF_TASK_CLEAR, REF_CASE_UPDATE, REF_CASE_CLEAR, DELETED, BACK, STATUS_UPDATE, RESULT_UPDATE, RESULT_RESET, REVIEW_UPDATE, REVIEW_RESET, RETEST_START,
  AUTH, AUTH_CANCEL, AUTH_UPDATED, AUTH_ENABLED, AUTH_DISABLED,
  SHARE_CREATED, SHARE_UPDATED, SHARE_DELETED,
  APIS_PARAMETER_ADD, APIS_PARAMETER_UPDATE, APIS_PARAMETER_DELETE, APIS_PARAMETER_DISABLED, APIS_PARAMETER_ENABLED, APIS_AUTH_UPDATE, APIS_SERVER_UPDATE, APIS_VARIABLE_REF_ADD, APIS_VARIABLE_REF_DELETE, APIS_DATASET_REF_ADD, APIS_DATASET_REF_DELETE,
  SCHEMA_INFO_UPDATED, SCHEMA_EXT_DOC_UPDATED, SCHEMA_SR_UPDATED, SCHEMA_SERVER_UPDATED, APIS_SERVER_SYNC, SCHEMA_TAG_UPDATED, SCHEMA_EXT_UPDATED, SCHEMA_OPENAPI_UPDATED, SCHEMA_OPENAPI_SYNC,
  SCHEMA_COMP_UPDATED, SCHEMA_COMP_DELETED,
  INDICATOR_APPLY, INDICATOR_CANCEL, INDICATOR_AUDIT, INDICATOR_ADD, INDICATOR_UPDATE, @Deprecated INDICATOR_DELETED,
  CLONE, MOVED, MOVED_TO, FOLLOW, FOLLOW_CANCEL, FAVOURITE, FAVOURITE_CANCEL,
  EXPORT, IMPORT, AUTH_HEADER_CONFIG, HOST_CONFIG, SYNC_CONFIG_ADD, SYNC_CONFIG_UPDATE, SYNC_CONFIG_EXEC, SYNC_CONFIG_DELETE,
  TARGET_TASK_GEN, TARGET_TASK_RESTART, TARGET_TASK_REOPEN, TARGET_TASK_DELETED,
  TARGET_SCRIPT_GEN, TARGET_SCRIPT_UPDATE, TARGET_SCRIPT_DELETED,
  TASK_GEN, TASK_START, TASK_CANCEL, TASK_PROCESSED, TASK_COMPLETED, TASK_CONFIRM_RESULT,
  TASK_STATUS, TASK_ASSIGNEE, TASK_ASSIGNEE_CLEAR, TASK_CONFIRMOR, TASK_CONFIRMOR_CLEAR, TASK_SUB_SET, TASK_SUB_CANCEL,
  TASK_RESTART, TASK_REOPEN,
  TASK_REMARK_ADD, TASK_REMARK_DELETE, SOFTWARE_VERSION_UPDATE, SOFTWARE_VERSION_MERGE,
  FUNC_TESTER, FUNC_REVIEW_ADD, FUNC_REVIEW_DELETE,
  FUNC_CASE_ASSOC_TASK, FUNC_CASE_ASSOC_TASK_CANCEL, FUNC_CASE_ASSOC_CASE, FUNC_CASE_ASSOC_CASE_CANCEL,
  TYPE_UPDATED, BUG_LEVEL_UPDATED, MISSING_BUG_SET, MISSING_BUG_CLEAR, PRIORITY, DEADLINE, DEADLINE_CLEAR,
  VERSION, VERSION_CLEAR, EVAL_WORKLOAD, EVAL_WORKLOAD_CLEAR, ACTUAL_WORKLOAD, ACTUAL_WORKLOAD_CLEAR,
  TAG, TAG_CLEAR, ASSOC_TASK, ASSOC_TASK_CANCEL, ASSOC_CASE, ASSOC_CASE_CANCEL,
  START, STOP, GEN_NOW,
  ADD_ASSOC_TARGET, DELETE_ASSOC_TARGET, ASSOC_TARGET,
  COMMENT_ADD, COMMENT_DELETE,
  ESTABLISH_BASELINE;

  public String getValue() {
    return this.name();
  }

  public String getDescMessageKey() {
    return "xcm.altester.activity." + this.name();
  }

  public String getDetailMessageKey() {
    return "xcm.altester.activity.detail." + this.name();
  }

}

