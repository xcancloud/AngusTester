package cloud.xcan.angus.core.tester.application.query.node.impl;

import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_AGENT_NOT_ONLINE_CODE;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_AGENT_NOT_ONLINE_T;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_IP_EXISTED_T;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_NOT_CONFIGURED_ROLE_CODE;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_NOT_CONFIGURED_ROLE_T;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_NOT_INSTALLED_AGENT_CODE;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_NOT_INSTALLED_AGENT_T;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_PURCHASE_BY_ORDER_REPEATED_CODE;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_PURCHASE_BY_ORDER_REPEATED_T;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_PURCHASE_UPDATE_ERROR_CODE;
import static cloud.xcan.angus.core.tester.domain.TesterCoreMessage.NODE_PURCHASE_UPDATE_ERROR_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isPrivateEdition;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.enums.NodeRole;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAwareProcessor;
import cloud.xcan.angus.core.jpa.page.FixedPageImpl;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.core.tester.application.query.node.NodeInfoQuery;
import cloud.xcan.angus.core.tester.application.query.node.NodeQuery;
import cloud.xcan.angus.core.tester.domain.node.Node;
import cloud.xcan.angus.core.tester.domain.node.NodeListRepo;
import cloud.xcan.angus.core.tester.domain.node.NodeRepo;
import cloud.xcan.angus.core.tester.domain.node.NodeSearchRepo;
import cloud.xcan.angus.core.tester.domain.node.role.NodeRoleRepo;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.model.result.command.SimpleCommandResult;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

/**
 * <p>
 * Implementation of NodeQuery for node management and query operations.
 * </p>
 * <p>
 * Provides methods for node detail retrieval, listing, filtering, quota checking, and role management.
 * </p>
 */
@Biz
@SummaryQueryRegister(name = "Node", table = "node",
    groupByColumns = {"created_date", "source", "enabled", "free", "install_agent"})
public class NodeQueryImpl implements NodeQuery {

  @Resource
  private NodeRepo nodeRepo;
  @Resource
  private NodeListRepo nodeListRepo;
  @Resource
  private NodeSearchRepo nodeSearchRepo;
  @Resource
  private NodeRoleRepo nodeRoleRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;
  @Resource
  private NodeInfoQuery nodeInfoQuery;

  @Transactional
  @Override
  /**
   * <p>
   * Get the detail of a node by its ID.
   * </p>
   * <p>
   * Only returns the node if it is a free node or belongs to the current tenant.
   * </p>
   * @param id Node ID
   * @return Node entity
   */
  public Node detail(Long id) {
    return new BizTemplate<Node>(false) {

      @Override
      protected Node process() {
        Node node = nodeRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Node"));
        assertResourceNotFound(node.isFreeNode()
            || node.getTenantId().equals(getOptTenantId()), id, "node");
        setNodeRoles(List.of(node));
        return node;
      }
    }.execute();
  }

  /**
   * <p>
   * Count the number of nodes matching the given specification.
   * </p>
   * @param spec Specification for filtering nodes
   * @return Number of nodes
   */
  @Override
  public Long count(Specification<Node> spec) {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        return nodeRepo.count(spec);
      }
    }.execute();
  }

  /**
   * <p>
   * List nodes with optional full-text search and role-based filtering.
   * </p>
   * <p>
   * If the user is a tenant client, only non-deleted nodes are listed.
   * </p>
   * <p>
   * If the user has no own nodes, returns free nodes for the specified role.
   * </p>
   * @param spec Node search specification
   * @param pageable Pagination information
   * @param fullTextSearch Whether to use full-text search
   * @param match Full-text search keywords
   * @return Page of nodes
   */
  @Override
  public Page<Node> list(GenericSpecification<Node> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Node>>() {
      @Override
      protected Page<Node> process() {
        if (isTenantClient()) {
          spec.getCriteria().add(SearchCriteria.equal("deleted", false));
        }

        Page<Node> page = null;
        Long tenantId0 = getTenantId(spec);
        if (isPrivateEdition() || hasOwnNodes(tenantId0)) {
          page = fullTextSearch
              ? nodeSearchRepo.find(spec.getCriteria(), pageable, Node.class, match)
              : nodeListRepo.find(spec.getCriteria(), pageable, Node.class, null);
        } else if (isUserAction()) {
          PrincipalContext.addExtension("isFreeNodes", true);
          page = getFreeWhenNonNodes(findFirstValue(spec.getCriteria(), "role"));
        }

        if (nonNull(page) && page.hasContent()) {
          setNodeRoles(page.getContent());
        }
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Find nodes by role for the current tenant.
   * </p>
   * @param nodeRole Node role
   * @return List of nodes
   */
  @Override
  public List<Node> findByRole(NodeRole nodeRole) {
    return nodeRepo.findByTenantIdAndRole(getOptTenantId(), nodeRole.getValue());
  }

  /**
   * <p>
   * Find nodes by a set of search filters.
   * </p>
   * @param filters Set of search criteria
   * @return List of nodes
   */
  @Override
  public List<Node> findByFilters(Set<SearchCriteria> filters) {
    return nodeRepo.findAllByFilters(filters);
  }

  /**
   * <p>
   * Check if the tenant has any nodes.
   * </p>
   * @param tenantId Tenant ID
   * @return true if the tenant has nodes, false otherwise
   */
  @Override
  public boolean hasOwnNodes(Long tenantId) {
    return nodeRepo.countByTenantId(tenantId) > 0;
  }

  /**
   * <p>
   * Determine if a node is a trial (shared) node in cloud service edition.
   * </p>
   * @param nodeId Node ID
   * @return true if the node is a trial node, false otherwise
   */
  @Override
  public boolean isTrailNode(Long nodeId) {
    // Show shared nodes when there are no nodes in the cloud service version
    if (isCloudServiceEdition()) {
      return new TenantAwareProcessor().call(() ->
          nodeRepo.countByIdInAndFree(List.of(nodeId), true) > 0, null);
    }
    return false;
  }

  /**
   * <p>
   * Get free nodes when the tenant has no nodes, only in cloud service edition.
   * </p>
   * @param role Node role
   * @return Page of free nodes
   */
  @Override
  public Page<Node> getFreeWhenNonNodes(String role) {
    // Show shared nodes when there are no nodes in the cloud service version
    if (isCloudServiceEdition()) {
      int count = nodeRepo.countByTenantId(getOptTenantId());
      if (count <= 0) {
        return new TenantAwareProcessor().call(() -> {
          List<Node> nodes = isNull(role) ? nodeRepo.findByTenantIdAndFree(OWNER_TENANT_ID, true)
              : nodeRepo.findByTenantIdAndFreeAndRole(OWNER_TENANT_ID, true, role);
          if (isNotEmpty(nodes)) {
            return new FixedPageImpl<>(nodes);
          }
          return Page.empty();
        }, null);
      }
    }
    return Page.empty();
  }

  /**
   * <p>
   * Find a map of node IDs to Node entities.
   * </p>
   * @param ids Collection of node IDs
   * @return Map of node ID to Node
   */
  @Override
  public Map<Long, Node> findNodeMap(Collection<Long> ids) {
    return nodeRepo.findAllById(ids).stream().collect(Collectors.toMap(Node::getId, x -> x));
  }

  /**
   * <p>
   * Get nodes by IDs, role, enabled status, and limit size.
   * </p>
   * @param nodeIds Set of node IDs
   * @param role Node role
   * @param enabled Enabled status
   * @param size Maximum number of nodes to return
   * @return List of nodes
   */
  @Override
  public List<Node> getNodes(Set<Long> nodeIds, NodeRole role, Boolean enabled, int size) {
    return getNodes(nodeIds, role, enabled, size, null);
  }

  /**
   * <p>
   * Get nodes by IDs, role, enabled status, limit size, and tenant ID.
   * </p>
   * @param nodeIds Set of node IDs
   * @param role Node role
   * @param enabled Enabled status
   * @param size Maximum number of nodes to return
   * @param tenantId Tenant ID
   * @return List of nodes
   */
  @Override
  public List<Node> getNodes(Set<Long> nodeIds, NodeRole role, Boolean enabled,
      int size, Long tenantId) {
    Set<SearchCriteria> filters = new HashSet<>();
    if (nonNull(tenantId)) {
      filters.add(SearchCriteria.equal("tenantId", tenantId));
    }
    if (isNotEmpty(nodeIds)) {
      filters.add(SearchCriteria.in("id", nodeIds));
    }
    if (nonNull(enabled)) {
      filters.add(SearchCriteria.equal("enabled", enabled));
    }
    List<Node> nodes = findByFilters(filters);
    if (nonNull(role) && isNotEmpty(nodes)) {
      Map<Long, Set<NodeRole>> nodeRoles = getNodeRoles(nodes.stream().map(Node::getId).toList());
      nodes = nodes.stream()
          .filter(x -> nodeRoles.containsKey(x.getId()) && nodeRoles.get(x.getId()).contains(role))
          .toList();
    }
    return size > 0 ? nodes.subList(0, Math.min(size, nodes.size())) : nodes;
  }

  /**
   * <p>
   * Check and find a node by ID, throw exception if not found.
   * </p>
   * @param id Node ID
   * @return Node entity
   */
  @Override
  public Node checkAndFind(Long id) {
    return nodeRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Node"));
  }

  /**
   * <p>
   * Check and find nodes by a collection of IDs, throw exception if any not found.
   * </p>
   * @param ids Collection of node IDs
   * @return List of nodes
   */
  @Override
  public List<Node> checkAndFind(Collection<Long> ids) {
    List<Node> nodes = nodeRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(nodes), ids, "Node");
    if (ids.size() != nodes.size()) {
      for (Node node : nodes) {
        assertResourceNotFound(ids.contains(node.getId()), node.getId(), "Node");
      }
    }
    return nodes;
  }

  /**
   * <p>
   * Check if a node has the specified role and agent installed.
   * </p>
   * @param id Node ID
   * @param role Node role
   * @return Node entity
   */
  @Override
  public Node checkRoleAndGetNode(Long id, NodeRole role) {
    Node nodeDb = detail(id); // Multi tenant automatic assembly disabled
    assertTrue(isNotEmpty(nodeDb.getRoles()) && nodeDb.getRoles().contains(role),
        NODE_NOT_CONFIGURED_ROLE_CODE, NODE_NOT_CONFIGURED_ROLE_T, new Object[]{id, role});
    // Verify that the node has the agent installed
    assertTrue(nonNull(nodeDb.getInstallAgent()) && nodeDb.getInstallAgent(),
        NODE_NOT_INSTALLED_AGENT_CODE, NODE_NOT_INSTALLED_AGENT_T, new Object[]{id});
    return nodeDb;
  }

  /**
   * <p>
   * Check if the IPs of the given nodes already exist in the system.
   * </p>
   * @param nodes List of nodes
   */
  @Override
  public void checkIpNotExisted(List<Node> nodes) {
    List<Node> existedNodes = nodeRepo
        .findByIpIn(nodes.stream().map(Node::getIp).collect(Collectors.toSet()));
    assertResourceExisted(existedNodes, NODE_IP_EXISTED_T,
        existedNodes.stream().map(Node::getIp).toArray());
  }

  /**
   * <p>
   * Check if the node quota is exceeded after increment.
   * </p>
   * @param incNum Number of nodes to add
   */
  @Override
  public void checkNodeQuota(int incNum) {
    int nodeNum = incNum + nodeRepo.countByTenantId(getOptTenantId());
    settingTenantQuotaManager.checkTenantQuota(QuotaResource.AngusTesterNode, null,
        (long) nodeNum);
  }

  /**
   * <p>
   * Check if updating node IPs would cause duplication, ignoring the node itself.
   * </p>
   * @param nodes List of nodes
   */
  @Override
  public void checkUpdateIpNotExisted(List<Node> nodes) {
    // Check and modify the IP duplication, ignore modifying the node itself
    Set<Node> ipNodes = nodes.stream().filter(n -> StringUtils.isNotEmpty(n.getIp()))
        .collect(Collectors.toSet());
    if (isNotEmpty(ipNodes)) {
      List<Node> nodesDb = nodeRepo.findByIpIn(ipNodes.stream().map(Node::getIp)
          .collect(Collectors.toSet()));
      if (isNotEmpty(nodesDb)) {
        for (Node existedNode : nodesDb) {
          for (Node ipNode : ipNodes) {
            assertResourceExisted(!existedNode.getIp().equals(ipNode.getIp()) || existedNode.getId()
                .equals(ipNode.getId()), NODE_IP_EXISTED_T, new Object[]{ipNode.getIp()});
          }
        }
      }
    }
  }

  /**
   * <p>
   * Check if nodes are not purchased online before allowing update.
   * </p>
   * @param nodesDb List of nodes
   */
  @Override
  public void checkNotPurchasedUpdate(List<Node> nodesDb) {
    for (Node node : nodesDb) {
      assertTrue(!node.getSource().isOnlineBuy(), NODE_PURCHASE_UPDATE_ERROR_CODE,
          NODE_PURCHASE_UPDATE_ERROR_T, new Object[]{node.getId()});
    }
  }

  /**
   * <p>
   * Check if an order has already been used to purchase a node.
   * </p>
   * @param orderId Order ID
   */
  @Override
  public void checkOrderPurchasedExisted(Long orderId) {
    int count = nodeRepo.countByOrderId(orderId);
    assertTrue(count < 1, NODE_PURCHASE_BY_ORDER_REPEATED_CODE,
        NODE_PURCHASE_BY_ORDER_REPEATED_T, new Object[]{orderId});
  }

  /**
   * <p>
   * Check if the node agent is available and online.
   * </p>
   * @param nodeId Node ID
   */
  @Override
  public void checkNodeAgentAvailable(Long nodeId) {
    Map<Long, SimpleCommandResult> resultMap = nodeInfoQuery.agentStatus(
        true, singletonList(nodeId));
    assertTrue(isNotEmpty(resultMap)
            && resultMap.get(nodeId).isSuccess(), NODE_AGENT_NOT_ONLINE_CODE,
        NODE_AGENT_NOT_ONLINE_T, new Object[]{nodeId});
  }

  /**
   * <p>
   * Get the roles of nodes by their IDs.
   * </p>
   * @param nodeIds List of node IDs
   * @return Map of node ID to set of roles
   */
  @Override
  public Map<Long, Set<NodeRole>> getNodeRoles(List<Long> nodeIds) {
    return nodeRoleRepo.findByNodeIdIn(nodeIds).stream()
        .collect(Collectors.groupingBy(
            cloud.xcan.angus.core.tester.domain.node.role.NodeRole::getNodeId,
            Collectors.mapping(x -> NodeRole.valueOf(x.getRole()), Collectors.toSet())));
  }

  /**
   * <p>
   * Set the roles for a list of nodes.
   * </p>
   * @param nodes List of nodes
   */
  @Override
  public void setNodeRoles(List<Node> nodes) {
    List<Long> nodeIds = nodes.stream().map(Node::getId).collect(Collectors.toList());
    Map<Long, Set<NodeRole>> rolesMap = getNodeRoles(nodeIds);
    if (isNotEmpty(rolesMap)) {
      for (Node node0 : nodes) {
        node0.setRoles(rolesMap.get(node0.getId()));
      }
    }
  }

  /**
   * <p>
   * Get the tenant ID from the specification or context.
   * </p>
   * @param spec Node specification
   * @return Tenant ID
   */
  private Long getTenantId(GenericSpecification<Node> spec) {
    Object tenantId =
        PrincipalContextUtils.isInnerApi() ? findFirstValue(spec.getCriteria(), "tenantId") : null;
    return nonNull(tenantId) ? Long.valueOf(tenantId.toString()) : getOptTenantId();
  }
}
