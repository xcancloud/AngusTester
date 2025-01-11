package cloud.xcan.sdf.core.angustester.interfaces.services.facade;

import cloud.xcan.sdf.core.angustester.domain.services.comp.ServicesCompType;
import cloud.xcan.sdf.core.angustester.interfaces.services.facade.vo.comp.ServicesCompDetailVo;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;

public interface ServicesCompFacade {

  IdKey<Long, Object> replace(Long serviceId, ServicesCompType type, String key,
      String component);

  void deleteByType(Long serviceId, ServicesCompType type, Set<String> keys);

  void deleteByRef(Long serviceId, Set<String> refs);

  void deleteAll(Long serviceId);

  ServicesCompDetailVo detailByRef(Long serviceId, String ref);

  List<ServicesCompDetailVo> listByType(Long serviceId, Set<ServicesCompType> types,
      Set<String> keys, Boolean ignoreModel);

  List<ServicesCompDetailVo> listByRef(Long serviceId, Set<String> refs,
      Boolean ignoreModel);

  List<ServicesCompDetailVo> listAll(Long serviceId, Boolean ignoreModel);

}
