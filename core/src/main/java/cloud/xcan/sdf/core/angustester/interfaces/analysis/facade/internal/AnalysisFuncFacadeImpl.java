package cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.internal;

import static cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.internal.assembler.AnalysisFuncAssembler.getSearchCriteria;
import static cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.internal.assembler.AnalysisFuncAssembler.toCaseStatisticsExportResource;
import static cloud.xcan.sdf.core.utils.ServletUtils.buildDownloadResourceResponseEntity;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.core.angustester.application.query.func.FuncCaseQuery;
import cloud.xcan.sdf.core.angustester.application.query.task.TaskQuery;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.BackloggedOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.BurnDownChartOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.CoreKpiOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.FuncCaseCount;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.FuncLastResourceCreationCount;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.FuncTesterCount;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.FuncTesterProgressCount;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.GrowthTrendOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.LeadTimeOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.OverdueAssessmentOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.ProgressOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.RecentDeliveryOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.ResourceCreationOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.ReviewEfficiencyOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.TestingEfficiencyOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.UnplannedWorkOverview;
import cloud.xcan.sdf.core.angustester.domain.func.cases.count.WorkloadOverview;
import cloud.xcan.sdf.core.angustester.domain.func.summary.FuncPlanWorkSummary;
import cloud.xcan.sdf.core.angustester.domain.func.summary.FuncProjectWorkSummary;
import cloud.xcan.sdf.core.angustester.domain.func.summary.FuncTesterWorkSummary;
import cloud.xcan.sdf.core.angustester.domain.kanban.BurnDownResourceType;
import cloud.xcan.sdf.core.angustester.domain.task.count.BurnDownChartCount;
import cloud.xcan.sdf.core.angustester.domain.task.count.TesterSubmittedBugOverview;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.AnalysisFuncFacade;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.CaseTesterWorkStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncAnalysisDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncCaseSummaryStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncCreatorStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncTesterSummaryStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncTesterWorkStatisticsDto;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AnalysisFuncFacadeImpl implements AnalysisFuncFacade {

  @Resource
  private FuncCaseQuery funcCaseQuery;

  @Resource
  private TaskQuery taskQuery;

  @Override
  public FuncLastResourceCreationCount creationResourcesStatistics(FuncCreatorStatisticsDto dto) {
    return funcCaseQuery.creationResourcesStatistics(dto.getProjectId(), dto.getPlanId(),
        dto.getCreatorObjectType(), dto.getCreatorObjectId(), dto.getCreatedDateStart(),
        dto.getCreatedDateEnd(), true, true, true);
  }

  @Override
  public FuncCaseCount countStatistics(FuncCaseSummaryStatisticsDto dto) {
    return funcCaseQuery.countStatistics(getSearchCriteria(dto));
  }

  @Override
  public ResponseEntity<org.springframework.core.io.Resource> countStatisticsExport(
      FuncCaseSummaryStatisticsDto dto, HttpServletResponse response) {
    FuncCaseCount count = funcCaseQuery.countStatistics(getSearchCriteria(dto));
    String fileName = "CaseCountStatisticsExport-" + System.currentTimeMillis() + ".xlsx";
    return buildDownloadResourceResponseEntity(-1, APPLICATION_OCTET_STREAM,
        fileName, 0, toCaseStatisticsExportResource(count, fileName));
  }

  @Override
  public List<FuncTesterCount> testerSummaryStatistics(FuncTesterSummaryStatisticsDto dto) {
    return funcCaseQuery.testerSummaryStatistics(dto.getProjectId(), dto.getPlanId());
  }

  @Override
  public List<FuncTesterProgressCount> testerProgressStatistics(
      FuncTesterSummaryStatisticsDto dto) {
    return funcCaseQuery.testerProgressStatistics(dto.getProjectId(), dto.getPlanId());
  }

  @Override
  public FuncProjectWorkSummary projectWorkStatistics(Long projectId) {
    return funcCaseQuery.projectWorkStatistics(projectId);
  }

  @Override
  public FuncPlanWorkSummary planWorkStatistics(Long planId) {
    return funcCaseQuery.planWorkStatistics(planId);
  }

  @Override
  public FuncTesterWorkSummary testerWorkStatistics(FuncTesterWorkStatisticsDto dto) {
    return funcCaseQuery.testerWorkStatistics(dto.getProjectId(), dto.getPlanId(), dto.getUserId());
  }

  @Override
  public Map<BurnDownResourceType, BurnDownChartCount> projectBurndownChart(Long projectId) {
    return funcCaseQuery.burndownChart(projectId, null, null, null,
        null, null, false, false).getTotalBurnDownCharts();
  }

  @Override
  public Map<BurnDownResourceType, BurnDownChartCount> planBurndownChart(Long planId) {
    return funcCaseQuery.burndownChart(null, planId, null, null,
        null, null, false, false).getTotalBurnDownCharts();
  }

  @Override
  public Map<BurnDownResourceType, BurnDownChartCount> testerBurndownChart(
      CaseTesterWorkStatisticsDto dto) {
    return funcCaseQuery.burndownChart(dto.getProjectId(), dto.getPlanId(),
        AuthObjectType.USER, dto.getUserId(), null, null, false, false).getTotalBurnDownCharts();
  }

  @Override
  public ProgressOverview progress(FuncAnalysisDto dto) {
    return funcCaseQuery.progress(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public BurnDownChartOverview burndownChart(FuncAnalysisDto dto) {
    return funcCaseQuery.burndownChart(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public WorkloadOverview workload(FuncAnalysisDto dto) {
    return funcCaseQuery.workload(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public OverdueAssessmentOverview overdueAssessment(FuncAnalysisDto dto) {
    return funcCaseQuery.overdueAssessment(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public TesterSubmittedBugOverview submittedBug(FuncAnalysisDto dto) {
    return taskQuery.submittedBug(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public TestingEfficiencyOverview testingEfficiency(FuncAnalysisDto dto) {
    return funcCaseQuery.testingEfficiency(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public CoreKpiOverview coreKpi(FuncAnalysisDto dto) {
    return funcCaseQuery.coreKpi(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public ReviewEfficiencyOverview reviewEfficiency(FuncAnalysisDto dto) {
    return funcCaseQuery.reviewEfficiency(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public BackloggedOverview backloggedWork(FuncAnalysisDto dto) {
    return funcCaseQuery.backloggedWork(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public RecentDeliveryOverview recentDelivery(FuncAnalysisDto dto) {
    return funcCaseQuery.recentDelivery(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public LeadTimeOverview leadTime(FuncAnalysisDto dto) {
    return funcCaseQuery.leadTime(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public UnplannedWorkOverview unplannedWork(FuncAnalysisDto dto) {
    return funcCaseQuery.unplannedWork(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public GrowthTrendOverview growthTrend(FuncAnalysisDto dto) {
    return funcCaseQuery.growthTrend(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

  @Override
  public ResourceCreationOverview resourceCreation(FuncAnalysisDto dto) {
    return funcCaseQuery.resourceCreation(dto.getProjectId(), dto.getPlanId(), dto.getOrgType(),
        dto.getOrgId(), dto.getStartTime(), dto.getEndTime(), dto.isContainsUserAnalysis(),
        dto.isContainsDataDetail());
  }

}
