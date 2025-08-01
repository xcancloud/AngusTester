package cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class ApisDesignUpdateNameDto {

  @NotNull
  @Schema(description = "API design identifier for name update operation", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotBlank
  @Schema(description = "API design name for identification and organization", requiredMode = RequiredMode.REQUIRED)
  @Length(max = MAX_NAME_LENGTH)
  private String name;

}
