package cloud.xcan.angus.core.tester.domain.apis.share;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ApisShareSearchRepo extends CustomBaseRepository<ApisShare> {

}
