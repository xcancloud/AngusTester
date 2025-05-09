package cloud.xcan.angus.core.tester.domain.task;

import cloud.xcan.angus.api.enums.EvalWorkloadMethod;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.model.script.TestType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TaskRepo extends BaseRepository<Task, Long> {

  List<Task> findAllBySprintIdAndTargetId(Long sprintId, Long targetId);

  int countByTargetIdAndTestType(Long targetId, TestType testType);

  @Query(value = "SELECT id FROM task WHERE target_id in ?1", nativeQuery = true)
  List<Long> findIdsByTargetIdIn(Collection<Long> targetIds);

  @Query(value = "SELECT * FROM task WHERE target_id in ?1", nativeQuery = true)
  List<Task> findByTargetIdIn(Collection<Long> targetIds);

  @Query(value = "SELECT id FROM task WHERE target_id in ?1 AND test_type in ?2", nativeQuery = true)
  List<Long> findIdsByTargetIdInAndTestTypeIn(Collection<Long> targetIds,
      Collection<String> testTypes);

  @Query(value = "SELECT id FROM task WHERE target_parent_id = ?1", nativeQuery = true)
  List<Long> findTaskIdByTargetParentId(Long targetParentId);

  @Query(value = "SELECT id FROM task WHERE target_parent_id = ?1 and test_type in ?2", nativeQuery = true)
  List<Long> findTaskIdByTargetParentIdAndTestTypeIn(Long targetParentId,
      Collection<String> testTypes);

  @Query(value = "SELECT DISTINCT id FROM task WHERE sprint_id IN ?1", nativeQuery = true)
  List<Long> findAll0IdBySprintIdIn(Collection<Long> ids);

  @Query(value = "SELECT DISTINCT id FROM task WHERE sprint_id = ?1 AND name IN ?2", nativeQuery = true)
  List<String> findNameBySprintIdAndNameIn(Long sprintId, Collection<String> taskNames);

  List<Task> findByProjectIdAndParentTaskIdIn(Long projectId, Collection<Long> taskIds);

  @Query(value = "SELECT * FROM task WHERE target_id = ?1 AND test_type = ?2", nativeQuery = true)
  List<Task> find0ByTargetIdAndTestType(Long targetId, String testType);

  long countBySprintId(Long springId);

  @Query(value = "SELECT count(id) FROM task WHERE sprint_id = ?1 AND status NOT IN ('COMPLETED','CANCELED')"
      + " AND deleted = 0 AND sprint_deleted =0", nativeQuery = true)
  long countNotCompletedBySprintId(Long id);

  @Modifying
  @Query(value = "UPDATE task SET sprint_auth = ?2 WHERE sprint_id = ?1", nativeQuery = true)
  void updateSprintAuthBySprintId(Long sprintId, Boolean enabled);

  @Modifying
  @Query("UPDATE Task s SET s.sprintDeleted=?2 WHERE s.sprintId IN ?1")
  void updateSprintDeleteStatusBySprint(Collection<Long> sprintIds, boolean deleted);

  @Modifying
  @Query("UPDATE Task s SET s.projectId=?2 WHERE s.sprintId = ?1")
  void updateProjectBySprintId(Long sprintId, Long projectId);

  @Modifying
  @Query(
      "UPDATE Task s SET s.totalNum=0, s.failNum=0, s.completedDate=null, s.canceledDate=null, s.processedDate=null, s.canceledDate=null, s.startDate=null, "
          + " s.status='PENDING', s.execResult=null, s.execFailureMessage = null, s.execTestNum = 0, s.execTestFailureNum = 0, s.execId=null, s.execName=null, s.execBy=null, s.execDate=null WHERE s.sprintId IN ?1")
  void updateResultToRestartBySprintIds(Collection<Long> sprintIds);

  @Modifying
  @Query(
      "UPDATE Task s SET s.completedDate=null, s.canceledDate=null, s.processedDate=null, s.canceledDate=null, s.startDate=null,"
          + " s.status='PENDING', s.execResult=null, s.execFailureMessage = null, s.execTestNum = 0, s.execTestFailureNum = 0, s.execId=null, s.execName=null, s.execBy=null, s.execDate=null WHERE s.sprintId IN ?1")
  void updateResultToReopenBySprintIds(Collection<Long> sprintIds);

  @Modifying
  @Query(value = "UPDATE task a SET a.deleted=?2, a.deleted_by =?3, a.deleted_date = ?4 WHERE a.id in ?1", nativeQuery = true)
  void updateDeleteStatus(List<Long> taskIds, Boolean delete, Long userId, LocalDateTime now);

  @Modifying
  @Query("UPDATE Task s SET s.deleted = false, s.deletedBy = -1, s.deletedDate = null WHERE s.id in ?1")
  void updateToUndeletedStatusByIdIn(Collection<Long> ids);

  @Modifying
  @Query("UPDATE Task s SET s.evalWorkloadMethod=?2 WHERE s.sprintId = ?1")
  void updateEvalWorkloadMethodBySprintId(Long id, EvalWorkloadMethod evalWorkloadMethod);

  @Modifying
  @Query("UPDATE Task s SET s.moduleId=-1 WHERE s.moduleId IN ?1")
  void updateModuleNull(Collection<Long> ids);

  @Modifying
  @Query(value = "UPDATE task SET parent_task_id = ?1 WHERE id IN ?2", nativeQuery = true)
  void updateTaskParent(Long id, Collection<Long> subTaskIds);

  @Modifying
  @Query(value = "UPDATE task SET software_version = ?3 WHERE project_id = ?1 AND software_version = ?2", nativeQuery = true)
  void updateVersionByProjectIdAndSoftwareVersion(Long projectId, String fromVersion, String toVersion);

  @Modifying
  @Query(value = "UPDATE task SET parent_task_id = null WHERE parent_task_id = ?1 AND id IN ?2", nativeQuery = true)
  void cancelTaskParent(Long id, Collection<Long> subTaskIds);

  @Modifying
  @Query(value = "DELETE FROM task WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

  @Modifying
  @Query(value = "DELETE FROM task WHERE target_id IN ?1", nativeQuery = true)
  void deleteByTargetIdIn(Collection<Long> targetIds);

  @Modifying
  @Query(value = "DELETE FROM task WHERE sprint_id = ?1 AND name IN ?2", nativeQuery = true)
  void deleteBySprintIdAndNameIn(Long sprintId, Collection<String> taskNames);

}
