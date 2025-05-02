package cloud.xcan.angus.core.tester.infra.persistence.mysql.master.apis;

import cloud.xcan.angus.core.tester.domain.apis.follow.ApisFollowP;
import cloud.xcan.angus.core.tester.domain.apis.follow.ApisFollowRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("apisFollowRepo")
public interface ApisFollowRepoMysql extends ApisFollowRepo {

  @Override
  @Query(value =
      "select af.id id, a.id apisId,a.summary apisName,a.method,a.endpoint apisUri FROM apis_follow af, apis a WHERE a.id = af.apis_id "
          + "AND af.project_id = ?1 AND af.created_by = ?2 AND MATCH(a.summary,a.uri,a.operation_id,a.ext_search_merge) AGAINST (?3 IN BOOLEAN MODE) "
          + "AND a.service_deleted = 0 AND a.deleted = 0",
      countQuery = "select count(af.id) FROM apis_follow af, apis a WHERE a.id = af.apis_id "
          + "AND af.project_id = ?1 AND af.created_by = ?2 AND MATCH(a.summary,a.uri,a.operation_id,a.ext_search_merge) AGAINST (?3 IN BOOLEAN MODE) "
          + "AND a.service_deleted = 0 AND a.deleted = 0",
      nativeQuery = true)
  Page<ApisFollowP> searchByMatch(Long projectId, Long userId, String apisName, Pageable pageable);

}
