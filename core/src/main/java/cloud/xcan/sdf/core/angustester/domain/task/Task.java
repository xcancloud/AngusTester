package cloud.xcan.sdf.core.angustester.domain.task;


import static cloud.xcan.sdf.spec.SpecConstant.DateFormat.DATE_FMT;
import static java.util.Objects.nonNull;

import cloud.xcan.sdf.api.commonlink.associate.AssociateUserType;
import cloud.xcan.sdf.api.enums.EvalWorkloadMethod;
import cloud.xcan.sdf.api.enums.Priority;
import cloud.xcan.sdf.api.enums.Result;
import cloud.xcan.sdf.api.pojo.Attachment;
import cloud.xcan.sdf.core.angustester.domain.ResourceFavouriteAndFollow;
import cloud.xcan.sdf.core.angustester.domain.ResourceTagAssoc;
import cloud.xcan.sdf.core.angustester.domain.activity.TaskActivityResource;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.sdf.core.angustester.domain.tag.TagTarget;
import cloud.xcan.sdf.core.angustester.domain.task.cases.TaskFuncCaseAssoc;
import cloud.xcan.sdf.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.sdf.model.script.TestType;
import cloud.xcan.sdf.spec.utils.ObjectUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "task")
@Where(clause = "deleted_flag = 0 AND sprint_deleted_flag =0")
@SQLDelete(sql = "update task set deleted_flag = 1 where id = ?")
@Setter
@Getter
@Accessors(chain = true)
public class Task extends TenantAuditingEntity<Task, Long> implements TaskActivityResource,
    TaskAssociateUser, TaskFuncCaseAssoc<Task, Long>, ResourceFavouriteAndFollow<Task, Long>,
    ResourceTagAssoc<Task, Long> {

  @Id
  public Long id;

  public String name;

  public String code;

  @Column(name = "project_id")
  private Long projectId;

  @Column(name = "sprint_id")
  private Long sprintId;

  @Column(name = "sprint_auth_flag")
  private Boolean sprintAuthFlag;

  @Column(name = "module_id")
  private Long moduleId;

  /**
   * true: Product backlog, false: Sprint backlog or is general project management.
   */
  @Column(name = "backlog_flag")
  private Boolean backlogFlag;

  @Column(name = "task_type")
  @Enumerated(EnumType.STRING)
  private TaskType taskType;

  @Column(name = "bug_level")
  @Enumerated(EnumType.STRING)
  private BugLevel bugLevel;

  @Column(name = "test_type")
  @Enumerated(EnumType.STRING)
  private TestType testType;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private Priority priority;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @Column(name = "software_version")
  private String softwareVersion;

  @Column(name = "assignee_id")
  private Long assigneeId;

  @Column(name = "confirmor_id")
  private Long confirmorId;

  @Column(name = "tester_id")
  private Long testerId;

  @Column(name = "missing_bug_flag")
  private Boolean missingBugFlag;

  @Column(name = "unplanned_flag")
  private Boolean unplannedFlag;

  @Column(name = "start_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime startDate;

  @Column(name = "deadline_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime deadlineDate;

  @Column(name = "overdue_flag")
  private Boolean overdueFlag;

  @Column(name = "canceled_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime canceledDate;

  @Column(name = "confirmed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime confirmedDate;

  @Column(name = "completed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime completedDate;

  @Column(name = "processed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime processedDate;

  /**
   * The number of task processing failures, counting rules:
   *
   * <pre>
   * - Increment 1 when the task confirmation fails;
   * - Increment 1 when the test task execution test result fails.
   * </pre>
   */
  @Column(name = "fail_num")
  private Integer failNum;

  /**
   * The total number of tasks processed, counting rules:
   *
   * <pre>
   * Increment 1 when the task enters the start processing status.
   * </pre>
   */
  @Column(name = "total_num")
  private Integer totalNum;

  @Column(name = "eval_workload_method")
  @Enumerated(EnumType.STRING)
  private EvalWorkloadMethod evalWorkloadMethod;

  @Column(name = "eval_workload")
  private BigDecimal evalWorkload;

  @Column(name = "actual_workload")
  private BigDecimal actualWorkload;

  @Column(name = "parent_task_id")
  private Long parentTaskId;

  @Type(type = "json")
  @Column(columnDefinition = "json", name = "attachments")
  public List<Attachment> attachments;

  @Column(name = "description")
  public String description;

  @Column(name = "target_id")
  private Long targetId;

  @Column(name = "target_parent_id")
  private Long targetParentId;

  @Column(name = "script_id")
  private Long scriptId;

  @Column(name = "exec_result")
  @Enumerated(EnumType.STRING)
  private Result execResult;

  @Column(name = "exec_failure_message")
  private String execFailureMessage;

  @Column(name = "exec_test_num")
  private Integer execTestNum;

  @Column(name = "exec_test_failure_num")
  private Integer execTestFailureNum;

  @Column(name = "exec_id")
  private Long execId;

  @Column(name = "exec_name")
  private String execName;

  @Column(name = "exec_by")
  private Long execBy;

  @Column(name = "exec_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime execDate;

  @Column(name = "sprint_deleted_flag")
  private Boolean sprintDeletedFlag;

  @Column(name = "deleted_by")
  private Long deletedBy;

  @Column(name = "deleted_flag")
  private Boolean deletedFlag;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "deleted_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime deletedDate;

  @Column(name = "created_by")
  //@CreatedBy // Fix: The imported value does not take effect
  private Long createdBy;

  @Column(name = "created_date")
  //@CreatedDate // Fix: The imported value does not take effect
  private LocalDateTime createdDate;

  @Transient
  private String assigneeName;
  @Transient
  private String assigneeAvatar;
  @Transient
  private List<TaskInfo> subTasks;
  @Transient
  private List<TaskInfo> assocTasks;
  @Transient
  private List<FuncCaseInfo> assocCases;
  @Transient
  private List<TagTarget> tagTargets;
  @Transient
  private String targetName;
  @Transient
  private String targetParentName;
  @Transient
  private List<AssociateUserType> currentAssociateType;
  @Transient
  private int commentNum;
  @Transient
  private int remarkNum;
  @Transient
  private Boolean favouriteFlag;
  @Transient
  private Boolean followFlag;
  @Transient
  private LinkedHashSet<Long> refTaskIds;
  @Transient
  private LinkedHashSet<Long> refCaseIds;

  public boolean isConfirmTask() {
    return nonNull(confirmorId);
  }

  public boolean hasTag() {
    return ObjectUtils.isNotEmpty(tagTargets);
  }

  public boolean isApiTest() {
    return TaskType.API_TEST.equals(taskType);
  }

  public boolean isScenarioTest() {
    return TaskType.SCENARIO_TEST.equals(taskType);
  }

  public boolean isTestTask() {
    return TaskType.SCENARIO_TEST.equals(taskType) || TaskType.API_TEST.equals(taskType);
  }

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public Long getTaskId() {
    return id;
  }

  @Override
  public Long getParentId() {
    return targetParentId;
  }

}
