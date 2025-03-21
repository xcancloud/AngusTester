package cloud.xcan.sdf.core.angustester.interfaces.apis.facade.internal;

import static cloud.xcan.sdf.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.sdf.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.sdf.api.PageResult;
import cloud.xcan.sdf.api.commonlink.ApisTargetType;
import cloud.xcan.sdf.core.angustester.application.cmd.apis.ApisTrashCmd;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisQuery;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisTrashQuery;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisTrashSearch;
import cloud.xcan.sdf.core.angustester.domain.apis.ApisBaseInfo;
import cloud.xcan.sdf.core.angustester.domain.apis.trash.ApisTrash;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.ApisTrashFacade;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.trash.ApisTrashSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.internal.assembler.ApisTrashAssembler;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.vo.trash.ApisTrashDetailVo;
import cloud.xcan.sdf.spec.utils.ObjectUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApisTrashFacadeImpl implements ApisTrashFacade {

  @Resource
  private ApisTrashCmd apisTrashCmd;

  @Resource
  private ApisTrashQuery apisTrashQuery;

  @Resource
  private ApisTrashSearch apisTrashSearch;

  @Resource
  private ApisQuery apisQuery;

  @Override
  public void clear(Long id) {
    apisTrashCmd.clear(id);
  }

  @Override
  public void clearAll(Long projectId) {
    apisTrashCmd.clearAll(projectId);
  }

  @Override
  public void back(Long id) {
    apisTrashCmd.back(id);
  }

  @Override
  public void backAll(Long projectId) {
    apisTrashCmd.backAll(projectId);
  }

  @Override
  public Long count(Long projectId) {
    return apisTrashQuery.count(projectId);
  }

  @Override
  public PageResult<ApisTrashDetailVo> search(ApisTrashSearchDto dto) {
    Page<ApisTrash> trashPage = apisTrashSearch
        .search(ApisTrashAssembler.getSearchCriteria(dto), dto.tranPage(), ApisTrash.class,
            getMatchSearchFields(dto.getClass()));
    if (trashPage.isEmpty()) {
      return PageResult.empty();
    }

    PageResult<ApisTrashDetailVo> detailVoPageResult = buildVoPageResult(trashPage,
        ApisTrashAssembler::toDetailVo);
    if (ApisTargetType.API.equals(dto.getTargetType())) {
      setApiMethodAndUri(trashPage, detailVoPageResult);
    }
    return detailVoPageResult;
  }

  private void setApiMethodAndUri(Page<ApisTrash> trashPage, PageResult<ApisTrashDetailVo> page) {
    List<ApisTrash> trashs = trashPage.getContent();
    List<ApisBaseInfo> apis = apisQuery.findBase0ByIdIn(
        trashs.stream().map(ApisTrash::getTargetId).collect(Collectors.toList()));
    Map<Long, ApisBaseInfo> apisMap = apis.stream()
        .collect(Collectors.toMap(ApisBaseInfo::getId, Function.identity()));
    for (ApisTrashDetailVo apisDetailVo : page.getList()) {
      ApisBaseInfo a = apisMap.get(apisDetailVo.getTargetId());
      if (ObjectUtils.isNotEmpty(a)) {
        apisDetailVo.setProtocol(a.getProtocol());
        apisDetailVo.setMethod(a.getMethod());
        apisDetailVo.setEndpoint(a.getEndpoint());
      }
    }
  }

}




