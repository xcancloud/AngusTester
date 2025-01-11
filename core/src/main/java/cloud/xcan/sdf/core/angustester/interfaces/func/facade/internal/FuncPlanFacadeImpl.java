package cloud.xcan.sdf.core.angustester.interfaces.func.facade.internal;

import static cloud.xcan.sdf.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.sdf.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.sdf.api.PageResult;
import cloud.xcan.sdf.core.angustester.application.cmd.func.FuncPlanCmd;
import cloud.xcan.sdf.core.angustester.application.query.func.FuncPlanQuery;
import cloud.xcan.sdf.core.angustester.application.query.func.FuncPlanSearch;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlan;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.FuncPlanFacade;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanAddDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanFindDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanReplaceDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanUpdateDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.internal.assembler.FuncCaseAssembler;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.internal.assembler.FuncPlanAssembler;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.vo.FuncCaseListVo;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.vo.plan.FuncPlanDetailVo;
import cloud.xcan.sdf.core.biz.NameJoin;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class FuncPlanFacadeImpl implements FuncPlanFacade {

  @Resource
  private FuncPlanCmd funcPlanCmd;

  @Resource
  private FuncPlanQuery funcPlanQuery;

  @Resource
  private FuncPlanSearch funcPlanSearch;

  @Override
  public IdKey<Long, Object> add(FuncPlanAddDto dto) {
    return funcPlanCmd.add(FuncPlanAssembler.addDtoToDomain(dto));
  }

  @Override
  public void update(FuncPlanUpdateDto dto) {
    funcPlanCmd.update(FuncPlanAssembler.updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(FuncPlanReplaceDto dto) {
    return funcPlanCmd.replace(FuncPlanAssembler.replaceDtoToDomain(dto));
  }

  @Override
  public void start(Long id) {
    funcPlanCmd.start(id);
  }

  @Override
  public void end(Long id) {
    funcPlanCmd.end(id);
  }

  @Override
  public void block(Long id) {
    funcPlanCmd.block(id);
  }

  @Override
  public IdKey<Long, Object> clone(Long id) {
    return funcPlanCmd.clone(id);
  }

  @Override
  public void resultReset(HashSet<Long> ids) {
    funcPlanCmd.resultReset(ids);
  }

  @Override
  public void reviewReset(HashSet<Long> ids) {
    funcPlanCmd.reviewReset(ids);
  }

  @Override
  public void delete(Long id) {
    funcPlanCmd.delete(id);
  }

  @NameJoin
  @Override
  public FuncPlanDetailVo detail(Long id) {
    FuncPlan funcPlan = funcPlanQuery.detail(id);
    return FuncPlanAssembler.toDetailVo(funcPlan);
  }

  @NameJoin
  @Override
  public PageResult<FuncPlanDetailVo> list(FuncPlanFindDto dto) {
    Page<FuncPlan> page = funcPlanQuery
        .find(FuncPlanAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, FuncPlanAssembler::toDetailVo);
  }

  @NameJoin
  @Override
  public PageResult<FuncPlanDetailVo> search(FuncPlanSearchDto dto) {
    Page<FuncPlan> page = funcPlanSearch.search(FuncPlanAssembler.getSearchCriteria(dto),
        dto.tranPage(), FuncPlan.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, FuncPlanAssembler::toDetailVo);
  }

  @NameJoin
  @Override
  public List<FuncCaseListVo> notReviewed(Long planId, Long moduleId, Long reviewId) {
    List<FuncCaseInfo> caseInfos = funcPlanQuery.notReviewed(planId, moduleId, reviewId);
    return isEmpty(caseInfos) ? null : caseInfos.stream().map(FuncCaseAssembler::toListVo)
        .collect(Collectors.toList());
  }

  @NameJoin
  @Override
  public List<FuncCaseListVo> notEstablishedBaseline(Long planId, Long moduleId, Long baselineId) {
    List<FuncCaseInfo> caseInfos = funcPlanQuery.notEstablishedBaseline(
        planId, moduleId, baselineId);
    return isEmpty(caseInfos) ? null : caseInfos.stream().map(FuncCaseAssembler::toListVo)
        .collect(Collectors.toList());
  }
}
