package cloud.xcan.sdf.core.angustester.interfaces.task.facade.dto;

import cloud.xcan.sdf.api.commonlink.apis.StrategyWhenDuplicated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaolong.liu
 */
@Valid
@ApiModel
@Getter
@Setter
@Accessors(chain = true)
public class TaskImportDto {

  @NotNull
  @ApiModelProperty(value = "Import project id", required = true)
  private Long projectId;

  @ApiModelProperty(value = "Import sprint id, it is required for agile project management")
  private Long sprintId;

  @NotNull
  @ApiModelProperty(value = "Strategy for handling duplicate apis. The COVER value overrides the local api, and the IGNORE value ignores the synchronization current api.",
      example = "COVER", required = true)
  private StrategyWhenDuplicated strategyWhenDuplicated;

  @NotNull
  @ApiModelProperty(value = "Import task file, only support excel file.", required = true)
  private MultipartFile file;

}
