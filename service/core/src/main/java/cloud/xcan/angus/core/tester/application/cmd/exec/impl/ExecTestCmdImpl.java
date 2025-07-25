package cloud.xcan.angus.core.tester.application.cmd.exec.impl;

import static cloud.xcan.angus.model.element.type.TestTargetType.PLUGIN_HTTP_NAME;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.exec.TestCaseResultInfo;
import cloud.xcan.angus.api.commonlink.exec.TestResultInfo;
import cloud.xcan.angus.api.enums.Result;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.tester.application.cmd.exec.ExecCmd;
import cloud.xcan.angus.core.tester.application.cmd.exec.ExecTestCmd;
import cloud.xcan.angus.core.tester.domain.apis.Apis;
import cloud.xcan.angus.core.tester.domain.apis.ApisRepo;
import cloud.xcan.angus.core.tester.domain.apis.cases.ApisCase;
import cloud.xcan.angus.core.tester.domain.apis.cases.ApisCaseRepo;
import cloud.xcan.angus.core.tester.domain.scenario.Scenario;
import cloud.xcan.angus.core.tester.domain.scenario.ScenarioRepo;
import cloud.xcan.angus.core.tester.domain.script.ScriptInfo;
import cloud.xcan.angus.core.tester.domain.script.ScriptInfoRepo;
import cloud.xcan.angus.core.tester.domain.task.Task;
import cloud.xcan.angus.core.tester.domain.task.TaskRepo;
import cloud.xcan.angus.core.tester.domain.task.TaskStatus;
import cloud.xcan.angus.model.script.TestType;
import cloud.xcan.angus.model.script.configuration.ScriptType;
import cloud.xcan.angus.model.script.pipeline.Arguments;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command implementation for managing test result updates and example imports for executions.
 * <p>
 * Provides methods for updating test results and importing example executions.
 * Handles result aggregation, status updates, and related entity updates.
 */
@Biz
public class ExecTestCmdImpl implements ExecTestCmd {

  @Resource
  private TaskRepo taskRepo;
  @Resource
  private ApisRepo apisRepo;
  @Resource
  private ScenarioRepo scenarioRepo;
  @Resource
  private ApisCaseRepo apisCaseRepo;
  @Resource
  private ScriptInfoRepo scriptInfoRepo;
  @Resource
  private ApplicationInfo applicationInfo;
  @Resource
  private ExecCmd execCmd;

  /**
   * Note: A scenario or apis may have multiple testing tasks.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void testResultUpdate(TestResultInfo testResult, List<TestCaseResultInfo> caseResults) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        if (nonNull(testResult)) {
          updateTaskTestResult(testResult);
        }

        if (testResult.getScriptSource().isApis()) {
          updateApisTestResult(testResult);
        } else if (testResult.getScriptSource().isScenario()) {
          updateScenarioTestResult(testResult);
        }

        if (isNotEmpty(caseResults)) {
          setApisCaseTestResult(caseResults);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Note: When API calls that are not user-action, tenant and user information must be injected
   * into the PrincipalContext.
   */
  //@Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> importExample(Long projectId) {
    return new BizTemplate<IdKey<Long, Object>>() {


      @Override
      protected IdKey<Long, Object> process() {
        ScriptInfo script = scriptInfoRepo.findTop1ByProjectIdAndPluginAndTypeIn(projectId,
            PLUGIN_HTTP_NAME, ScriptType.TEST_PERFORMANCE.getValue());
        if (isNull(script)) {
          return null;
        }
        return execCmd.addByRemoteScript(
            script.getName() + "-" + script.getType().getMessage(), script.getId(),
            script.getType(), null, new Arguments(), applicationInfo.isCloudServiceEdition());
      }
    }.execute();
  }

  /**
   * Update test results for tasks, APIs, scenarios, and cases.
   * <p>
   * Aggregates and updates results for related entities based on execution outcome.
   */
  private void updateTaskTestResult(TestResultInfo testResult) {
    List<Task> taskDbs = taskRepo.find0ByTargetIdAndTestType(testResult.getScriptSourceId(),
        TestType.of(testResult.getScriptType()).getValue());
    if (isNotEmpty(taskDbs)) {
      for (Task taskDb : taskDbs) {
        setTaskTestResult(taskDb, testResult);
      }
      taskRepo.saveAll(taskDbs);
    }
  }

  /**
   * Update test results for APIs.
   * <p>
   * Updates the test result for an API based on the execution outcome.
   */
  private void updateApisTestResult(TestResultInfo testResult) {
    Apis apisDb = apisRepo.findById(testResult.getScriptSourceId()).orElse(null);
    if (nonNull(apisDb)) {
      switch (testResult.getScriptType()) {
        case TEST_FUNCTIONALITY:
          apisDb.setTestFuncPassed(testResult.isPassed())
              .setTestFuncFailureMessage(testResult.getFailureMessage());
          break;
        case TEST_PERFORMANCE:
          apisDb.setTestPerfPassed(testResult.isPassed())
              .setTestPerfFailureMessage(testResult.getFailureMessage());
          break;
        case TEST_STABILITY:
          apisDb.setTestStabilityPassed(testResult.isPassed())
              .setTestStabilityFailureMessage(testResult.getFailureMessage());
          break;
        case MOCK_DATA:
          // No action required for MOCK_DATA
          break;
      }
      apisRepo.save(apisDb);
    }
  }

  /**
   * Update test results for scenarios.
   * <p>
   * Updates the test result for a scenario based on the execution outcome.
   */
  private void updateScenarioTestResult(TestResultInfo testResult) {
    Scenario scenarioDb = scenarioRepo.findById(testResult.getScriptSourceId()).orElse(null);
    if (nonNull(scenarioDb)) {
      switch (testResult.getScriptType()) {
        case TEST_FUNCTIONALITY:
          scenarioDb.setTestFuncPassed(testResult.isPassed())
              .setTestFuncFailureMessage(testResult.getFailureMessage());
          break;
        case TEST_PERFORMANCE:
          scenarioDb.setTestPerfPassed(testResult.isPassed())
              .setTestPerfFailureMessage(testResult.getFailureMessage());
          break;
        case TEST_STABILITY:
          scenarioDb.setTestStabilityPassed(testResult.isPassed())
              .setTestStabilityFailureMessage(testResult.getFailureMessage());
          break;
        case MOCK_DATA:
          // No action required for MOCK_DATA
          break;
      }
      scenarioRepo.save(scenarioDb);
    }
  }

  /**
   * Set test results for API cases.
   * <p>
   * Updates the test result for each API case based on the execution outcome.
   */
  private void setApisCaseTestResult(List<TestCaseResultInfo> caseResults) {
    Map<Long, TestCaseResultInfo> caseResultInfoMap = caseResults.stream()
        .filter(x -> nonNull(x.getPassed())) // A null value means not executed, disabled.
        .collect(Collectors.toMap(TestCaseResultInfo::getCaseId, x -> x));
    List<ApisCase> caseDbs = apisCaseRepo.findAllById(caseResultInfoMap.keySet());
    if (isNotEmpty(caseDbs)) {
      for (ApisCase caseDb : caseDbs) {
        setCaseTestResult(caseDb, caseResultInfoMap.get(caseDb.getId()));
      }
      apisCaseRepo.saveAll(caseDbs);
    }
  }

  /**
   * Set test result for a task.
   * <p>
   * Updates the status, script ID, execution result, failure message, test numbers, and
   * execution details of a task based on the test result.
   */
  private void setTaskTestResult(Task taskDb, TestResultInfo resultInfo) {
    taskDb.setStatus(resultInfo.isPassed() ? TaskStatus.COMPLETED : taskDb.getStatus())
        .setScriptId(resultInfo.getScriptId())
        .setExecResult(resultInfo.isPassed() ? Result.SUCCESS : Result.FAIL)
        .setExecFailureMessage(resultInfo.getFailureMessage())
        .setExecTestNum(resultInfo.getTestNum())
        .setExecTestFailureNum(resultInfo.getTestFailureNum())
        .setExecId(resultInfo.getExecId())
        .setExecName(resultInfo.getExecName())
        .setExecBy(resultInfo.getExecBy())
        .setExecDate(resultInfo.getLastExecStartDate());

    if (taskDb.getStatus().isCompleted()) {
      taskDb.setStartDate(resultInfo.getLastExecStartDate());
      taskDb.setCompletedDate(resultInfo.getLastExecEndDate());
    }
    if (!resultInfo.isPassed()) {
      taskDb.setFailNum(nullSafe(taskDb.getFailNum(), 0) + 1);
    }
    taskDb.setTotalNum(nullSafe(taskDb.getFailNum(), 0) + 1);
  }

  /**
   * Set test result for an API case.
   * <p>
   * Updates the execution result, failure message, test numbers, and execution details of an
   * API case based on the test result.
   */
  private void setCaseTestResult(ApisCase apisCaseDb, TestCaseResultInfo caseResult) {
    apisCaseDb.setExecResult(caseResult.getPassed() ? Result.SUCCESS : Result.FAIL)
        .setExecFailureMessage(caseResult.getFailureMessage())
        .setExecTestNum(caseResult.getTestNum())
        .setExecTestFailureNum(caseResult.getTestFailureNum())
        .setExecId(caseResult.getExecId())
        .setExecName(caseResult.getExecName())
        .setExecBy(caseResult.getExecBy())
        .setExecDate(caseResult.getLastExecDate());
  }

}
