package cloud.xcan.angus.core.tester.interfaces.apis.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.tester.interfaces.apis.facade.internal.assembler.ApisCaseAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.tester.interfaces.apis.facade.internal.assembler.ApisCaseAssembler.getSpecification;
import static cloud.xcan.angus.core.tester.interfaces.apis.facade.internal.assembler.ApisCaseAssembler.toDetailVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.tester.application.cmd.apis.ApisCaseCmd;
import cloud.xcan.angus.core.tester.application.query.apis.ApisCaseQuery;
import cloud.xcan.angus.core.tester.application.query.apis.ApisCaseSearch;
import cloud.xcan.angus.core.tester.domain.apis.cases.ApisCase;
import cloud.xcan.angus.core.tester.domain.apis.cases.ApisCaseInfo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.ApisCaseFacade;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.cases.ApisCaseAddDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.cases.ApisCaseFindDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.cases.ApisCaseReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.cases.ApisCaseSearchDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.dto.cases.ApisCaseUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.internal.assembler.ApisCaseAssembler;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.cases.ApisCaseDetailVo;
import cloud.xcan.angus.core.tester.interfaces.apis.facade.vo.cases.ApisCaseListVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApisCaseFacadeImpl implements ApisCaseFacade {

  @Resource
  private ApisCaseCmd apisCaseCmd;

  @Resource
  private ApisCaseQuery apisCaseQuery;

  @Resource
  private ApisCaseSearch apisCaseSearch;

  @Override
  public List<IdKey<Long, Object>> add(List<ApisCaseAddDto> dto) {
    List<ApisCase> cases = dto.stream().map(ApisCaseAssembler::addDtoToDomain)
        .collect(Collectors.toList());
    return apisCaseCmd.add(cases);
  }

  @Override
  public void update(List<ApisCaseUpdateDto> dto) {
    List<ApisCase> apisCases = dto.stream()
        .map(ApisCaseAssembler::updateDtoToDomain).collect(Collectors.toList());
    apisCaseCmd.update(apisCases);
  }

  @Override
  public void replace(List<ApisCaseReplaceDto> dto) {
    List<ApisCase> cases = dto.stream()
        .map(ApisCaseAssembler::replaceDtoToDomain).collect(Collectors.toList());
    apisCaseCmd.replace(cases);
  }

  @Override
  public void rename(Long id, String name) {
    apisCaseCmd.rename(id, name);
  }

  @Override
  public void enabled(Set<Long> ids, Boolean enabled) {
    apisCaseCmd.enabled(ids, enabled);
  }

  @Override
  public void syncToScript(Long apisId) {
    apisCaseCmd.syncToScript(apisId);
  }

  @Override
  public List<IdKey<Long, Object>> clone(Set<Long> ids) {
    return apisCaseCmd.clone(ids);
  }

  @Override
  public void delete(Collection<Long> ids) {
    apisCaseCmd.delete(ids);
  }

  @NameJoin
  @Override
  public ApisCaseDetailVo detail(Long id) {
    ApisCase detail = apisCaseQuery.detail(id);
    return toDetailVo(detail);
  }

  @NameJoin
  @Override
  public PageResult<ApisCaseListVo> list(ApisCaseFindDto dto) {
    Page<ApisCaseInfo> page = apisCaseQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, ApisCaseAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<ApisCaseListVo> search(ApisCaseSearchDto dto) {
    Page<ApisCaseInfo> page = apisCaseSearch.search(getSearchCriteria(dto),
        dto.tranPage(), ApisCaseInfo.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ApisCaseAssembler::toListVo);
  }

}
