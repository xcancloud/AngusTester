package cloud.xcan.sdf.core.angustester.application.query.apis.impl;

import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;

import cloud.xcan.sdf.core.angustester.application.query.apis.ApisFollowQuery;
import cloud.xcan.sdf.core.angustester.domain.apis.follow.ApisFollowP;
import cloud.xcan.sdf.core.angustester.domain.apis.follow.ApisFollowRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class ApisFollowQueryImpl implements ApisFollowQuery {

  @Resource
  private ApisFollowRepo apisFollowRepo;

  @Override
  public Page<ApisFollowP> search(Long projectId, String name, PageRequest pageable) {
    return new BizTemplate<Page<ApisFollowP>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<ApisFollowP> process() {
        return isEmpty(name) ? apisFollowRepo.search(projectId, getUserId(), pageable)
            : apisFollowRepo.searchByMatch(projectId, getUserId(), name, pageable);
      }
    }.execute();
  }

  @Override
  public Long count(Long projectId) {
    return new BizTemplate<Long>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Long process() {
        return isNull(projectId) ? apisFollowRepo.countByCreatedBy(getUserId())
            : apisFollowRepo.countByProjectIdAndCreatedBy(projectId, getUserId());
      }
    }.execute();
  }
}




