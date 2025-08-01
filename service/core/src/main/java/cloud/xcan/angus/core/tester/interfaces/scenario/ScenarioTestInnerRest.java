package cloud.xcan.angus.core.tester.interfaces.scenario;


import cloud.xcan.angus.core.tester.interfaces.scenario.facade.ScenarioTestFacade;
import cloud.xcan.angus.model.scenario.ScenarioTestCount;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.dto.OrgAndDateFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scenario Test - Internal", description = "Scenario Testing Internal API - System-level interfaces for managing and auditing automated test scenario executions with internal monitoring capabilities.")
@Validated
@RestController
@RequestMapping("/innerapi/v1")
public class ScenarioTestInnerRest {

  @Resource
  private ScenarioTestFacade scenarioTestFacade;

  @Operation(summary = "Query enabled test types for scenario",
      description = "Retrieve all enabled test types configured for the specified scenario.",
      operationId = "scenario:test:enabled:find:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled test types retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Scenario not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/scenario/{id}/test/enabled")
  public ApiLocaleResult<List<TestType>> testEnabledFind(
      @Parameter(name = "id", description = "Scenario identifier for test type query", required = true) @PathVariable("id") Long scenario) {
    return ApiLocaleResult.success(scenarioTestFacade.testEnabledFind(scenario));
  }

  @Operation(summary = "Query project test scenario count",
      description = "Get comprehensive count of test scenarios and their execution statistics for the project.",
      operationId = "project:test:scenario:count:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Project test scenario count retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Project not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/project/{id}/test/scenario/count")
  public ApiLocaleResult<ScenarioTestCount> countProjectTestScenarios(
      @Parameter(name = "id", description = "Project identifier for test scenario count", required = true) @PathVariable("id") Long projectId,
      @ParameterObject OrgAndDateFilterDto dto) {
    return ApiLocaleResult.success(scenarioTestFacade.countProjectTestScenarios(projectId, dto));
  }

}
