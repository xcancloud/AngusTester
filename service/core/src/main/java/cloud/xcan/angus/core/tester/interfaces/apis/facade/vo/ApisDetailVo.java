package cloud.xcan.angus.core.tester.interfaces.apis.facade.vo;


import cloud.xcan.angus.api.commonlink.apis.ApiSource;
import cloud.xcan.angus.extension.angustester.api.ApiImportSource;
import cloud.xcan.angus.model.AngusConstant;
import cloud.xcan.angus.model.apis.ApiStatus;
import cloud.xcan.angus.model.element.ActionOnEOF;
import cloud.xcan.angus.model.element.SharingMode;
import cloud.xcan.angus.model.element.assertion.Assertion;
import cloud.xcan.angus.model.element.extraction.HttpExtraction;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.http.HttpMethod;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.extension.ApisProtocol;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApisDetailVo {

  private Long id;

  private ApiSource source;

  private ApiImportSource importSource;

  private Long projectId;

  //@NameJoinField(id = "projectId", repository = "projectRepo")
  //private String projectName;

  private Long serviceId;

  @NameJoinField(id = "serviceId", repository = "servicesRepo")
  private String serviceName;

  @Schema(example = "http")
  private ApisProtocol protocol;

  @Schema(example = "GET")
  private HttpMethod method;

  @DoInFuture("Add endpoint field")
  @Schema(example = "/comm/api/v1/country/{id}")
  private String endpoint;

  /////////////////////////OpenAPI Document//////////////////////////
  private List<String> tags;

  private String summary;

  private String description;

  private ExternalDocumentation externalDocs;

  private String operationId;

  private List<Parameter> parameters;

  private RequestBody requestBody;

  private Map<String, ApiResponse> responses;

  private Boolean deprecated;

  private List<SecurityRequirement> security;

  @Schema(description = "Available server urls. The data source includes the current request server, api servers, and parent services servers.")
  private List<Server> availableServers;

  private Map<String, Object> extensions;
  /////////////////////////OpenAPI Document//////////////////////////

  private SecurityScheme authentication;

  private List<Assertion<HttpExtraction>> assertions;

  private ApiStatus status;

  private Map<String, Tag> tagSchemas;

  private Long ownerId;

  @NameJoinField(id = "ownerId", repository = "commonUserBaseRepo")
  private String ownerName;

  private Boolean favourite;

  private Boolean follow;

  private Boolean auth;

  private Boolean serviceAuth;

  private Boolean secured;

  /**
   * Process actions when the dataset reaches the end of reading, default `RECYCLE`.
   *
   * @see AngusConstant#DEFAULT_ACTION_ON_EOF
   */
  private ActionOnEOF datasetActionOnEOF;

  /**
   * Dataset sharing mode when multi threads, default `ALL_THREAD`.
   *
   * @see AngusConstant#DEFAULT_SHARING_MODE
   */
  private SharingMode datasetSharingMode;

  /**
   * Whether to enable functional testing, default enabled.
   * <p>
   * After enabled, the test results will be included in the efficiency analysis.
   */
  private Boolean testFunc;

  private Boolean testFuncPassed;

  private String testFuncFailureMessage;

  /**
   * Whether to enable performance testing, default enabled.
   * <p>
   * After enabled, the test results will be included in the efficiency analysis.
   */
  private Boolean testPerf;

  private Boolean testPerfPassed;

  private String testPerfFailureMessage;

  /**
   * Whether to enable stability testing, default enabled.
   * <p>
   * After enabled, the test results will be included in the efficiency analysis
   */
  private Boolean testStability;

  private Boolean testStabilityPassed;

  private String testStabilityFailureMessage;

  private String syncName;

  private Map<String, String> resolvedRefModels;

  private Long tenantId;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  protected Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;

  @JsonAnyGetter
  public Map<String, Object> getExtensions() {
    return extensions;
  }

}



