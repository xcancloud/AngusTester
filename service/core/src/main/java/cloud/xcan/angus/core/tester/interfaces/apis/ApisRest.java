package cloud.xcan.angus.core.tester.interfaces.apis;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.core.tester.interfaces.apis.facade.ApisFacade;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisArchiveDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisExportDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisInfoFindDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisMoveDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.ApisUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.mock.ApisAssocMockApisAddDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.schema.ApisSchemaOpenApiDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.ApisDetailVo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.ApisInfoListVo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.mock.ApisAssocMockApiVo;
import cloud.xcan.angus.model.apis.ApiStatus;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.validator.constraints.Length;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * REST controller for API management operations.
 * <p>
 * This controller provides comprehensive API management functionality including CRUD operations,
 * lifecycle management, testing configurations, and integration with external systems.
 * It serves as the central registry for maintaining interface debug values, schema definitions,
 * and version history across the entire API ecosystem.
 * <p>
 * Key features provided:
 * - API creation, modification, and deletion
 * - Schema import and export operations
 * - Status lifecycle management
 * - Testing configuration management
 * - Mock service integration
 * - Batch operations for efficiency
 * - Archive and restore functionality
 * <p>
 * All operations support multi-tenancy and include proper validation, error handling,
 * and audit logging. The controller integrates with the facade pattern for business
 * logic encapsulation and follows RESTful design principles.
 *
 * @author Angus Team
 * @since 1.0.0
 */
@Tag(name = "Apis", description = "API Management - Central registry for maintaining interface debug value, schema definitions, and version history")
@Validated
@RestController
@RequestMapping("/api/v1/apis")
public class ApisRest {

  /**
   * Facade service for API management business operations.
   * <p>
   * Encapsulates complex business logic and coordinates between multiple
   * domain services to provide high-level API management functionality.
   */
  @Resource
  private ApisFacade apisFacade;

  /**
   * Archives unarchived APIs to move them out of active use.
   * <p>
   * Archiving allows APIs to be preserved for historical reference while
   * removing them from active development and testing workflows. This operation
   * supports batch processing for efficiency when managing multiple APIs.
   * <p>
   * The operation validates that all target APIs exist and are in a valid
   * state for archiving before proceeding with the batch operation.
   *
   * @param dto list of archive requests containing API IDs and archive metadata
   * @return success result with list of archived API identifiers
   */
  @Operation(summary = "Archive the unarchived api", operationId = "apis:archive")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Archived Successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/archive")
  public ApiLocaleResult<List<IdKey<Long, Object>>> archive(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApisArchiveDto> dto) {
    return ApiLocaleResult.success(apisFacade.archive(dto));
  }

  /**
   * Updates existing API configurations with new values.
   * <p>
   * Supports partial updates to API specifications, allowing modification of
   * specific fields without requiring a complete replacement. This operation
   * is optimized for frequent updates during API development and testing phases.
   * <p>
   * The update operation preserves existing values for fields not included
   * in the update request and maintains referential integrity across related entities.
   *
   * @param dto list of update requests containing API IDs and field modifications
   * @return success result indicating completion of update operations
   */
  @Operation(summary = "Update api", operationId = "apis:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApisUpdateDto> dto) {
    apisFacade.update(dto);
    return ApiLocaleResult.success();
  }

  /**
   * Replaces entire API configurations with new definitions.
   * <p>
   * Unlike update operations, replace operations completely substitute the
   * existing API definition with the provided specification. This is typically
   * used for major schema changes or when importing APIs from external sources.
   * <p>
   * The replace operation ensures data consistency by validating the new
   * definition before applying changes and rolling back on validation failures.
   *
   * @param dto list of replacement requests containing complete API definitions
   * @return success result with details of replaced APIs
   */
  @Operation(summary = "Replace api", operationId = "apis:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<?> replace(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApisReplaceDto> dto) {
    return ApiLocaleResult.success(apisFacade.replace(dto));
  }

  /**
   * Updates the display name of a specific API.
   * <p>
   * Provides a convenient endpoint for renaming APIs without requiring
   * a full update operation. The name change is immediately reflected
   * across all related documentation and user interfaces.
   * <p>
   * Name validation ensures uniqueness within the service scope and
   * compliance with naming conventions for API identifiers.
   *
   * @param id the unique identifier of the API to rename
   * @param name the new display name for the API
   * @return success result indicating the rename operation completed
   */
  @Operation(summary = "Update the name of api", operationId = "apis:replace:name")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/name")
  public ApiLocaleResult<?> rename(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @Length(max = MAX_NAME_LENGTH_X2) @Parameter(name = "name", description = "New apis name", required = true) @RequestParam("name") String name) {
    apisFacade.rename(id, name);
    return ApiLocaleResult.success();
  }

  /**
   * Moves an API from one service to another service.
   * <p>
   * This operation facilitates API reorganization by transferring ownership
   * and association from the source service to the target service. All related
   * metadata, test configurations, and historical data are preserved during the move.
   * <p>
   * The move operation validates that the target service exists and that the
   * user has appropriate permissions for both source and target services.
   *
   * @param dto move request containing source API ID and target service information
   * @return success result indicating the move operation completed
   */
  @Operation(summary = "Move the api to another service", operationId = "apis:move")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Move successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/move")
  public ApiLocaleResult<?> move(@Valid @RequestBody ApisMoveDto dto) {
    apisFacade.move(dto);
    return ApiLocaleResult.success();
  }

  /**
   * Creates a copy of an existing API with a new identifier.
   * <p>
   * Cloning is useful for creating variations of existing APIs or establishing
   * starting points for new API development based on proven patterns. The cloned
   * API inherits all configuration from the source but receives a new unique identifier.
   * <p>
   * The clone operation preserves the original API's schema, test configurations,
   * and documentation while allowing independent modification of the copy.
   *
   * @param id the unique identifier of the API to clone
   * @return success result containing the identifier of the newly created clone
   */
  @Operation(summary = "Clone api", operationId = "apis:clone")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cloned successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/clone")
  public ApiLocaleResult<IdKey<Long, Object>> clone(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisFacade.clone(id));
  }

  /**
   * Permanently deletes APIs from the system.
   * <p>
   * This operation performs soft deletion, marking APIs as deleted while preserving
   * data for audit and recovery purposes. Deleted APIs are excluded from normal
   * operations but can be restored if needed within the retention period.
   * <p>
   * Batch deletion is supported for efficiency when removing multiple APIs.
   * The operation validates user permissions and API dependencies before proceeding.
   *
   * @param ids set of API identifiers to delete
   */
  @Operation(summary = "Delete api", operationId = "apis:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Parameter(name = "ids", description = "Api ids", required = true)
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    apisFacade.delete(ids);
  }

  /**
   * Updates the lifecycle status of a specific API.
   * <p>
   * Status changes control API visibility and availability across different
   * environments and user groups. Common status transitions include draft to
   * published, published to deprecated, and deprecated to retired.
   * <p>
   * Status updates trigger appropriate notifications and workflow actions
   * to ensure stakeholders are informed of API lifecycle changes.
   *
   * @param id the unique identifier of the API to update
   * @param status the new status to assign to the API
   * @return success result indicating the status update completed
   */
  @Operation(summary = "Modify api status", operationId = "apis:status:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Modified successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<?> statusUpdate(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "status", description = "Api status", required = true) @RequestParam("status") ApiStatus status) {
    apisFacade.statusUpdate(id, status);
    return ApiLocaleResult.success();
  }

  /**
   * Replaces the server configuration of a specific API.
   * <p>
   * This operation allows updating the server endpoints and parameters
   * for a specific API version. It is particularly useful for dynamic
   * API testing or when server details need to be modified without
   * changing the entire API definition.
   * <p>
   * The replace operation validates that the API exists and that the
   * provided server configuration is valid.
   *
   * @param id the unique identifier of the API to update
   * @param dto the new server configuration to apply
   * @return success result indicating the server configuration update completed
   */
  @Operation(summary = "Replace server configuration of the api", operationId = "apis:schema:server:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{id}/schema/server")
  public ApiLocaleResult<?> serverReplace(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @RequestBody Server dto) {
    apisFacade.serverReplace(id, dto);
    return ApiLocaleResult.success();
  }

  /**
   * Replaces the server configuration of a specific API with a list of servers.
   * <p>
   * This operation allows replacing the entire server configuration for a
   * specific API version with a new set of servers. It is useful for
   * bulk updates or when the entire server list needs to be refreshed.
   * <p>
   * The replaceAll operation validates that the API exists and that the
   * provided server list is not empty.
   *
   * @param id the unique identifier of the API to update
   * @param dto the new list of servers to apply
   * @return success result indicating the server configuration update completed
   */
  @Operation(summary = "Replace all server configuration of the api", operationId = "apis:schema:server:all:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Api not found")
  })
  @PutMapping("/{id}/schema/server/all")
  public ApiLocaleResult<?> serverReplaceAll(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestBody List<Server> dto) {
    apisFacade.serverReplaceAll(id, dto);
    return ApiLocaleResult.success();
  }

  /**
   * Deletes specific server configurations from an API.
   * <p>
   * This operation allows removing specific server URLs from an API's
   * server configuration. It is useful for cleaning up unused servers
   * or when a service is no longer available.
   * <p>
   * The delete operation validates that the API exists and that the
   * provided server URLs are not empty.
   *
   * @param id the unique identifier of the API to update
   * @param urls the set of server URLs to delete
   */
  @Operation(summary = "Delete servers of the api", operationId = "apis:schema:server:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/schema/server")
  public void serverDelete(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "urls", description = "Server url", required = true) @RequestParam("urls") Set<String> urls) {
    apisFacade.serverDelete(id, urls);
  }

  /**
   * Retrieves the server configuration for a specific API.
   * <p>
   * This operation returns the complete list of servers configured for
   * a specific API version. It includes the current API request server,
   * the servers configured for the API itself, and the servers of its
   * parent services.
   * <p>
   * The serverList operation validates that the API exists.
   *
   * @param id the unique identifier of the API to query
   * @return success result containing the list of servers
   */
  @Operation(summary = "Query the server configuration of api", description = "Note: `The data source includes "
          + "the current api request server, servers configuration, and parent services servers configuration`", operationId = "apis:schema:server:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Api not found")
  })
  @GetMapping("/{id}/schema/server")
  public ApiLocaleResult<List<Server>> serverList(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisFacade.serverList(id));
  }

  /**
   * Associates a mock API with a specific API.
   * <p>
   * This operation allows linking a mock service to an API, enabling
   * the API to use a mock implementation for testing purposes.
   * <p>
   * The assocMockApisAdd operation validates that the API exists and
   * that the provided mock API association details are valid.
   *
   * @param id the unique identifier of the API to associate
   * @param dto the mock API association details
   * @return success result indicating the association creation completed
   */
  @Operation(summary = "Add mock api association", operationId = "apis:association:mock:apis:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/{id}/association/mock/apis")
  public ApiLocaleResult<IdKey<Long, Object>> assocMockApisAdd(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @RequestBody ApisAssocMockApisAddDto dto) {
    return ApiLocaleResult.success(apisFacade.assocMockApisAdd(id, dto));
  }

  /**
   * Retrieves the mock API information associated with a specific API.
   * <p>
   * This operation returns the details of all mock services linked to
   * a specific API. It provides information about the mock service's
   * configuration, status, and any associated metadata.
   * <p>
   * The assocMockApisDetail operation validates that the API exists.
   *
   * @param id the unique identifier of the API to query
   * @return success result containing the mock API details
   */
  @Operation(summary = "Query the mock apis information associated with the api", operationId = "apis:association:mock:apis:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/association/mock/apis")
  public ApiLocaleResult<ApisAssocMockApiVo> assocMockApisDetail(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apisFacade.assocMockApis(id));
  }

  /**
   * Retrieves the detailed information of a specific API.
   * <p>
   * This operation returns comprehensive details about an API, including
   * its configuration, schema, and associated mock services. It supports
   * resolving references to other APIs or mock services.
   * <p>
   * The detail operation validates that the API exists.
   *
   * @param id the unique identifier of the API to query
   * @param resolveRef flag to indicate if references should be resolved
   * @return success result containing the API details
   */
  @Operation(summary = "Query the detail of api", operationId = "apis:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ApisDetailVo> detail(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "resolveRef", description = "Resolve reference flag, default false", required = true)
      @RequestParam(value = "resolveRef", required = false) Boolean resolveRef) {
    return ApiLocaleResult.success(apisFacade.detail(id, resolveRef));
  }

  /**
   * Retrieves the OpenAPI document for a specific API.
   * <p>
   * This operation returns the OpenAPI specification (JSON or YAML)
   * for a specific API version. It includes the complete schema definition
   * and any associated metadata.
   * <p>
   * The openapiDetail operation validates that the API exists.
   *
   * @param id the unique identifier of the API to query
   * @param dto the OpenAPI schema details
   * @return success result containing the OpenAPI document
   */
  @Operation(summary = "Query the OpenAPI document of api", operationId = "apis:openapi:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping(value = "/{id}/openapi")
  public ApiLocaleResult<String> openapiDetail(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @ParameterObject ApisSchemaOpenApiDto dto) {
    return ApiLocaleResult.successData(apisFacade.openapiDetail(id, dto));
  }

  /**
   * Checks if a specific API exists.
   * <p>
   * This operation is a lightweight check to determine if an API is
   * registered in the system. It does not return detailed information
   * but confirms the existence of the API.
   * <p>
   * The check operation validates that the API exists.
   *
   * @param id the unique identifier of the API to check
   * @return success result indicating the API exists
   */
  @Operation(summary = "Check api", operationId = "apis:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resource existed"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/check")
  public ApiLocaleResult<?> check(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id) {
    apisFacade.check(id);
    return ApiLocaleResult.success();
  }

  /**
   * Retrieves detailed information for a list of APIs.
   * <p>
   * This operation returns detailed information for a batch of API
   * identifiers, including their configurations and associated mock services.
   * It supports resolving references to other APIs or mock services.
   * <p>
   * The listDetail operation validates that the provided API IDs are not empty.
   *
   * @param ids set of API identifiers to query
   * @param resolveRef flag to indicate if references should be resolved
   * @return success result containing the detailed API information
   */
  @Operation(summary = "Query the detail list of api", operationId = "apis:list:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/list/detail")
  public ApiLocaleResult<List<ApisDetailVo>> listDetail(
      @Parameter(name = "ids", description = "Api ids", required = true)
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids,
      @Parameter(name = "resolveRef", description = "Resolve reference flag, default false", required = true)
      @RequestParam(value = "resolveRef", required = false) Boolean resolveRef) {
    return ApiLocaleResult.success(apisFacade.listDetail(ids, resolveRef));
  }

  /**
   * Retrieves basic information for a list of APIs.
   * <p>
   * This operation returns a paginated list of API identifiers and
   * their basic information, such as name, status, and creation date.
   * It supports filtering and pagination.
   * <p>
   * The list operation validates the provided find criteria.
   *
   * @param dto find criteria for the API list
   * @return success result containing the paginated API list
   */
  @Operation(summary = "Query the basic information of api", operationId = "apis:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ApisInfoListVo>> list(
      @Valid @ParameterObject ApisInfoFindDto dto) {
    return ApiLocaleResult.success(apisFacade.list(dto));
  }

  /**
   * Exports the OpenAPI specification for a specific API.
   * <p>
   * This operation allows exporting the OpenAPI specification (JSON or YAML)
   * for a specific API version to a file. It supports various file formats
   * and handles content negotiation.
   * <p>
   * The export operation validates that the API exists.
   *
   * @param id the unique identifier of the API to export
   * @param dto export criteria
   * @param response HTTP response object for file streaming
   * @return ResponseEntity with the exported file
   */
  @Operation(summary = "Export the OpenAPI specification of api", operationId = "apis:openapi:export")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Exported Successfully")})
  @GetMapping(value = "/{id}/openapi/export")
  public ResponseEntity<org.springframework.core.io.Resource> export(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id,
      @Valid @ParameterObject ApisExportDto dto, HttpServletResponse response) {
    return apisFacade.export(id, dto, response);
  }

}
