package cloud.xcan.sdf.core.angustester.infra.persistence.postgres.report;

import cloud.xcan.sdf.core.angustester.domain.report.ReportInfoRepo;
import org.springframework.stereotype.Repository;

@Repository("reportInfoRepo")
public interface ReportInfoRepoPostgres extends ReportInfoRepo {

}
