package cloud.xcan.sdf.core.angustester.application.query.func;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlan;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FuncPlanQuery {

  FuncPlan detail(Long id);

  Page<FuncPlan> find(GenericSpecification<FuncPlan> spec, Pageable pageable);

  List<FuncCaseInfo> notReviewed(Long planId, Long moduleId, Long reviewId);

  List<FuncCaseInfo> notEstablishedBaseline(Long planId, Long moduleId, Long baselineId);

  boolean isAuthCtrl(Long planId);

  FuncPlan checkAndFind(Long id);

  List<FuncPlan> checkAndFind(Collection<Long> ids);

  void checkNameExists(Long projectId, String name);

  void checkHasStarted(FuncPlan planDb);

  void checkReviewEnabled(FuncPlan plan);

  void checkPlanCaseCompleted(Long id);

  void checkConsistent(Long planId, HashSet<Long> caseIds);

  void checkQuota();

  void setCaseNum(List<FuncPlan> plans, Set<Long> planIds);

  void setProgress(List<FuncPlan> plans, Set<Long> planIds);

  void setMembers(List<FuncPlan> plans, Set<Long> planIds);

  void setSafeCloneName(FuncPlan plan);

  boolean checkAndSetAuthObjectIdCriteria(Set<SearchCriteria> criterias);

  List<FuncPlan> getPlanCreatedSummaries(Long projectId, Long planId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd, AuthObjectType creatorOrgType,
      Long creatorOrgId);

}
