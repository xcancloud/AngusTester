package cloud.xcan.sdf.core.angustester.domain.script.auth;


import cloud.xcan.sdf.api.commonlink.script.ScriptPermission;
import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.core.jpa.hibernate.type.json.JsonStringType;
import cloud.xcan.sdf.core.jpa.multitenancy.TenantEntity;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "script_auth")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ScriptAuth extends TenantEntity<ScriptAuth, Long> {

  @Id
  private Long id;

  @Column(name = "script_id")
  private Long scriptId;

  @Column(name = "auth_object_type")
  @Enumerated(EnumType.STRING)
  private AuthObjectType authObjectType;

  @Column(name = "auth_object_id")
  private Long authObjectId;

  @Column(name = "auths")
  @Type(type = "json")
  private List<ScriptPermission> auths;

  @Column(name = "creator_flag")
  private Boolean creatorFlag;

  @Column(name = "created_by")
  @CreatedBy
  private Long createdBy;

  @Column(name = "created_date")
  @CreatedDate
  private Date createdDate;

  public boolean isCreatorAuth() {
    return Objects.nonNull(creatorFlag) && creatorFlag;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
