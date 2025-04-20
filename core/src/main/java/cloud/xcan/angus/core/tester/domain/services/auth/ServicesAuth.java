package cloud.xcan.angus.core.tester.domain.services.auth;


import cloud.xcan.angus.api.commonlink.services.ServicesPermission;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.biz.ResourceId;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "services_auth")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ServicesAuth extends TenantEntity<ServicesAuth, Long> {

  @Id
  private Long id;

  @Column(name = "service_id")
  private Long serviceId;

  @Column(name = "auth_object_type")
  @Enumerated(EnumType.STRING)
  private AuthObjectType authObjectType;

  @ResourceId
  @Column(name = "auth_object_id")
  private Long authObjectId;

  @Column(name = "auths", columnDefinition = "json")
  @Type(JsonType.class)
  private List<ServicesPermission> auths;

  @Column(name = "creator")
  private Boolean creator;

  @Column(name = "created_by")
  @CreatedBy
  private Long createdBy;

  @Column(name = "created_date")
  @CreatedDate
  private LocalDateTime createdDate;

  public boolean isCreatorAuth() {
    return Objects.nonNull(creator) && creator;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
