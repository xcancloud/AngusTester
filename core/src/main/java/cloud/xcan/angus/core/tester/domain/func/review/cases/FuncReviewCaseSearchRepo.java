package cloud.xcan.angus.core.tester.domain.func.review.cases;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface FuncReviewCaseSearchRepo extends CustomBaseRepository<FuncReviewCase> {

}
