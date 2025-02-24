package cloud.xcan.sdf.core.angustester.interfaces.apis.facade.internal;


import static cloud.xcan.sdf.core.angustester.interfaces.apis.facade.internal.assembler.ApisTestAssembler.generateToScript;
import static cloud.xcan.sdf.core.angustester.interfaces.apis.facade.internal.assembler.ApisTestAssembler.generateToTask;

import cloud.xcan.sdf.api.angusctrl.exec.ExecResultRemote;
import cloud.xcan.sdf.core.angustester.application.cmd.apis.ApisTestCmd;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisTestQuery;
import cloud.xcan.sdf.core.angustester.domain.task.TaskType;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.ApisTestFacade;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.test.ApisTestScriptGenerateDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.dto.test.ApisTestTaskGenerateDto;
import cloud.xcan.sdf.core.angustester.interfaces.apis.facade.vo.test.TestResultDetailVo;
import cloud.xcan.sdf.core.angustester.interfaces.task.facade.TaskTestFacade;
import cloud.xcan.sdf.model.script.TestType;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author xiaolong.liu
 */
@Component
public class ApisTestFacadeImpl implements ApisTestFacade {

  @Resource
  private ApisTestCmd apisTestCmd;

  @Resource
  private ApisTestQuery apisTestQuery;

  @Resource
  private TaskTestFacade taskTestFacade;

  @Resource
  private ExecResultRemote execResultRemote;

  @Override
  public void testEnabled(Long apisId, Set<TestType> testTypes, boolean enabled) {
    apisTestCmd.testEnabled(apisId, testTypes, enabled);
  }

  @Override
  public List<TestType> testEnabledFind(Long apisId) {
    return apisTestQuery.findEnabledTestTypes(apisId);
  }

  @Override
  public void scriptGenerate(Long apisId, Set<ApisTestScriptGenerateDto> dtos) {
    apisTestCmd.scriptGenerate(apisId, generateToScript(dtos));
  }

  @Override
  public void scriptDelete(Long apisId, Set<TestType> testTypes) {
    apisTestCmd.scriptDelete(apisId, testTypes);
  }

  @Override
  public void testTaskGenerate(Long apisId, @Nullable Long taskSprintId, Set<ApisTestTaskGenerateDto> dtos) {
    apisTestCmd.testTaskGenerate(apisId, taskSprintId, generateToTask(apisId, dtos), false);
  }

  @Override
  public void testTaskRetest(Long apisId) {
    apisTestCmd.testTaskRetest(apisId, true);
  }

  @Override
  public void testTaskReopen(Long apisId) {
    apisTestCmd.testTaskRetest(apisId, false);
  }

  @Override
  public void testTaskDelete(Long apisId, Set<TestType> testTypes) {
    apisTestCmd.testTaskDelete(Collections.singletonList(apisId), testTypes);
  }

  @Override
  public void testExecAdd(Long apisId, Set<TestType> testTypes, @Nullable List<Server> servers) {
    apisTestCmd.testExecAdd(apisId, testTypes, servers);
  }

  @Override
  public void testExecAdd(HashSet<Long> apisIds, HashSet<TestType> testTypes,
      @Nullable List<Server> servers) {
    apisTestCmd.testExecAdd(apisIds, testTypes, servers);
  }

  @Override
  public void testCaseExecAdd(Long apisId, LinkedHashSet<Long> caseIds) {
    apisTestCmd.testCaseExecAdd(apisId, caseIds);
  }

  @Override
  public TestResultDetailVo testResultDetail(Long apisId) {
    TestResultDetailVo result = new TestResultDetailVo();
    result.setTestResult(execResultRemote.apisResult(apisId).orElseContentThrow());
    result.setAssocTasks(taskTestFacade.assocList(TaskType.API_TEST, apisId));
    return result;
  }

}
