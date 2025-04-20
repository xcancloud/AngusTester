package cloud.xcan.angus.core.tester.interfaces.apis;


import cloud.xcan.angus.core.tester.interfaces.apis.facade.ApisTestFacade;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ApisTestInner", description = "Internal API Testing - Service internal apis testing query entrance.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/apis")
public class ApisTestDoorRest {

  @Resource
  private ApisTestFacade apisTestFacade;

  @Operation(description = "Find enabled functionality, performance, stability testing type of apis.", operationId = "apis:test:enabled:find:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/test/enabled")
  public ApiLocaleResult<List<TestType>> testEnabledFind(
      @Parameter(name = "id", required = true) @PathVariable("id") Long apisId) {
    return ApiLocaleResult.success(apisTestFacade.testEnabledFind(apisId));
  }

}
