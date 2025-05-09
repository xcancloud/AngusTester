package cloud.xcan.angus.core.tester.application.cmd.mock;

import cloud.xcan.angus.agent.message.mockservice.StartVo;
import cloud.xcan.angus.agent.message.mockservice.StopVo;
import cloud.xcan.angus.api.commonlink.apis.StrategyWhenDuplicated;
import cloud.xcan.angus.core.tester.domain.apis.Apis;
import cloud.xcan.angus.core.tester.domain.mock.apis.MockApis;
import cloud.xcan.angus.core.tester.domain.mock.service.MockService;
import cloud.xcan.angus.core.tester.domain.services.schema.SchemaFormat;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public interface MockServiceCmd {

  IdKey<Long, Object> add(MockService services);

  void update(MockService service);

  void submitUpdate(MockService service, MockService serviceDb);

  IdKey<Long, Object> replace(MockService service);

  void submitReplace(MockService service, MockService serviceDb);

  void instanceSync(Long id);

  IdKey<Long, Object> fileImport(MockService service);

  IdKey<Long, Object> servicesAssoc(MockService toService);

  void servicesAssocUpdate(Long mockServiceId, Long serviceId);

  List<StartVo> start(HashSet<Long> ids);

  List<StopVo> stop(HashSet<Long> ids);

  void imports(Long id, StrategyWhenDuplicated strategyWhenDuplicated,
      Boolean deleteWhenNotExisted, String content, MultipartFile file);

  IdKey<Long, Object> importExample(Long projectId);

  void importApisExample(Long id);

  void associationDelete(Long id);

  void delete(HashSet<Long> ids, Boolean force);

  List<MockApis> addMockApisAndResponses(MockService service, List<Apis> apis);

  File export(Long mockServiceId, Set<Long> mockApiIds, SchemaFormat format);

}




