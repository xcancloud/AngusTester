package cloud.xcan.angus.core.tester.interfaces.analysis;


import cloud.xcan.angus.api.gm.analysis.dto.CustomizationSummaryDto;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryDefinitionVo;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.AnalysisCustomizationFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * It will automatically invalid after being consolidated to other package.
 */
@Tag(name = "AnalysisCustomization", description =
    "Customized definition statistical analysis implementation apis, "
        + "used to query supported analysis resource definition information and to retrieve analysis and statistical information")
@Validated
@RestController
@RequestMapping("/api/v1/analysis/customization")
public class AnalysisCustomizationRest {

  @Resource
  private AnalysisCustomizationFacade analysisCustomizationFacade;

  @Operation(summary = "Resource customization analysis definition", operationId = "analysis:customization:summary:definition")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary/definition")
  public ApiLocaleResult<SummaryQueryDefinitionVo> definition() {
    return ApiLocaleResult.success(analysisCustomizationFacade.definitions());
  }

  @Operation(summary = "Resource customization analysis", operationId = "analysis:customization:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary")
  public ApiLocaleResult<Object> summary(
      @Valid @ParameterObject CustomizationSummaryDto dto) {
    return ApiLocaleResult.success(analysisCustomizationFacade.summary(dto));
  }

  @Operation(summary = "Batch resources customization analysis", operationId = "analysis:customization:summary:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary/batch")
  public ApiLocaleResult<Map<String, Object>> summary(
      @Valid @ParameterObject List<CustomizationSummaryDto> dto) {
    return ApiLocaleResult.success(analysisCustomizationFacade.summary(dto));
  }


}
