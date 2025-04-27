package cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.cases;

import static cloud.xcan.angus.api.commonlink.TesterConstant.MAX_OPT_CASE_NUM;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@Setter
@Getter
@Accessors(chain = true)
public class FuncReviewCaseAddDto {

  @NotNull
  @Schema(description = "Review id", requiredMode = RequiredMode.REQUIRED)
  private Long reviewId;

  @NotEmpty
  @Size(max = MAX_OPT_CASE_NUM)
  @Schema(description = "Review case ids", requiredMode = RequiredMode.REQUIRED)
  private LinkedHashSet<Long> caseIds;

}
