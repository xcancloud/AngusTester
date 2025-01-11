package cloud.xcan.sdf.core.angustester.interfaces.analysis.facade;


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
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.CaseTesterWorkStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncAnalysisDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncCaseSummaryStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncCreatorStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncTesterSummaryStatisticsDto;
import cloud.xcan.sdf.core.angustester.interfaces.analysis.facade.dto.FuncTesterWorkStatisticsDto;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface AnalysisFuncFacade {

  FuncLastResourceCreationCount creationResourcesStatistics(FuncCreatorStatisticsDto dto);

  FuncCaseCount countStatistics(FuncCaseSummaryStatisticsDto dto);

  ResponseEntity<Resource> countStatisticsExport(FuncCaseSummaryStatisticsDto dto,
      HttpServletResponse response);

  List<FuncTesterCount> testerSummaryStatistics(FuncTesterSummaryStatisticsDto dto);

  List<FuncTesterProgressCount> testerProgressStatistics(FuncTesterSummaryStatisticsDto dto);

  FuncProjectWorkSummary projectWorkStatistics(Long projectId);

  FuncPlanWorkSummary planWorkStatistics(Long planId);

  FuncTesterWorkSummary testerWorkStatistics(FuncTesterWorkStatisticsDto dto);

  Map<BurnDownResourceType, BurnDownChartCount> projectBurndownChart(Long projectId);

  Map<BurnDownResourceType, BurnDownChartCount> planBurndownChart(Long planId);

  Map<BurnDownResourceType, BurnDownChartCount> testerBurndownChart(CaseTesterWorkStatisticsDto dto);

  ProgressOverview progress(FuncAnalysisDto dto);

  BurnDownChartOverview burndownChart(FuncAnalysisDto dto);

  WorkloadOverview workload(FuncAnalysisDto dto);

  OverdueAssessmentOverview overdueAssessment(FuncAnalysisDto dto);

  TesterSubmittedBugOverview submittedBug(FuncAnalysisDto dto);

  TestingEfficiencyOverview testingEfficiency(FuncAnalysisDto dto);

  CoreKpiOverview coreKpi(FuncAnalysisDto dto);

  ReviewEfficiencyOverview reviewEfficiency(FuncAnalysisDto dto);

  BackloggedOverview backloggedWork(FuncAnalysisDto dto);

  RecentDeliveryOverview recentDelivery(FuncAnalysisDto dto);

  LeadTimeOverview leadTime(FuncAnalysisDto dto);

  UnplannedWorkOverview unplannedWork(FuncAnalysisDto dto);

  GrowthTrendOverview growthTrend(FuncAnalysisDto dto);

  ResourceCreationOverview resourceCreation(FuncAnalysisDto dto);

}
