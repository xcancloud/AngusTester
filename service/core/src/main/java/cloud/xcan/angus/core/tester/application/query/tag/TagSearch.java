package cloud.xcan.angus.core.tester.application.query.tag;

import cloud.xcan.angus.core.tester.domain.tag.Tag;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagSearch {

  Page<Tag> search(Set<SearchCriteria> criteria, Pageable pageable, Class<Tag> cls,
      String... matches);
}
