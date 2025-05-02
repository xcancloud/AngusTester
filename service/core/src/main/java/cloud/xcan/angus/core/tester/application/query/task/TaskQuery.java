package cloud.xcan.angus.core.tester.application.query.task;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.domain.ResourceFavouriteAndFollow;
import cloud.xcan.angus.core.tester.domain.activity.Activity;
import cloud.xcan.angus.core.tester.domain.task.Task;
import cloud.xcan.angus.core.tester.domain.task.TaskAssociateUser;
import cloud.xcan.angus.core.tester.domain.task.TaskInfo;
import cloud.xcan.angus.core.tester.domain.task.TaskType;
import cloud.xcan.angus.core.tester.domain.task.count.BackloggedOverview;
import cloud.xcan.angus.core.tester.domain.task.count.BugOverview;
import cloud.xcan.angus.core.tester.domain.task.count.BurnDownChartOverview;
import cloud.xcan.angus.core.tester.domain.task.count.CoreKpiOverview;
import cloud.xcan.angus.core.tester.domain.task.count.FailureAssessmentOverview;
import cloud.xcan.angus.core.tester.domain.task.count.GrowthTrendOverview;
import cloud.xcan.angus.core.tester.domain.task.count.LeadTimeOverview;
import cloud.xcan.angus.core.tester.domain.task.count.OverdueAssessmentOverview;
import cloud.xcan.angus.core.tester.domain.task.count.ProcessingEfficiencyOverview;
import cloud.xcan.angus.core.tester.domain.task.count.ProgressOverview;
import cloud.xcan.angus.core.tester.domain.task.count.RecentDeliveryOverview;
import cloud.xcan.angus.core.tester.domain.task.count.ResourceCreationOverview;
import cloud.xcan.angus.core.tester.domain.task.count.TaskAssigneeCount;
import cloud.xcan.angus.core.tester.domain.task.count.TaskAssigneeProgressCount;
import cloud.xcan.angus.core.tester.domain.task.count.TaskCount;
import cloud.xcan.angus.core.tester.domain.task.count.TaskLastResourceCreationCount;
import cloud.xcan.angus.core.tester.domain.task.count.TesterSubmittedBugOverview;
import cloud.xcan.angus.core.tester.domain.task.count.UnplannedWorkOverview;
import cloud.xcan.angus.core.tester.domain.task.count.WorkloadOverview;
import cloud.xcan.angus.core.tester.domain.task.sprint.TaskSprint;
import cloud.xcan.angus.core.tester.domain.task.summary.TaskAssigneeWorkSummary;
import cloud.xcan.angus.core.tester.domain.task.summary.TaskProjectWorkSummary;
import cloud.xcan.angus.core.tester.domain.task.summary.TaskSprintWorkSummary;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.annotations.NonNullable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TaskQuery {

  Task detail(Long taskId);

  TaskCount countStatistics(Set<SearchCriteria> criteria);

  Page<Task> find(GenericSpecification<Task> spec, PageRequest pageable);

  List<TaskInfo> notAssociatedSubtask(Long id, Long moduleId);

  List<TaskInfo> notAssociatedTaskInCase(Long caseId, @Nullable Long moduleId,
      @Nullable TaskType taskType);

  List<TaskInfo> notAssociatedTaskInTask(Long taskId, @Nullable Long moduleId,
      @Nullable TaskType taskType);

  List<TaskInfo> assocList(TaskType taskType, Long targetId);

  TaskLastResourceCreationCount creationResourcesStatistics(Long projectId, Long sprintId,
      AuthObjectType creatorObjectType, Long creatorObjectId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd, boolean joinSprint, boolean joinMeeting);

  List<TaskAssigneeCount> assigneeSummaryStatistics(Long projectId, Long sprintId);

  List<TaskAssigneeProgressCount> assigneeProgressStatistics(Long projectId, Long sprintId);

  TaskProjectWorkSummary projectWorkStatistics(Long projectId);

  TaskSprintWorkSummary sprintWorkStatistics(Long sprintId);

  TaskAssigneeWorkSummary assigneeWorkStatistics(Long projectId, Long sprintId, Long userId);

  ProgressOverview progress(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  BurnDownChartOverview burndownChart(@NonNullable Long projectId, Long sprintId,
      AuthObjectType assigneeOrgType, Long assigneeOrgId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd, boolean joinAssigneeDetail, boolean joinDataDetail);

  WorkloadOverview workload(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  OverdueAssessmentOverview overdueAssessment(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  BugOverview bug(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  ProcessingEfficiencyOverview processingEfficiency(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  CoreKpiOverview coreKpi(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  FailureAssessmentOverview failureAssessment(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  BackloggedOverview backloggedWork(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  RecentDeliveryOverview recentDelivery(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  LeadTimeOverview leadTime(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  UnplannedWorkOverview unplannedWork(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  TesterSubmittedBugOverview submittedBug(@NonNullable Long projectId,
      Long sprintId, AuthObjectType creatorOrgType, Long creatorOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  GrowthTrendOverview growthTrend(@NonNullable Long projectId,
      Long sprintId, AuthObjectType assigneeOrgType, Long assigneeOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinAssigneeDetail, boolean joinDataDetail);

  ResourceCreationOverview resourceCreation(@NonNullable Long projectId,
      Long sprintId, AuthObjectType creatorOrgType, Long creatorOrgId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd,
      boolean joinCreatorDetail, boolean joinDataDetail);

  TaskInfo findLeastByProjectId(Long projectId);

  Task checkAndFind(Long id);

  TaskInfo checkAndFindInfo(Long id);

  List<TaskInfo> checkAndFindInfo(Collection<Long> ids);

  List<Task> checkAndFind(Collection<Long> ids);

  Map<String, List<TaskInfo>> checkAndFindByProjectAndName(Long projectId, Set<String> names);

  Map<String, List<TaskInfo>> checkAndFindByPlanAndName(Long sprintId, Set<String> names);

  void checkSubTasksIsCompleted(Long projectId, Long id);

  void checkUpdateParentNotCircular(Long projectId, List<Task> tasks);

  List<TaskInfo> findSub(Long taskId);

  List<TaskInfo> findAllSubInfo(Long projectId, Collection<Long> taskIds);

  List<Task> findAllSub(Long projectId, Collection<Long> taskIds);

  List<Long> findAllSubIds(Long projectId, Collection<Long> taskIds);

  Long countByProjectId(Long id);

  List<Long> findAllIdByProjectIdIn(List<Long> projectIds);

  Set<Long> getAssociateUsersBySprintId(Long sprintId);

  Set<Long> getAssociateUsersByTaskInfo(List<? extends TaskAssociateUser> tasksDb);

  boolean hasModifyAttachments(List<Attachment> attachments, Task taskDb);

  void checkAssigneeUserPermission(Task taskDb);

  void checkConfirmorUserPermission(Task taskDb);

  void checkTaskExists(List<Long> reqTaskIds, List<Task> taskDbs);

  void checkTaskOpenStatus(List<Task> taskDbs);

  void checkAddNameExists(Long projectId, TaskSprint sprintDb, String name);

  void checkUpdateNameExists(Long projectId, Long sprintId, String name, Long taskId);

  void checkTargetTaskExists(Long targetId, TestType testType, TaskType taskType);

  void checkQuota(Long springId, int incr);

  void setFavourite(List<? extends ResourceFavouriteAndFollow<?, ?>> tasks);

  void setFollow(List<? extends ResourceFavouriteAndFollow<?, ?>> tasks);

  void setCurrentRoles(List<Task> tasks);

  void setApiTargetName(List<Task> tasks);

  void setScenarioTargetName(List<Task> tasks);

  void setTaskProgress(List<Task> tasks);

  void setTaskInfoProgress(List<TaskInfo> tasks);

  void assembleAndSendModifyNoticeEvent(List<Task> tasksDb, List<Activity> activities);

  void assembleAndSendModifyNoticeEvent(Task taskDb, Activity activity);

  void assembleAndSendModifyNoticeEvent(TaskInfo taskDb, Activity activity);

  void assembleAndSendModifyAssigneeNoticeEvent(Task taskDb);

  void assembleAndSendPendingConfirmationNoticeEvent(Task taskDb);

}
