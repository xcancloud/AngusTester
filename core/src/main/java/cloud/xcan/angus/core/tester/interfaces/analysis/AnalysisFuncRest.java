package cloud.xcan.angus.core.tester.interfaces.analysis;


import cloud.xcan.angus.core.tester.domain.func.cases.count.BackloggedOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.BurnDownChartOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.CoreKpiOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.FuncCaseCount;
import cloud.xcan.angus.core.tester.domain.func.cases.count.FuncLastResourceCreationCount;
import cloud.xcan.angus.core.tester.domain.func.cases.count.FuncTesterCount;
import cloud.xcan.angus.core.tester.domain.func.cases.count.FuncTesterProgressCount;
import cloud.xcan.angus.core.tester.domain.func.cases.count.GrowthTrendOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.LeadTimeOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.OverdueAssessmentOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.ProgressOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.RecentDeliveryOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.ResourceCreationOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.ReviewEfficiencyOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.TestingEfficiencyOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.UnplannedWorkOverview;
import cloud.xcan.angus.core.tester.domain.func.cases.count.WorkloadOverview;
import cloud.xcan.angus.core.tester.domain.func.summary.FuncPlanWorkSummary;
import cloud.xcan.angus.core.tester.domain.func.summary.FuncProjectWorkSummary;
import cloud.xcan.angus.core.tester.domain.func.summary.FuncTesterWorkSummary;
import cloud.xcan.angus.core.tester.domain.kanban.BurnDownResourceType;
import cloud.xcan.angus.core.tester.domain.task.count.BurnDownChartCount;
import cloud.xcan.angus.core.tester.domain.task.count.TesterSubmittedBugOverview;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.AnalysisFuncFacade;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.CaseTesterWorkStatisticsDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.FuncAnalysisDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.FuncCaseSummaryStatisticsDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.FuncCreatorStatisticsDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.FuncTesterSummaryStatisticsDto;
import cloud.xcan.angus.core.tester.interfaces.analysis.facade.dto.FuncTesterWorkStatisticsDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "AnalysisFunc")
@Validated
@RestController
@RequestMapping("/api/v1/analysis/func")
public class AnalysisFuncRest {

  @Resource
  private AnalysisFuncFacade analysisAngusFuncFacade;

  @Operation(description = "The count of cases creation statistics. Cases and associated plan, tag, and module creation count statistics", operationId = "analysis:func:resources:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/resources/count")
  public ApiLocaleResult<FuncLastResourceCreationCount> funcResourcesStatistics(
      @Valid FuncCreatorStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.creationResourcesStatistics(dto));
  }

  @Operation(description = "The count of cases statistics", operationId = "analysis:func:case:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/case/count")
  public ApiLocaleResult<FuncCaseCount> countStatistics(@Valid FuncCaseSummaryStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.countStatistics(dto));
  }

  @Operation(description = "Export the count of cases statistics", operationId = "analysis:func:case:count:export")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Exported Successfully")})
  @GetMapping(value = "/case/count/export")
  public ResponseEntity<org.springframework.core.io.Resource> countStatisticsExport(
      @Valid FuncCaseSummaryStatisticsDto dto, HttpServletResponse response) {
    return analysisAngusFuncFacade.countStatisticsExport(dto, response);
  }

  @Operation(description = "The count of case statistics for tester", operationId = "analysis:func:tester:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/tester/count")
  public ApiLocaleResult<List<FuncTesterCount>> testerSummaryStatistics(
      @Valid FuncTesterSummaryStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.testerSummaryStatistics(dto));
  }

  @Operation(description = "The case progress analysis of testers", operationId = "analysis:func:tester:progress")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/tester/progress")
  public ApiLocaleResult<List<FuncTesterProgressCount>> testerProgressStatistics(
      @Valid FuncTesterSummaryStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.testerProgressStatistics(dto));
  }

  @Operation(description = "The work summary of cases for project", operationId = "analysis:func:project:work:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/project/{projectId}/work/summary")
  public ApiLocaleResult<FuncProjectWorkSummary> projectWorkStatistics(
      @Parameter(name = "projectId", description = "Project id", required = true) @PathVariable("projectId") Long projectId) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.projectWorkStatistics(projectId));
  }

  @Operation(description = "The work summary of cases for plan", operationId = "analysis:func:plan:work:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/plan/{planId}/work/summary")
  public ApiLocaleResult<FuncPlanWorkSummary> planWorkStatistics(
      @Parameter(name = "planId", description = "Plan id", required = true) @PathVariable("planId") Long planId) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.planWorkStatistics(planId));
  }

  @Operation(description = "The work summary of cases for tester", operationId = "analysis:func:tester:work:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/tester/work/summary")
  public ApiLocaleResult<FuncTesterWorkSummary> testerWorkStatistics(
      @Valid FuncTesterWorkStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.testerWorkStatistics(dto));
  }

  @Operation(description = "The summary of cases burndown for project", operationId = "analysis:func:project:burndown:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/project/{projectId}/burndown")
  public ApiLocaleResult<Map<BurnDownResourceType, BurnDownChartCount>> projectBurndownChart(
      @Parameter(name = "projectId", description = "Project id", required = true) @PathVariable("projectId") Long projectId) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.projectBurndownChart(projectId));
  }

  @Operation(description = "The summary of cases burndown for plan", operationId = "analysis:func:plan:burndown:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/plan/{planId}/burndown")
  public ApiLocaleResult<Map<BurnDownResourceType, BurnDownChartCount>> planBurndownChart(
      @Parameter(name = "planId", description = "Plan id", required = true) @PathVariable("planId") Long planId) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.planBurndownChart(planId));
  }

  @Operation(description = "The summary of cases burndown for tester", operationId = "analysis:func:tester:burndown:summary")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/tester/burndown")
  public ApiLocaleResult<Map<BurnDownResourceType, BurnDownChartCount>> testerBurndownChart(
      @Valid CaseTesterWorkStatisticsDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.testerBurndownChart(dto));
  }

  @Operation(description = "The overview of cases progress", operationId = "analysis:func:progress:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/progress/overview")
  public ApiLocaleResult<ProgressOverview> progress(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.progress(dto));
  }

  @Operation(description = "The overview of cases burndown", operationId = "analysis:func:burndown:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/burndown/overview")
  public ApiLocaleResult<BurnDownChartOverview> burndownChart(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.burndownChart(dto));
  }

  @Operation(description = "The overview of cases workload", operationId = "analysis:func:workload:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/workload/overview")
  public ApiLocaleResult<WorkloadOverview> workload(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.workload(dto));
  }

  @Operation(description = "The overview of overdue cases assessment", operationId = "analysis:func:overdue:assessment:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/overdue/assessment/overview")
  public ApiLocaleResult<OverdueAssessmentOverview> overdueAssessment(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.overdueAssessment(dto));
  }

  @Operation(description = "The overview of tester submitted bug", operationId = "analysis:func:submitted:bug:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/submitted/bug/overview")
  public ApiLocaleResult<TesterSubmittedBugOverview> submittedBug(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.submittedBug(dto));
  }

  @Operation(description = "The overview of cases testing efficiency", operationId = "analysis:func:testing:efficiency:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/testing/efficiency/overview")
  public ApiLocaleResult<TestingEfficiencyOverview> testingEfficiency(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.testingEfficiency(dto));
  }

  @Operation(description = "The overview of cases core KPI", operationId = "analysis:func:core:kpi:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/core/kpi/overview")
  public ApiLocaleResult<CoreKpiOverview> coreKpi(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.coreKpi(dto));
  }

  @Operation(description = "The overview of cases review efficiency", operationId = "analysis:func:review:efficiency:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/review/efficiency/overview")
  public ApiLocaleResult<ReviewEfficiencyOverview> reviewEfficiency(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.reviewEfficiency(dto));
  }

  @Operation(description = "The overview of backlogged cases", operationId = "analysis:func:backlogged:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/backlogged/overview")
  public ApiLocaleResult<BackloggedOverview> backloggedWork(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.backloggedWork(dto));
  }

  @Operation(description = "The overview of recent delivery cases", operationId = "analysis:func:recent:delivery:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/recent/delivery/overview")
  public ApiLocaleResult<RecentDeliveryOverview> recentDelivery(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.recentDelivery(dto));
  }

  @Operation(description = "The overview of cases lead time", operationId = "analysis:func:leadtime:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/leadtime/overview")
  public ApiLocaleResult<LeadTimeOverview> leadTime(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.leadTime(dto));
  }

  @Operation(description = "The overview of unplanned work cases", operationId = "analysis:func:unplanned:work:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/unplanned/work/overview")
  public ApiLocaleResult<UnplannedWorkOverview> unplannedWork(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.unplannedWork(dto));
  }

  @Operation(description = "The overview of case growth trend", operationId = "analysis:func:growth:trend:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/growth/trend/overview")
  public ApiLocaleResult<GrowthTrendOverview> growthTrend(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.growthTrend(dto));
  }

  @Operation(description = "The overview of function resource creation", operationId = "analysis:func:resource:creation:overview")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/resource/creation/overview")
  public ApiLocaleResult<ResourceCreationOverview> resourceCreation(@Valid FuncAnalysisDto dto) {
    return ApiLocaleResult.success(analysisAngusFuncFacade.resourceCreation(dto));
  }

}
