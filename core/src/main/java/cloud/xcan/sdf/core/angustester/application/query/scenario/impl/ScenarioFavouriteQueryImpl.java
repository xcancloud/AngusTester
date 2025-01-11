package cloud.xcan.sdf.core.angustester.application.query.scenario.impl;

import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNull;
import static org.apache.commons.lang.StringUtils.isEmpty;

import cloud.xcan.sdf.core.angustester.application.query.scenario.ScenarioFavouriteQuery;
import cloud.xcan.sdf.core.angustester.domain.scenario.Scenario;
import cloud.xcan.sdf.core.angustester.domain.scenario.ScenarioRepo;
import cloud.xcan.sdf.core.angustester.domain.scenario.favorite.ScenarioFavourite;
import cloud.xcan.sdf.core.angustester.domain.scenario.favorite.ScenarioFavouriteRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class ScenarioFavouriteQueryImpl implements ScenarioFavouriteQuery {

  @Resource
  private ScenarioFavouriteRepo scenarioFavouriteRepo;

  @Resource
  private ScenarioRepo scenarioRepo;

  @Override
  public Page<ScenarioFavourite> search(Long projectId, String name, PageRequest pageable) {
    return new BizTemplate<Page<ScenarioFavourite>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<ScenarioFavourite> process() {
        Page<ScenarioFavourite> page = isEmpty(name)
            ? scenarioFavouriteRepo.search(projectId, getUserId(), pageable)
            : scenarioFavouriteRepo.searchByMatch(projectId, getUserId(), name, pageable);
        setScenarioInfo(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public Long count(Long projectId) {
    return new BizTemplate<Long>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Long process() {
        return isNull(projectId) ? scenarioFavouriteRepo.countByCreatedBy(getUserId())
            : scenarioFavouriteRepo.countByProjectIdAndCreatedBy(projectId, getUserId());
      }
    }.execute();
  }

  public void setScenarioInfo(List<ScenarioFavourite> favourites) {
    if (isNotEmpty(favourites)) {
      Map<Long, Scenario> scenarioMap = scenarioRepo
          .findAll0ByIdIn(favourites.stream().map(ScenarioFavourite::getScenarioId).collect(
              Collectors.toSet())).stream().collect(Collectors.toMap(Scenario::getId, x -> x));
      for (ScenarioFavourite favourite : favourites) {
        if (scenarioMap.containsKey(favourite.getScenarioId())) {
          favourite.setScenarioName(scenarioMap.get(favourite.getScenarioId()).getName());
          favourite.setPlugin(scenarioMap.get(favourite.getScenarioId()).getPlugin());
        }
      }
    }
  }
}




