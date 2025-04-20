package cloud.xcan.angus.core.tester.interfaces.scenario.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.tester.interfaces.scenario.facade.internal.assembler.ScenarioAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.scenario.facade.internal.assembler.ScenarioAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.scenario.facade.internal.assembler.ScenarioAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.tester.scenario.vo.ScenarioDetailVo;
import cloud.xcan.angus.api.tester.scenario.vo.ScenarioListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.tester.application.cmd.scenario.ScenarioCmd;
import cloud.xcan.angus.core.tester.application.query.scenario.ScenarioQuery;
import cloud.xcan.angus.core.tester.application.query.scenario.ScenarioSearch;
import cloud.xcan.angus.core.tester.domain.scenario.Scenario;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.ScenarioFacade;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.ScenarioAddDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.ScenarioInfoFindDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.ScenarioInfoSearchDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.ScenarioReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.ScenarioUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.internal.assembler.ScenarioAssembler;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ScenarioFacadeImpl implements ScenarioFacade {

  @Resource
  private ScenarioCmd scenarioCmd;

  @Resource
  private ScenarioQuery scenarioQuery;

  @Resource
  private ScenarioSearch scenarioSearch;

  @Override
  public IdKey<Long, Object> add(ScenarioAddDto dto) {
    return scenarioCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(ScenarioUpdateDto dto) {
    scenarioCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(ScenarioReplaceDto dto) {
    return scenarioCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void move(Long scenarioId, Long targetProjectId) {
    scenarioCmd.move(scenarioId, targetProjectId);
  }

  @Override
  public IdKey<Long, Object> clone(Long id) {
    return scenarioCmd.clone(id);
  }

  @Override
  public List<IdKey<Long, Object>> importExample(Long projectId) {
    return scenarioCmd.importExample(projectId);
  }

  @Override
  public void delete(Long id) {
    scenarioCmd.delete(id);
  }

  @NameJoin
  @Override
  public ScenarioDetailVo detail(Long id) {
    Scenario scenario = scenarioQuery.detail(id);
    return ScenarioAssembler.toDetailVo(scenario);
  }

  @NameJoin
  @Override
  public List<ScenarioListVo> list(Set<Long> ids) {
    return scenarioQuery.list(ids).stream().map(ScenarioAssembler::toListVo)
        .collect(Collectors.toList());
  }

  @NameJoin
  @Override
  public PageResult<ScenarioListVo> list(ScenarioInfoFindDto dto) {
    Page<Scenario> page = scenarioQuery
        .find(ScenarioAssembler.getSpecification(dto), dto.tranPage(), Scenario.class);
    return buildVoPageResult(page, ScenarioAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<ScenarioListVo> search(ScenarioInfoSearchDto dto) {
    Page<Scenario> apisPage = scenarioSearch
        .search(ScenarioAssembler.getSearchCriteria(dto), dto.tranPage(), Scenario.class,
            getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(apisPage, ScenarioAssembler::toListVo);
  }

}
