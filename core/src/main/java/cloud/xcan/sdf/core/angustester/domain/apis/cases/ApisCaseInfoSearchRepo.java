package cloud.xcan.sdf.core.angustester.domain.apis.cases;

import cloud.xcan.sdf.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ApisCaseInfoSearchRepo extends CustomBaseRepository<ApisCaseInfo> {


}
