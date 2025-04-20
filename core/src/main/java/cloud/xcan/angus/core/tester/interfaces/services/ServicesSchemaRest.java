package cloud.xcan.angus.core.tester.interfaces.services;


import cloud.xcan.angus.core.tester.interfaces.services.facade.ServicesSchemaFacade;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.schema.ApisSchemaOpenApiDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.vo.schema.ServiceSchemaDetailVo;
import cloud.xcan.angus.core.tester.interfaces.services.facade.vo.schema.ServiceServerVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ServicesSchema")
@Validated
@RestController
@RequestMapping("/api/v1/services")
public class ServicesSchemaRest {

  @Resource
  private ServicesSchemaFacade servicesSchemaFacade;

  @Operation(description = "Replace the services schema info. Provides metadata about the API. "
      + "Note: `Metadata is required for an OpenAPI document, so it cannot be deleted after adding it`.", operationId = "services:schema:info:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/info")
  public ApiLocaleResult<?> infoReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody Info dto) {
    servicesSchemaFacade.infoReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the schema info of services", operationId = "services:schema:info:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")})
  @GetMapping(value = "/{serviceId}/schema/info")
  public ApiLocaleResult<Info> infoDetail(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.infoDetail(serviceId));
  }

  @Operation(description = "Replace the services referencing for external documentation. "
      + "Allows referencing an external resource for extended documentation for OpenAPI document.", operationId = "services:schema:externalDoc:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/externalDoc")
  public ApiLocaleResult<?> externalDocReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody ExternalDocumentation dto) {
    servicesSchemaFacade.externalDocReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the additional external documentation of services", operationId = "services:schema:externalDoc:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")})
  @GetMapping(value = "/{serviceId}/schema/externalDoc")
  public ApiLocaleResult<ExternalDocumentation> externalDocDetail(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.externalDocDetail(serviceId));
  }

  @Operation(description =
      "Replace security requirements of the services. A declaration of which security mechanisms can be used across the API. "
          + "For more details on the security requirements, please see: [OpenAPI Specification#Security Requirement Object](https://swagger.io/specification/#security-requirement-object)", operationId = "services:schema:securityRequirement:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/securityRequirement")
  public ApiLocaleResult<?> securityRequirementReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody SecurityRequirement dto) {
    servicesSchemaFacade.securityRequirementReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description =
      "Replace all security requirements of the services. A declaration of which security mechanisms can be used across the API. "
          + "For more details on the security requirements, please see: [OpenAPI Specification#Security Requirement Object](https://swagger.io/specification/#security-requirement-object)", operationId = "services:schema:securityRequirement:all:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/securityRequirement/all")
  public ApiLocaleResult<?> securityRequirementReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      // Warn:: Swagger-UI caught TypeError: Cannot read properties of undefined (reading 'anyOf')
      @RequestBody List<SecurityRequirement> dto) {
    servicesSchemaFacade.securityRequirementReplaceAll(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete security requirements of the services. Delete the security requirements of services by name", operationId = "services:schema:securityRequirement:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{serviceId}/schema/securityRequirement")
  public void securityRequirementDelete(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "names", description = "Security schema names", required = true) @RequestParam("names") Set<String> names) {
    servicesSchemaFacade.securityRequirementDelete(serviceId, names);
  }

  @Operation(description = "Query all security requirements of the services. Query the all security requirement of services", operationId = "services:schema:securityRequirement:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema/securityRequirement")
  public ApiLocaleResult<List<SecurityRequirement>> securityRequirementList(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.securityRequirementList(serviceId));
  }

  @Operation(description =
      "Replace server configuration of the services. A declaration of which servers can be used across the API. "
          + "For more details on the server, please see: [OpenAPI Specification#Server Object](https://swagger.io/specification/#server-object)", operationId = "services:schema:server:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/server")
  public ApiLocaleResult<?> serverReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody Server dto) {
    servicesSchemaFacade.serverReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Synchronous server configuration to the services apis", operationId = "services:schema:server:apis:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/server/{serverId}/apis/sync")
  public ApiLocaleResult<?> apisServerReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "serverId", description = "Server id", required = true) @PathVariable("serverId") Long serverId) {
    servicesSchemaFacade.apisServerReplace(serviceId, serverId);
    return ApiLocaleResult.success();
  }

  @Operation(description =
      "Replace all server configurations of the services. A declaration of which servers can be used across the API. "
          + "Note: `The local server will be deleted when it does not exist in the request`. "
          + "For more details on the server, please see: [OpenAPI Specification#Server Object](https://swagger.io/specification/#server-object)", operationId = "services:schema:server:all:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/server/all")
  public ApiLocaleResult<?> serverReplaceAll(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody List<Server> dto) {
    servicesSchemaFacade.serverReplaceAll(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete servers of the services. Delete the servers of services by url", operationId = "services:schema:server:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{serviceId}/schema/server")
  public void serverDelete(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "ids", description = "Server ids", required = true) @RequestParam("ids") Set<Long> ids) {
    servicesSchemaFacade.serverDelete(serviceId, ids);
  }

  @Operation(description = "Query all server configurations of the services", operationId = "services:schema:server:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema/server")
  public ApiLocaleResult<List<Server>> serverList(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.serverList(serviceId, false));
  }

  @Operation(description = "Query the detail of server configurations", operationId = "services:schema:server:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema/server/{serverId}")
  public ApiLocaleResult<Server> serverDetail(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "serverId", description = "Server id", required = true) @PathVariable("serverId") Long serverId) {
    return ApiLocaleResult.success(servicesSchemaFacade.serverDetail(serviceId, serverId));
  }

  @Operation(description = "Query all server configurations of the project services", operationId = "services:schema:server:byProject")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/schema/server")
  public ApiLocaleResult<List<ServiceServerVo>> serverListByProject(
      @Parameter(name = "projectId", description = "Project id", required = true) @RequestParam("projectId") Long projectId) {
    return ApiLocaleResult.success(servicesSchemaFacade.serverListByProject(projectId));
  }

  @Operation(description =
      "Replace tag of the services. Note: `The order of the tags can be used to reflect on their order by the parsing tools`. "
          + "Not all tags that are used by the Operation Object must be declared. The tags that are not declared MAY be organized randomly or based on the tools' logic.", operationId = "services:schema:tag:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/tag")
  public ApiLocaleResult<?> tagReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody Tag dto) {
    servicesSchemaFacade.tagReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description =
      "Replace all tags of the services. Note: `The order of the tags can be used to reflect on their order by the parsing tools`. "
          + "Not all tags that are used by the Operation Object must be declared. The tags that are not declared MAY be organized "
          + "randomly or based on the tools' logic.", operationId = "services:schema:tag:all:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/tag/all")
  public ApiLocaleResult<?> tagReplaceAll(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody List<Tag> dto) {
    servicesSchemaFacade.tagReplaceAll(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete tags of the services. Delete the tags of services by name", operationId = "services:schema:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{serviceId}/schema/tag")
  public void tagDelete(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "names", description = "Report names", required = true) @RequestParam("names") Set<String> names) {
    servicesSchemaFacade.tagDelete(serviceId, names);
  }

  @Operation(description = "Query all tags of the services", operationId = "services:schema:tag:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema/tag")
  public ApiLocaleResult<List<Tag>> tagList(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.tagList(serviceId));
  }

  @Operation(description = "Replace all schema extensions of the services. "
      + "For more information, please see: [Specification Extensions](https://swagger.io/specification/#specification-extensions)", operationId = "services:schema:extensions:all:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/schema/extensions")
  public ApiLocaleResult<?> extensionsReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @RequestBody Map<String, Object> dto) {
    servicesSchemaFacade.extensionsReplace(serviceId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query all schema extensions of the services", operationId = "services:schema:extensions:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema/extensions")
  public ApiLocaleResult<Map<String, Object>> extensionsList(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.extensionsList(serviceId));
  }

  @Operation(description = "Query the schema detail of services", operationId = "services:schema:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping("/{serviceId}/schema")
  public ApiLocaleResult<ServiceSchemaDetailVo> detail(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId) {
    return ApiLocaleResult.success(servicesSchemaFacade.detail(serviceId));
  }

  /**
   * @param serviceId       Services id
   * @param forced          When the modification is not forced, if it detects that `Operation
   *                        Object` is deleted or updated, it will stop the modification and return
   *                        a prompt; otherwise, it will directly modify or delete the interface
   *                        that does not exist in OpenAPI, Default false.
   * @param gzipCompression It is recommended to enable gzip compression. After enabling it, the
   *                        data size can be reduced by more than 20 times. By default, gzip
   *                        compression is enabled.
   * @param content         OpenAPI document yaml or json content
   */
  @Operation(description = "Replace the OpenAPI document of services", operationId = "services:openapi:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @PutMapping("/{serviceId}/openapi")
  public ApiLocaleResult<?> openapiReplace(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      @Parameter(name = "forced", description = "Forced modification", required = true) @RequestParam(value = "forced") Boolean forced,
      @Parameter(name = "gzipCompression", description = "Whether to turn on Gzip compression", required = true) @RequestParam(value = "gzipCompression") Boolean gzipCompression,
      @RequestBody @NotEmpty String content) {
    servicesSchemaFacade.openapiReplace(serviceId, forced, gzipCompression, content);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the OpenAPI document of services", operationId = "services:openapi:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Services not found")
  })
  @GetMapping(value = "/{serviceId}/openapi")
  public ApiLocaleResult<String> openapiDetail(
      @Parameter(name = "serviceId", description = "Services id", required = true) @PathVariable("serviceId") Long serviceId,
      ApisSchemaOpenApiDto dto) {
    return ApiLocaleResult.successData(servicesSchemaFacade.openapiDetail(serviceId, dto));
  }
}
