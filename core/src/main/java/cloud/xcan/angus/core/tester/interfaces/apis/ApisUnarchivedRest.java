package cloud.xcan.angus.core.tester.interfaces.apis;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.core.tester.interfaces.apis.facade.ApisUnarchivedFacade;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisUnarchivedAddDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisUnarchivedFindDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisUnarchivedSearchDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisUnarchivedUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.ApisUnarchivedDetailVo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.ApisUnarchivedListVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ApisUnarchived")
@Validated
@RestController
@RequestMapping("/api/v1/apis/unarchived")
public class ApisUnarchivedRest {

  @Resource
  private ApisUnarchivedFacade apisUnarchivedFacade;

  @Operation(description = "Add the unarchived apis", operationId = "apis:unarchived:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApisUnarchivedAddDto> dtos) {
    return ApiLocaleResult.success(apisUnarchivedFacade.add(dtos));
  }

  @Operation(description = "Update the unarchived apis", operationId = "apis:unarchived:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/{id}")
  public ApiLocaleResult<?> update(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApisUnarchivedUpdateDto> dtos) {
    apisUnarchivedFacade.update(dtos);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Replace the name of unarchived apis", operationId = "apis:unarchived:name:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/name")
  public ApiLocaleResult<?> rename(
      @Parameter(name = "id", description = "Unarchived apis id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "name", description = "Unarchived apis name", required = true) @Valid @Length(max = MAX_NAME_LENGTH_X2) @RequestParam("name") String name) {
    apisUnarchivedFacade.rename(id, name);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete the unarchived apis", operationId = "apis:unarchived:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(name = "id", description = "Unarchived apis id", required = true) @PathVariable("id") Long id) {
    apisUnarchivedFacade.delete(id);
  }

  @Operation(description = "Delete all the unarchived apis", operationId = "apis:unarchived:delete:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void deleteAll() {
    apisUnarchivedFacade.deleteAll();
  }

  @Operation(description = "Query the detail of unarchived apis", operationId = "apis:unarchived:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ApisUnarchivedDetailVo> detail(
      @Parameter(name = "id", description = "Unarchived apis id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisUnarchivedFacade.detail(id));
  }

  @Operation(description = "Query the number of unarchived apis", operationId = "apis:unarchived:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/count")
  public ApiLocaleResult<Long> count(
      @RequestParam("projectId") @Parameter(name = "projectId", description = "Project id") Long projectId) {
    return ApiLocaleResult.success(apisUnarchivedFacade.count(projectId));
  }

  @Operation(description = "Query the list of unarchived apis", operationId = "apis:unarchived:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ApisUnarchivedListVo>> list(
      @Valid ApisUnarchivedFindDto dto) {
    return ApiLocaleResult.success(apisUnarchivedFacade.list(dto));
  }

  @Operation(description = "Fulltext search the unarchived apis", operationId = "apis:unarchived:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<ApisUnarchivedListVo>> search(
      @Valid ApisUnarchivedSearchDto dto) {
    return ApiLocaleResult.success(apisUnarchivedFacade.search(dto));
  }
}
