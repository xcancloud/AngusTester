package cloud.xcan.angus.core.tester.application.cmd.scenario.impl;

import static cloud.xcan.angus.api.commonlink.TesterConstant.MAX_SCE_MONITOR_HISTORY_NUM;
import static cloud.xcan.angus.core.tester.application.converter.ScenarioMonitorHistoryConverter.assembleExecDebugStartByMonitorDto;
import static cloud.xcan.angus.core.tester.application.converter.ScenarioMonitorHistoryConverter.assembleScenarioMonitorResultInfo;
import static cloud.xcan.angus.core.tester.application.converter.ScenarioMonitorHistoryConverter.readExecutionLogFromRemote;

import cloud.xcan.angus.api.ctrl.exec.ExecDebugDoorRemote;
import cloud.xcan.angus.api.ctrl.exec.dto.debug.ExecDebugStartByMonitorDto;
import cloud.xcan.angus.api.ctrl.exec.vo.debug.ExecDebugDetailVo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.scenario.ScenarioMonitorHistoryCmd;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitor;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorHistory;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorHistoryRepo;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorStatus;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Biz
public class ScenarioMonitorHistoryCmdImpl extends CommCmd<ScenarioMonitorHistory, Long>
    implements ScenarioMonitorHistoryCmd {

  @Resource
  private ScenarioMonitorHistoryRepo scenarioMonitorHistoryRepo;

  @Resource
  private ExecDebugDoorRemote execDebugDoorRemote;

  @Transactional(rollbackOn = Exception.class)
  @Override
  public ScenarioMonitorHistory run(ScenarioMonitor monitor) {
    return new BizTemplate<ScenarioMonitorHistory>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected ScenarioMonitorHistory process() {
        ScenarioMonitorHistory history = new ScenarioMonitorHistory();
        history.setProjectId(monitor.getProjectId()).setMonitorId(monitor.getId());
        try {
          ExecDebugStartByMonitorDto dto = assembleExecDebugStartByMonitorDto(monitor);
          ExecDebugDetailVo result = execDebugDoorRemote.startByMonitor(dto).orElseContentThrow();
          assembleScenarioMonitorResultInfo(history, result);
          try {
            readExecutionLogFromRemote(result, history);
          } catch (Throwable e) {
            log.warn("Exception in querying scenario monitoring execution logs: {}",
                e.getMessage());
          }
        } catch (Exception e) {
          history.setStatus(ScenarioMonitorStatus.FAILURE)
              .setFailureMessage("Execution scenario monitoring exception: " + e.getMessage());
        }
        insert0(history);

        // Maximum retention of historical records
        scenarioMonitorHistoryRepo.deleteHistory(monitor.getId(), MAX_SCE_MONITOR_HISTORY_NUM);
        return history;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ScenarioMonitorHistory, Long> getRepository() {
    return scenarioMonitorHistoryRepo;
  }

}
