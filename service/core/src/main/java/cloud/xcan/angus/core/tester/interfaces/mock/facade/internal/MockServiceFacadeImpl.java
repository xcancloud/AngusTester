package cloud.xcan.angus.core.tester.interfaces.mock.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.fileImportDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.getSpecification;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.servicesAssocDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.toDetailVo;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.ServletUtils.buildDownloadResourceResponseEntity;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import cloud.xcan.angus.agent.message.mockservice.StartVo;
import cloud.xcan.angus.agent.message.mockservice.StopVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.tester.application.cmd.mock.MockServiceCmd;
import cloud.xcan.angus.core.tester.application.query.mock.MockServiceQuery;
import cloud.xcan.angus.core.tester.domain.mock.service.MockService;
import cloud.xcan.angus.core.tester.domain.mock.service.MockServiceInfo;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.MockServiceFacade;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceAddDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceExportDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceFileImportDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceFindDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceImportDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceServicesAssocDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.service.MockServiceUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockServiceAssembler;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.service.MockServiceDetailVo;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.service.MockServiceListVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MockServiceFacadeImpl implements MockServiceFacade {

  @Resource
  private MockServiceCmd mockServiceCmd;

  @Resource
  private MockServiceQuery mockServiceQuery;

  @Override
  public IdKey<Long, Object> add(MockServiceAddDto dto) {
    return mockServiceCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(MockServiceUpdateDto dto) {
    mockServiceCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(MockServiceReplaceDto dto) {
    return mockServiceCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void instanceSync(Long id) {
    mockServiceCmd.instanceSync(id);
  }

  @Override
  public IdKey<Long, Object> fileImport(MockServiceFileImportDto dto) {
    return mockServiceCmd.fileImport(fileImportDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> servicesAssoc(MockServiceServicesAssocDto dto) {
    return mockServiceCmd.servicesAssoc(servicesAssocDtoToDomain(dto));
  }

  @Override
  public void servicesAssocUpdate(Long mockServiceId, Long serviceId) {
    mockServiceCmd.servicesAssocUpdate(mockServiceId, serviceId);
  }

  @Override
  public void imports(MockServiceImportDto dto) {
    mockServiceCmd.imports(dto.getMockServiceId(), dto.getStrategyWhenDuplicated(),
        dto.getDeleteWhenNotExisted(), dto.getContent(), dto.getFile());
  }

  @Override
  public IdKey<Long, Object> importExample(Long projectId) {
    return mockServiceCmd.importExample(projectId);
  }

  @Override
  public void importApisExample(Long id) {
    mockServiceCmd.importApisExample(id);
  }

  @Override
  public List<StartVo> start(HashSet<Long> ids) {
    return mockServiceCmd.start(ids);
  }

  @Override
  public List<StopVo> stop(HashSet<Long> ids) {
    return mockServiceCmd.stop(ids);
  }

  @Override
  public void assocDelete(Long id) {
    mockServiceCmd.associationDelete(id);
  }

  @Override
  public void delete(HashSet<Long> ids, Boolean force) {
    mockServiceCmd.delete(ids, force);
  }

  @Override
  public Set<Long> assocApisIdsList(Long mockServiceId) {
    return mockServiceQuery.assocApisIdsList(mockServiceId);
  }

  @NameJoin
  @Override
  public MockServiceDetailVo detail(Long id) {
    MockService service = mockServiceQuery.detail(id);
    return toDetailVo(service);
  }

  @NameJoin
  @Override
  public PageResult<MockServiceListVo> list(MockServiceFindDto dto) {
    Page<MockServiceInfo> page = mockServiceQuery.find(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, MockServiceAssembler::toServiceListVo);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<org.springframework.core.io.Resource> export(
      MockServiceExportDto dto, HttpServletResponse response) {
    File file = mockServiceCmd.export(dto.getMockServiceId(), dto.getMockApiIds(), dto.getFormat());
    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    return buildDownloadResourceResponseEntity(-1,
        APPLICATION_OCTET_STREAM, file.getName(), file.length(), resource);
  }

}
