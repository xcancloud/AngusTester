package cloud.xcan.angus.core.tester.application.query.func;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.angus.core.tester.domain.func.review.FuncReview;
import cloud.xcan.angus.core.tester.domain.func.review.cases.FuncReviewCase;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FuncReviewCaseQuery {

  FuncReviewCase detail(Long caseId);

  Page<FuncReviewCase> list(GenericSpecification<FuncReviewCase> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  void checkReviewCaseValid(FuncReview funcReviewDb, List<FuncCaseInfo> funcCasesDb);

  FuncReviewCase checkAndFind(Long id);

  List<FuncReviewCase> checkAndFind(Collection<Long> ids);

  void setCaseInfo(List<FuncReviewCase> reviewCases);

}
