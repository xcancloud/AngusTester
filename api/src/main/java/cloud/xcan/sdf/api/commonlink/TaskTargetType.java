package cloud.xcan.sdf.api.commonlink;

import cloud.xcan.sdf.spec.experimental.EndpointRegister;
import cloud.xcan.sdf.spec.locale.EnumMessage;

@EndpointRegister
public enum TaskTargetType implements EnumMessage<String> {
  TASK, TASK_SPRINT;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isTask() {
    return this.equals(TASK);
  }

  public boolean isSprint() {
    return this.equals(TASK_SPRINT);
  }

}
