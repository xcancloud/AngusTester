package cloud.xcan.angus.core.tester.application.cmd.exec.impl;

import static cloud.xcan.angus.core.tester.application.converter.ExecResultConverter.assembleCaseResultInfo;
import static cloud.xcan.angus.core.tester.application.converter.ExecResultConverter.assembleTestResultInfo;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_ERROR_RATE_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NODE_CPU_MEAN_USAGE_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NODE_DISK_USAGE_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NODE_MEMORY_MEAN_USAGE_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NODE_METRICS_IS_MISSING;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_NODE_NETWORK_MEAN_USAGE_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_PERF_INDICATOR_IS_EMPTY;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_PERF_SAMPLING_SUMMARY_IS_EMPTY;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_RESPONSE_TIME_EXCEEDS_T;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_SAMPLE_CONTENT_IGNORED_OR_MISSING;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_SAMPLING_SUMMARY_IS_EMPTY;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_STABILITY_INDICATOR_IS_EMPTY;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_STABILITY_SAMPLING_SUMMARY_IS_EMPTY;
import static cloud.xcan.angus.core.tester.domain.CtrlCoreMessage.EXEC_TPS_NOT_EXCEEDS_T;
import static cloud.xcan.angus.parser.AngusParser.YAML_MAPPER;
import static cloud.xcan.angus.spec.locale.MessageHolder.message;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Comparator.comparingDouble;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import cloud.xcan.angus.api.commonlink.exec.result.AssertionSummary;
import cloud.xcan.angus.api.commonlink.exec.result.ExecSampleResultContent;
import cloud.xcan.angus.api.commonlink.exec.result.ExecTargetSummary;
import cloud.xcan.angus.api.commonlink.exec.result.NodeUsageSummary;
import cloud.xcan.angus.api.enums.Percentile;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.tester.application.cmd.exec.ExecTestCmd;
import cloud.xcan.angus.core.tester.application.cmd.exec.ExecTestResultCmd;
import cloud.xcan.angus.core.tester.application.converter.ExecResultConverter;
import cloud.xcan.angus.core.tester.application.converter.ExecSampleConverter;
import cloud.xcan.angus.core.tester.application.query.exec.ExecSampleExtcQuery;
import cloud.xcan.angus.core.tester.application.query.exec.ExecSampleQuery;
import cloud.xcan.angus.core.tester.application.query.indicator.IndicatorFuncQuery;
import cloud.xcan.angus.core.tester.application.query.indicator.IndicatorPerfQuery;
import cloud.xcan.angus.core.tester.application.query.indicator.IndicatorStabilityQuery;
import cloud.xcan.angus.core.tester.application.query.node.NodeUsageQuery;
import cloud.xcan.angus.core.tester.domain.exec.Exec;
import cloud.xcan.angus.core.tester.domain.exec.result.ExecTestCaseResult;
import cloud.xcan.angus.core.tester.domain.exec.result.ExecTestCaseResultRepo;
import cloud.xcan.angus.core.tester.domain.exec.result.ExecTestResult;
import cloud.xcan.angus.core.tester.domain.exec.result.ExecTestResultBase;
import cloud.xcan.angus.core.tester.domain.exec.result.ExecTestResultRepo;
import cloud.xcan.angus.core.tester.domain.indicator.IndicatorFunc;
import cloud.xcan.angus.core.tester.domain.indicator.IndicatorPerf;
import cloud.xcan.angus.core.tester.domain.indicator.IndicatorStability;
import cloud.xcan.angus.core.tester.infra.metricsds.domain.sample.ExecSample;
import cloud.xcan.angus.core.tester.infra.metricsds.domain.sample.ExecSampleContent;
import cloud.xcan.angus.core.tester.interfaces.exec.facade.vo.sample.ExecSampleSummaryInfoVo;
import cloud.xcan.angus.idgen.uid.impl.CachedUidGenerator;
import cloud.xcan.angus.model.element.assertion.Assertion;
import cloud.xcan.angus.model.element.http.Http;
import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.remote.message.SysException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command implementation for generating and aggregating execution test results.
 * <p>
 * Provides methods for generating, assembling, and updating test results for executions, cases, and indicators.
 * Handles result aggregation, assertion summary, and status judgment.
 */
@Slf4j
@Biz
public class ExecTestResultCmdImpl implements ExecTestResultCmd {

  @Resource
  private ExecTestResultRepo execTestResultRepo;
  @Resource
  private ExecTestCaseResultRepo execTestCaseResultRepo;
  @Resource
  private ExecSampleQuery execSampleQuery;
  @Resource
  private ExecSampleExtcQuery execSampleExtcQuery;
  @Resource
  private NodeUsageQuery nodeUsageQuery;
  @Resource
  private CachedUidGenerator uidGenerator;
  @Resource
  private ExecTestCmd execTestCmd;
  @Resource
  private IndicatorFuncQuery indicatorFuncQuery;
  @Resource
  private IndicatorPerfQuery indicatorPerfQuery;
  @Resource
  private IndicatorStabilityQuery indicatorStabilityQuery;

  /**
   * Generate and aggregate test results for an execution.
   * <p>
   * Parses script, aggregates samples, indicators, and case results, and updates related entities.
   */
  @Override
  public void generateResult(Exec execDb) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        AngusScript angusScript;
        try {
          angusScript = YAML_MAPPER.readValue(execDb.getScript(), AngusScript.class);
        } catch (JsonProcessingException e) {
          throw SysException.of("Parsing script failed", e);
        }
        execDb.setAngusScript(angusScript);

        ExecSampleSummaryInfoVo finalTotalSampleSummary = null;
        if (execDb.getScriptType().isNonFunctionalTesting() || !angusScript.needResetAfterRamp()) {
          ExecSample finalSample = execSampleQuery.getExecLatestTotalMergeSample(execDb);
          finalTotalSampleSummary = ExecSampleConverter.toSample(finalSample);
        }

        Map<Long, NodeUsageSummary> nodeUsageSummaries = null;
        if (isNotEmpty(execDb.getAppNodeIds()) && execDb.getScriptType().isStabilityTesting()) {
          nodeUsageSummaries = nodeUsageQuery.getUsageSummaries(execDb.getAppNodeIds(),
              execDb.getActualStartDate(), execDb.getEndDate());
        }

        List<ExecSampleResultContent> sampleContents = null;
        Map<String, List<ExecSampleResultContent>> nameSampleContentsMap = null;
        if (execDb.getScriptType().isFunctionalTesting()) {
          List<ExecSampleContent> sampleExtcContents =
              execSampleExtcQuery.findIterationSampleContent(execDb.getId());
          // Only for the last sampling statistical assertion
          sampleExtcContents.sort(Comparator.comparing(ExecSampleContent::getIteration));

          if (isNotEmpty(sampleExtcContents)) {
            sampleContents = sampleExtcContents.stream()
                .filter(x -> nonNull(x.getContent())).map(ExecResultConverter::toTestResultContent)
                .collect(Collectors.toList());
          }
          nameSampleContentsMap = isEmpty(sampleContents) ? null : sampleContents.stream()
              .collect(Collectors.groupingBy(ExecSampleResultContent::getName));
        }

        // Generate apis and scenario test result
        ExecTestResult testResult = generateTestResult(finalTotalSampleSummary, nodeUsageSummaries,
            sampleContents, nameSampleContentsMap, execDb);

        // Generate apis functional case result
        List<ExecTestCaseResult> caseResults = null;
        if (execDb.getScriptType().isFunctionalTesting() && execDb.getScriptSource().isApis()
            && isNotEmpty(execDb.getAssocApiCaseIds())) {
          caseResults = generateTestCaseResult(nameSampleContentsMap, execDb);
          if (isNotEmpty(caseResults)) {
            execTestCaseResultRepo.saveAll(caseResults);
          }
        }

        if (isNotEmpty(caseResults)) {
          ExecTargetSummary caseSummary = assembleExecCaseSummary(caseResults);
          testResult.setCaseSummary(caseSummary);
        }

        if (testResult.isPassed()) {
          testResult.setFailureMessage(null);
        }

        execTestResultRepo.save(testResult);

        try {
          execTestCmd.testResultUpdate(assembleTestResultInfo(execDb, testResult),
              assembleCaseResultInfo(execDb, caseResults));
        } catch (Exception e) {
          log.error("Failed to synchronize execution {} test results, cause: {}",
              execDb.getId(), e.getMessage());
        }
        return null;
      }
    }.execute();
  }

  private ExecTestResult generateTestResult(ExecSampleSummaryInfoVo finalTotalSampleSummary,
      Map<Long, NodeUsageSummary> nodeUsageSummaries, List<ExecSampleResultContent> sampleContents,
      Map<String, List<ExecSampleResultContent>> nameSampleContentsMap, Exec execDb) {

    ExecTestResult testResult = initTestResult(execDb);

    assembleTestResult(finalTotalSampleSummary, nodeUsageSummaries, sampleContents,
        nameSampleContentsMap, execDb, testResult);
    return testResult;
  }

  private ExecTestResult initTestResult(Exec execDb) {
    ExecTestResult testResultDb = execTestResultRepo.findByExecId(execDb.getId());
    if (nonNull(testResultDb)) {
      testResultDb.setExecStatus(execDb.getStatus())
          .setScriptType(execDb.getScriptType()).setTestNum(testResultDb.getTestNum() + 1)
          .setExecBy(execDb.getExecBy()).setLastExecDate(execDb.getEndDate());
    } else {
      // Fix: Delete other execution test results in the SCE or API
      // Deleting execution will not delete test results
      execTestResultRepo.deleteByScriptTypeAndScriptSourceId(execDb.getScriptType().getValue(),
          execDb.getScriptSourceId());
      testResultDb = new ExecTestResult().setId(uidGenerator.getUID())
          .setExecId(execDb.getId()).setExecStatus(execDb.getStatus())
          .setPlugin(execDb.getPlugin()).setScriptType(execDb.getScriptType())
          .setScriptId(execDb.getScriptId()).setScriptSource(execDb.getScriptSource())
          .setScriptSourceId(execDb.getScriptSourceId())
          /*.setPassed(false)*/.setTestNum(1)/*.setTestFailureNum(0)*/
          .setExecBy(execDb.getExecBy()).setLastExecDate(execDb.getEndDate())
          .setCreatedDate(LocalDateTime.now());
    }
    return testResultDb;
  }

  private void assembleTestResult(ExecSampleSummaryInfoVo finalTotalSampleSummary,
      Map<Long, NodeUsageSummary> nodeUsageSummaries, List<ExecSampleResultContent> sampleContents,
      Map<String, List<ExecSampleResultContent>> nameSampleContentsMap, Exec execDb,
      ExecTestResult testResult) {
    setTestIndicator(execDb, testResult);

    testResult.setPassed(true); // By default
    if (execDb.getScriptType().isNonFunctionalTesting() && execDb.getAngusScript()
        .needResetAfterRamp()) {
      finalTotalSampleSummary = assembleFinalTotalRampSampleSummary(execDb,
          testResult.getIndicatorPerf());
    }
    testResult.setSampleSummary(finalTotalSampleSummary);
    testResult.setTargetSummary(
        assembleTargetSummary(execDb.getAngusScript(), nameSampleContentsMap));
    testResult.setNodeUsageSummary(nodeUsageSummaries);
    testResult.setSampleContent(sampleContents);

    if (!execDb.getStatus().isCompleted()) {
      testResult.setPassed(false);
      testResult.setFailureMessage(execDb.getMeterMessage());
    } else {
      if (execDb.getScriptType().isPerformanceTesting()) {
        if (isNull(testResult.getIndicatorPerf())) {
          testResult.setPassed(false);
          testResult.setFailureMessage(message(EXEC_PERF_INDICATOR_IS_EMPTY));
        } else if (isNull(testResult.getSampleSummary())) {
          testResult.setPassed(false);
          testResult.setFailureMessage(message(EXEC_PERF_SAMPLING_SUMMARY_IS_EMPTY));
        } else {
          PerfTo perfTo = testResult.getIndicatorPerf();
          ExecSampleSummaryInfoVo summary = testResult.getSampleSummary();
          judgePerfTestResult(testResult, perfTo, summary);
        }
      } else if (execDb.getScriptType().isStabilityTesting()) {
        if (isNull(testResult.getIndicatorStability())) {
          testResult.setPassed(false);
          testResult.setFailureMessage(message(EXEC_STABILITY_INDICATOR_IS_EMPTY));
        } else if (isNull(testResult.getSampleSummary())) {
          testResult.setPassed(false);
          testResult.setFailureMessage(message(EXEC_STABILITY_SAMPLING_SUMMARY_IS_EMPTY));
        } else {
          StabilityTo stabilityTo = testResult.getIndicatorStability();
          ExecSampleSummaryInfoVo summary = testResult.getSampleSummary();
          judgeStabilityTestResult(testResult, stabilityTo, summary, nodeUsageSummaries);
        }
      } else if (execDb.getScriptType().isFunctionalTesting()) {
        if (isNotEmpty(testResult.getSampleContent())) {
          // All iterations and cases must pass
          boolean success = testResult.getSampleContent().stream()
              .allMatch(x -> nonNull(x.getSampleResult()) && x.getSampleResult().isSuccess());
          if (success) {
            testResult.setPassed(true);
          } else {
            ExecSampleResultContent lastSampleContent = testResult.getSampleContent().stream()
                .filter(x -> nonNull(x.getSampleResult()) && !x.getSampleResult().isSuccess())
                .findFirst().orElse(null);
            testResult.setPassed(false);
            testResult.setFailureMessage(nonNull(lastSampleContent)
                ? lastSampleContent.getSampleResult().getFailMessage() : execDb.getMeterMessage());
          }
        } else {
          testResult.setPassed(execDb.getStatus().isCompleted());
          testResult.setFailureMessage(execDb.getMeterMessage());
        }
      } else if (execDb.getScriptType().isCustomizedTesting()) {
        if (isNull(testResult.getSampleSummary())) {
          testResult.setPassed(false);
          testResult.setFailureMessage(message(EXEC_SAMPLING_SUMMARY_IS_EMPTY));
        } else {
          testResult.setPassed(true);
          testResult.setFailureMessage(null /*Customized*/);
        }
      } else {
        throw SysException.of("Unsupported script test type");
      }
    }
    if (!testResult.isPassed()) {
      testResult.setTestFailureNum(testResult.getTestFailureNum() + 1);
    }
  }

  private void setTestIndicator(Exec execDb, ExecTestResult testResult) {
    if (execDb.getScriptType().isFunctionalTesting()) {
      IndicatorFunc func = indicatorFuncQuery.detailOrDefault(
          CombinedTargetType.valueOf(execDb.getScriptSource().getValue()),
          execDb.getScriptSourceId());
      testResult.setIndicatorFunc(
          new FuncTo(func.isSmoke(), func.getSmokeCheckSetting(),
              func.getUserDefinedSmokeAssertion(), func.isSecurity(),
              func.getSecurityCheckSetting(), func.getUserDefinedSecurityAssertion()));
    } else if (execDb.getScriptType().isPerformanceTesting()) {
      IndicatorPerf perf = indicatorPerfQuery.detailOrDefault(
          CombinedTargetType.valueOf(execDb.getScriptSource().getValue()),
          execDb.getScriptSourceId());
      testResult.setIndicatorPerf(
          new PerfTo(perf.getThreads(), perf.getDuration(), perf.getRampUpThreads(),
              perf.getRampUpInterval(), perf.getArt(), perf.getPercentile(), perf.getTps(),
              perf.getErrorRate()));
    } else if (execDb.getScriptType().isStabilityTesting()) {
      IndicatorStability sta = indicatorStabilityQuery.detailOrDefault(
          CombinedTargetType.valueOf(execDb.getScriptSource().getValue()),
          execDb.getScriptSourceId());
      testResult.setIndicatorStability(
          new StabilityTo(sta.getThreads(), sta.getDuration(), sta.getTps(), sta.getArt(),
              sta.getPercentile(), sta.getErrorRate(), sta.getCpu(), sta.getMemory(), sta.getDisk(),
              sta.getNetwork()));
    }
  }

  @Nullable
  private ExecSampleSummaryInfoVo assembleFinalTotalRampSampleSummary(Exec execDb, PerfTo perfTo) {
    ExecSampleSummaryInfoVo finalTotalRampSampleSummary = null;
    List<ExecSample> rampTotalSampleSummaries = execSampleQuery
        .getExecLatestTotalMergeSampleByRampNum(execDb);
    if (isNotEmpty(rampTotalSampleSummaries)) {
      Comparator<ExecSample> tpsComparator = comparingDouble(ExecSample::getTps).reversed();
      rampTotalSampleSummaries.sort(tpsComparator);
      ExecSample sample = rampTotalSampleSummaries.stream().filter(x ->
              (isNull(perfTo.getTps()) || perfTo.getTps() <= x.getTps())
                  && (isNull(perfTo.getErrorRate()) || perfTo.getErrorRate() >= x.getErrorRate())
                  && (isNull(perfTo.getArt()) || isNull(perfTo.getPercentile())
                  || judgeArtPassed(x, perfTo.getPercentile(), perfTo.getArt())))
          .findFirst().orElse(rampTotalSampleSummaries.get(0));
      finalTotalRampSampleSummary = ExecSampleConverter.toSample(sample);
    }
    return finalTotalRampSampleSummary;
  }

  private List<ExecTestCaseResult> generateTestCaseResult(
      Map<String, List<ExecSampleResultContent>> nameSampleContentsMap, Exec execDb) {

    List<ExecTestCaseResult> caseResults = initTestCaseResult(execDb);

    if (isEmpty(caseResults)) {
      return caseResults;
    }

    assembleCaseTestResult(nameSampleContentsMap, caseResults);
    return caseResults;
  }

  private List<ExecTestCaseResult> initTestCaseResult(Exec execDb) {
    List<ExecTestCaseResult> caseResults = new ArrayList<>();
    // All the enabled and disabled cases
    // Note: Testing case ID missing are treated as invalid use cases, that is, ignore the test result.
    Map<Long, Http> allScriptCases = execDb.getAngusScript().getTask().getPipelines().stream()
        .filter(x -> x instanceof Http && nonNull(((Http) x).getCaseId()))
        .collect(Collectors.toMap(x -> ((Http) x).getCaseId(), x -> ((Http) x)));
    if (isEmpty(allScriptCases)) {
      return caseResults;
    }

    // Only enabled cases
    Set<Long> testCaseIds = new HashSet<>(execDb.getAssocApiCaseIds());

    // Existed cases
    List<ExecTestCaseResult> caseResultsDbs = execTestCaseResultRepo.findByCaseIdIn(testCaseIds);
    Set<Long> existedCaseIds = new HashSet<>();

    if (isNotEmpty(caseResultsDbs)) {
      for (ExecTestCaseResult caseResultsDb : caseResultsDbs) {
        // Update existed cases
        if (testCaseIds.contains(caseResultsDb.getCaseId())) {
          caseResultsDb.setExecId(execDb.getId())
              .setTestNum(caseResultsDb.getTestNum() + 1)
              .setExecBy(execDb.getExecBy())
              .setLastExecDate(execDb.getEndDate());
        }
        existedCaseIds.add(caseResultsDb.getCaseId());
      }
      caseResults.addAll(caseResultsDbs);
    }

    // Add new cases
    if (!testCaseIds.isEmpty()) {
      List<ExecTestCaseResult> newApisResults = allScriptCases.keySet()
          .stream().filter(x -> !existedCaseIds.contains(x))
          .map(x -> new ExecTestCaseResult().setId(uidGenerator.getUID())
              .setExecId(execDb.getId()).setExecStatus(execDb.getStatus())
              .setPlugin(execDb.getPlugin()).setScriptType(execDb.getScriptType())
              .setScriptId(execDb.getScriptId()).setApisId(execDb.getScriptSourceId())
              .setCaseId(x).setCaseName(allScriptCases.get(x).getName())
              .setEnabled(allScriptCases.get(x).isEnabled())
              .setCaseType(allScriptCases.get(x).getCaseType())
              /*.setPassed(false)*/.setTestNum(
                  allScriptCases.get(x).isEnabled() ? 1 : 0)/*.setTestFailureNum(0)*/
              .setExecBy(execDb.getExecBy()).setLastExecDate(execDb.getEndDate())
              .setCreatedDate(LocalDateTime.now())).toList();
      caseResults.addAll(newApisResults);
    }

    // Delete the test results than not exist cases
    execTestCaseResultRepo.deleteByApisIdAndIdNotIn(execDb.getScriptSourceId(),
        allScriptCases.keySet());
    return caseResults;
  }

  private void assembleCaseTestResult(
      Map<String, List<ExecSampleResultContent>> nameSampleContentsMap,
      List<ExecTestCaseResult> caseResults) {
    if (isEmpty(caseResults)) {
      return;
    }

    for (ExecTestCaseResult caseResult : caseResults) {
      if (!caseResult.getEnabled()) {
        caseResult.setPassed(null);
        caseResult.setFailureMessage(null/*Testing cases are disabled*/);
        break;
      }

      if (!nameSampleContentsMap.containsKey(caseResult.getCaseName())) {
        caseResult.setPassed(false);
        caseResult.setTestFailureNum(caseResult.getTestFailureNum() + 1);
        caseResult.setFailureMessage(message(EXEC_SAMPLE_CONTENT_IGNORED_OR_MISSING));
        break;
      }

      List<ExecSampleResultContent> caseSampleContent = nameSampleContentsMap.get(
          caseResult.getCaseName());
      caseResult.setSampleContent(caseSampleContent);

      setSamplePassResult(caseResult, caseSampleContent);

      caseResult.setAssertionSummary(assembleAssertionSummary(caseSampleContent));
    }
  }

  private static void setSamplePassResult(ExecTestCaseResult caseResult,
      List<ExecSampleResultContent> caseSampleContent) {
    ExecSampleResultContent successResult = caseSampleContent.stream()
        .filter(x -> nonNull(x.getSampleResult()) && !x.getSampleResult().isSuccess()).findFirst()
        .orElse(null);
    if (isNull(successResult)) {
      caseResult.setPassed(true);
    } else {
      caseResult.setTestFailureNum(caseResult.getTestFailureNum() + 1);
      caseResult.setFailureMessage(nonNull(successResult.getSampleResult())
          ? successResult.getSampleResult().getFailMessage()
          : "Testing cases sampling content is empty");
    }
  }

  private static @NotNull AssertionSummary assembleAssertionSummary(
      List<ExecSampleResultContent> caseSampleContent) {
    List<Assertion> allAssertions = new ArrayList<>();
    List<Assertion> assertions = null;

    for (ExecSampleResultContent resultContent : caseSampleContent) {
      if (nonNull(resultContent.getSampleResult())
          && isNotEmpty(resultContent.getSampleResult().getAssertions())) {
        assertions = resultContent.getSampleResult().getAssertions();
        allAssertions.addAll(assertions);
      }
    }

    AssertionSummary assertionSummary = new AssertionSummary();
    Map<String, List<Assertion>> allAssertionsMap = allAssertions.stream()
        .collect(Collectors.groupingBy(Assertion::getName));
    if (isNotEmpty(allAssertionsMap) && isNotEmpty(assertions)) {
      long successNum = 0, failureNum = 0, disabledNum = 0;
      for (Entry<String, List<Assertion>> entry : allAssertionsMap.entrySet()) {
        if (entry.getValue().stream().anyMatch(x -> x.isEnabled() && x.isSuccess())) {
          successNum++;
        }
        if (entry.getValue().stream().anyMatch(x -> x.isEnabled() && !x.isSuccess())) {
          failureNum++;
        }
      }
      disabledNum = assertions.stream().filter(x -> !x.isEnabled()).count();

      assertionSummary.setTotalNum(assertions.size());
      assertionSummary.setSuccessNum(successNum);
      assertionSummary.setFailureNum(failureNum);
      assertionSummary.setDisabledNum(disabledNum);
    }
    return assertionSummary;
  }

  public ExecTargetSummary assembleTargetSummary(AngusScript angusScript,
      Map<String, List<ExecSampleResultContent>> nameSampleContentsMap) {
    ExecTargetSummary summary = new ExecTargetSummary();
    if (isNotEmpty(nameSampleContentsMap)) {
      summary.setTotalNum(angusScript.getTask().getPipelines().size());
      summary.setDisabledNum(
          angusScript.getTask().getPipelines().stream().filter(x -> !x.isEnabled()).count());
      summary.setSuccessNum(
          nameSampleContentsMap.entrySet().stream().filter(x -> x.getValue().stream().allMatch(y
              -> nonNull(y.getSampleResult()) && y.getSampleResult().isSuccess())).count());
      summary.setFailNum(nameSampleContentsMap.entrySet().stream()
          .filter(x -> x.getValue().stream().anyMatch(y
              -> nonNull(y.getSampleResult()) && !y.getSampleResult().isSuccess())).count());
    }
    return summary;
  }

  private static @NotNull ExecTargetSummary assembleExecCaseSummary(
      List<ExecTestCaseResult> caseResults) {
    ExecTargetSummary caseSummary = new ExecTargetSummary();
    caseSummary.setTotalNum(caseResults.size());
    caseSummary.setDisabledNum(caseResults.stream().filter(x -> !x.getEnabled()).count());
    caseSummary.setSuccessNum(caseResults.stream()
        .filter(x -> x.getEnabled() && nonNull(x.getPassed()) && x.getPassed()).count());
    caseSummary.setFailNum(caseResults.stream()
        .filter(x -> x.getEnabled() && nonNull(x.getPassed()) && !x.getPassed()).count());
    return caseSummary;
  }

  private void judgePerfTestResult(ExecTestResultBase perfResult, PerfTo perfTo,
      ExecSampleSummaryInfoVo summary) {
    boolean artPassed = true;
    String notPassedMessage = null;
    if (nonNull(perfTo.getArt()) && nonNull(perfTo.getPercentile())) {
      artPassed = judgeArtPassed(summary, perfTo.getPercentile(), perfTo.getArt());
    }
    if (!artPassed) {
      notPassedMessage = message(EXEC_RESPONSE_TIME_EXCEEDS_T,
          new Object[]{perfTo.getPercentile().getMessage(), perfTo.getArt(),});
    }

    boolean tpsPassed = true;
    if (nonNull(perfTo.getTps())) {
      tpsPassed = summary.getTps() >= perfTo.getTps();
      if (!tpsPassed && isNull(notPassedMessage)) {
        notPassedMessage = message(EXEC_TPS_NOT_EXCEEDS_T,
            new Object[]{perfTo.getTps(), summary.getTps()});
      }
    }

    boolean errorRatePassed = true;
    if (nonNull(perfTo.getErrorRate())) {
      errorRatePassed = summary.getErrorRate() <= perfTo.getErrorRate();
      if (!errorRatePassed && isNull(notPassedMessage)) {
        notPassedMessage = message(EXEC_ERROR_RATE_EXCEEDS_T,
            new Object[]{perfTo.getErrorRate(), summary.getErrorRate()});
      }
    }

    boolean passed = artPassed && tpsPassed && errorRatePassed;
    perfResult.setPassed(passed);
    perfResult.setFailureMessage(notPassedMessage);
  }

  private void judgeStabilityTestResult(ExecTestResultBase stabilityResult, StabilityTo stabilityTo,
      ExecSampleSummaryInfoVo summary, Map<Long, NodeUsageSummary> nodeUsageSummaries) {
    boolean artPassed = true;
    String notPassedMessage = null;
    if (nonNull(stabilityTo.getArt()) && nonNull(stabilityTo.getPercentile())) {
      artPassed = judgeArtPassed(summary, stabilityTo.getPercentile(), stabilityTo.getArt());
    }
    if (!artPassed) {
      notPassedMessage = message(EXEC_RESPONSE_TIME_EXCEEDS_T,
          new Object[]{stabilityTo.getPercentile().getMessage(), stabilityTo.getArt()});
    }

    boolean tpsPassed = true;
    if (nonNull(stabilityTo.getTps())) {
      tpsPassed = summary.getTps() >= stabilityTo.getTps();
      if (!tpsPassed && isNull(notPassedMessage)) {
        notPassedMessage = message(EXEC_TPS_NOT_EXCEEDS_T,
            new Object[]{stabilityTo.getTps(), summary.getTps()});
      }
    }

    boolean errorRatePassed = true;
    if (nonNull(stabilityTo.getErrorRate())) {
      errorRatePassed = summary.getErrorRate() <= stabilityTo.getErrorRate();
      if (!errorRatePassed && isNull(notPassedMessage)) {
        notPassedMessage = message(EXEC_ERROR_RATE_EXCEEDS_T,
            new Object[]{stabilityTo.getErrorRate(), summary.getErrorRate()});
      }
    }

    boolean passed = artPassed && tpsPassed && errorRatePassed;

    Long usageFailedNodeId = null;
    if (passed && stabilityTo.hasNodeUsage()) {
      if (isNull(nodeUsageSummaries)) {
        passed = false;
        notPassedMessage = message(EXEC_NODE_METRICS_IS_MISSING);
      } else {
        for (Entry<Long, NodeUsageSummary> entry : nodeUsageSummaries.entrySet()) {
          NodeUsageSummary nodeUsage = entry.getValue();
          if (nonNull(stabilityTo.getCpu()) && nodeUsage.getMeanCpu() > stabilityTo.getCpu()) {
            usageFailedNodeId = entry.getKey();
            notPassedMessage = message(EXEC_NODE_CPU_MEAN_USAGE_EXCEEDS_T,
                new Object[]{usageFailedNodeId, stabilityTo.getCpu()});
            passed = false;
            break;
          }
          if (nonNull(stabilityTo.getMemory()) && nodeUsage.getMeanMemory()
              > stabilityTo.getMemory()) {
            usageFailedNodeId = entry.getKey();
            notPassedMessage = message(EXEC_NODE_MEMORY_MEAN_USAGE_EXCEEDS_T,
                new Object[]{usageFailedNodeId, stabilityTo.getMemory()});
            passed = false;
            break;
          }
          if (nonNull(stabilityTo.getDisk()) && nodeUsage.getMeanFilesystem()
              > stabilityTo.getDisk()) {
            usageFailedNodeId = entry.getKey();
            notPassedMessage = message(EXEC_NODE_DISK_USAGE_EXCEEDS_T,
                new Object[]{usageFailedNodeId, stabilityTo.getDisk()});
            passed = false;
            break;
          }
          if (nonNull(stabilityTo.getNetwork()) && nodeUsage.getMeanNetwork()
              > stabilityTo.getNetwork()) {
            usageFailedNodeId = entry.getKey();
            notPassedMessage = message(EXEC_NODE_NETWORK_MEAN_USAGE_EXCEEDS_T,
                new Object[]{usageFailedNodeId, stabilityTo.getNetwork()});
            passed = false;
            break;
          }
        }
      }
    }
    stabilityResult.setPassed(passed);
    stabilityResult.setFailureMessage(notPassedMessage);
    stabilityResult.setUsageFailedNodeId(usageFailedNodeId);
  }

  private boolean judgeArtPassed(ExecSample summary, Percentile percentile, Long art) {
    switch (percentile) {
      case P50 -> {
        return summary.getTranP50() <= art;
      }
      case P75 -> {
        return summary.getTranP75() <= art;
      }
      case P90 -> {
        return summary.getTranP90() <= art;
      }
      case P95 -> {
        return summary.getTranP95() <= art;
      }
      case P99 -> {
        return summary.getTranP99() <= art;
      }
      case P999 -> {
        return summary.getTranP999() <= art;
      }
      default -> {
        //ALL;
        return summary.getTranMax() <= art;
      }
    }
  }

  private boolean judgeArtPassed(ExecSampleSummaryInfoVo summary, Percentile percentile, Long art) {
    switch (percentile) {
      case P50 -> {
        return summary.getTranP50() <= art;
      }
      case P75 -> {
        return summary.getTranP75() <= art;
      }
      case P90 -> {
        return summary.getTranP90() <= art;
      }
      case P95 -> {
        return summary.getTranP95() <= art;
      }
      case P99 -> {
        return summary.getTranP99() <= art;
      }
      case P999 -> {
        return summary.getTranP999() <= art;
      }
      default -> {
        //ALL;
        return summary.getTranMax() <= art;
      }
    }
  }

}
