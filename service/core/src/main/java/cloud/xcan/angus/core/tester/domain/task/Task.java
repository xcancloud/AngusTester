package cloud.xcan.angus.core.tester.domain.task;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.associate.AssociateUserType;
import cloud.xcan.angus.api.enums.EvalWorkloadMethod;
import cloud.xcan.angus.api.enums.Priority;
import cloud.xcan.angus.api.enums.Result;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.api.pojo.Progress;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.tester.domain.ResourceFavouriteAndFollow;
import cloud.xcan.angus.core.tester.domain.ResourceTagAssoc;
import cloud.xcan.angus.core.tester.domain.activity.MainTargetActivityResource;
import cloud.xcan.angus.core.tester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.angus.core.tester.domain.tag.TagTarget;
import cloud.xcan.angus.core.tester.domain.task.cases.TaskFuncCaseAssoc;
import cloud.xcan.angus.model.script.TestType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Task domain entity representing a comprehensive task management system for R&D and testing activities.
 * <p>
 * This entity serves as the central hub for managing various types of tasks including development tasks,
 * testing tasks, API testing, scenario testing, and bug tracking. It supports hierarchical task structures,
 * sprint management, workload estimation, and comprehensive status tracking.
 * <p>
 * The task entity integrates with multiple subsystems including activity tracking, resource management,
 * tagging system, and execution monitoring. It supports both product backlog and sprint backlog management
 * with flexible assignment and confirmation workflows.
 * <p>
 * Key features include:
 * - Multi-type task support (development, testing, API, scenario)
 * - Sprint and project association
 * - Workload estimation and tracking
 * - Hierarchical task relationships
 * - Execution result tracking
 * - Soft deletion with audit trail
 * - Multi-tenancy support
 *
 * @author Angus Team
 * @since 1.0.0
 */
@Entity
@Table(name = "task")
@SQLRestriction("deleted = 0 AND sprint_deleted =0")
@SQLDelete(sql = "update task set deleted = 1 where id = ?")
@Setter
@Getter
@Accessors(chain = true)
public class Task extends TenantAuditingEntity<Task, Long> implements MainTargetActivityResource,
    TaskAssociateUser, TaskFuncCaseAssoc<Task, Long>, ResourceFavouriteAndFollow<Task, Long>,
    ResourceTagAssoc<Task, Long> {

  /**
   * Primary key identifier for the task.
   */
  @Id
  public Long id;

  /**
   * Human-readable name of the task for identification and display purposes.
   */
  public String name;

  /**
   * Unique code identifier for the task, typically used for external references.
   */
  public String code;

  /**
   * Reference to the project this task belongs to.
   * <p>
   * Tasks are always associated with a project for organizational purposes.
   */
  @Column(name = "project_id")
  private Long projectId;

  /**
   * Reference to the sprint this task is assigned to.
   * <p>
   * Null value indicates the task is in the product backlog or not sprint-assigned.
   */
  @Column(name = "sprint_id")
  private Long sprintId;

  /**
   * Authorization flag indicating whether the task has proper sprint permissions.
   */
  @Column(name = "sprint_auth")
  private Boolean sprintAuth;

  /**
   * Reference to the module or component this task relates to.
   */
  @Column(name = "module_id")
  private Long moduleId;

  /**
   * Software version this task is targeting or associated with.
   */
  @Column(name = "software_version")
  private String softwareVersion;

  /**
   * Backlog type indicator for task categorization.
   * <p>
   * true: Product backlog - long-term planning and feature requests
   * false: Sprint backlog or general project management tasks
   */
  @Column(name = "backlog")
  private Boolean backlog;

  /**
   * Classification of the task type for workflow and processing logic.
   */
  @Column(name = "task_type")
  @Enumerated(EnumType.STRING)
  private TaskType taskType;

  /**
   * Severity level for bug-type tasks, indicating the impact and urgency.
   */
  @Column(name = "bug_level")
  @Enumerated(EnumType.STRING)
  private BugLevel bugLevel;

  /**
   * Type of testing to be performed for test-related tasks.
   */
  @Column(name = "test_type")
  @Enumerated(EnumType.STRING)
  private TestType testType;

  /**
   * Priority level for task scheduling and resource allocation.
   */
  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private Priority priority;

  /**
   * Current status of the task in its lifecycle workflow.
   */
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  /**
   * User ID of the person assigned to execute this task.
   */
  @Column(name = "assignee_id")
  private Long assigneeId;

  /**
   * User ID of the person responsible for confirming task completion.
   * <p>
   * When set, this task requires confirmation before being marked as complete.
   */
  @Column(name = "confirmor_id")
  private Long confirmorId;

  /**
   * User ID of the designated tester for testing-related tasks.
   */
  @Column(name = "tester_id")
  private Long testerId;

  /**
   * Flag indicating whether this task represents a missing bug that was discovered.
   */
  @Column(name = "missing_bug")
  private Boolean missingBug;

  /**
   * Flag indicating whether this task was not originally planned and added ad-hoc.
   */
  @Column(name = "unplanned")
  private Boolean unplanned;

  /**
   * Planned start date for task execution.
   */
  @Column(name = "start_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime startDate;

  /**
   * Deadline for task completion.
   */
  @Column(name = "deadline_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime deadlineDate;

  /**
   * Flag indicating whether the task has exceeded its deadline.
   */
  @Column(name = "overdue")
  private Boolean overdue;

  /**
   * Timestamp when the task was canceled.
   */
  @Column(name = "canceled_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime canceledDate;

  /**
   * Timestamp when the task was confirmed as completed.
   */
  @Column(name = "confirmed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime confirmedDate;

  /**
   * Timestamp when the task was marked as completed.
   */
  @Column(name = "completed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime completedDate;

  /**
   * Timestamp when the task was last processed or updated.
   */
  @Column(name = "processed_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime processedDate;

  /**
   * Counter for the number of task processing failures.
   * <p>
   * Counting rules:
   * - Increment by 1 when task confirmation fails
   * - Increment by 1 when test task execution results in failure
   */
  @Column(name = "fail_num")
  private Integer failNum;

  /**
   * Counter for the total number of times this task has been processed.
   * <p>
   * Counting rules:
   * - Increment by 1 when the task enters the start processing status
   */
  @Column(name = "total_num")
  private Integer totalNum;

  /**
   * Method used for evaluating the workload of this task.
   */
  @Column(name = "eval_workload_method")
  @Enumerated(EnumType.STRING)
  private EvalWorkloadMethod evalWorkloadMethod;

  /**
   * Estimated workload for completing this task (in hours or story points).
   */
  @Column(name = "eval_workload")
  private BigDecimal evalWorkload;

  /**
   * Actual workload spent on completing this task (in hours or story points).
   */
  @Column(name = "actual_workload")
  private BigDecimal actualWorkload;

  /**
   * Reference to the parent task if this is a subtask.
   * <p>
   * Supports hierarchical task structures for complex work breakdown.
   */
  @Column(name = "parent_task_id")
  private Long parentTaskId;

  /**
   * List of file attachments associated with this task.
   * <p>
   * Stored as JSON to accommodate flexible attachment metadata.
   */
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "attachments")
  public List<Attachment> attachments;

  /**
   * Detailed description of the task requirements, acceptance criteria, and notes.
   */
  public String description;

  /**
   * Reference to the primary target entity this task is associated with.
   */
  @Column(name = "target_id")
  private Long targetId;

  /**
   * Reference to the parent of the target entity for hierarchical organization.
   */
  @Column(name = "target_parent_id")
  private Long targetParentId;

  /**
   * Reference to the script used for automated task execution.
   */
  @Column(name = "script_id")
  private Long scriptId;

  /**
   * Result of the last script execution for this task.
   */
  @Column(name = "exec_result")
  @Enumerated(EnumType.STRING)
  private Result execResult;

  /**
   * Error message from the last failed execution attempt.
   */
  @Column(name = "exec_failure_message")
  private String execFailureMessage;

  /**
   * Total number of tests executed for this task.
   */
  @Column(name = "exec_test_num")
  private Integer execTestNum;

  /**
   * Number of failed tests in the last execution.
   */
  @Column(name = "exec_test_failure_num")
  private Integer execTestFailureNum;

  /**
   * Reference to the execution record for detailed execution information.
   */
  @Column(name = "exec_id")
  private Long execId;

  /**
   * Name of the execution for identification purposes.
   */
  @Column(name = "exec_name")
  private String execName;

  /**
   * User ID of the person who initiated the execution.
   */
  @Column(name = "exec_by")
  private Long execBy;

  /**
   * Timestamp of the last execution.
   */
  @Column(name = "exec_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime execDate;

  /**
   * Flag indicating whether the associated sprint has been deleted.
   */
  @Column(name = "sprint_deleted")
  private Boolean sprintDeleted;

  /**
   * User ID of the person who deleted this task.
   */
  @Column(name = "deleted_by")
  private Long deletedBy;

  /**
   * Soft deletion flag to mark tasks as deleted without physical removal.
   */
  @Column(name = "deleted")
  private Boolean deleted;

  /**
   * Timestamp when the task was deleted.
   */
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "deleted_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime deletedDate;

  /**
   * User ID of the person who created this task.
   * <p>
   * Note: Manual assignment due to imported value not taking effect with @CreatedBy
   */
  @Column(name = "created_by")
  //@CreatedBy // Fix: The imported value does not take effect
  private Long createdBy;

  /**
   * Timestamp when the task was created.
   * <p>
   * Note: Manual assignment due to imported value not taking effect with @CreatedDate
   */
  @Column(name = "created_date")
  //@CreatedDate // Fix: The imported value does not take effect
  private LocalDateTime createdDate;

  // Transient fields for runtime data population
  
  /**
   * Display name of the assigned user (populated at runtime).
   */
  @Transient
  private String assigneeName;
  
  /**
   * Avatar URL of the assigned user (populated at runtime).
   */
  @Transient
  private String assigneeAvatar;
  
  /**
   * List of subtasks belonging to this task (populated at runtime).
   */
  @Transient
  private List<TaskInfo> subTasks;
  
  /**
   * List of tasks associated with this task (populated at runtime).
   */
  @Transient
  private List<TaskInfo> assocTasks;
  
  /**
   * List of functional test cases associated with this task (populated at runtime).
   */
  @Transient
  private List<FuncCaseInfo> assocCases;
  
  /**
   * List of tags assigned to this task (populated at runtime).
   */
  @Transient
  private List<TagTarget> tagTargets;
  
  /**
   * Display name of the target entity (populated at runtime).
   */
  @Transient
  private String targetName;
  
  /**
   * Display name of the target's parent entity (populated at runtime).
   */
  @Transient
  private String targetParentName;
  
  /**
   * Current association types for this task (populated at runtime).
   */
  @Transient
  private List<AssociateUserType> currentAssociateType;
  
  /**
   * Number of comments on this task (populated at runtime).
   */
  @Transient
  private int commentNum;
  
  /**
   * Number of remarks on this task (populated at runtime).
   */
  @Transient
  private int remarkNum;
  
  /**
   * Number of activity entries for this task (populated at runtime).
   */
  @Transient
  private int activityNum;
  
  /**
   * Flag indicating whether the current user has marked this task as favorite (populated at runtime).
   */
  @Transient
  private Boolean favourite;
  
  /**
   * Flag indicating whether the current user is following this task (populated at runtime).
   */
  @Transient
  private Boolean follow;
  
  /**
   * Set of referenced task IDs (populated at runtime).
   */
  @Transient
  private LinkedHashSet<Long> refTaskIds;
  
  /**
   * Set of referenced case IDs (populated at runtime).
   */
  @Transient
  private LinkedHashSet<Long> refCaseIds;
  
  /**
   * Progress information for this task (populated at runtime).
   */
  @Transient
  private Progress progress;

  /**
   * Checks if this task requires confirmation before completion.
   * <p>
   * A task is considered a confirmation task if it has a confirmor assigned.
   *
   * @return true if the task has a confirmor assigned, false otherwise
   */
  public boolean isConfirmTask() {
    return nonNull(confirmorId);
  }

  /**
   * Checks if this task has any tags assigned to it.
   *
   * @return true if the task has tags, false otherwise
   */
  public boolean hasTag() {
    return isNotEmpty(tagTargets);
  }

  /**
   * Checks if this task is an API testing task.
   *
   * @return true if the task type is API_TEST, false otherwise
   */
  public boolean isApiTest() {
    return TaskType.API_TEST.equals(taskType);
  }

  /**
   * Checks if this task is a scenario testing task.
   *
   * @return true if the task type is SCENARIO_TEST, false otherwise
   */
  public boolean isScenarioTest() {
    return TaskType.SCENARIO_TEST.equals(taskType);
  }

  /**
   * Checks if this task is any type of testing task.
   * <p>
   * Currently includes both scenario testing and API testing tasks.
   *
   * @return true if the task is a testing task, false otherwise
   */
  public boolean isTestTask() {
    return TaskType.SCENARIO_TEST.equals(taskType) || TaskType.API_TEST.equals(taskType);
  }

  /**
   * Returns the primary identifier for this entity.
   * <p>
   * Required by the parent TenantAuditingEntity class for identity management.
   *
   * @return the task's primary key identifier
   */
  @Override
  public Long identity() {
    return this.id;
  }

  /**
   * Returns the main target ID for activity tracking purposes.
   * <p>
   * For tasks, the main target is the task itself.
   *
   * @return the task's ID as the main target identifier
   */
  @Override
  public Long getMainTargetId() {
    return this.id;
  }

  /**
   * Returns the parent ID for hierarchical organization.
   * <p>
   * Uses sprint ID if available, otherwise falls back to project ID.
   *
   * @return the sprint ID or project ID as the parent identifier
   */
  @Override
  public Long getParentId() {
    return nullSafe(this.sprintId, this.projectId);
  }

  /**
   * Returns the task ID for functional case association.
   * <p>
   * Required by the TaskFuncCaseAssoc interface.
   *
   * @return the task's ID
   */
  @Override
  public Long getTaskId() {
    return this.id;
  }
}
