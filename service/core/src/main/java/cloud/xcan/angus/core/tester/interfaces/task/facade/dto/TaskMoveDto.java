package cloud.xcan.angus.core.tester.interfaces.task.facade.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author XiaoLong Liu
 */
@Getter
@Setter
@Accessors(chain = true)
public class TaskMoveDto {

  @NotNull
  @Size(min = 1)
  @Schema(description = "Source task identifiers for bulk movement operation", requiredMode = RequiredMode.REQUIRED)
  private List<Long> taskIds;

  //@NotNull
  @Schema(description = "Target sprint identifier for task reassignment. Empty value moves tasks to product backlog"/*, requiredMode = RequiredMode.REQUIRED*/)
  private Long targetSprintId;

}
