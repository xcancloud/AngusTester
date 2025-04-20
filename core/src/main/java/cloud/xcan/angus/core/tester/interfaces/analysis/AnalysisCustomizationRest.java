package cloud.xcan.angus.core.tester.interfaces.analysis;


import cloud.xcan.angus.api.gm.analysis.dto.CustomizationSummaryDto;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryDefinitionVo;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.AnalysisCustomizationFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * It will automatically invalid after being consolidated to other package.
 */
@Api(tags = "AnalysisCustomization")
@Validated
@RestController
@RequestMapping("/api/v1/analysis/customization")
public class AnalysisCustomizationRest {

  @Resource
  private AnalysisCustomizationFacade analysisCustomizationFacade;

  @Operation(description = "Resource customization analysis definition", operationId = "analysis:customization:summary:definition")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary/definition")
  public ApiLocaleResult<SummaryQueryDefinitionVo> definition() {
    return ApiLocaleResult.success(analysisCustomizationFacade.definitions());
  }

  @Operation(description = "Resource customization analysis", operationId = "analysis:customization:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary")
  public ApiLocaleResult<Object> summary(@Valid CustomizationSummaryDto dto) {
    return ApiLocaleResult.success(analysisCustomizationFacade.summary(dto));
  }

  @Operation(description = "Batch resources customization analysis", operationId = "analysis:customization:summary:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/summary/batch")
  public ApiLocaleResult<Map<String, Object>> summary(
      @Valid @Size(min = 1) List<CustomizationSummaryDto> dtos) {
    return ApiLocaleResult.success(analysisCustomizationFacade.summary(dtos));
  }


}
