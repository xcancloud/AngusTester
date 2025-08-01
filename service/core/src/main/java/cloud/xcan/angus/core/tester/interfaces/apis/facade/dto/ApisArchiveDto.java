package cloud.xcan.angus.core.tester.interfaces.apis.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_DOC_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OPENAPI_SUMMARY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X4;

import cloud.xcan.angus.model.apis.ApiStatus;
import cloud.xcan.angus.model.element.ActionOnEOF;
import cloud.xcan.angus.model.element.SharingMode;
import cloud.xcan.angus.model.element.assertion.Assertion;
import cloud.xcan.angus.model.element.extraction.HttpExtraction;
import cloud.xcan.angus.spec.http.HttpMethod;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.extension.ApisProtocol;
import io.swagger.v3.oas.models.extension.ExtensionKey;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class ApisArchiveDto {

  @Schema(description = "Unarchived API identifier for archive operation")
  private Long unarchivedId;

  @NotNull
  @Schema(description = "Service identifier for API association and organization", requiredMode = RequiredMode.REQUIRED)
  private Long serviceId;

  @NotNull
  @Schema(example = "http", description = "API protocol specification for communication type definition", requiredMode = RequiredMode.REQUIRED)
  private ApisProtocol protocol;

  @NotNull
  @Schema(example = "GET", description = "HTTP method for API request specification", requiredMode = RequiredMode.REQUIRED)
  private HttpMethod method;

  /**
   * @see io.swagger.v3.oas.models.Paths#keySet()
   */
  @Length(max = MAX_URL_LENGTH_X4)
  @Schema(description = "API endpoint path without query parameters for resource identification", example = "/comm/api/v1/country/{id}")
  private String endpoint;

  /////////////////////////OpenAPI Document//////////////////////////
  // private List<String> tags;

  @Length(max = MAX_OPENAPI_SUMMARY_LENGTH)
  @Schema(description = "API summary or name for identification and documentation", example = "Add user api")
  private String summary;

  @Length(max = MAX_OPENAPI_DOC_DESC_LENGTH)
  @Schema(description = "API description with CommonMark syntax support for rich text representation")
  private String description;

  // private ExternalDocumentation externalDocs;

  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Unique operation identifier for OpenAPI specification resolution")
  private String operationId;

  @Size(max = MAX_PARAM_SIZE)
  @Schema(description = "API request parameters following OpenAPI Parameter Object specification")
  private List<Parameter> parameters;

  @Schema(description = "API request body following OpenAPI Request Body Object specification")
  private RequestBody requestBody;

  @Schema(description = "API responses following OpenAPI Response Object specification")
  private Map<String, ApiResponse> responses;

  //  private Boolean deprecated;

  //  private List<SecurityRequirement> security;

  @Schema(description = "Current server configuration for API connection and deployment information")
  private Server currentServer;

  /**
   * Include Extension:
   * <p>
   * - RequestSetting: Extension key in {@link Operation#getExtensions()} is
   * {@link ExtensionKey#REQUEST_SETTING_KEY}
   */
  @JsonAnyGetter
  @Schema(description = "Extension fields for custom business attributes and configuration")
  private Map<String, Object> extensions = new HashMap<>();
  /////////////////////////OpenAPI Document//////////////////////////

  @Schema(description = "Security scheme definition following OpenAPI Security Scheme Object specification")
  private SecurityScheme authentication;

  @Valid
  @Size(max = MAX_PARAM_SIZE)
  @Schema(description = "HTTP assertion configuration for interface execution result validation")
  private List<Assertion<HttpExtraction>> assertions;

  @Schema(description = "Authorization control flag for API access management, defaults to disabled")
  private Boolean auth;

  @Schema(description = "Dataset end-of-file action configuration, defaults to RECYCLE")
  private ActionOnEOF datasetActionOnEOF;

  @Schema(description = "Dataset sharing mode for multi-thread execution, defaults to ALL_THREAD")
  private SharingMode datasetSharingMode;

  @Schema(description = "Functional testing enablement flag, defaults to enabled")
  private Boolean testFunc;

  @Schema(description = "Performance testing enablement flag, defaults to enabled")
  private Boolean testPerf;

  @Schema(description = "Stability testing enablement flag, defaults to enabled")
  private Boolean testStability;

  @NotNull
  @Schema(description = "API owner or developer identifier for ownership management", requiredMode = RequiredMode.REQUIRED)
  private Long ownerId;

  @NotNull
  @Schema(description = "API status for lifecycle management and workflow control", example = "DEV_COMPLETED", requiredMode = RequiredMode.REQUIRED)
  private ApiStatus status;
}
