package cloud.xcan.angus.core.tester.domain.services;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ServicesSearchRepo extends CustomBaseRepository<Services> {

}
