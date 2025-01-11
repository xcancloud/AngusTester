package cloud.xcan.sdf.core.angustester.interfaces.mock.facade.vo.service;

import cloud.xcan.sdf.core.angustester.domain.mock.service.MockServiceSource;
import cloud.xcan.sdf.core.angustester.domain.mock.service.MockServiceStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xiaolong.liu
 */
@Getter
@Setter
@Accessors(chain = true)
public class MockServiceInfoVo {

  private Long id;

  private String name;

  private MockServiceSource source;

  private MockServiceStatus status;

  private Long nodeId;

  private String serviceDomainUrl;

  private String serviceHostUrl;

  private boolean authFlag;

  private Long tenantId;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  private LocalDateTime lastModifiedDate;

}
