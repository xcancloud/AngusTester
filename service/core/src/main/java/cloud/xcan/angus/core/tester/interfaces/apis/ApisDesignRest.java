package cloud.xcan.angus.core.tester.interfaces.apis;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.tester.interfaces.apis.facade.ApisDesignFacade;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignAddDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignContentReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignExportDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignFindDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignImportDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignSearchDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.design.ApisDesignUpdateNameDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.design.ApisDesignDetailVo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.design.ApisDesignVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Tag(name = "ApisDesign", description = "OpenAPI Design Governance - Standardization hub for API specification development, version control, and compliance monitoring.")
@Validated
@RestController
@RequestMapping("/api/v1/apis/design")
public class ApisDesignRest {

  @Resource
  private ApisDesignFacade apisDesignFacade;

  @Operation(summary = "Add the design of apis", operationId = "apis:design:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody ApisDesignAddDto dto) {
    return ApiLocaleResult.success(apisDesignFacade.add(dto));
  }

  @Operation(summary = "Replace the name of apis design", operationId = "apis:design:name:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping
  public ApiLocaleResult<?> updateName(@Valid @RequestBody ApisDesignUpdateNameDto dto) {
    apisDesignFacade.updateName(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace the design content of apis", operationId = "apis:design:content:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping("/content")
  public ApiLocaleResult<?> replaceContent(@Valid @RequestBody ApisDesignContentReplaceDto dto) {
    apisDesignFacade.replaceContent(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Release the design of apis", operationId = "apis:design:release")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Release successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping("/{id}/release")
  public ApiLocaleResult<?> release(
      @Parameter(name = "id", description = "Design id", required = true) @PathVariable("id") Long id) {
    apisDesignFacade.release(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Clone the design of apis", operationId = "apis:design:clone")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cloned successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping("/{id}/clone")
  public ApiLocaleResult<IdKey<Long, Object>> clone(
      @Parameter(name = "id", description = "Design id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisDesignFacade.clone(id));
  }

  @Operation(summary = "Design existing service and apis", operationId = "apis:design:services:generate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cloned successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping("/services/{id}/associate")
  public ApiLocaleResult<?> servicesAssociate(
      @Parameter(name = "id", description = "Apis service id", required = true) @PathVariable("id") Long serviceId) {
    apisDesignFacade.servicesAssociate(serviceId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Generate the services of designed apis", operationId = "apis:design:services:generate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cloned successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @PutMapping("/{id}/services/assoc")
  public ApiLocaleResult<?> servicesGenerate(
      @Parameter(name = "id", description = "Design id", required = true) @PathVariable("id") Long id) {
    apisDesignFacade.servicesGenerate(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Import the apis design", operationId = "apis:design:import")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Imported successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiLocaleResult<IdKey<Long, Object>> imports(
      @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE), schema = @Schema(type = "object")) @Valid ApisDesignImportDto dto) {
    return ApiLocaleResult.success(apisDesignFacade.imports(dto));
  }

  @Operation(summary = "Delete the design of apis", operationId = "apis:design:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @DeleteMapping
  public void delete(
      @Parameter(name = "ids", description = "Apis design ids", required = true)
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    apisDesignFacade.delete(ids);
  }

  @Operation(summary = "Query the sharing detail of apis", operationId = "space:design:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Sharing does not exist")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ApisDesignDetailVo> detail(
      @Parameter(name = "id", description = "Design id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisDesignFacade.detail(id));
  }

  @Operation(summary = "Query the design list of apis", operationId = "apis:design:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ApisDesignVo>> list(
      @Valid @ParameterObject ApisDesignFindDto dto) {
    return ApiLocaleResult.success(apisDesignFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the design of apis", operationId = "apis:design:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<ApisDesignVo>> search(
      @Valid @ParameterObject ApisDesignSearchDto dto) {
    return ApiLocaleResult.success(apisDesignFacade.search(dto));
  }

  @Operation(summary = "Export the designed OpenAPI specification of apis", operationId = "apis:design:export")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Exported Successfully")})
  @GetMapping(value = "/export")
  public ResponseEntity<org.springframework.core.io.Resource> export(
      @Valid @ParameterObject ApisDesignExportDto dto, HttpServletResponse response) {
    return apisDesignFacade.export(dto, response);
  }

}
