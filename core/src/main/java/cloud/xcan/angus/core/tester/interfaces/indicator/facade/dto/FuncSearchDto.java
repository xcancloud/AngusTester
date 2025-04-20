package cloud.xcan.angus.core.tester.interfaces.indicator.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Valid

@Setter
@Getter
@Accessors(chain = true)
public class FuncSearchDto extends PageQuery {

  @Schema(description = "Functionality indicator id")
  private Long id;

  @NotNull
  @EnumPart(enumClass = CombinedTargetType.class, allowableValues = {"SERVICE", "API"})
  @Schema(allowableValues = "SERVICE,API", requiredMode = RequiredMode.REQUIRED)
  private CombinedTargetType targetType;

  @Schema(description = "Required when app administrators query all functionality indicator")
  private Boolean admin;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Target name")
  private String targetName;

  @Schema(description = "Functionality indicator creator id")
  private Long createdBy;

  @Schema(description = "Functionality indicator creation time")
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}



