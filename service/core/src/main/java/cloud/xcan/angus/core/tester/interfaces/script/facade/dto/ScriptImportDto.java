package cloud.xcan.angus.core.tester.interfaces.script.facade.dto;


import static cloud.xcan.angus.spec.experimental.BizConstant.ANGUS_SCRIPT_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X4;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Accessors(chain = true)
public class ScriptImportDto {

  @NotNull
  @Schema(description = "Project identifier for script association", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Script name for identification and management", example = "script-01")
  private String name;

  @Schema(type = "string", format = "binary", description = "Script file for import processing") // TODO 10MB
  private MultipartFile file;

  @Length(max = ANGUS_SCRIPT_LENGTH) // 10MB
  @Schema(description = "Script content in YAML or JSON format for import processing")
  private String content;

  @Length(max = MAX_DESC_LENGTH_X4)
  @Schema(description = "Script description for detailed information")
  private String description;
}
