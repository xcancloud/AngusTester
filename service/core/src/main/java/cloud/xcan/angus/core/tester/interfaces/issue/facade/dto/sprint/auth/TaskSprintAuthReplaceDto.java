package cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.sprint.auth;

import cloud.xcan.angus.core.tester.domain.issue.sprint.TaskSprintPermission;
import cloud.xcan.angus.validator.CollectionValueNotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TaskSprintAuthReplaceDto {

  @NotEmpty
  @Size(min = 1)
  @CollectionValueNotNull // Fix:: invalid enumeration value is null element
  @Schema(description = "Authorization permissions for sprint access control and operation rights", requiredMode = RequiredMode.REQUIRED)
  private Set<TaskSprintPermission> permissions;

}




