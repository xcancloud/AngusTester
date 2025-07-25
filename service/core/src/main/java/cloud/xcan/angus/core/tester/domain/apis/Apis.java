package cloud.xcan.angus.core.tester.domain.apis;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.apis.ApiSource;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.tester.domain.ResourceFavouriteAndFollow;
import cloud.xcan.angus.core.tester.domain.activity.ActivityResource;
import cloud.xcan.angus.core.tester.domain.apis.converter.ApiResponseConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.ExtensionsConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.ExternalDocConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.HttpAssertionConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.ParameterConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.RequestBodyConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.SecurityRequirementConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.SecuritySchemeConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.ServerConverter;
import cloud.xcan.angus.core.tester.domain.apis.converter.ServersConverter;
import cloud.xcan.angus.core.tester.domain.task.TaskInfo;
import cloud.xcan.angus.extension.angustester.api.ApiImportSource;
import cloud.xcan.angus.model.AngusConstant;
import cloud.xcan.angus.model.apis.ApiStatus;
import cloud.xcan.angus.model.element.ActionOnEOF;
import cloud.xcan.angus.model.element.SharingMode;
import cloud.xcan.angus.model.element.assertion.Assertion;
import cloud.xcan.angus.model.element.extraction.HttpExtraction;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.PathMatchers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.extension.ApiServerSource;
import io.swagger.v3.oas.models.extension.ApisProtocol;
import io.swagger.v3.oas.models.extension.ExtensionKey;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * API entity representing a comprehensive API definition and testing configuration.
 * <p>
 * This entity serves as the central repository for API specifications, combining OpenAPI
 * documentation standards with testing configurations and execution tracking. It supports
 * multiple protocols (HTTP, WebSocket), various authentication schemes, and comprehensive
 * testing capabilities including functional, performance, and stability testing.
 * <p>
 * The entity is designed to work with OpenAPI 3.x specifications while extending them
 * with testing-specific configurations such as assertions, test datasets, and execution
 * parameters. It integrates with the broader testing ecosystem including tasks, services,
 * and execution monitoring.
 * <p>
 * Key features include:
 * - Full OpenAPI 3.x specification support
 * - Multi-protocol support (HTTP, WebSocket)
 * - Comprehensive authentication and security configurations
 * - Testing-specific extensions (assertions, datasets, execution tracking)
 * - Multi-tenancy and soft deletion support
 * - Activity tracking and resource management integration
 * - Flexible server and environment management
 *
 * @author Angus Team
 * @since 1.0.0
 */
@JsonInclude(value = Include.NON_EMPTY)
@JsonIncludeProperties(value = {"parameters", "requestBody", "authentication"})
// For extract variables
@Entity
@Table(name = "apis")
@SQLRestriction("deleted = 0 AND service_deleted = 0 ")
@SQLDelete(sql = "update apis set deleted = 1 where id = ?")
@DynamicUpdate
@Setter
@Getter
@Accessors(chain = true)
public class Apis extends TenantAuditingEntity<Apis, Long> implements ActivityResource,
    ResourceFavouriteAndFollow<Apis, Long> {

  /**
   * Primary key identifier for the API.
   */
  @Id
  private Long id;

  /**
   * Reference to the project this API belongs to for organizational purposes.
   */
  @Column(name = "project_id")
  private Long projectId;

  /**
   * Reference to the service this API is part of for logical grouping.
   */
  @Column(name = "service_id")
  private Long serviceId;

  /**
   * Source type indicating how this API was created or imported.
   */
  @Enumerated(EnumType.STRING)
  private ApiSource source;

  /**
   * Specific import source when the API was imported from external systems.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "import_source")
  private ApiImportSource importSource;

  /**
   * Protocol type for this API endpoint.
   * <p>
   * Corresponds to OpenAPI Operation servers configuration and determines
   * the communication protocol (HTTP, HTTPS, WebSocket, etc.).
   *
   * @see Operation#getServers()
   */
  @Enumerated(EnumType.STRING)
  private ApisProtocol protocol;

  /**
   * HTTP method for this API endpoint.
   * <p>
   * Defines the HTTP verb (GET, POST, PUT, DELETE, etc.) for REST API operations.
   *
   * @see PathItem
   */
  @Enumerated(EnumType.STRING)
  private HttpMethod method;

  /**
   * API endpoint path with optional path parameters.
   * <p>
   * Represents the URL path pattern for this API, supporting OpenAPI path templating.
   */
  private String endpoint;

  /////////////////////////OpenAPI Document Fields//////////////////////////
  
  /**
   * List of tags for API categorization and organization.
   * <p>
   * Tags are used to group related operations and provide metadata for
   * documentation generation and API organization.
   *
   * @see Operation#getTags()
   */
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "tags")
  private List<String> tags;

  /**
   * Brief summary of what the API operation does.
   * <p>
   * Should be concise and descriptive, typically used in API documentation
   * and UI displays for quick identification.
   *
   * @see Operation#getSummary()
   */
  private String summary;

  /**
   * Detailed description of the API operation.
   * <p>
   * Provides comprehensive information about the operation's purpose,
   * behavior, and usage. Supports CommonMark syntax for rich formatting.
   *
   * @see Operation#getDescription()
   */
  private String description;

  /**
   * External documentation reference for additional information.
   * <p>
   * Links to external resources such as documentation websites,
   * specifications, or related materials.
   *
   * @see Operation#getExternalDocs()
   */
  @Convert(converter = ExternalDocConverter.class)
  @Column(name = "external_docs")
  private ExternalDocumentation externalDocs;

  /**
   * Unique identifier for the operation within the API specification.
   * <p>
   * Used for code generation and internal references. Should be unique
   * across all operations in the API specification.
   *
   * @see Operation#getOperationId()
   */
  @Column(name = "operation_id")
  private String operationId;

  /**
   * List of parameters for the API operation.
   * <p>
   * Includes path, query, header, and cookie parameters with their
   * schemas, validation rules, and documentation.
   *
   * @see Operation#getParameters()
   */
  @Convert(converter = ParameterConverter.class)
  @Column(name = "parameters")
  private List<Parameter> parameters;

  /**
   * Request body specification for operations that accept payload.
   * <p>
   * Defines the structure, content types, and validation rules for
   * request payloads in POST, PUT, PATCH operations.
   *
   * @see Operation#getRequestBody()
   */
  @Convert(converter = RequestBodyConverter.class)
  @Column(name = "request_body")
  private RequestBody requestBody;

  /**
   * Map of possible responses for the API operation.
   * <p>
   * Keyed by HTTP status codes (including 'default'), each response
   * defines the structure, headers, and content types for that status.
   *
   * @see Operation#getResponses()
   */
  @Convert(converter = ApiResponseConverter.class)
  @Column(name = "responses")
  private Map<String, ApiResponse> responses;

  /**
   * Flag indicating whether this API operation is deprecated.
   * <p>
   * Deprecated operations should not be used for new implementations
   * and may be removed in future versions.
   *
   * @see Operation#getDeprecated()
   */
  private Boolean deprecated;

  /**
   * List of security requirements for accessing this operation.
   * <p>
   * Each requirement specifies alternative security schemes that can
   * be used to access the operation.
   *
   * @see Operation#getSecurity()
   */
  @Convert(converter = SecurityRequirementConverter.class)
  @Column(name = "security")
  private List<SecurityRequirement> security;

  /**
   * Currently selected server for API execution.
   * <p>
   * The available API servers are determined by combining multiple sources
   * including operation-specific servers, API-level servers, and parent
   * service servers based on the ApiServerSource configuration.
   * <p>
   * Note: The complete list of available servers can be accessed through
   * the getAvailableServers() method which aggregates servers from different
   * sources according to their ApiServerSource priority.
   *
   * @see Operation#getServers()
   * @see ApiServerSource
   */
  @Convert(converter = ServerConverter.class)
  @Column(name = "current_server")
  private Server currentServer;

  /**
   * Reference ID for the currently selected server.
   * <p>
   * Used for database relationships and server management.
   */
  @Column(name = "current_server_id")
  private Long currentServerId;

  /**
   * List of operation-specific servers.
   * <p>
   * These servers are specific to this operation and have the highest
   * priority when determining available execution targets.
   * Source classification: {@link ApiServerSource#API_SERVERS}
   */
  @Convert(converter = ServersConverter.class)
  @Column(name = "servers")
  private List<Server> servers;

  /**
   * Extension properties for the API operation.
   * <p>
   * Includes custom extensions that extend the OpenAPI specification
   * with additional functionality. Common extensions include:
   * <p>
   * - RequestSetting: Stored with extension key {@link ExtensionKey#REQUEST_SETTING_KEY}
   *   for operation-specific request configuration
   * <p>
   * Extensions follow the OpenAPI pattern of using 'x-' prefixed keys
   * for vendor-specific or custom properties.
   *
   * @see Operation#getExtensions()
   * @see ExtensionKey#REQUEST_SETTING_KEY
   */
  @Convert(converter = ExtensionsConverter.class)
  @Column(name = "extensions")
  private Map<String, Object> extensions;
  
  /////////////////////////End OpenAPI Document Fields//////////////////////////

  /**
   * Hash value of the API schema for change detection and versioning.
   * <p>
   * Used to quickly determine if the API specification has changed
   * without comparing the entire schema structure.
   */
  @Column(name = "schema_hash")
  private int schemaHash;

  /**
   * Authentication scheme configuration for this API.
   * <p>
   * Defines the security mechanism required to access this API operation,
   * including API keys, OAuth, JWT, or other authentication methods.
   */
  @Convert(converter = SecuritySchemeConverter.class)
  @Column(name = "authentication")
  private SecurityScheme authentication;

  /**
   * List of HTTP assertions for automated testing validation.
   * <p>
   * Assertions define the expected behavior and validation rules for
   * API responses, enabling automated functional testing and verification.
   */
  @Convert(converter = HttpAssertionConverter.class)
  @Column(name = "assertions")
  private List<Assertion<HttpExtraction>> assertions;

  /**
   * User ID of the API owner responsible for maintenance and updates.
   */
  @Column(name = "owner_id")
  private Long ownerId;

  /**
   * Current lifecycle status of the API.
   * <p>
   * Tracks the API through various stages such as draft, published,
   * deprecated, or retired for lifecycle management.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private ApiStatus status;

  /**
   * Flag indicating whether authentication is required for this API.
   */
  @Column(name = "auth")
  private Boolean auth;

  /**
   * Flag indicating whether service-level authentication is enforced.
   */
  @Column(name = "service_auth")
  private Boolean serviceAuth;

  /**
   * Flag indicating whether this API endpoint requires secure communication.
   */
  private Boolean secured;

  /**
   * Action to take when test dataset reaches end of file during execution.
   * <p>
   * Default value is RECYCLE to restart from the beginning of the dataset.
   * Other options include STOP to halt execution or RANDOM for random selection.
   *
   * @see AngusConstant#DEFAULT_ACTION_ON_EOF
   */
  @Column(name = "dataset_action_on_eof")
  @Enumerated(EnumType.STRING)
  private ActionOnEOF datasetActionOnEOF;

  /**
   * Dataset sharing strategy for multi-threaded test execution.
   * <p>
   * Default value is ALL_THREAD where all threads share the same dataset.
   * Alternative modes include PER_THREAD for thread-specific datasets.
   *
   * @see AngusConstant#DEFAULT_SHARING_MODE
   */
  @Column(name = "dataset_sharing_mode")
  @Enumerated(EnumType.STRING)
  private SharingMode datasetSharingMode;

  /**
   * Flag to enable functional testing for this API.
   * <p>
   * When enabled, functional tests are executed and results are included
   * in efficiency analysis and quality metrics reporting.
   */
  @Column(name = "test_func")
  private Boolean testFunc;

  /**
   * Result of the last functional test execution.
   * <p>
   * True indicates all functional tests passed, false indicates failures.
   */
  @Column(name = "test_func_passed")
  private Boolean testFuncPassed;

  /**
   * Error message from the last failed functional test execution.
   */
  @Column(name = "test_func_failure_message")
  private String testFuncFailureMessage;

  /**
   * Flag to enable performance testing for this API.
   * <p>
   * When enabled, performance tests are executed and results are included
   * in efficiency analysis and performance metrics reporting.
   */
  @Column(name = "test_perf")
  private Boolean testPerf;

  /**
   * Result of the last performance test execution.
   * <p>
   * True indicates performance criteria were met, false indicates failures.
   */
  @Column(name = "test_perf_passed")
  private Boolean testPerfPassed;

  /**
   * Error message from the last failed performance test execution.
   */
  @Column(name = "test_perf_failure_message")
  private String testPerfFailureMessage;

  /**
   * Flag to enable stability testing for this API.
   * <p>
   * When enabled, stability tests are executed and results are included
   * in efficiency analysis and reliability metrics reporting.
   */
  @Column(name = "test_stability")
  private Boolean testStability;

  /**
   * Result of the last stability test execution.
   * <p>
   * True indicates stability criteria were met, false indicates failures.
   */
  @Column(name = "test_stability_passed")
  private Boolean testStabilityPassed;

  /**
   * Error message from the last failed stability test execution.
   */
  @Column(name = "test_stability_failure_message")
  private String testStabilityFailureMessage;

  /**
   * Synchronization name reference for project synchronization operations.
   * <p>
   * Links to the project sync configuration for tracking synchronization
   * status and managing updates from external sources.
   *
   * @see cloud.xcan.angus.core.tester.domain.services.sync.ProjectSync#name
   */
  @Column(name = "sync_name")
  private String syncName;

  /**
   * Merged search field for efficient ID-based queries and indexing.
   * <p>
   * This field combines multiple identifiers to enable fast search operations
   * across different API identification schemes and external references.
   */
  @Column(name = "ext_search_merge")
  private String extSearchMerge;

  /**
   * Flag indicating whether the parent service has been deleted.
   * <p>
   * Used for cascade deletion logic and maintaining referential integrity.
   */
  @Column(name = "service_deleted")
  private Boolean serviceDeleted;

  /**
   * User ID of the person who deleted this API.
   */
  @Column(name = "deleted_by")
  private Long deletedBy;

  /**
   * Soft deletion flag to mark APIs as deleted without physical removal.
   */
  @Column(name = "deleted")
  private Boolean deleted;

  /**
   * Timestamp when the API was deleted.
   */
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "deleted_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime deletedDate;

  // Transient fields for runtime data population

  /**
   * Display name of the parent service (populated at runtime).
   */
  @Transient
  private String serviceName;

  /**
   * List of all available servers from various sources (populated at runtime).
   * <p>
   * Aggregates servers from operation-level, API-level, and service-level
   * configurations based on ApiServerSource priority rules.
   */
  @Transient
  private List<Server> availableServers;

  /**
   * Resolved reference models for schema validation (populated at runtime).
   * <p>
   * Contains the actual schema definitions for $ref references used
   * in the API specification.
   */
  @Transient
  private Map<String, String> resolvedRefModels;

  /**
   * Tag schema definitions for documentation and validation (populated at runtime).
   */
  @Transient
  private Map<String, Tag> tagSchemas;

  /**
   * Resolved authentication scheme from references (populated at runtime).
   * <p>
   * Contains the actual security scheme definition when the authentication
   * field contains a $ref reference.
   */
  @Transient
  private SecurityScheme refAuthentication;

  /**
   * Associated mock API identifier (populated at runtime).
   */
  @Transient
  private Long mockApisId;

  /**
   * Associated mock service identifier (populated at runtime).
   */
  @Transient
  private Long mockServiceId;

  /**
   * Flag indicating whether the current user has marked this API as favorite (populated at runtime).
   */
  @Transient
  private Boolean favourite;

  /**
   * Flag indicating whether the current user is following this API (populated at runtime).
   */
  @Transient
  private Boolean follow;

  /**
   * Unarchived API identifier for version tracking (populated at runtime).
   */
  @Transient
  private Long unarchivedId;

  /**
   * Display name of the API creator (populated at runtime).
   */
  @Transient
  private String createdByName;

  /**
   * Avatar URL of the API creator (populated at runtime).
   */
  @Transient
  private String avatar;

  /**
   * Map of test type to associated task information (populated at runtime).
   * <p>
   * Provides quick access to task information for different test types
   * (functional, performance, stability) associated with this API.
   */
  @Transient
  private Map<TestType, TaskInfo> testTypeTaskMap;

  /**
   * Checks if authentication is enabled for this API.
   *
   * @return true if authentication is required and enabled, false otherwise
   */
  public boolean isEnabledAuth() {
    return nonNull(auth) && auth;
  }

  /**
   * Checks if this API uses WebSocket protocol.
   *
   * @return true if the protocol is WebSocket, false otherwise
   */
  public boolean isWebSocket() {
    return nonNull(protocol) && protocol.isWebSocket();
  }

  /**
   * Checks if this API uses HTTP protocol.
   *
   * @return true if the protocol is HTTP/HTTPS, false otherwise
   */
  public boolean isHttp() {
    return nonNull(protocol) && protocol.isHttp();
  }

  /**
   * Checks if this API has been released and is available for use.
   *
   * @return true if the API status indicates it's released, false otherwise
   */
  public boolean isReleased() {
    return nonNull(status) && status.isReleased();
  }

  /**
   * Determines which test types are enabled for this API.
   * <p>
   * Examines the functional, performance, and stability test flags
   * to return a set of enabled test types.
   *
   * @return set of test types that are enabled for this API
   */
  public Set<TestType> needTestTypes() {
    Set<TestType> needTested = new HashSet<>();
    if (nonNull(testFunc) && testFunc) {
      needTested.add(TestType.FUNCTIONAL);
    }
    if (nonNull(testPerf) && testPerf) {
      needTested.add(TestType.PERFORMANCE);
    }
    if (nonNull(testStability) && testStability) {
      needTested.add(TestType.STABILITY);
    }
    return needTested;
  }

  /**
   * Checks if the authentication scheme is a reference to an external definition.
   *
   * @return true if authentication contains a $ref reference, false otherwise
   */
  public boolean isAuthSchemaRef(){
    return nonNull(authentication) && isNotEmpty(authentication.get$ref());
  }

  /**
   * Checks if a specific schema reference is included in the resolved models.
   *
   * @param refKey the reference key to check
   * @return true if the reference is resolved and available, false otherwise
   */
  public boolean includeSchemaRef(String refKey){
    return isNotEmpty(resolvedRefModels) && resolvedRefModels.containsKey(refKey);
  }

  /**
   * Returns the primary identifier for this entity.
   * <p>
   * Required by the parent TenantAuditingEntity class for identity management.
   *
   * @return the API's primary key identifier
   */
  @Override
  public Long identity() {
    return id;
  }

  /**
   * Returns the parent ID for hierarchical organization.
   * <p>
   * For APIs, the parent is the service they belong to.
   *
   * @return the service ID as the parent identifier
   */
  @Override
  public Long getParentId() {
    return this.serviceId;
  }

  /**
   * Returns the display name for this API.
   * <p>
   * Uses the summary field as the primary display name for the API.
   *
   * @return the API summary as the display name
   */
  @Override
  public String getName() {
    return this.summary;
  }

  /**
   * Generates a string representation of the API's key configuration values.
   * <p>
   * Includes parameters, request body, server, authentication, and assertions
   * for debugging and logging purposes.
   *
   * @return formatted string with key API configuration details
   */
  public String toValueString() {
    return new StringJoiner(", ")
        .add("parameters=" + parameters)
        .add("requestBody=" + requestBody)
        .add("currentServer=" + currentServer)
        .add("authentication=" + authentication)
        .add("assertions=" + assertions)
        .toString();
  }

  /**
   * Compares API schema information for equality.
   * <p>
   * This method is deprecated in favor of using schemaHash() for performance.
   * Compares OpenAPI specification fields to determine if two APIs have
   * the same schema definition.
   *
   * @param o the API to compare with
   * @return true if the APIs have the same schema information, false otherwise
   * @deprecated Use schemaHash() instead for better performance
   */
  @Deprecated
  public boolean sameSchemaInfoAs(Apis o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return Objects.equals(tags, o.tags) &&
        Objects.equals(summary, o.summary) &&
        Objects.equals(description, o.description) &&
        Objects.equals(externalDocs, o.externalDocs) &&
        Objects.equals(operationId, o.operationId) &&
        Objects.equals(parameters, o.parameters) &&
        Objects.equals(requestBody, o.requestBody) &&
        Objects.equals(responses, o.responses) &&
        Objects.equals(deprecated, o.deprecated) &&
        Objects.equals(security, o.security) &&
        // Objects.equals(currentServer, o.currentServer) && -> temp
        // Objects.equals(currentServerId, o.currentServerId) && -> temp
        Objects.equals(servers, o.servers) &&
        Objects.equals(extensions, o.extensions);
    // && Objects.equals(authentication, o.authentication); && -> tmp
  }

  /**
   * Checks if this API has the same identity as another API.
   * <p>
   * Two APIs are considered to have the same identity if they belong to the
   * same service, use the same HTTP method, and have matching endpoint paths
   * (using path pattern matching for parameterized paths).
   *
   * @param api the API to compare with
   * @return true if the APIs have the same identity, false otherwise
   */
  @Override
  public boolean sameIdentityAs(Apis api) {
    return Objects.equals(serviceId, api.serviceId)
        && Objects.equals(method, api.method)
        && PathMatchers.getPathMatcher().match(endpoint, api.endpoint);
  }
}
