package cloud.xcan.sdf.core.angustester.domain.task.follow;


public interface TaskFollowP {

  Long getId();

  Long getProjectId();

  Long getSprintId();

  Long getTaskId();

  String getTaskName();

  String getTaskType();

  String getTaskCode();
}
