package cloud.xcan.angus.core.tester.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.tester.domain.issue.Task;
import cloud.xcan.angus.core.tester.domain.issue.TaskListRepo;
import cloud.xcan.angus.core.tester.domain.issue.TaskSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 * @author XiaoLong Liu
 */
@Repository
public class TaskSearchRepoMysql extends AbstractSearchRepository<Task> implements TaskSearchRepo {

  @Resource
  private TaskListRepo taskListRepo;

  /**
   * Non-main mainClass conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Task> mainClz,
      Object[] params, String... matches) {
    return taskListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria, "task", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return taskListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }

}
