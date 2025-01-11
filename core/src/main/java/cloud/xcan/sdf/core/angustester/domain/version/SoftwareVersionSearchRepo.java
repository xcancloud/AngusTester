package cloud.xcan.sdf.core.angustester.domain.version;

import cloud.xcan.sdf.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftwareVersionSearchRepo extends CustomBaseRepository<SoftwareVersion> {

}
