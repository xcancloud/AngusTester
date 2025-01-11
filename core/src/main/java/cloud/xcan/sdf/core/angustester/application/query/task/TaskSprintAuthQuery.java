package cloud.xcan.sdf.core.angustester.application.query.task;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.core.angustester.domain.task.sprint.TaskSprintPermission;
import cloud.xcan.sdf.core.angustester.domain.task.sprint.auth.TaskSprintAuth;
import cloud.xcan.sdf.core.angustester.domain.task.sprint.auth.TaskSprintAuthCurrent;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskSprintAuthQuery {

  Boolean status(Long sprintId);

  List<TaskSprintPermission> userAuth(Long sprintId, Long userId, Boolean adminFlag);

  TaskSprintAuthCurrent currentUserAuth(Long sprintId, Boolean adminFlag);

  void check(Long sprintId, TaskSprintPermission authPermission, Long userId);

  Page<TaskSprintAuth> find(GenericSpecification<TaskSprintAuth> spec, List<String> sprintIds,
      Pageable pageable);

  TaskSprintAuth checkAndFind(Long id);

  void checkModifySprintAuth(Long userId, Long sprintId);

  void checkDeleteSprintAuth(Long userId, Long sprintId);

  void checkAddTaskAuth(Long userId, Long sprintId);

  void checkModifyTaskAuth(Long userId, Long sprintId);

  void checkGrantAuth(Long userId, Long sprintId);

  void checkAuth(Long userId, Long sprintId, TaskSprintPermission permission);

  void checkAuth(Long userId, Long sprintId, TaskSprintPermission permission,
      boolean ignoreAdmin, boolean ignorePublic);

  void batchCheckPermission(Collection<Long> sprintIds, TaskSprintPermission permission);

  void checkRepeatAuth(Long sprintId, Long authObjectId, AuthObjectType authObjectType);

  List<Long> findByAuthObjectIdsAndPermission(Long userId, TaskSprintPermission permission);

  List<TaskSprintAuth> findAuth(Long userId, Long sprintId);

  List<TaskSprintAuth> findAuth(Long userId, Collection<Long> sprintIds);

  List<TaskSprintPermission> getUserAuth(Long sprintId, Long userId);

  boolean isCreator(Long userId, Long sprintId);

  boolean isAdminUser();

}
