package cloud.xcan.sdf.core.angustester.application.query.func.impl;

import static cloud.xcan.sdf.core.angustester.application.converter.FuncCaseConverter.getCommonCreatorResourcesFilter;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.api.manager.UserManager;
import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.application.query.func.FuncBaselineQuery;
import cloud.xcan.sdf.core.angustester.application.query.project.ProjectMemberQuery;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaseline;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaselineInfo;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaselineInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaselineRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class FuncBaselineQueryImpl implements FuncBaselineQuery {

  @Resource
  private FuncBaselineRepo funcBaselineRepo;

  @Resource
  private FuncBaselineInfoRepo funcBaselineInfoRepo;

  @Resource
  private ProjectMemberQuery projectMemberQuery;

  @Resource
  private UserManager userManager;

  @Override
  public FuncBaseline detail(Long id) {
    return new BizTemplate<FuncBaseline>() {
      FuncBaseline baselineDb;

      @Override
      protected void checkParams() {
        baselineDb = checkAndFind(id);
      }

      @Override
      protected FuncBaseline process() {
        return baselineDb;
      }
    }.execute();
  }

  @Override
  public Page<FuncBaselineInfo> find(GenericSpecification<FuncBaselineInfo> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<FuncBaselineInfo>>() {
      @Override
      protected void checkParams() {
        // Check the project permission
        projectMemberQuery.checkMember(spec.getCriterias());
      }

      @Override
      protected Page<FuncBaselineInfo> process() {
        Page<FuncBaselineInfo> page = funcBaselineInfoRepo.findAll(spec, pageable);

        if (page.hasContent()) {
          // Set user name and avatar
          userManager.setUserNameAndAvatar(page.getContent(), "createdBy", "createdByName",
              "createdByAvatar");
        }
        return page;
      }
    }.execute();
  }

  @Override
  public FuncBaseline checkAndFind(Long id) {
    return funcBaselineRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "FuncBaseline"));
  }

  @Override
  public FuncBaselineInfo checkAndFindInfo(Long id) {
    return funcBaselineInfoRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "FuncBaseline"));
  }

  @Override
  public List<FuncBaseline> checkAndFind(Collection<Long> ids) {
    List<FuncBaseline> baselines = funcBaselineRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(baselines), ids.iterator().next(), "FuncBaseline");
    if (ids.size() != baselines.size()) {
      for (FuncBaseline baseline : baselines) {
        assertResourceNotFound(ids.contains(baseline.getId()), baseline.getId(), "FuncBaseline");
      }
    }
    return baselines;
  }

  @Override
  public List<FuncBaseline> getBaselineCreatedSummaries(Long projectId, Long planId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd, AuthObjectType creatorOrgType,
      Long creatorOrgId) {
    // Find all when condition is null, else find by condition
    Set<Long> creatorIds = Objects.isNull(creatorOrgType) ? null
        : userManager.getUserIdByOrgType0(creatorOrgType, creatorOrgId);
    Set<SearchCriteria> allFilters = getCommonCreatorResourcesFilter(projectId, planId,
        createdDateStart, createdDateEnd, creatorIds);
    return funcBaselineRepo.findAllByFilters(allFilters);
  }

}
