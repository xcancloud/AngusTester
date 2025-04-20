package cloud.xcan.angus.core.tester.interfaces.services;


import cloud.xcan.angus.core.tester.interfaces.services.facade.ServicesTestFacade;
import cloud.xcan.angus.model.services.ApisTestCount;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.dto.OrgAndDateFilterDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ServicesTestInner")
@Validated
@RestController
@RequestMapping("/innerapi/v1")
public class ServicesTestDoorRest {

  @Resource
  private ServicesTestFacade servicesTestFacade;

  @Operation(description = "The testing apis summary the functionality, performance, stability testing of service", operationId = "services:test:apis:count:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/services/{id}/test/apis/count")
  public ApiLocaleResult<ApisTestCount> countServiceTestApis(
      @Parameter(name = "id", required = true) @PathVariable("id") Long serviceId,
      OrgAndDateFilterDto dto) {
    return ApiLocaleResult.success(servicesTestFacade.countServiceTestApis(serviceId, dto));
  }

  @Operation(description = "The testing apis summary the functionality, performance, stability testing of project", operationId = "project:test:apis:count:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/project/{id}/test/apis/count")
  public ApiLocaleResult<ApisTestCount> countProjectTestApis(
      @Parameter(name = "id", required = true) @PathVariable("id") Long projectId,
      OrgAndDateFilterDto dto) {
    return ApiLocaleResult.success(servicesTestFacade.countProjectTestApis(projectId, dto));
  }

}
