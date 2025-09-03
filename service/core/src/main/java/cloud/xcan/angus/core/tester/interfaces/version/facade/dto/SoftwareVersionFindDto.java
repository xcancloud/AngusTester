package cloud.xcan.angus.core.tester.interfaces.version.facade.dto;

import cloud.xcan.angus.core.tester.domain.version.SoftwareVersionStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class SoftwareVersionFindDto extends PageQuery {

  @NotNull
  @Schema(description = "Project identifier for version filtering and organization", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @Schema(description = "Version identifier for specific version lookup")
  private Long id;

  @Schema(description = "Version name for partial matching search")
  private String name;

  @Schema(description = "Version status for lifecycle filtering")
  private SoftwareVersionStatus status;

  @Schema(description = "Version created by for audit filtering")
  private Long createdBy;

  @Schema(description = "Version creation timestamp for timeline filtering")
  private LocalDateTime createdDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
