package cloud.xcan.sdf.core.angustester.domain.apis.follow;


import cloud.xcan.sdf.core.jpa.multitenancy.TenantEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "apis_follow")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ApisFollow extends TenantEntity<ApisFollow, Long> {

  @Id
  private Long id;

  @Column(name = "project_id")
  private Long projectId;

  @Column(name = "apis_id")
  private Long apisId;

  @Column(name = "created_by")
  @CreatedBy
  private Long createdBy;

  @Column(name = "created_date")
  @CreatedDate
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
