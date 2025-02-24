package cloud.xcan.sdf.core.angustester.application.query.kanban.impl;

import static cloud.xcan.sdf.core.angustester.application.converter.FuncCaseConverter.getCaseTesterResourcesFilter;
import static cloud.xcan.sdf.core.angustester.application.converter.KanbanCtoCaseConverter.assembleCaseOverview;
import static cloud.xcan.sdf.core.angustester.application.converter.KanbanCtoCaseConverter.assembleTesterOverview;
import static cloud.xcan.sdf.core.angustester.application.converter.KanbanCtoTaskConverter.assembleAssigneeOverview;
import static cloud.xcan.sdf.core.angustester.application.converter.KanbanCtoTaskConverter.assembleTaskOverview;
import static cloud.xcan.sdf.core.angustester.application.converter.TaskConverter.getTaskAssigneeResourcesFilter;
import static cloud.xcan.sdf.core.angustester.application.query.func.impl.FuncCaseQueryImpl.getCaseSummary;
import static cloud.xcan.sdf.core.angustester.application.query.task.impl.TaskQueryImpl.getTaskSummary;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisTestQuery;
import cloud.xcan.sdf.core.angustester.application.query.common.CommonQuery;
import cloud.xcan.sdf.core.angustester.application.query.kanban.KanbanCtoQuery;
import cloud.xcan.sdf.core.angustester.application.query.project.ProjectMemberQuery;
import cloud.xcan.sdf.core.angustester.application.query.scenario.ScenarioTestQuery;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseInfo;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCaseRepo;
import cloud.xcan.sdf.core.angustester.domain.func.summary.FuncCaseEfficiencySummary;
import cloud.xcan.sdf.core.angustester.domain.kanban.CtoCaseOverview;
import cloud.xcan.sdf.core.angustester.domain.kanban.CtoTaskOverview;
import cloud.xcan.sdf.core.angustester.domain.kanban.TestApisCount;
import cloud.xcan.sdf.core.angustester.domain.kanban.TestScenarioCount;
import cloud.xcan.sdf.core.angustester.domain.task.TaskInfo;
import cloud.xcan.sdf.core.angustester.domain.task.TaskInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.task.TaskRepo;
import cloud.xcan.sdf.core.angustester.domain.task.summary.TaskEfficiencySummary;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.JoinSupplier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Biz
public class KanbanCtoQueryImpl implements KanbanCtoQuery {

  @Resource
  private TaskRepo taskRepo;

  @Resource
  private TaskInfoRepo taskInfoRepo;

  @Resource
  private FuncCaseRepo funcCaseRepo;

  @Resource
  private FuncCaseInfoRepo funcCaseInfoRepo;

  @Resource
  private ApisTestQuery apisTestQuery;

  @Resource
  private ScenarioTestQuery scenarioTestQuery;

  @Resource
  private ProjectMemberQuery projectMemberQuery;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private JoinSupplier joinSupplier;

  @Override
  public CtoTaskOverview taskCtoOverview(AuthObjectType creatorObjectType,
      Long creatorObjectId, Long projectId, Long planId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd, boolean joinTaskList, boolean joinAssigneeOverview) {
    return new BizTemplate<CtoTaskOverview>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected CtoTaskOverview process() {
        CtoTaskOverview overview = new CtoTaskOverview();

        Set<Long> memberIds = projectMemberQuery.getMemberIds(creatorObjectType,
            creatorObjectId, projectId);
        // Note: All project data will be querying when creator is null
        Set<SearchCriteria> allFilters = getTaskAssigneeResourcesFilter(projectId, planId,
            createdDateStart, createdDateEnd, isNull(creatorObjectId) || isNull(creatorObjectType)
                ? null : memberIds);
        List<TaskEfficiencySummary> tasks = taskRepo.findProjectionByFilters(TaskInfo.class,
            TaskEfficiencySummary.class, allFilters);

        // Add members who have tasks but have been removed from the project
        memberIds.addAll(tasks.stream().map(TaskEfficiencySummary::getAssigneeId)
            .filter(Objects::nonNull).collect(Collectors.toSet()));
        overview.setMemberNum(memberIds.size());
        overview.setAssignees(commonQuery.getUserInfoMap(memberIds));

        if (isEmpty(tasks)) {
          return overview;
        }

        overview.setAssigneeNum(tasks.stream().map(TaskEfficiencySummary::getAssigneeId)
            .collect(Collectors.toSet()).size());

        assembleTaskOverview(tasks, overview);

        if (joinAssigneeOverview) {
          assembleAssigneeOverview(tasks, overview);
        }

        if (joinTaskList) {
          List<TaskInfo> taskInfos = taskInfoRepo.findAllByFilters(allFilters);
          overview.setTasks(joinSupplier.execute(() -> getTaskSummary(taskInfos)));
        }
        return overview;
      }
    }.execute();
  }

  @Override
  public CtoCaseOverview caseCtoOverview(AuthObjectType creatorObjectType,
      Long creatorObjectId, Long projectId, Long planId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd, boolean joinCaseList, boolean joinTesterOverview) {
    return new BizTemplate<CtoCaseOverview>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected CtoCaseOverview process() {
        CtoCaseOverview overview = new CtoCaseOverview();

        Set<Long> memberIds = projectMemberQuery.getMemberIds(creatorObjectType,
            creatorObjectId, projectId);

        TestApisCount testApisCount = apisTestQuery.countTestResult(projectId, creatorObjectType,
            creatorObjectId, createdDateStart, createdDateEnd);
        overview.setApisTestCount(testApisCount);

        TestScenarioCount scenarioCount = scenarioTestQuery.countTestResult(projectId,
            creatorObjectType, creatorObjectId, createdDateStart, createdDateEnd);
        overview.setScenarioTestCount(scenarioCount);

        // Note: All project data will be querying when creator is null
        Set<SearchCriteria> allFilters = getCaseTesterResourcesFilter(projectId, planId,
            createdDateStart, createdDateEnd, isNull(creatorObjectId) || isNull(creatorObjectType)
                ? null : memberIds);
        List<FuncCaseEfficiencySummary> cases = funcCaseRepo.findProjectionByFilters(
            FuncCaseInfo.class, FuncCaseEfficiencySummary.class, allFilters);

        // Add members who have cases but have been removed from the project
        memberIds.addAll(cases.stream().map(FuncCaseEfficiencySummary::getTesterId)
            .filter(Objects::nonNull).collect(Collectors.toSet()));
        overview.setMemberNum(memberIds.size());
        overview.setTesters(commonQuery.getUserInfoMap(memberIds));
        if (isEmpty(cases)) {
          return overview;
        }

        overview.setTesterNum(cases.stream().map(FuncCaseEfficiencySummary::getTesterId)
            .collect(Collectors.toSet()).size());

        assembleCaseOverview(cases, overview);

        if (joinTesterOverview) {
          assembleTesterOverview(cases, overview);
        }

        if (joinCaseList) {
          List<FuncCaseInfo> caseInfos = funcCaseInfoRepo.findAllByFilters(allFilters);
          overview.setCases(joinSupplier.execute(() -> getCaseSummary(caseInfos)));
        }
        return overview;
      }
    }.execute();
  }
}
