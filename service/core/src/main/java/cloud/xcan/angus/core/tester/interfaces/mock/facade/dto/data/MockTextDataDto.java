package cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.data;

import static cloud.xcan.angus.api.commonlink.TesterConstant.MAX_MOCK_FUNC_ITERATIONS;
import static cloud.xcan.angus.api.commonlink.TesterConstant.MAX_MOCK_FUNC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class MockTextDataDto {

  @Length(max = MAX_PARAM_NAME_LENGTH)
  @Schema(example = "Greeting", description = "Business unique identification key for output mapping")
  private String outKey;

  @NotBlank
  @Length(max = MAX_MOCK_FUNC_LENGTH)
  @Schema(example = "Good morning, Mr. @Name()", description = "Text content with embedded JMock function expressions for dynamic generation", requiredMode = RequiredMode.REQUIRED)
  private String text;

  @NotNull
  @Min(1)
  @Max(MAX_MOCK_FUNC_ITERATIONS)
  @Schema(description = "Number of text iterations with default value of 1", requiredMode = RequiredMode.REQUIRED)
  private Integer iterations = 1;

}




