package cloud.xcan.sdf.core.angustester.application.query.task;

import cloud.xcan.sdf.core.angustester.domain.task.favorite.TaskFavouriteP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TaskFavouriteQuery {

  Page<TaskFavouriteP> search(Long projectId, String name, PageRequest pageable);

  Long count(Long projectId);

}




