package cloud.xcan.sdf.core.angustester.interfaces.apis.facade;

import cloud.xcan.sdf.api.PageResult;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.ApisUnarchivedAddDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.ApisUnarchivedFindDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.ApisUnarchivedSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.ApisUnarchivedUpdateDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.vo.ApisUnarchivedDetailVo;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.vo.ApisUnarchivedListVo;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.List;

public interface ApisUnarchivedFacade {

  List<IdKey<Long, Object>> add(List<ApisUnarchivedAddDto> dto);

  void update(List<ApisUnarchivedUpdateDto> dtos);

  void delete(Long id);

  void rename(Long id, String name);

  void deleteAll();

  ApisUnarchivedDetailVo detail(Long id);

  Long count(Long projectId);

  PageResult<ApisUnarchivedListVo> list(ApisUnarchivedFindDto dto);

  PageResult<ApisUnarchivedListVo> search(ApisUnarchivedSearchDto dto);

}
