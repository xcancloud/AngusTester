package cloud.xcan.sdf.core.angustester.application.query.project;

import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.domain.project.Project;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectSearch {

  Page<Project> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Project> clz,
      String... matches);

}




