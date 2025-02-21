package cloud.xcan.sdf.core.angustester.application.query.report.impl;

import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.application.query.project.ProjectMemberQuery;
import cloud.xcan.sdf.core.angustester.application.query.report.ReportQuery;
import cloud.xcan.sdf.core.angustester.application.query.report.ReportSearch;
import cloud.xcan.sdf.core.angustester.domain.report.ReportInfo;
import cloud.xcan.sdf.core.angustester.domain.report.ReportSearchRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class ReportSearchImpl implements ReportSearch {

  @Resource
  private ReportSearchRepo reportSearchRepo;

  @Resource
  private ReportQuery reportQuery;

  @Resource
  private ProjectMemberQuery projectMemberQuery;

  @Override
  public Page<ReportInfo> search(Set<SearchCriteria> criteria, PageRequest pageable,
      Class<ReportInfo> clz, String... matches) {
    return new BizTemplate<Page<ReportInfo>>() {
      @Override
      protected void checkParams() {
        // Check the project member permission
        projectMemberQuery.checkMember(criteria);
      }

      @Override
      protected Page<ReportInfo> process() {
        Page<ReportInfo> page = reportSearchRepo.find(criteria, pageable, clz, matches);
        if (page.isEmpty()) {
          return page;
        }

        // Set the current user report permissions
        reportQuery.setReportInfoCurrentAuths(page.getContent());
        return page;
      }
    }.execute();
  }
}
