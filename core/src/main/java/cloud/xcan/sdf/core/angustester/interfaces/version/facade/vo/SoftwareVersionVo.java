package cloud.xcan.sdf.core.angustester.interfaces.version.facade.vo;

import cloud.xcan.sdf.api.NameJoinField;
import cloud.xcan.sdf.core.angustester.domain.task.count.ProgressCount;
import cloud.xcan.sdf.core.angustester.domain.version.SoftwareVersionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SoftwareVersionVo {

  private Long id;

  private Long projectId;

  private String name;

  private LocalDateTime startDate;

  private LocalDateTime releaseDate;

  private SoftwareVersionStatus status;

  private String description;

  private ProgressCount progress;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;
}
