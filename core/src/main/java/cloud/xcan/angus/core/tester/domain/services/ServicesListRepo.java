package cloud.xcan.angus.core.tester.domain.services;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author XiaoLong Liu
 */
@NoRepositoryBean
public interface ServicesListRepo extends CustomBaseRepository<Services> {

  StringBuilder getSqlTemplate0(SearchMode mode, Class<Services> mainClz,
      Set<SearchCriteria> criteria, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);

}
