package cloud.xcan.angus.core.tester.interfaces.test.facade.internal;

import static cloud.xcan.angus.core.tester.interfaces.test.facade.internal.assembler.FuncCaseFollowAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.tester.application.cmd.test.FuncCaseFollowCmd;
import cloud.xcan.angus.core.tester.application.query.test.FuncCaseFollowQuery;
import cloud.xcan.angus.core.tester.domain.test.follow.FuncCaseFollowP;
import cloud.xcan.angus.core.tester.interfaces.test.facade.FuncCaseFollowFacade;
import cloud.xcan.angus.core.tester.interfaces.test.facade.dto.follow.FuncCaseFollowFindDto;
import cloud.xcan.angus.core.tester.interfaces.test.facade.internal.assembler.FuncCaseFollowAssembler;
import cloud.xcan.angus.core.tester.interfaces.test.facade.vo.follow.FuncCaseFollowDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class FuncCaseFollowFacadeImpl implements FuncCaseFollowFacade {

  @Resource
  private FuncCaseFollowCmd funcCaseFollowCmd;

  @Resource
  private FuncCaseFollowQuery funcCaseFollowQuery;

  @Override
  public IdKey<Long, Object> add(Long apiId) {
    return funcCaseFollowCmd.add(addDtoToDomain(apiId));
  }

  @Override
  public void cancel(Long caseId) {
    funcCaseFollowCmd.cancel(caseId);
  }

  @Override
  public void cancelAll(Long projectId) {
    funcCaseFollowCmd.cancelAll(projectId);
  }

  @Override
  public PageResult<FuncCaseFollowDetailVo> list(FuncCaseFollowFindDto dto) {
    Page<FuncCaseFollowP> page = funcCaseFollowQuery
        .list(dto.getProjectId(), dto.getCaseName(), dto.tranPage());
    return buildVoPageResult(page, FuncCaseFollowAssembler::toDetailVo);
  }

  @Override
  public Long count(Long projectId) {
    return funcCaseFollowQuery.count(projectId);
  }

}




