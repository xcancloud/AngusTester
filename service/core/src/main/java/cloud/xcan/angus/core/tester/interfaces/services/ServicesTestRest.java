package cloud.xcan.angus.core.tester.interfaces.services;


import cloud.xcan.angus.api.commonlink.exec.result.ExecApisResultInfo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.test.ApisTestScriptGenerateDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.ServicesSchemaFacade;
import cloud.xcan.angus.core.tester.interfaces.services.facade.ServicesTestFacade;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.test.ServicesTestTaskGenerateDto;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.model.services.ApisTestCount;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.dto.OrgAndDateFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ServicesTest", description = "API Test and Analytics - Configure and analyze user-initiated API test and results")
@Validated
@RestController
@RequestMapping("/api/v1")
public class ServicesTestRest {

  @Resource
  private ServicesTestFacade servicesTestFacade;

  @Resource
  private ServicesSchemaFacade servicesSchemaFacade;

  @Operation(summary = "Enable or disable the testing of service apis",
      description = "After enabled, the test will be marked as a mandatory activity and the results will be included in the performance analysis",
      operationId = "services:test:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/services/{id}/test/enabled")
  public ApiLocaleResult<?> testEnabled(
      @Parameter(name = "id", required = true) @PathVariable("id") Long serviceId,
      @Valid @NotEmpty @Parameter(description = "Apis test type", required = true) @RequestParam(value = "testTypes") HashSet<TestType> testTypes,
      @Valid @NotNull @Parameter(name = "enabled", description = "Enabled or Disabled", required = true) @RequestParam(value = "enabled") Boolean enabled) {
    servicesTestFacade.testEnabled(serviceId, testTypes, enabled);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "The api testing count of service", operationId = "services:test:apis:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/services/{id}/test/apis/count")
  public ApiLocaleResult<ApisTestCount> countServiceTestApis(
      @Parameter(name = "id", required = true) @PathVariable("id") Long serviceId,
      @ParameterObject OrgAndDateFilterDto dto) {
    return ApiLocaleResult.success(servicesTestFacade.countServiceTestApis(serviceId, dto));
  }

  @Operation(summary = "The api testing count of project", operationId = "project:test:apis:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/project/{id}/test/apis/count")
  public ApiLocaleResult<ApisTestCount> countProjectTestApis(
      @Parameter(name = "id", required = true) @PathVariable("id") Long projectId,
      @ParameterObject OrgAndDateFilterDto dto) {
    return ApiLocaleResult.success(servicesTestFacade.countProjectTestApis(projectId, dto));
  }

  @Operation(summary = "Configure and generate the testing scripts of service", operationId = "services:test:script:generate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Configure and generate successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/services/{id}/test/script/generate")
  public ApiLocaleResult<?> scriptGenerate(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId,
      @Valid @NotEmpty @RequestBody Set<ApisTestScriptGenerateDto> dto) {
    servicesTestFacade.scriptGenerate(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete the testing scripts of service", operationId = "services:test:script:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/services/{id}/test/script")
  public void scriptDelete(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId,
      @Parameter(name = "testTypes", description = "Test type", required = true) @RequestParam("testTypes") HashSet<TestType> testTypes) {
    servicesTestFacade.scriptDelete(serviceId, testTypes);
  }

  @Operation(summary = "Configure and generate testing tasks of service", operationId = "services:test:task:generate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Configure and generate successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/services/{id}/test/task/generate")
  public ApiLocaleResult<?> generate(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId,
      @Parameter(name = "taskSprintId", description = "Task sprint id, it is required for agile project management")
      @RequestParam(value = "taskSprintId", required = false) Long taskSprintId,
      @Valid @NotEmpty @RequestBody Set<ServicesTestTaskGenerateDto> dto) {
    servicesTestFacade.testTaskGenerate(serviceId, taskSprintId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Restart the existing testing tasks of service", operationId = "services:test:task:restart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restarted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping("/services/{id}/test/task/restart")
  public ApiLocaleResult<?> testTaskRestart(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId) {
    servicesTestFacade.testTaskRestart(serviceId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Reopen the existing testing tasks of service", operationId = "services:test:task:reopen")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reopened successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping("/services/{id}/test/task/reopen")
  public ApiLocaleResult<?> testTaskReopen(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId) {
    servicesTestFacade.testTaskReopen(serviceId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete the testing tasks of service", operationId = "services:test:task:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/services/{id}/test/task")
  public void testTaskDelete(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId,
      @Parameter(name = "testTypes", description = "Test type", required = true) @RequestParam("testTypes") HashSet<TestType> testTypes) {
    servicesTestFacade.testTaskDelete(serviceId, testTypes);
  }

  @Operation(summary = "Query all server configurations of the service", operationId = "services:test:schema:server:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/services/{id}/test/schema/server")
  public ApiLocaleResult<List<Server>> serverList(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.serverList(serviceId, true));
  }

  @Operation(summary = "Create the testing execution of service apis",
      description = "If the script does not exist, create the script", operationId = "services:test:apis:exec:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/services/{id}/exec")
  public ApiLocaleResult<?> testExecAdd(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long servicesId,
      @Parameter(name = "testTypes", description = "Test type", required = true) @RequestParam("testTypes") HashSet<TestType> testTypes,
      @RequestBody @Nullable List<Server> servers) {
    servicesTestFacade.testExecAdd(servicesId, testTypes, servers);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Create the smoke testing execution of service apis",
      description = "If the script does not exist, create the script", operationId = "services:smoke:test:apis:exec:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/services/{id}/smoke/exec")
  public ApiLocaleResult<?> testSmokeExecAdd(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long servicesId,
      @RequestBody @Nullable List<Server> servers) {
    servicesTestFacade.testSmokeExecAdd(servicesId, servers);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Create the smoke testing execution of service apis",
      description = "if the script does not exist, create the script", operationId = "services:security:test:apis:exec:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/services/{id}/security/exec")
  public ApiLocaleResult<?> testSecurityExecAdd(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long servicesId,
      @RequestBody @Nullable List<Server> servers) {
    servicesTestFacade.testSecurityExecAdd(servicesId, servers);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the test results of service", operationId = "services:test:apis:result:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/services/{id}/test/result")
  public ApiLocaleResult<ExecApisResultInfo> testServiceResult(
      @Parameter(name = "id", description = "Services id", required = true) @PathVariable("id") Long serviceId) {
    return ApiLocaleResult.success(servicesTestFacade.testServiceResult(serviceId));
  }

  @Operation(summary = "Query the test results of project", operationId = "project:test:apis:result:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/project/{id}/test/result")
  public ApiLocaleResult<ExecApisResultInfo> testProjectResult(
      @Parameter(name = "id", description = "Project id", required = true) @PathVariable("id") Long projectId) {
    return ApiLocaleResult.success(servicesTestFacade.testProjectResult(projectId));
  }

}
