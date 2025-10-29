package cloud.xcan.angus.core.tester.domain.test.plan.auth;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface FuncPlanAuthRepo extends BaseRepository<FuncPlanAuth, Long> {

  List<FuncPlanAuth> findAllByAuthObjectIdIn(List<Long> orgIds);

  List<FuncPlanAuth> findAllByPlanIdInAndAuthObjectIdIn(Collection<Long> planIds, List<Long> orgIds);

  List<FuncPlanAuth> findAllByPlanIdAndAuthObjectIdIn(Long planId, List<Long> orgIds);

  List<FuncPlanAuth> findAllByPlanId(Long planId);

  long countByPlanIdAndAuthObjectIdAndAuthObjectType(Long planId, Long authObjectId,
      AuthObjectType authObjectType);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM func_plan_auth WHERE plan_id IN ?1", nativeQuery = true)
  void deleteByPlanIdIn(List<Long> planIds);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM func_plan_auth WHERE plan_id = ?1 AND auth_object_id IN ?2 AND creator = ?3", nativeQuery = true)
  void deleteByPlanIdAndAuthObjectIdInAndCreator(Long planId, Set<Long> creatorIds,
      boolean creator);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM func_plan_auth WHERE plan_id = ?1 AND auth_object_id IN ?2", nativeQuery = true)
  void deleteByPlanIdAndAuthObjectId(Long planId, Collection<Long> testerIds);
}
