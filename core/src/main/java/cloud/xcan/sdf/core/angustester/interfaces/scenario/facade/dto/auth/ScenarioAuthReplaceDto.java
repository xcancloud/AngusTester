package cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.auth;

import cloud.xcan.sdf.core.angustester.domain.scenario.auth.ScenarioPermission;
import cloud.xcan.sdf.web.validator.annotations.CollectionValueNotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@ApiModel
@Setter
@Getter
@Accessors(chain = true)
public class ScenarioAuthReplaceDto {

  @NotEmpty
  @Size(min = 1)
  @CollectionValueNotNull // Fix:: invalid enumeration value is null element
  @ApiModelProperty(value = "Authorization permissions(Operation permission)", required = true)
  private Set<ScenarioPermission> permissions;

}




