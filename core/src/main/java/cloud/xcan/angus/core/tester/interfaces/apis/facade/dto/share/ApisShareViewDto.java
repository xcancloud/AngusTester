package cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.share;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PUBLIC_TOKEN_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@Getter
@Setter
@Accessors(chain = true)
public class ApisShareViewDto {

  @NotNull
  @Schema(description = "Share id", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotEmpty
  @Length(max = MAX_PUBLIC_TOKEN_LENGTH)
  @Schema(description = "Share public access token", requiredMode = RequiredMode.REQUIRED)
  private String pat;

}
