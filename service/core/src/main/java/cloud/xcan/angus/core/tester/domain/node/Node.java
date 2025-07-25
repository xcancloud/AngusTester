package cloud.xcan.angus.core.tester.domain.node;


import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.node.NodeSource;
import cloud.xcan.angus.api.enums.NodeRole;
import cloud.xcan.angus.api.pojo.node.NodeSpecData;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.tester.infra.iaas.InstanceChargeType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

/**
 * Node domain entity representing a test execution node in the distributed testing system.
 * <p>
 * This entity manages the lifecycle and configuration of testing nodes, including both
 * cloud instances and on-premise machines. Each node can be assigned different roles
 * and contains all necessary connection and authentication information.
 * <p>
 * The node entity supports multi-tenancy and soft deletion patterns, with automatic
 * auditing of creation and modification timestamps.
 *
 * @author Angus Team
 * @since 1.0.0
 */
@Entity
@Table(name = "node")
@SQLRestriction("deleted = 0")
@Setter
@Getter
@Accessors(chain = true)
public class Node extends TenantAuditingEntity<Node, Long> {

  /**
   * Primary key identifier for the node.
   */
  @Id
  private Long id;

  /**
   * Human-readable name of the node for identification purposes.
   */
  private String name;

  /**
   * Internal IP address of the node, typically used for private network communication.
   */
  private String ip;

  /**
   * Public IP address of the node, used for external access and communication.
   */
  private String publicIp;

  /**
   * Cloud region identifier where the node is deployed.
   * <p>
   * This field is particularly relevant for cloud-based instances to track
   * geographical distribution and latency considerations.
   */
  private String regionId;

  /**
   * Domain name associated with the node for DNS-based access.
   */
  private String domain;

  /**
   * SSH username for remote access to the node.
   */
  private String username;

  /**
   * SSH password for authentication (consider using key-based auth for production).
   */
  private String password;

  /**
   * SSH port number for remote connection (default is typically 22).
   */
  private Integer sshPort;

  /**
   * Node specification data containing hardware and software configuration details.
   * <p>
   * Stored as JSON to accommodate flexible specification formats across different
   * cloud providers and node types.
   */
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "spec")
  private NodeSpecData spec;

  /**
   * Source type indicating how this node was created or provisioned.
   */
  @Enumerated(EnumType.STRING)
  private NodeSource source;

  /**
   * Indicates whether this node is available for free use without cost implications.
   */
  private Boolean free;

  /**
   * Indicates whether the node is currently enabled and available for task execution.
   */
  private Boolean enabled;

  /**
   * Cloud provider instance identifier for tracking the underlying infrastructure.
   */
  private String instanceId;

  /**
   * Human-readable name of the cloud instance.
   */
  private String instanceName;

  /**
   * Expiration date for cloud instances with limited lifecycle.
   */
  private LocalDateTime instanceExpiredDate;

  /**
   * Soft deletion flag to mark nodes as deleted without physical removal.
   */
  private Boolean deleted;

  /**
   * Indicates whether the node has expired and should not be used for new tasks.
   */
  private Boolean expired;

  /**
   * Reference to the order ID if this node was provisioned through a billing system.
   */
  private Long orderId;

  /**
   * Billing model for cloud instances (e.g., on-demand, reserved, spot).
   */
  @Enumerated(EnumType.STRING)
  private InstanceChargeType chargeType;

  /**
   * Flag indicating whether the Angus agent should be installed on this node.
   */
  public Boolean installAgent;

  /**
   * Synchronization status flag for tracking node state consistency.
   */
  private Boolean sync;

  /**
   * Merged search field for efficient ID-based queries and indexing.
   * <p>
   * This field combines multiple identifiers to enable fast search operations
   * across different node identification schemes.
   */
  private String extSearchMerge;

  /**
   * Set of roles assigned to this node for capability-based task routing.
   * <p>
   * This transient field is populated at runtime based on current assignments
   * and is not persisted to the database.
   */
  @Transient
  private Set<NodeRole> roles;

  /**
   * Validates whether the node has complete SSH configuration for remote access.
   * <p>
   * A node is considered to have full SSH configuration if it has either a public IP
   * or internal IP, along with username, password, and SSH port configured.
   *
   * @return true if all required SSH configuration fields are present and valid
   */
  public boolean hasFullSshConfig() {
    return (isNotEmpty(this.publicIp) || isNotEmpty(this.ip))
        && isNotEmpty(this.username) && isNotEmpty(this.password)
        && isNotEmpty(this.sshPort);
  }

  /**
   * Checks if this node is designated as a free-tier node.
   * <p>
   * Free nodes are typically used for development, testing, or trial purposes
   * and may have usage limitations or restrictions.
   *
   * @return true if the node is marked as free, false otherwise
   */
  public boolean isFreeNode() {
    return nonNull(free) && free;
  }

  /**
   * Returns the primary identifier for this entity.
   * <p>
   * This method is required by the parent TenantAuditingEntity class
   * for identity management and auditing purposes.
   *
   * @return the node's primary key identifier
   */
  @Override
  public Long identity() {
    return this.id;
  }

  /**
   * Compares this node with another object for equality.
   * <p>
   * Two nodes are considered equal if all their persistent fields have the same values.
   * This implementation excludes transient fields like roles from the comparison.
   *
   * @param o the object to compare with this node
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node node = (Node) o;
    return Objects.equals(id, node.id) &&
        Objects.equals(name, node.name) &&
        Objects.equals(ip, node.ip) &&
        Objects.equals(publicIp, node.publicIp) &&
        Objects.equals(regionId, node.regionId) &&
        Objects.equals(domain, node.domain) &&
        Objects.equals(username, node.username) &&
        Objects.equals(password, node.password) &&
        Objects.equals(sshPort, node.sshPort) &&
        Objects.equals(spec, node.spec) &&
        source == node.source &&
        Objects.equals(free, node.free) &&
        Objects.equals(enabled, node.enabled) &&
        Objects.equals(instanceId, node.instanceId) &&
        Objects.equals(instanceName, node.instanceName) &&
        Objects.equals(instanceExpiredDate, node.instanceExpiredDate) &&
        Objects.equals(deleted, node.deleted) &&
        Objects.equals(expired, node.expired) &&
        Objects.equals(orderId, node.orderId) &&
        chargeType == node.chargeType &&
        Objects.equals(installAgent, node.installAgent) &&
        Objects.equals(sync, node.sync);
  }

  /**
   * Generates a hash code for this node based on its persistent fields.
   * <p>
   * The hash code is computed using all persistent fields to ensure consistency
   * with the equals method implementation.
   *
   * @return hash code value for this node
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name, ip, publicIp, regionId, domain, username, password, sshPort, spec,
        source, free, enabled, instanceId, instanceName, instanceExpiredDate, deleted,
        expired, orderId, chargeType, installAgent, sync);
  }
}
