package cloud.xcan.angus.core.tester.interfaces.mock.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockApisLogAssembler.getSpecification;
import static cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockApisLogAssembler.toApiLogDetailVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.tester.application.query.mock.MockApisLogQuery;
import cloud.xcan.angus.core.tester.domain.mock.apis.log.MockApisLogInfo;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.MockApisLogFacade;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.dto.apis.log.MockApisLogFindDto;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.internal.assembler.MockApisLogAssembler;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.apis.log.MockApisLogDetailVo;
import cloud.xcan.angus.core.tester.interfaces.mock.facade.vo.apis.log.MockApisLogListVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MockApisLogFacadeImpl implements MockApisLogFacade {

  @Resource
  private MockApisLogQuery mockApisLogQuery;

  @NameJoin
  @Override
  public MockApisLogDetailVo detail(Long id) {
    return toApiLogDetailVo(mockApisLogQuery.detail(id));
  }

  @Override
  public PageResult<MockApisLogListVo> list(Long mockServiceId, MockApisLogFindDto dto) {
    Page<MockApisLogInfo> page = mockApisLogQuery.list(mockServiceId, getSpecification(dto),
        dto.tranPage(), dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, MockApisLogAssembler::toApisLogListVo);
  }

}
