package cloud.xcan.angus.core.tester.interfaces.version.facade.vo;

import cloud.xcan.angus.core.tester.domain.issue.TaskStatus;
import cloud.xcan.angus.core.tester.domain.issue.count.ProgressCount;
import cloud.xcan.angus.core.tester.domain.issue.summary.TaskSummary;
import cloud.xcan.angus.core.tester.domain.version.SoftwareVersionStatus;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SoftwareVersionDetailVo {

  private Long id;

  private Long projectId;

  private String name;

  private LocalDateTime startDate;

  private LocalDateTime releaseDate;

  private SoftwareVersionStatus status;

  private String description;

  private ProgressCount progress;

  private Map<TaskStatus, List<TaskSummary>> taskByStatus;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;
}
