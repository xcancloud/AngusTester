package cloud.xcan.sdf.core.angustester.infra.persistence.postgres.task;

import cloud.xcan.sdf.core.angustester.domain.task.follow.TaskFollowP;
import cloud.xcan.sdf.core.angustester.domain.task.follow.TaskFollowRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskFollowRepoPostgres extends TaskFollowRepo {

  @Override
  @Query(value =
      "select sf.id id, s.id taskId, s.name taskName, s.project_id projectId, s.sprint_id sprintId, s.task_type taskType, s.code taskCode "
          + "FROM task_follow sf, task s WHERE s.id = sf.task_id "
          + "AND sf.project_id = ?1 AND sf.created_by = ?2 AND s.sprint_deleted_flag = 0 AND s.deleted_flag = 0 "
          + "AND to_tsvector(s.name, s.code) @@ plainto_tsquery(?3)",
      countQuery = "select count(sf.id) FROM task_follow sf, task s WHERE s.id = sf.task_id "
          + "AND sf.project_id = ?1 AND sf.created_by = ?2 AND s.sprint_deleted_flag = 0 AND s.deleted_flag = 0 "
          + "AND to_tsvector(s.name, s.code) @@ plainto_tsquery(?3)",
      nativeQuery = true)
  Page<TaskFollowP> searchByMatch(Long projectId, Long userId, String taskName, Pageable pageable);

}
