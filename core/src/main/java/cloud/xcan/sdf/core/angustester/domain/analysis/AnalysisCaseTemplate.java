package cloud.xcan.sdf.core.angustester.domain.analysis;

import cloud.xcan.sdf.spec.experimental.EndpointRegister;
import cloud.xcan.sdf.spec.locale.EnumMessage;

@EndpointRegister
public enum AnalysisCaseTemplate implements EnumMessage<String> {
  PROGRESS,
  BURNDOWN,
  WORKLOAD,
  OVERDUE_ASSESSMENT,
  SUBMITTED_BUGS,
  TESTING_EFFICIENCY,
  CORE_KPI,
  REVIEW_EFFICIENCY,
  BACKLOG_CASES,
  RECENT_DELIVERY,
  LEAD_TIME,
  UNPLANNED_CASES,
  CASE_GROWTH_TREND,
  RESOURCE_CREATION;
  //CUSTOM_DEFINITION;

  @Override
  public String getValue() {
    return this.name();
  }
}
