package cloud.xcan.angus.core.tester.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.tester.domain.activity.Activity;
import cloud.xcan.angus.core.tester.domain.activity.ActivityListRepo;
import cloud.xcan.angus.core.tester.domain.activity.ActivitySearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.stereotype.Repository;

@Repository
public class ActivitySearchRepoMysql extends AbstractSearchRepository<Activity>
    implements ActivitySearchRepo {

  @Resource
  private ActivityListRepo activityListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Activity> mainClz,
      Object[] objects, String... matches) {
    return activityListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "activity", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return activityListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
