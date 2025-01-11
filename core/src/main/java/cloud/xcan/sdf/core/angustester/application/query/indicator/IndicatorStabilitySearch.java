package cloud.xcan.sdf.core.angustester.application.query.indicator;

import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.domain.indicator.IndicatorStability;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndicatorStabilitySearch {

  Page<IndicatorStability> search(Set<SearchCriteria> criterias, Pageable pageable,
      Class<IndicatorStability> clz);

}




