package cloud.xcan.angus.core.tester.infra.persistence.mysql.metrics;

import cloud.xcan.angus.core.tester.infra.metricsds.domain.usage.network.NetUsageRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface NetUsageRepoMysql extends NetUsageRepo {

}
