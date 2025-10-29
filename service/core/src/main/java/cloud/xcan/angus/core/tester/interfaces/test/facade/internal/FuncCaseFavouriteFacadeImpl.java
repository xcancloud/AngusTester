package cloud.xcan.angus.core.tester.interfaces.test.facade.internal;

import static cloud.xcan.angus.core.tester.interfaces.test.facade.internal.assembler.FuncCaseFavouriteAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.tester.application.cmd.test.FuncCaseFavouriteCmd;
import cloud.xcan.angus.core.tester.application.query.test.FuncCaseFavouriteQuery;
import cloud.xcan.angus.core.tester.domain.test.favourite.FuncCaseFavouriteP;
import cloud.xcan.angus.core.tester.interfaces.test.facade.FuncCaseFavouriteFacade;
import cloud.xcan.angus.core.tester.interfaces.test.facade.dto.favourite.FuncCaseFavouriteFindDto;
import cloud.xcan.angus.core.tester.interfaces.test.facade.internal.assembler.FuncCaseFavouriteAssembler;
import cloud.xcan.angus.core.tester.interfaces.test.facade.vo.favourite.FuncCaseFavouriteDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class FuncCaseFavouriteFacadeImpl implements FuncCaseFavouriteFacade {

  @Resource
  private FuncCaseFavouriteCmd funcCaseFavouriteCmd;

  @Resource
  private FuncCaseFavouriteQuery funcCaseFavouriteQuery;

  @Override
  public IdKey<Long, Object> add(Long caseId) {
    return funcCaseFavouriteCmd.add(addDtoToDomain(caseId));
  }

  @Override
  public void cancel(Long caseId) {
    funcCaseFavouriteCmd.cancel(caseId);
  }

  @Override
  public void cancelAll(Long projectId) {
    funcCaseFavouriteCmd.cancelAll(projectId);
  }

  @Override
  public PageResult<FuncCaseFavouriteDetailVo> list(FuncCaseFavouriteFindDto dto) {
    Page<FuncCaseFavouriteP> page = funcCaseFavouriteQuery
        .list(dto.getProjectId(), dto.getCaseName(), dto.tranPage());
    return buildVoPageResult(page, FuncCaseFavouriteAssembler::toDetailVo);
  }

  @Override
  public Long count(Long projectId) {
    return funcCaseFavouriteQuery.count(projectId);
  }
}




