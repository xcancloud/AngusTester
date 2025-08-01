package cloud.xcan.angus.core.tester.interfaces.mock;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.tester.interfaces.mock.facade.MockApisResponseFacade;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.apis.response.MockApisResponseAddDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.apis.response.MockApisResponseReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.apis.response.MockApiResponseVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mock Apis Response", description = "Mock Response Definition and Management - Comprehensive management of mock response templates including status codes, headers, body schemas, and response variations")
@Validated
@RestController
@RequestMapping("/api/v1/mock/apis")
public class MockApisResponseRest {

  @Resource
  private MockApisResponseFacade mockApisResponseFacade;

  @Operation(summary = "Add mock API response definitions",
      description = "Create new mock response definitions with status codes, headers, and body schemas for API simulation",
      operationId = "mock:apis:response:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Mock API response definitions created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{apisId}/response")
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Parameter(name = "apisId", description = "Mock API identifier for response association", required = true) @PathVariable("apisId") Long apisId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<MockApisResponseAddDto> dto) {
    return ApiLocaleResult.success(mockApisResponseFacade.add(apisId, dto));
  }

  @Operation(summary = "Replace mock API response definitions",
      description = "Replace existing mock response definitions with new configurations for API simulation updates",
      operationId = "mock:apis:response:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Mock API response definitions replaced successfully")})
  @PutMapping("/{apisId}/response")
  public ApiLocaleResult<?> replace(
      @Parameter(name = "apisId", description = "Mock API identifier for response replacement", required = true) @PathVariable("apisId") Long apisId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<MockApisResponseReplaceDto> dto) {
    mockApisResponseFacade.replace(apisId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete mock API response definitions",
      description = "Remove specific mock response definitions from mock API configuration",
      operationId = "mock:apis:response:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Mock API response definitions deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{apisId}/response")
  public void delete(
      @Parameter(name = "apisId", description = "Mock API identifier for response deletion", required = true) @PathVariable("apisId") Long apisId,
      @Parameter(name = "responseIds", description = "Mock API response identifiers for batch deletion", required = true)
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("responseIds") HashSet<Long> responseIds) {
    mockApisResponseFacade.delete(apisId, responseIds);
  }

  @Operation(summary = "Query all mock API response definitions",
      description = "Retrieve all response definitions for specific mock API including status codes, headers, and body schemas",
      operationId = "mock:apis:response:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Mock API response definitions retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Mock API not found")})
  @GetMapping(value = "/{apisId}/response")
  public ApiLocaleResult<List<MockApiResponseVo>> all(
      @Parameter(name = "apisId", description = "Mock API identifier for response query", required = true) @PathVariable("apisId") Long apisId) {
    return ApiLocaleResult.success(mockApisResponseFacade.all(apisId));
  }

}
