package cloud.xcan.angus.core.tester.interfaces.func.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X2;

import cloud.xcan.angus.api.enums.ReviewStatus;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class FuncCaseReviewDto {

  @NotNull
  @Schema(description = "Functional testing case id", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @EnumPart(enumClass = ReviewStatus.class, allowableValues = {"PASSED", "FAILED"})
  @Schema(description = "The result of case review", requiredMode = RequiredMode.REQUIRED)
  private ReviewStatus reviewStatus;

  @Length(max = MAX_DESC_LENGTH_X2)
  @Schema(description = "The remark of case review")
  private String reviewRemark;

}
