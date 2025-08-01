package cloud.xcan.angus.core.tester.application.query.exec.impl;

import static cloud.xcan.angus.api.commonlink.CtrlConstant.MAX_FREE_CONCURRENT_TASK;
import static cloud.xcan.angus.api.commonlink.CtrlConstant.MAX_FREE_EXEC_NUM;
import static cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource.AngusTesterConcurrency;
import static cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource.AngusTesterConcurrentTask;
import static cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource.AngusTesterNode;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_ALREADY_IN_RUNNING_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_ALREADY_IN_STOPPED_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NO_OP_PERMISSION;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NO_OP_PERMISSION_CODE;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_TRIAL_CONCURRENT_TASK_OVER_LIMIT_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_TRIAL_TASK_OVER_LIMIT_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.NODE_IS_NOT_APP_ROLE_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.NODE_IS_NOT_EXEC_ROLE_T;
import static cloud.xcan.angus.core.utils.AngusUtils.toServer;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasPolicy;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.model.AngusConstant.SAMPLE_TOTAL_NAME;
import static cloud.xcan.angus.model.element.type.TestTargetType.PLUGIN_HTTP_NAME;
import static cloud.xcan.angus.parser.AngusParser.YAML_MAPPER;
import static cloud.xcan.angus.spec.locale.MessageHolder.message;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.agent.message.runner.RunnerRunVo;
import cloud.xcan.angus.api.commonlink.TesterConstant;
import cloud.xcan.angus.api.commonlink.exec.ExecStatus;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.enums.NodeRole;
import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.api.manager.SettingTenantManager;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.pojo.node.NodeInfo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SneakyThrow0;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndName;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.core.tester.application.query.exec.ExecQuery;
import cloud.xcan.angus.core.tester.application.query.exec.ExecSampleExtcQuery;
import cloud.xcan.angus.core.tester.application.query.exec.ExecSampleQuery;
import cloud.xcan.angus.core.tester.application.query.node.NodeInfoQuery;
import cloud.xcan.angus.core.tester.application.query.script.ScriptQuery;
import cloud.xcan.angus.core.tester.domain.exec.Exec;
import cloud.xcan.angus.core.tester.domain.exec.ExecInfo;
import cloud.xcan.angus.core.tester.domain.exec.ExecInfoListRepo;
import cloud.xcan.angus.core.tester.domain.exec.ExecInfoRepo;
import cloud.xcan.angus.core.tester.domain.exec.ExecInfoSearchRepo;
import cloud.xcan.angus.core.tester.domain.exec.ExecRepo;
import cloud.xcan.angus.core.tester.domain.node.Node;
import cloud.xcan.angus.core.tester.domain.script.ScriptInfo;
import cloud.xcan.angus.core.tester.infra.metricsds.domain.sample.ExecSampleContent;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.model.element.extraction.HttpExtraction;
import cloud.xcan.angus.model.element.http.Http;
import cloud.xcan.angus.model.element.pipeline.PipelineBuilder;
import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.model.script.ScriptSource;
import cloud.xcan.angus.model.script.configuration.Configuration;
import cloud.xcan.angus.model.script.configuration.ScriptType;
import cloud.xcan.angus.parser.AngusParser;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of ExecQuery interface for managing execution queries and operations.
 * <p>
 * This class provides comprehensive functionality for querying execution information,
 * managing quotas, validating permissions, and setting execution details. It handles
 * both regular executions and trial executions with different quota constraints.
 * <p>
 * The implementation includes methods for finding executions by various criteria,
 * checking quotas and permissions, setting execution metadata, and managing
 * execution lifecycle operations.
 * <p>
 * Supports summary query registration for analytics and reporting purposes.
 */
@Biz
@SummaryQueryRegister(name = "Exec", table = "exec",
    groupByColumns = {"created_date", "script_type", "status",}
)
public class ExecQueryImpl implements ExecQuery {

  @Resource
  private ExecRepo execRepo;
  @Resource
  private ExecInfoRepo execInfoRepo;
  @Resource
  private ExecSampleExtcQuery execSampleExtcQuery;
  @Resource
  private ExecInfoListRepo execInfoListRepo;
  @Resource
  private ExecInfoSearchRepo execInfoSearchRepo;
  @Resource
  private ExecSampleQuery execSampleQuery;
  @Resource
  private SettingManager settingManager;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;
  @Resource
  private SettingTenantManager settingTenantManager;
  @Resource
  private ScriptQuery scriptQuery;
  @Resource
  private NodeInfoQuery nodeInfoQuery;

  /**
   * Retrieves detailed execution information by ID.
   * <p>
   * Finds the execution by ID and populates it with complete information including
   * parsed script content, node information, sample content, script names, and
   * operation permissions.
   * <p>
   * The method performs comprehensive data enrichment to provide a complete
   * execution view for detailed analysis and management.
   *
   * @param id the execution ID to retrieve details for
   * @return Exec object with complete execution information
   */
  @Override
  public Exec detail(Long id) {
    return new BizTemplate<Exec>() {

      @Override
      protected Exec process() {
        Exec exec = checkAndFind(id);

        setParsedScriptContent(exec);

        setExecNodeInfo(exec);

        setSampleContent(exec, id);

        List<Exec> execs = List.of(exec);
        setExecScriptName(execs);

        setExecCurrentOperationPermission(execs);

        execSampleQuery.setExecLatestTotalMergeSample(execs);
        return exec;
      }
    }.execute();
  }

  /**
   * Retrieves execution information for a collection of execution IDs.
   * <p>
   * Finds execution information for the specified IDs and optionally joins
   * sample summary data. This method is designed for sharding scenarios
   * where it may be invoked by internal APIs.
   * <p>
   * The joinSampleSummary parameter controls whether to include detailed
   * sample summary information in the results.
   *
   * @param ids collection of execution IDs to retrieve information for
   * @param joinSampleSummary whether to include sample summary data
   * @return List of ExecInfo objects with execution information
   */
  @Override
  public List<ExecInfo> listInfo(Set<Long> ids, Boolean joinSampleSummary) {
    return new BizTemplate<List<ExecInfo>>() {

      @Override
      protected List<ExecInfo> process() {
        List<ExecInfo> execs = findInfo(ids);

        // For sharding invoke by innerapi
        setSampleSummary(execs, joinSampleSummary);
        return execs;
      }
    }.execute();
  }

  /**
   * Retrieves execution information by source type and resource IDs.
   * <p>
   * Finds execution information for the specified resource type and IDs,
   * optionally joining sample summary data. This method supports queries
   * across different execution sources like scripts, scenarios, or APIs.
   * <p>
   * The joinSampleSummary parameter controls whether to include detailed
   * sample summary information in the results.
   *
   * @param resourceType the type of resource (script, scenario, etc.)
   * @param resourceIds set of resource IDs to find executions for
   * @param joinSampleSummary whether to include sample summary data
   * @return List of ExecInfo objects with execution information
   */
  @Override
  public List<ExecInfo> listInfoBySource(ScriptSource resourceType, Set<Long> resourceIds,
      Boolean joinSampleSummary) {
    return new BizTemplate<List<ExecInfo>>() {

      @Override
      protected List<ExecInfo> process() {
        List<ExecInfo> execs = findInfoBySource(resourceType, resourceIds);

        setSampleSummary(execs, joinSampleSummary);
        return execs;
      }
    }.execute();
  }

  /**
   * Retrieves the raw script content for an execution.
   * <p>
   * Finds the execution by ID and returns the unparsed script content.
   * This method is useful for debugging or when raw script access is needed.
   *
   * @param id the execution ID to retrieve script content for
   * @return the raw script content as a string
   */
  @Override
  public String script(Long id) {
    return new BizTemplate<String>() {
      Exec execDb;

      @Override
      protected void checkParams() {
        execDb = checkAndFind(id);
      }

      @Override
      protected String process() {
        return execDb.getScript();
      }
    }.execute();
  }

  /**
   * Finds server information from HTTP plugin executions.
   * <p>
   * Extracts server information from HTTP plugin executions by parsing
   * the script content and extracting server configurations from both
   * configuration variables and pipeline elements.
   * <p>
   * Only supports HTTP plugin executions and returns distinct server URLs.
   *
   * @param id the execution ID to extract server information from
   * @return List of Server objects with server configuration
   * @throws ProtocolException if script parsing fails
   */
  @Override
  public List<Server> findServers(Long id) {
    return new BizTemplate<List<Server>>() {
      Exec execDb;

      @Override
      protected void checkParams() {
        execDb = checkAndFind(id);
        assertTrue(PLUGIN_HTTP_NAME.equals(execDb.getPlugin()), "Only http plugin is supported");
      }

      @Override
      protected List<Server> process() {
        AngusScript angusScript;
        try {
          angusScript = YAML_MAPPER.readValue(execDb.getScript(), AngusScript.class);
        } catch (JsonProcessingException e) {
          throw ProtocolException.of("Execution script parsing exception: " + e.getMessage());
        }
        List<Server> servers = new ArrayList<>();
        if (nonNull(angusScript.getConfiguration()) && isNotEmpty(
            angusScript.getConfiguration().getVariables())) {
          servers.addAll(angusScript.getConfiguration().getVariables().stream()
              .filter(x -> x.isExtractable() && x.getExtraction() instanceof HttpExtraction)
              .map(x -> (HttpExtraction) x.getExtraction())
              .filter(x -> nonNull(x.getRequest().getServer())
                  && nonNull(x.getRequest().getServer().getUrl()))
              .map(x -> toServer(x.getRequest().getServer())).toList());
        }
        if (nonNull(angusScript.getTask()) && isNotEmpty(angusScript.getTask().getPipelines())) {
          servers.addAll(angusScript.getTask().getPipelines().stream().filter(
                  x -> x instanceof Http && nonNull(((Http) x).getRequest())
                      && nonNull(((Http) x).getRequest().getServer())
                      && nonNull(((Http) x).getRequest().getServer().getUrl()))
              .map(x -> toServer(((Http) x).getRequest().getServer())).toList());
        }
        return servers.stream()
            .filter(cloud.xcan.angus.spec.utils.ObjectUtils.distinctByKey(Server::getUrl))
            .collect(Collectors.toList());
      }
    }.execute();
  }

  /**
   * Lists executions with pagination and search capabilities.
   * <p>
   * Provides paginated execution listing with support for both regular
   * database queries and full-text search operations. The method handles
   * project-based filtering and enriches results with script names,
   * operation permissions, and sample data.
   * <p>
   * Supports different search modes based on the fullTextSearch parameter
   * and can apply additional matching criteria for refined results.
   *
   * @param spec the search specification for filtering executions
   * @param pageable pagination parameters
   * @param fullTextSearch whether to use full-text search
   * @param match additional matching criteria for search
   * @return Page of ExecInfo objects with execution information
   */
  @SneakyThrow0(level = "WARN") // Check exec quota in running
  @Override
  public Page<ExecInfo> list(GenericSpecification<ExecInfo> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<ExecInfo>>() {
      String projectId;

      @Override
      protected void checkParams() {
        projectId = findFirstValue(spec.getCriteria(), "projectId", SearchOperation.EQUAL);
        assertNotNull(projectId, "projectId must not be null");
      }

      @Override
      protected Page<ExecInfo> process() {
        Page<ExecInfo> page = fullTextSearch
            ? execInfoSearchRepo.find(spec.getCriteria(), pageable, ExecInfo.class, match)
            : execInfoListRepo.find(spec.getCriteria(), pageable, ExecInfo.class, null);
        if (page.hasContent()) {
          setExecInfoScriptName(page.getContent());
          setExecInfoCurrentOperationPermission(page.getContent());
          execSampleQuery.setExecInfoLatestTotalMergeSample(page.getContent());
        }
        return page;
      }
    }.execute();
  }

  /**
   * Finds executions by node ID.
   * <p>
   * Retrieves all executions associated with a specific node ID.
   * This method is useful for node-specific execution analysis and management.
   *
   * @param nodeId the node ID to find executions for
   * @return List of ExecInfo objects for the specified node
   */
  @Override
  public List<ExecInfo> findByNodeId(Long nodeId) {
    return new BizTemplate<List<ExecInfo>>() {

      @Override
      protected List<ExecInfo> process() {
        return execInfoRepo.findAllByNodeId(nodeId);
      }
    }.execute();
  }

  /**
   * Finds execution by script ID, type, and source.
   * <p>
   * Searches for executions based on script ID and filters by script type
   * and source. For API sources, returns the first execution found.
   * For other sources, filters by the specified script type.
   *
   * @param scriptId the script ID to search for
   * @param scriptType the script type to filter by
   * @param scriptSource the script source to filter by
   * @return Exec object if found, null otherwise
   */
  @Override
  public Exec findByScript(Long scriptId, ScriptType scriptType, ScriptSource scriptSource) {
    List<Exec> execsDb = execRepo.findByScriptId(scriptId);
    if (isEmpty(execsDb)) {
      return null;
    }
    return scriptSource.isApis() ? execsDb.get(0)
        : execsDb.stream().filter(x -> x.getScriptType().equals(scriptType)).findFirst()
            .orElse(null);
  }

  /**
   * Finds execution information by ID.
   *
   * @param id the execution ID to find
   * @return ExecInfo object if found, null otherwise
   */
  @Override
  public ExecInfo findInfo(Long id) {
    return execInfoRepo.findById(id).orElse(null);
  }

  /**
   * Finds execution information by source type and resource IDs.
   *
   * @param resourceType the type of resource
   * @param resourceIds set of resource IDs to find executions for
   * @return List of ExecInfo objects for the specified resources
   */
  @Override
  public List<ExecInfo> findInfoBySource(ScriptSource resourceType,
      Set<Long> resourceIds) {
    return execInfoRepo.findByScriptSourceAndScriptSourceIdIn(resourceType, resourceIds);
  }

  /**
   * Finds execution information for a collection of execution IDs.
   *
   * @param execIds collection of execution IDs to find information for
   * @return List of ExecInfo objects for the specified executions
   */
  @Override
  public List<ExecInfo> findInfo(Collection<Long> execIds) {
    return execInfoRepo.findAllById(execIds);
  }

  /**
   * Finds execution information map for a collection of execution IDs.
   *
   * @param execIds collection of execution IDs to find information for
   * @return Map of execution ID to IdAndName objects
   */
  @Override
  public Map<Long, IdAndName> findInfoMap(Collection<Long> execIds) {
    return execRepo.findInfoByIdIn(execIds).stream()
        .collect(Collectors.toMap(IdAndName::getId, x -> x));
  }

  /**
   * Finds tenant event notice types configuration.
   * <p>
   * Retrieves the configured event notice types for a tenant, either from
   * cached settings or from the global default configuration.
   *
   * @param tenantId the tenant ID to get event notice types for
   * @return Map of event codes to notice types
   */
  @Override
  public Map<String, List<NoticeType>> findTenantEventNoticeTypes(Long tenantId) {
    Long finalTenantId = nullSafe(tenantId, getOptTenantId());
    String cachedSettingTenant = settingTenantManager.getCachedSetting(finalTenantId);
    SettingTenant settingTenant = settingTenantManager.parseCachedSetting(cachedSettingTenant);
    List<TesterEvent> eventData =
        isNull(settingTenant) || isEmpty(settingTenant.getTesterEventData())
            ? settingManager.setting(SettingKey.TESTER_EVENT).getTesterEvent()
            : settingTenant.getTesterEventData();
    return eventData.stream()
        .filter(x -> isNotEmpty(x.getEventCode()) && isNotEmpty(x.getNoticeTypes())).
        collect(Collectors.toMap(TesterEvent::getEventCode, TesterEvent::getNoticeTypes));
  }

  /**
   * Finds execution by ID and throws ResourceNotFound if not found.
   *
   * @param id the execution ID to find
   * @return Exec object if found
   * @throws ResourceNotFound if the execution is not found
   */
  @Override
  public Exec checkAndFind(Long id) {
    return execRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Exec"));
  }

  /**
   * Finds executions by IDs and throws ResourceNotFound for missing ones.
   *
   * @param ids collection of execution IDs to find
   * @return List of Exec objects for the specified executions
   * @throws ResourceNotFound if any execution is not found
   */
  @Override
  public List<Exec> checkAndFind(Collection<Long> ids) {
    List<Exec> execs = execRepo.findAllById(ids);
    Set<Long> execIds = execs.stream().map(Exec::getId).collect(Collectors.toSet());
    ids.removeAll(execIds);
    assertResourceNotFound(isEmpty(ids), ids, "Exec");
    return execs;
  }

  /**
   * Finds execution information by ID and throws ResourceNotFound if not found.
   *
   * @param id the execution ID to find
   * @return ExecInfo object if found
   * @throws ResourceNotFound if the execution is not found
   */
  @Override
  public ExecInfo checkAndFindInfo(Long id) {
    return execInfoRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Exec"));
  }

  /**
   * Finds execution information by IDs and throws ResourceNotFound for missing ones.
   *
   * @param ids collection of execution IDs to find
   * @return List of ExecInfo objects for the specified executions
   * @throws ResourceNotFound if any execution is not found
   */
  @Override
  public List<ExecInfo> checkAndFindInfo(Collection<Long> ids) {
    List<ExecInfo> execs = execInfoRepo.findAllById(ids);
    Set<Long> execIds = execs.stream().map(ExecInfo::getId).collect(Collectors.toSet());
    ids.removeAll(execIds);
    assertResourceNotFound(isEmpty(ids), ids, "Exec");
    return execs;
  }

  /**
   * Checks if an execution is not currently running.
   * <p>
   * Validates that the execution status is not in a running state.
   * Throws an exception if the execution is already running.
   *
   * @param exec the execution to check
   * @throws BizException if the execution is already running
   */
  @Override
  public void checkNotRunning(Exec exec) {
    assertTrue(!exec.getStatus().isRunning(), EXEC_ALREADY_IN_RUNNING_T,
        new Object[]{exec.getName()});
  }

  /**
   * Checks if an execution is not in a stopped state.
   * <p>
   * Validates that the execution status is not in a wide stopped state.
   * Throws an exception if the execution is already stopped.
   *
   * @param exec the execution information to check
   * @throws BizException if the execution is already stopped
   */
  @Override
  public void checkNotStopped(ExecInfo exec) {
    assertTrue(!exec.getStatus().isWideStopped(), EXEC_ALREADY_IN_STOPPED_T,
        new Object[]{exec.getName()});
  }

  /**
   * Validates node configuration and availability.
   * <p>
   * Checks that configured nodes exist and have the correct role types.
   * Validates both available nodes (for execution) and application nodes.
   * <p>
   * For trial executions, this validation may be bypassed or modified
   * based on trial-specific requirements.
   *
   * @param configuration the configuration containing node selectors
   * @param trial whether this is a trial execution
   * @throws ResourceNotFound if configured nodes do not exist
   * @throws BizException if nodes do not have the required roles
   */
  @Override
  public void checkNodeValid(Configuration configuration, boolean trial) {
    if (!configuration.hasAppNodes() && !configuration.hasAvailableNodes()) {
      return;
    }
    Set<Long> nodeIds = getAllNodeIds(configuration);
    if (isNotEmpty(nodeIds)) {
      Map<Long, Node> scriptMap = nodeInfoQuery.getNodeMap(nodeIds);
      if (isNotEmpty(scriptMap)) {
        if (isNotEmpty(configuration.getNodeSelectors().getAvailableNodeIds())) {
          for (Long nodeId : configuration.getNodeSelectors().getAvailableNodeIds()) {
            assertResourceNotFound(scriptMap.containsKey(nodeId), nodeId, "Node");
            assertTrue(scriptMap.get(nodeId).getRoles().contains(NodeRole.EXECUTION),
                message(NODE_IS_NOT_EXEC_ROLE_T, new Object[]{nodeId}));
          }
        }
        if (isNotEmpty(configuration.getNodeSelectors().getAppNodeIds())) {
          for (Long nodeId : configuration.getNodeSelectors().getAppNodeIds()) {
            assertResourceNotFound(scriptMap.containsKey(nodeId), nodeId, "Node");
            assertTrue(scriptMap.get(nodeId).getRoles().contains(NodeRole.APPLICATION),
                message(NODE_IS_NOT_APP_ROLE_T, new Object[]{nodeId}));
          }
        }
      }
    }
  }

  /**
   * Checks if the current user has permission to operate on an execution.
   * <p>
   * Validates that the current user is either an admin, the execution creator,
   * or has been granted operation permissions. For system users or admin users,
   * permission checks are bypassed.
   * <p>
   * Throws a BizException if the user lacks permission to operate on the execution.
   *
   * @param exec the execution to check permissions for
   * @throws BizException if the user lacks permission
   */
  @Override
  public void checkPermission(Exec exec) {
    if (isEmpty(exec) || isAdminUser() || !isUserAction()) {
      return;
    }
    if (-1L != exec.getCreatedBy() && !exec.getCreatedBy().equals(getUserId())) {
      throw BizException.of(EXEC_NO_OP_PERMISSION_CODE, EXEC_NO_OP_PERMISSION);
    }
  }

  /**
   * Checks if the current user has permission to operate on execution information.
   * <p>
   * Similar to checkPermission but operates on ExecInfo objects.
   * Validates that the current user is either an admin, the execution creator,
   * or has been granted operation permissions.
   * <p>
   * Throws a BizException if the user lacks permission to operate on the execution.
   *
   * @param exec the execution information to check permissions for
   * @throws BizException if the user lacks permission
   */
  @Override
  public void checkPermissionInfo(ExecInfo exec) {
    if (isEmpty(exec) || isAdminUser() || !isUserAction()) {
      return;
    }
    if (-1L != exec.getCreatedBy() && !exec.getCreatedBy().equals(getUserId())) {
      throw BizException.of(EXEC_NO_OP_PERMISSION_CODE, EXEC_NO_OP_PERMISSION);
    }
  }

  /**
   * Checks if adding the specified increment would exceed the tenant's execution quota.
   * <p>
   * For trial executions, checks against free execution limits.
   * For regular executions, validates against tenant-specific quota limits.
   * <p>
   * Only performs the check if the increment is greater than 0.
   *
   * @param incr the increment to check against the quota
   * @param trial whether this is a trial execution
   * @throws QuotaException if the quota would be exceeded
   */
  @Override
  public void checkAddQuota(long incr, boolean trial) {
    if (incr > 0) {
      if (trial) {
        long num = execRepo.countByTenantIdAndTrial(getOptTenantId(), true);
        if (num >= MAX_FREE_EXEC_NUM) {
          throw QuotaException.of(message(EXEC_TRIAL_TASK_OVER_LIMIT_T,
              new Object[]{MAX_FREE_EXEC_NUM}));
        }
      } else {
        long num = execRepo.countByTenantId(getOptTenantId());
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.AngusTesterExecution,
            null, num + incr);
      }
    }
  }

  /**
   * Checks thread and node quota limits for a configuration.
   * <p>
   * Validates that the configuration's thread count and node count do not
   * exceed tenant quota limits. For trial executions, this validation
   * may be bypassed or modified.
   * <p>
   * Only performs validation for non-trial executions with valid configurations.
   *
   * @param trial whether this is a trial execution
   * @param configuration the configuration to validate
   * @throws QuotaException if quota limits would be exceeded
   */
  @Override
  public void checkThreadAndNodesQuota(Boolean trial, Configuration configuration) {
    /* If trial, Front end verification, backend forcibly overwrites as security value */
    if ((isNull(trial) || !trial) && nonNull(configuration)) {
      if (nonNull(configuration.getThread())) {
        settingTenantQuotaManager.checkTenantQuota(AngusTesterConcurrency,
            null, (long) configuration.getThread().getThreads());
      }
      if (nonNull(configuration.getNodeSelectors())
          && nonNull(configuration.getNodeSelectors().getNum())) {
        settingTenantQuotaManager.checkTenantQuota(AngusTesterNode,
            null, (long) configuration.getNodeSelectors().getNum());
      }
    }
  }

  /**
   * Checks concurrent task quota with error handling.
   * <p>
   * Wrapper method that uses SneakyThrow0 to handle quota checking errors
   * gracefully. Delegates to the actual quota checking logic.
   *
   * @param incr the increment to check against the concurrent task quota
   * @param trial whether this is a trial execution
   */
  @SneakyThrow0(level = "WARN")
  @Override
  public void checkConcurrentTaskQuota(long incr, boolean trial) {
    if (incr > 0) {
      checkConcurrentTaskQuota00(incr, trial);
    }
  }

  /**
   * Checks concurrent task quota without error handling.
   * <p>
   * Direct quota checking method that performs the actual validation
   * without additional error handling wrappers.
   *
   * @param incr the increment to check against the concurrent task quota
   * @param trial whether this is a trial execution
   */
  @Override
  public void checkConcurrentTaskQuota0(long incr, boolean trial) {
    if (incr > 0) {
      checkConcurrentTaskQuota00(incr, trial);
    }
  }

  private void checkConcurrentTaskQuota00(long incr, boolean trial) {
    long num = execRepo.countByTenantIdAndStatus(getOptTenantId(), ExecStatus.RUNNING);
    if (trial) {
      if (num >= MAX_FREE_CONCURRENT_TASK) {
        throw QuotaException.of(message(EXEC_TRIAL_CONCURRENT_TASK_OVER_LIMIT_T,
            new Object[]{MAX_FREE_CONCURRENT_TASK}));
      }
    } else {
      settingTenantQuotaManager.checkTenantQuota(AngusTesterConcurrentTask, null, num + incr);
    }
  }

  @Override
  public boolean isAdminUser() {
    return hasPolicy(TesterConstant.ANGUSTESTER_ADMIN) || isTenantSysAdmin();
  }

  /**
   * Retrieves the pipeline target mappings for a given execution ID.
   * <p>
   * Parses the script content and returns the enabled pipeline target name mapping.
   *
   * @param execId the execution ID
   * @return LinkedHashMap mapping pipeline names to target lists
   */
  @Override
  public LinkedHashMap<String, List<String>> getPipelineTargetMappings(Long execId) {
    Exec execDb = checkAndFind(execId);
    setParsedScriptContent(execDb);
    return execDb.getPipelineTargetMappings();
  }

  /**
   * Determines if the current user has permission to operate on the given execution.
   * <p>
   * Checks admin status, creator, executor, or script creator for permission.
   *
   * @param exec the execution to check
   * @return true if the user has permission, false otherwise
   */
  @Override
  public boolean hasPermission(Exec exec) {
    if (isAdminUser()) {
      return true;
    }
    return -1L == exec.getCreatedBy() || exec.getCreatedBy().equals(getUserId())
        || (nonNull(exec.getExecBy()) && exec.getExecBy().equals(getUserId()))
        || (nonNull(exec.getScriptCreatedBy()) && exec.getScriptCreatedBy().equals(getUserId()));
  }

  /**
   * Determines if the current user has permission to operate on the given execution info.
   * <p>
   * Checks admin status, creator, executor, or script creator for permission.
   *
   * @param exec the execution info to check
   * @return true if the user has permission, false otherwise
   */
  @Override
  public boolean hasPermissionInfo(ExecInfo exec) {
    if (isAdminUser()) {
      return true;
    }
    return -1L == exec.getCreatedBy() || exec.getCreatedBy().equals(getUserId())
        || (nonNull(exec.getExecBy()) && exec.getExecBy().equals(getUserId()))
        || (nonNull(exec.getScriptCreatedBy()) && exec.getScriptCreatedBy().equals(getUserId()));
  }

  /**
   * Sets the script name and source name for a list of executions.
   * <p>
   * Looks up script information and populates the script name and source name fields.
   *
   * @param execs the list of executions to update
   */
  @Override
  public void setExecScriptName(List<Exec> execs) {
    if (isNotEmpty(execs)) {
      Set<Long> scriptIds = execs.stream().map(Exec::getScriptId)
          .filter(Objects::nonNull).collect(Collectors.toSet());
      if (isNotEmpty(scriptIds)) {
        List<ScriptInfo> scripts = scriptQuery.infos(scriptIds);
        if (isNotEmpty(scripts)) {
          Map<Long, ScriptInfo> scriptMap = scripts.stream()
              .collect(Collectors.toMap(ScriptInfo::getId, x -> x));
          for (Exec exec : execs) {
            if (scriptMap.containsKey(exec.getScriptId())) {
              exec.setScriptName(scriptMap.get(exec.getScriptId()).getName());
              exec.setScriptSourceName(scriptMap.get(exec.getScriptId()).getSourceName());
            }
          }
        }
      }
    }
  }

  /**
   * Sets the script name and source name for a list of execution info objects.
   * <p>
   * Looks up script information and populates the script name and source name fields.
   *
   * @param execs the list of execution info objects to update
   */
  @Override
  public void setExecInfoScriptName(List<ExecInfo> execs) {
    if (isNotEmpty(execs)) {
      Set<Long> scriptIds = execs.stream().map(ExecInfo::getScriptId)
          .filter(Objects::nonNull).collect(Collectors.toSet());
      if (isNotEmpty(scriptIds)) {
        List<ScriptInfo> scripts = scriptQuery.infos(scriptIds);
        if (isNotEmpty(scripts)) {
          Map<Long, ScriptInfo> scriptMap = scripts.stream()
              .collect(Collectors.toMap(ScriptInfo::getId, x -> x));
          for (ExecInfo exec : execs) {
            if (scriptMap.containsKey(exec.getScriptId())) {
              exec.setScriptName(scriptMap.get(exec.getScriptId()).getName());
              exec.setScriptSourceName(scriptMap.get(exec.getScriptId()).getSourceName());
            }
          }
        }
      }
    }
  }

  /**
   * Sets node information for an execution.
   * <p>
   * Populates available, execution, and application node lists based on node IDs.
   *
   * @param exec the execution to update
   */
  @Override
  public void setExecNodeInfo(Exec exec) {
    Set<Long> nodeIds = getAllNodeIds(exec);
    if (isNotEmpty(nodeIds)) {
      Map<Long, Node> nodeMap = nodeInfoQuery.getNodeMap(nodeIds);
      if (isNotEmpty(nodeMap)) {
        if (isNotEmpty(exec.getAvailableNodeIds())) {
          Set<Long> availableNodesIds = exec.getAvailableNodeIds();
          exec.setAvailableNodes(nodeMap.entrySet().stream()
              .filter(x -> availableNodesIds.contains(x.getKey()))
              .map(x -> CoreUtils.copyProperties(x.getValue(), new NodeInfo()))
              .collect(Collectors.toList()));
        }
        Set<Long> execNodesIds = isNotEmpty(exec.getExecNodeIds()) ? exec.getExecNodeIds()
            : isEmpty(exec.getLastSchedulingResult())
                // Fix:: When scheduling fails, the execution node is empty.
                ? Collections.emptySet() : exec.getLastSchedulingResult().stream()
                .map(RunnerRunVo::getDeviceId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (isNotEmpty(execNodesIds)) {
          exec.setExecNodes(nodeMap.entrySet().stream()
              .filter(x -> execNodesIds.contains(x.getKey()))
              .map(x -> CoreUtils.copyProperties(x.getValue(), new NodeInfo()))
              .collect(Collectors.toList()));
        }
        if (isNotEmpty(exec.getAppNodeIds())) {
          Set<Long> appNodesIds = exec.getAppNodeIds();
          exec.setAppNodes(nodeMap.entrySet().stream()
              .filter(x -> appNodesIds.contains(x.getKey()))
              .map(x -> CoreUtils.copyProperties(x.getValue(), new NodeInfo()))
              .collect(Collectors.toList()));
        }
      }
    }
  }

  /**
   * Sets sample content for an execution if it is a functional testing type.
   * <p>
   * Retrieves and sets sample contents for the execution.
   *
   * @param exec the execution to update
   * @param id the execution ID
   */
  @Override
  public void setSampleContent(Exec exec, Long id) {
    if (exec.getScriptType().isFunctionalTesting()) {
      List<ExecSampleContent> sampleContents = execSampleExtcQuery.findIterationSampleContent(id);
      exec.setSampleContents(sampleContents);
    }
  }

  /**
   * Sets operation permission flag for a list of executions.
   * <p>
   * Determines if the current user has operation permission for each execution.
   *
   * @param execs the list of executions to update
   */
  @Override
  public void setExecCurrentOperationPermission(List<Exec> execs) {
    boolean isAdmin = isAdminUser();
    for (Exec exec : execs) {
      exec.setHasOperationPermission(isAdmin || hasPermission(exec));
    }
  }

  /**
   * Sets operation permission flag for a list of execution info objects.
   * <p>
   * Determines if the current user has operation permission for each execution info.
   *
   * @param execs the list of execution info objects to update
   */
  @Override
  public void setExecInfoCurrentOperationPermission(List<ExecInfo> execs) {
    boolean isAdmin = isAdminUser();
    for (ExecInfo exec : execs) {
      exec.setHasOperationPermission(isAdmin || hasPermissionInfo(exec));
    }
  }

  /**
   * Sets sample summary for a list of execution info objects if required.
   * <p>
   * Optionally sets the latest total merge sample for each execution info.
   *
   * @param execs the list of execution info objects to update
   * @param joinSampleSummary whether to include sample summary data
   */
  @Override
  public void setSampleSummary(List<ExecInfo> execs, Boolean joinSampleSummary) {
    if (isEmpty(execs)) {
      return;
    }

    // setExecInfoScriptName(execs);
    // setExecInfoCurrentOperationPermission(execs);

    if (isNull(joinSampleSummary) || joinSampleSummary) {
      execSampleQuery.setExecInfoLatestTotalMergeSample(execs);
    }
  }

  /**
   * Parses and sets the script content for an execution.
   * <p>
   * Deserializes the script content from YAML format and sets the configuration,
   * task, and pipeline target mappings. Handles cases where pipeline names
   * might be empty for single task executions.
   * <p>
   * If parsing fails, throws a SysException with the error details.
   *
   * @param exec the execution to set parsed script content for
   * @throws SysException if script content format is invalid
   */
  @Override
  public void setParsedScriptContent(Exec exec) {
    try {
      AngusScript angusScript = AngusParser.YAML_MAPPER.readValue(exec.getScript(),
          AngusScript.class);
      exec.setConfiguration(angusScript.getConfiguration());
      exec.setTask(angusScript.getTask());
      if (isNotEmpty(angusScript.getTask().getPipelines())) {
        PipelineBuilder builder = PipelineBuilder.of(angusScript.getTask().getPipelines());
        // Fix: Single task pipeline name can be empty
        if (isNull(builder) || isEmpty(builder.getEnabledTargetNameMapping())) {
          LinkedHashMap<String, List<String>> pipelines = new LinkedHashMap<>();
          pipelines.put(SAMPLE_TOTAL_NAME, new ArrayList<>());
          exec.setPipelineTargetMappings(pipelines);
          return;
        } else if (builder.getEnabledTargetNameMapping().size() == 1 && isEmpty(
            builder.getEnabledTargetNameMapping().values().stream().findFirst().orElse(null))) {
          builder.getEnabledTargetNameMapping().clear();
          builder.getEnabledTargetNameMapping().put(SAMPLE_TOTAL_NAME, new ArrayList<>());
        } else {
          builder.getEnabledTargetNameMapping().put(SAMPLE_TOTAL_NAME, new ArrayList<>());
        }
        exec.setPipelineTargetMappings(builder.getEnabledTargetNameMapping());
      }
    } catch (Exception e) {
      throw SysException.of("Script content format error: " + e.getMessage());
    }
  }

  /**
   * Retrieves all node IDs referenced by an execution.
   * <p>
   * Collects available, execution, application, and last scheduled node IDs.
   *
   * @param exec the execution to extract node IDs from
   * @return Set of node IDs
   */
  private Set<Long> getAllNodeIds(Exec exec) {
    Set<Long> nodeIds = new HashSet<>();
    if (isNotEmpty(exec.getAvailableNodeIds())) {
      nodeIds.addAll(exec.getAvailableNodeIds());
    }
    if (isNotEmpty(exec.getExecNodeIds())) {
      nodeIds.addAll(exec.getExecNodeIds());
    }
    if (isNotEmpty(exec.getAppNodeIds())) {
      nodeIds.addAll(exec.getAppNodeIds());
    }
    if (isNotEmpty(exec.getLastSchedulingResult())) {
      nodeIds.addAll(exec.getLastSchedulingResult().stream()
          .map(RunnerRunVo::getDeviceId).filter(Objects::nonNull).toList());
    }
    return nodeIds;
  }

  /**
   * Retrieves all node IDs referenced by a configuration.
   * <p>
   * Collects available and application node IDs from the configuration.
   *
   * @param configuration the configuration to extract node IDs from
   * @return Set of node IDs
   */
  private Set<Long> getAllNodeIds(Configuration configuration) {
    Set<Long> nodeIds = new HashSet<>();
    if (nonNull(configuration.getNodeSelectors())) {
      if (isNotEmpty(configuration.getNodeSelectors().getAvailableNodeIds())) {
        nodeIds.addAll(configuration.getNodeSelectors().getAvailableNodeIds());
      }
      if (isNotEmpty(configuration.getNodeSelectors().getAppNodeIds())) {
        nodeIds.addAll(configuration.getNodeSelectors().getAppNodeIds());
      }
    }
    return nodeIds;
  }
}
