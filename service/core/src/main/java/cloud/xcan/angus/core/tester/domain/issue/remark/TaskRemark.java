package cloud.xcan.angus.core.tester.domain.issue.remark;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author XiaoLong Liu
 */
@Entity
@Table(name = "task_remark")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class TaskRemark extends EntitySupport<TaskRemark, Long> {

  @Id
  private Long id;

  @Column(name = "task_id")
  private Long taskId;

  private String content;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  private Long createdBy;

  @Override
  public Long identity() {
    return this.id;
  }
}
