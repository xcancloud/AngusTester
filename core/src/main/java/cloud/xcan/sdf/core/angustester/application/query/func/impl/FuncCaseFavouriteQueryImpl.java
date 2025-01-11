package cloud.xcan.sdf.core.angustester.application.query.func.impl;

import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import cloud.xcan.sdf.core.angustester.application.query.func.FuncCaseFavouriteQuery;
import cloud.xcan.sdf.core.angustester.domain.func.favourite.FuncCaseFavouriteP;
import cloud.xcan.sdf.core.angustester.domain.func.favourite.FuncCaseFavouriteRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class FuncCaseFavouriteQueryImpl implements FuncCaseFavouriteQuery {

  @Resource
  private FuncCaseFavouriteRepo funcCaseFavouriteRepo;

  @Override
  public Page<FuncCaseFavouriteP> search(Long projectId, String name, PageRequest pageable) {
    return new BizTemplate<Page<FuncCaseFavouriteP>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<FuncCaseFavouriteP> process() {
        return isEmpty(name) ? funcCaseFavouriteRepo.search(projectId, getUserId(), pageable)
            : funcCaseFavouriteRepo.searchByMatch(projectId, getUserId(), name, pageable);
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
        return isNull(projectId) ? funcCaseFavouriteRepo.countByCreatedBy(getUserId())
            : funcCaseFavouriteRepo.countByProjectIdAndCreatedBy(projectId, getUserId());
      }
    }.execute();
  }

}

