package cloud.xcan.angus.core.tester.domain.analysis;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;

@EndpointRegister
public enum AnalysisTaskTemplateDesc implements EnumMessage<String> {
  PROGRESS,
  BURNDOWN,
  WORKLOAD,
  OVERDUE_ASSESSMENT,
  BUGS,
  HANDLING_EFFICIENCY,
  CORE_KPI,
  FAILURES,
  BACKLOG_TASKS,
  RECENT_DELIVERY,
  LEAD_TIME,
  UNPLANNED_TASKS,
  TASK_GROWTH_TREND,
  RESOURCE_CREATION,
  CUSTOM_DEFINITION;

  @Override
  public String getValue() {
    return this.name();
  }
}
