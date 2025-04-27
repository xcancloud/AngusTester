package cloud.xcan.angus.core.tester.domain.task.summary;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.api.commonlink.associate.AssociateUserType;
import cloud.xcan.angus.api.enums.EvalWorkloadMethod;
import cloud.xcan.angus.api.enums.Priority;
import cloud.xcan.angus.api.enums.Result;
import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.api.pojo.Progress;
import cloud.xcan.angus.core.tester.domain.task.BugLevel;
import cloud.xcan.angus.core.tester.domain.task.TaskStatus;
import cloud.xcan.angus.core.tester.domain.task.TaskType;
import cloud.xcan.angus.core.tester.interfaces.func.facade.vo.FuncCaseInfoVo;
import cloud.xcan.angus.core.tester.interfaces.task.facade.vo.TaskInfoVo;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.IdAndNameVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TaskDetailSummary {

  private Long id;

  private String name;

  private String code;

  private Long projectId;

  @NameJoinField(id = "projectId", repository = "projectRepo")
  private String projectName;

  private Long sprintId;

  @NameJoinField(id = "sprintId", repository = "taskSprintRepo")
  private String sprintName;

  private Long moduleId;

  @NameJoinField(id = "moduleId", repository = "moduleRepo")
  private String moduleName;

  @Schema(description = "Backlog flag. true: Sprint backlog, false: Product backlog")
  private Boolean backlog;

  private Priority priority;

  private TaskStatus status;

  private TaskType taskType;

  private BugLevel bugLevel;

  private TestType testType;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime startDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime deadlineDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime canceledDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  public LocalDateTime confirmedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime completedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  public LocalDateTime processedDate;

  private Long assigneeId;

  @NameJoinField(id = "assigneeId", repository = "commonUserBaseRepo")
  private String assigneeName;

  private Long confirmorId;

  @NameJoinField(id = "confirmorId", repository = "commonUserBaseRepo")
  private String confirmorName;

  private Long testerId;

  @NameJoinField(id = "testerId", repository = "commonUserBaseRepo")
  private String testerName;

  private Boolean missingBug;

  private Boolean unplanned;

  private Progress subTaskProgress;

  private List<TaskInfoVo> subTaskInfos;

  private List<TaskInfoVo> refTaskInfos;

  private List<FuncCaseInfoVo> refCaseInfos;

  private List<IdAndNameVo> tags;

  private EvalWorkloadMethod evalWorkloadMethod;

  private BigDecimal evalWorkload;

  private BigDecimal actualWorkload;

  @Schema(description="The number of task processing failures")
  private int failNum;

  @Schema(description="The total number of tasks processed")
  private int totalNum;

  private List<Attachment> attachments;

  private String description;

  private List<AssociateUserType> currentAssociateType;

  private Boolean confirmTask;

  private Boolean overdue;

  private Long targetId;

  private String targetName;

  private Long targetParentId;

  private String targetParentName;

  private Long scriptId;

  @NameJoinField(id = "scriptId", repository = "scriptInfoRepo")
  private String scriptName;

  private Result execResult;

  private String execFailureMessage;

  private Integer execTestNum;

  private Integer execTestFailureNum;

  private Long execId;

  private String execName;

  private Long execBy;

  @NameJoinField(id = "execBy", repository = "commonUserBaseRepo")
  private String execByName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime execDate;

  private Boolean favourite;

  private Boolean follow;

  private int commentNum;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  private LocalDateTime lastModifiedDate;

}
