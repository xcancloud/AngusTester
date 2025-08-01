package cloud.xcan.angus.core.tester.interfaces.services.facade.dto.config;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HTTP_AUTH_PARAM_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.pojo.auth.SimpleHttpAuth;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class ServicesSyncTestDto {

  @NotEmpty
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "OpenAPI documentation URL for synchronization testing", 
  example = "http://192.168.0.101:1807/v2/api-docs?group=Api", requiredMode = RequiredMode.REQUIRED)
  private String apiDocsUrl;

  @Valid
  @Length(max = MAX_HTTP_AUTH_PARAM_NUM)
  @Schema(description = "Authentication configuration for protected synchronization URLs")
  private List<SimpleHttpAuth> auths;

}
