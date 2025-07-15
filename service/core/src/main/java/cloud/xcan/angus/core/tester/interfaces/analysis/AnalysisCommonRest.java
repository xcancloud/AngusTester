package cloud.xcan.angus.core.tester.interfaces.analysis;


import cloud.xcan.angus.core.tester.domain.data.DataResourcesCreationCount;
import cloud.xcan.angus.core.tester.domain.kanban.TestScenarioCount;
import cloud.xcan.angus.core.tester.domain.mock.service.MockServiceCount;
import cloud.xcan.angus.core.tester.domain.scenario.count.ScenarioResourcesCreationCount;
import cloud.xcan.angus.core.tester.domain.script.count.ScriptCount;
import cloud.xcan.angus.core.tester.domain.script.count.ScriptResourcesCreationCount;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.AnalysisCommonFacade;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.MockServiceCountDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.ResourcesStatisticsDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.ScriptStatisticsDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analysis - Common", description = "Public Resource Analytics - Aggregated analytics for shared infrastructure resources (scenario, script, data, etc)")
@Validated
@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisCommonRest {

  @Resource
  private AnalysisCommonFacade analysisAngusFacade;

  @Operation(summary = "Scenario creation resources count statistics", operationId = "analysis:scenario:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/scenario/resources/count")
  public ApiLocaleResult<ScenarioResourcesCreationCount> scenarioResourcesStatistics(
      @Valid @ParameterObject ResourcesStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.scenarioResourcesStatistics(dto));
  }

  @Operation(summary = "Query the test results of scenario", operationId = "analysis:scenario:test:result:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/scenario/test/result/count")
  public ApiLocaleResult<TestScenarioCount> testResult(
      @Valid @ParameterObject ResourcesStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.testResult(dto));
  }

  @Operation(summary = "Script creation resources count statistics", operationId = "analysis:script:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/script/resources/count")
  public ApiLocaleResult<ScriptResourcesCreationCount> scriptResourcesStatistics(
      @Valid @ParameterObject ResourcesStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.scriptResourcesStatistics(dto));
  }

  @Operation(summary = "Script resources count statistics", operationId = "analysis:script:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/script/count")
  public ApiLocaleResult<ScriptCount> scriptCountStatistics(
      @Valid @ParameterObject ScriptStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.scriptCountStatistics(dto));
  }

  @Operation(summary = "Mock service resources and mock status count statistics", operationId = "analysis:mock:service:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/mock/service/count")
  public ApiLocaleResult<MockServiceCount> mockServiceCount(
      @Valid @ParameterObject MockServiceCountDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.mockServiceCountStatistics(dto));
  }

  @Operation(summary = "Data creation resources statistics", operationId = "analysis:data:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/data/resources")
  public ApiLocaleResult<DataResourcesCreationCount> dataResourcesStatistics(
      @Valid @ParameterObject ResourcesStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFacade.dataResourcesStatistics(dto));
  }

}
