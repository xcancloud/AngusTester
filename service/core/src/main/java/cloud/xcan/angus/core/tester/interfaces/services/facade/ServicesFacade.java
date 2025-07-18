package cloud.xcan.angus.core.tester.interfaces.services.facade;

import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.service.MockServiceDetailVo;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.ServicesAddDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.ServicesExportDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.ServicesFindDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.dto.ServicesImportDto;
import cloud.xcan.angus.core.tester.interfaces.services.facade.vo.ServiceVo;
import cloud.xcan.angus.core.tester.interfaces.services.facade.vo.ServicesDetailVo;
import cloud.xcan.angus.model.apis.ApiStatus;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ServicesFacade {

  IdKey<Long, Object> add(ServicesAddDto dto);

  void rename(Long id, String name);

  void statusUpdate(Long id, ApiStatus status);

  void clone(Long id);

  IdKey<Long, Object> imports(ServicesImportDto dto);

  List<IdKey<Long, Object>> importExample(Long projectId);

  void delete(Long id);

  ServicesDetailVo detail(Long id, Boolean joinSchema);

  MockServiceDetailVo associationMockService(Long id);

  PageResult<ServiceVo> list(ServicesFindDto dto);

  ResponseEntity<Resource> export(ServicesExportDto dto, HttpServletResponse response);

}
