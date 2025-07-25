package cloud.xcan.angus.core.tester.application.cmd.scenario.impl;

import static cloud.xcan.angus.api.commonlink.CombinedTargetType.SCENARIO_MONITOR;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.tester.application.converter.ActivityConverter.toActivities;
import static cloud.xcan.angus.core.tester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.angus.core.tester.application.converter.ScenarioMonitorConverter.setReplaceInfo;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.activity.ActivityCmd;
import cloud.xcan.angus.core.tester.application.cmd.scenario.ScenarioMonitorCmd;
import cloud.xcan.angus.core.tester.application.cmd.scenario.ScenarioMonitorHistoryCmd;
import cloud.xcan.angus.core.tester.application.query.common.CommonQuery;
import cloud.xcan.angus.core.tester.application.query.scenario.ScenarioAuthQuery;
import cloud.xcan.angus.core.tester.application.query.scenario.ScenarioMonitorQuery;
import cloud.xcan.angus.core.tester.application.query.scenario.ScenarioQuery;
import cloud.xcan.angus.core.tester.domain.activity.ActivityType;
import cloud.xcan.angus.core.tester.domain.scenario.Scenario;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitor;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorHistory;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorHistoryRepo;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorRepo;
import cloud.xcan.angus.core.tester.domain.scenario.monitor.ScenarioMonitorStatus;
import cloud.xcan.angus.core.tester.domain.setting.NoticeSetting;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Biz
public class ScenarioMonitorCmdImpl extends CommCmd<ScenarioMonitor, Long>
    implements ScenarioMonitorCmd {

  @Resource
  private ScenarioMonitorRepo scenarioMonitorRepo;
  @Resource
  private ScenarioMonitorHistoryRepo scenarioMonitorHistoryRepo;
  @Resource
  private ScenarioMonitorHistoryCmd scenarioMonitorHistoryCmd;
  @Resource
  private ScenarioMonitorQuery scenarioMonitorQuery;
  @Resource
  private ScenarioQuery scenarioQuery;
  @Resource
  private ScenarioAuthQuery scenarioAuthQuery;
  @Resource
  private CommonQuery commonQuery;
  @Resource
  private ActivityCmd activityCmd;

  @Transactional(rollbackOn = Exception.class)
  @Override
  public IdKey<Long, Object> add(ScenarioMonitor monitor) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Scenario scenarioDb;

      @Override
      protected void checkParams() {
        // Check the scenario exists
        assertNotNull(monitor.getScenarioId(), "scenarioId is required");
        scenarioDb = scenarioQuery.checkAndFind(monitor.getScenarioId());
        // Check the scenario view permission
        scenarioAuthQuery.checkViewAuth(getUserId(), scenarioDb.getId());
        // Check the monitor name exists
        scenarioMonitorQuery.checkExits(monitor.getProjectId(), monitor.getName());
        // Check the required notice setting
        NoticeSetting setting = monitor.getNoticeSetting();
        assertTrue(!setting.getEnabled()
                || nonNull(setting.getOrgType()) && nonNull(setting.getOrgs()),
            "Notice org info is required");
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Save monitor
        monitor.setProjectId(scenarioDb.getProjectId());
        monitor.setStatus(ScenarioMonitorStatus.PENDING);
        monitor.setScriptId(scenarioDb.getScriptId());
        monitor.setNextExecDate(monitor.getTimeSetting().getNextDate(LocalDateTime.now()));
        IdKey<Long, Object> idKey = insert(monitor);

        // Save activity
        activityCmd.add(toActivity(SCENARIO_MONITOR, monitor, ActivityType.CREATED));
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void update(ScenarioMonitor monitor) {
    new BizTemplate<Void>() {
      ScenarioMonitor monitorDb;

      @Override
      protected void checkParams() {
        // Check the monitor exists
        monitorDb = scenarioMonitorQuery.checkAndFind(monitor.getId());
        // Check the monitor exists
        if (isNotEmpty(monitor.getName()) && !monitorDb.getName().equals(monitor.getName())) {
          scenarioMonitorQuery.checkExits(monitorDb.getProjectId(), monitor.getName());
        }
      }

      @Override
      protected Void process() {
        if (nonNull(monitor.getTimeSetting())) {
          monitor.setNextExecDate(monitor.getTimeSetting()
              .getNextDate(nullSafe(monitorDb.getLastMonitorDate(), LocalDateTime.now())));
        }
        scenarioMonitorRepo.save(copyPropertiesIgnoreNull(monitor, monitorDb));
        activityCmd.add(toActivity(SCENARIO_MONITOR, monitorDb, ActivityType.UPDATED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public IdKey<Long, Object> replace(ScenarioMonitor monitor) {
    return new BizTemplate<IdKey<Long, Object>>() {
      ScenarioMonitor monitorDb;

      @Override
      protected void checkParams() {
        if (nonNull(monitor.getId())) {
          // Check the monitor exists
          monitorDb = scenarioMonitorQuery.checkAndFind(monitor.getId());
          // Check the monitor name exists
          if (isNotEmpty(monitor.getName()) && !monitorDb.getName().equals(monitor.getName())) {
            scenarioMonitorQuery.checkExits(monitorDb.getProjectId(), monitor.getName());
          }
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(monitor.getId())) {
          return add(monitor);
        }

        setReplaceInfo(monitorDb, monitor);
        scenarioMonitorRepo.save(monitorDb);

        activityCmd.add(toActivity(SCENARIO_MONITOR, monitorDb, ActivityType.UPDATED));

        return new IdKey<Long, Object>().setId(monitorDb.getId()).setKey(monitorDb.getName());
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void runNow(Long id) {
    new BizTemplate<Void>() {
      ScenarioMonitor monitorDb;

      @Override
      protected void checkParams() {
        // Check the monitor exists
        monitorDb = scenarioMonitorQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        try {
          if (!isUserAction()) {
            // Transfer principal downwards
            commonQuery.setInnerPrincipal(monitorDb.getTenantId(), monitorDb.getCreatedBy());
          }

          LocalDateTime now = LocalDateTime.now();
          ScenarioMonitorHistory history = scenarioMonitorHistoryCmd.run(monitorDb);
          monitorDb.setStatus(history.getStatus())
              .setFailureMessage(lengthSafe(history.getFailureMessage(), 400))
              .setLastMonitorHistoryId(history.getId())
              .setLastMonitorDate(history.getCreatedDate());
          if (nonNull(monitorDb.getTimeSetting()) && !monitorDb.getTimeSetting().isOnetime()) {
            // Trigger the next execution
            monitorDb.setNextExecDate(monitorDb.getTimeSetting().getNextDate(now));
          }
          scenarioMonitorRepo.save(monitorDb);

          // Add scenario monitor failure event
          if (monitorDb.getStatus().isFailure()) {
            scenarioMonitorQuery.assembleAndSendScenarioMonitorFailureNoticeEvent(monitorDb);
          }
        } finally {
          if (!isUserAction()) {
            PrincipalContext.remove();
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void delete(Collection<Long> ids) {
    new BizTemplate<Void>() {
      List<ScenarioMonitor> monitorDb;

      @Override
      protected void checkParams() {
        monitorDb = scenarioMonitorQuery.checkAndFind(ids);
      }

      @Override
      protected Void process() {
        scenarioMonitorHistoryRepo.deleteByMonitorIdIn(ids);
        scenarioMonitorRepo.deleteByIdIn(ids);

        activityCmd.addAll(toActivities(SCENARIO_MONITOR, monitorDb, ActivityType.DELETED));
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ScenarioMonitor, Long> getRepository() {
    return scenarioMonitorRepo;
  }

}
