package cloud.xcan.sdf.core.angustester.domain.func.plan;

import cloud.xcan.sdf.spec.experimental.EndpointRegister;
import cloud.xcan.sdf.spec.locale.EnumMessage;

@EndpointRegister
public enum FuncPlanStatus implements EnumMessage<String> {
  PENDING, IN_PROGRESS, COMPLETED, BLOCKED;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isCompleted() {
    return this.equals(COMPLETED);
  }

  public boolean allowStart() {
    return PENDING.equals(this) || COMPLETED.equals(this) || BLOCKED.equals(this);
  }

  public boolean allowEnd() {
    return IN_PROGRESS.equals(this);
  }

  public boolean allowBlock() {
    return PENDING.equals(this) || IN_PROGRESS.equals(this);
  }

  public boolean isNotInProcess() {
    return this.equals(PENDING) || this.equals(COMPLETED);
  }

  public boolean isComplete() {
    return this.equals(COMPLETED);
  }

  public boolean isStarted(){
    return !this.equals(PENDING);
  }

  public boolean isSupportInReview(){
    return this.equals(PENDING) || this.equals(COMPLETED) || this.equals(IN_PROGRESS);
  }
}
