package cloud.xcan.sdf.core.angustester.domain.report.record;

import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.core.jpa.repository.NameJoinRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReportRecordInfoRepo extends BaseRepository<ReportRecordInfo, Long>,
    NameJoinRepository<ReportRecordInfo, Long> {

}
