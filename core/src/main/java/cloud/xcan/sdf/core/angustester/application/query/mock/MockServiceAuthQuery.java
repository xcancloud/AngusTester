package cloud.xcan.sdf.core.angustester.application.query.mock;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.core.angustester.domain.mock.service.auth.MockServiceAuth;
import cloud.xcan.sdf.core.angustester.domain.mock.service.auth.MockServicePermission;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MockServiceAuthQuery {

  Boolean status(Long serviceId);

  List<MockServicePermission> userAuth(Long serviceId, Long userId, Boolean adminFlag);

  void check(Long serviceId, MockServicePermission authPermission, Long authObjectId);

  Page<MockServiceAuth> find(Long serviceId, GenericSpecification<MockServiceAuth> spec,
      Pageable pageable);

  MockServiceAuth checkAndFind(Long id);

  void checkAddAuth(Long userId, Long serviceId);

  void checkViewAuth(Long userId, Long serviceId);

  void checkModifyAuth(Long userId, Long serviceId);

  void checkDeleteAuth(Long userId, Long serviceId);

  void checkGrantAuth(Long userId, Long serviceId);

  void checkAuth(Long userId, Long apisId, MockServicePermission permission);

  void checkAuth(Long userId, Long datasourceId, MockServicePermission permission,
      boolean ignoreAdmin, boolean ignorePublic);

  void batchCheckPermission(Collection<Long> serviceIds, MockServicePermission permission);

  void checkRepeatAuth(Long serviceId, Long authObjectId, AuthObjectType authObjectType);

  List<Long> findByAuthObjectIdsAndPermission(Long userId, MockServicePermission permission);

  List<MockServiceAuth> findAuth(Long userId, Long serviceId);

  List<MockServiceAuth> findAuth(Long userId, Collection<Long> serviceIds);

}




