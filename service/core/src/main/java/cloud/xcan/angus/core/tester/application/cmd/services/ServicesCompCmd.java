package cloud.xcan.angus.core.tester.application.cmd.services;

import cloud.xcan.angus.api.commonlink.apis.StrategyWhenDuplicated;
import cloud.xcan.angus.core.tester.domain.services.comp.ServicesComp;
import cloud.xcan.angus.core.tester.domain.services.comp.ServicesCompType;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.models.Components;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ServicesCompCmd {

  IdKey<Long, Object> replace(Long serviceId, ServicesCompType type, String key,
      String component);

  void deleteByType(Long serviceId, ServicesCompType type, Set<String> keys);

  void deleteByRef(Long serviceId, Set<String> refs);

  void deleteAll(Long serviceId);

  void replaceByOpenApi(Long serviceId, Components components,
      StrategyWhenDuplicated strategyWhenDuplicated, boolean deleteWhenNotExisted);

  void batchInsert0(Long serviceId, Collection<ServicesComp> newComps);

  void batchUpdate0(Long serviceId, Collection<ServicesComp> updatedComps);

  void deleteByServiceIdAndRefIn(Long serviceId, Collection<String> refs);

  void deleteByServiceIdIn(List<Long> allIds);

  void clone(Long clonedServiceId, Long serviceId);

}
