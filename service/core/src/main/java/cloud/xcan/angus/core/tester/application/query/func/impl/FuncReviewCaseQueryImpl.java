package cloud.xcan.angus.core.tester.application.query.func.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.tester.domain.TesterFuncPluginMessage.CASE_REVIEW_REPEATED_T;
import static cloud.xcan.angus.remote.search.SearchCriteria.equal;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.application.query.func.FuncCaseQuery;
import cloud.xcan.angus.core.tester.application.query.func.FuncReviewCaseQuery;
import cloud.xcan.angus.core.tester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.angus.core.tester.domain.func.review.FuncReview;
import cloud.xcan.angus.core.tester.domain.func.review.cases.FuncReviewCase;
import cloud.xcan.angus.core.tester.domain.func.review.cases.FuncReviewCaseRepo;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class FuncReviewCaseQueryImpl implements FuncReviewCaseQuery {

  @Resource
  private FuncReviewCaseRepo funcReviewCaseRepo;

  @Resource
  private FuncCaseQuery funcCaseQuery;

  @Override
  public FuncReviewCase detail(Long id) {
    return new BizTemplate<FuncReviewCase>() {
      FuncReviewCase reviewCaseDb;

      @Override
      protected void checkParams() {
        reviewCaseDb = checkAndFind(id);
      }

      @Override
      protected FuncReviewCase process() {
        return reviewCaseDb;
      }
    }.execute();
  }

  @Override
  public Page<FuncReviewCase> list(GenericSpecification<FuncReviewCase> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<FuncReviewCase>>() {

      @Override
      protected Page<FuncReviewCase> process() {
        Set<SearchCriteria> criteria = spec.getCriteria();
        criteria.add(equal("lastReview", true));

        Page<FuncReviewCase> page = funcReviewCaseRepo.findAll(spec, pageable);
        setCaseInfo(page.getContent());
        return page;
      }
    }.execute();
  }

  @Override
  public void checkReviewCaseValid(FuncReview reviewDb, List<FuncCaseInfo> funcCasesDb) {
    // Fix:: Check review status is valid
    // funcReviewQuery.checkHasStarted(reviewDb);
    // Check the cases status is valid
    funcCaseQuery.checkInfoCanReview(funcCasesDb);
    // Check the cases review is not repeated
    List<Long> caseIds = funcCasesDb.stream().map(FuncCaseInfo::getId).collect(Collectors.toList());
    Set<Long> pendingReviewCaseIds = funcReviewCaseRepo.findPendingCaseIdByCaseIdInAndReviewIdNot(
        caseIds, reviewDb.getId());
    if (!pendingReviewCaseIds.isEmpty()) {
      FuncCaseInfo repeatedReviewCase = funcCasesDb.stream()
          .filter(x -> pendingReviewCaseIds.contains(x.getId())).findFirst().get();
      throw ProtocolException.of(CASE_REVIEW_REPEATED_T,
          new Object[]{reviewDb.getName(), repeatedReviewCase.getName()});
    }
  }

  @Override
  public FuncReviewCase checkAndFind(Long id) {
    return funcReviewCaseRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "FuncReviewCase"));
  }

  @Override
  public List<FuncReviewCase> checkAndFind(Collection<Long> ids) {
    List<FuncReviewCase> reviewCases = funcReviewCaseRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(reviewCases), ids.iterator().next(), "FuncReviewCase");
    if (ids.size() != reviewCases.size()) {
      for (FuncReviewCase reviewCase : reviewCases) {
        assertResourceNotFound(ids.contains(reviewCase.getId()), reviewCase.getId(),
            "FuncReviewCase");
      }
    }
    return reviewCases;
  }

  @Override
  public void setCaseInfo(List<FuncReviewCase> reviewCases) {
    if (isNotEmpty(reviewCases)){
      Map<Long, FuncCaseInfo> caseInfoMap = funcCaseQuery.checkAndFindInfo(
              reviewCases.stream().map(FuncReviewCase::getCaseId).collect(
                  Collectors.toList())).stream()
          .collect(Collectors.toMap(FuncCaseInfo::getId, x -> x));
      for (FuncReviewCase reviewCase : reviewCases) {
        reviewCase.setCaseInfo(caseInfoMap.get(reviewCase.getCaseId()));
      }
    }
  }
}
