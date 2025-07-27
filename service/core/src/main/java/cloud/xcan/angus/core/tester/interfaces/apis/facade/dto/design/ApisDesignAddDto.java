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
public class ApisDesignAddDto {

  @NotNull
  @Schema(description = "Project identifier for API design association", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @NotBlank
  @Schema(description = "API design name for identification and organization", requiredMode = RequiredMode.REQUIRED)
  @Length(max = MAX_NAME_LENGTH)
  private String name;

  @NotBlank
  @Schema(description = "OpenAPI specification version for design compatibility",
      allowableValues = "3.0.0, 3.0.1, 3.0.2, 3.0.3, 3.1.0", example = "3.0.1", requiredMode = RequiredMode.REQUIRED)
  private String openapiSpecVersion;

}
