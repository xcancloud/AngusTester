package cloud.xcan.sdf.core.angustester.infra.persistence.postgres.mock;

import cloud.xcan.sdf.core.angustester.domain.mock.apis.MockApis;
import cloud.xcan.sdf.core.angustester.domain.mock.apis.MockApisRepo;
import cloud.xcan.sdf.core.jpa.repository.NameJoinRepository;
import org.springframework.stereotype.Repository;

@Repository("mockApisRepo")
public interface MockApisRepoPostgres extends MockApisRepo,
    NameJoinRepository<MockApis, Long> {

}
