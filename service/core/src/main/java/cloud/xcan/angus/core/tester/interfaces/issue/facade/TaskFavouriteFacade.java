package cloud.xcan.angus.core.tester.interfaces.issue.facade;

import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.favorite.TaskFavouriteFindDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.vo.favorite.TaskFavouriteDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;

public interface TaskFavouriteFacade {

  IdKey<Long, Object> add(Long taskId);

  void cancel(Long taskId);

  void cancelAll(Long projectId);

  PageResult<TaskFavouriteDetailVo> list(TaskFavouriteFindDto dto);

  Long count(Long projectId);

}
