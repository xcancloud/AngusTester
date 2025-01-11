package cloud.xcan.sdf.core.angustester.application.query.scenario.impl;

import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.SCENARIO_MONITOR;
import static cloud.xcan.sdf.api.commonlink.EventUtils.assembleAngusTesterUserNoticeEvent;
import static cloud.xcan.sdf.core.angustester.application.converter.ScenarioMonitorConverter.assembleScenarioMonitorCount0;
import static cloud.xcan.sdf.core.angustester.domain.TesterEventMessage.ScenarioMonitorFailed;
import static cloud.xcan.sdf.core.angustester.domain.TesterEventMessage.ScenarioMonitorFailedCode;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.sdf.spec.locale.MessageHolder.message;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.sdf.api.enums.NoticeType;
import cloud.xcan.sdf.api.info.IdAndName;
import cloud.xcan.sdf.api.manager.UserManager;
import cloud.xcan.sdf.api.message.http.ResourceExisted;
import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.core.angustester.application.query.common.CommonQuery;
import cloud.xcan.sdf.core.angustester.application.query.scenario.ScenarioMonitorHistoryQuery;
import cloud.xcan.sdf.core.angustester.application.query.scenario.ScenarioMonitorQuery;
import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitor;
import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitorHistoryInfo;
import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitorRepo;
import cloud.xcan.sdf.core.angustester.domain.setting.NoticeSetting;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.event.EventSender;
import cloud.xcan.sdf.core.event.source.EventContent;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import cloud.xcan.sdf.core.pojo.principal.PrincipalContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class ScenarioMonitorQueryImpl implements ScenarioMonitorQuery {

  @Resource
  private ScenarioMonitorRepo scenarioMonitorRepo;

  @Resource
  private ScenarioMonitorHistoryQuery scenarioMonitorHistoryQuery;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private UserManager userManager;

  @Override
  public ScenarioMonitor detail(Long id) {
    return new BizTemplate<ScenarioMonitor>() {
      ScenarioMonitor monitorDb;

      @Override
      protected void checkParams() {
        monitorDb = checkAndFind(id);
      }

      @Override
      protected ScenarioMonitor process() {
        List<ScenarioMonitorHistoryInfo> histories =
            scenarioMonitorHistoryQuery.findInfoById(List.of(id));
        assembleScenarioMonitorCount0(monitorDb, histories);
        return monitorDb;
      }
    }.execute();
  }

  @Override
  public Page<ScenarioMonitor> find(GenericSpecification<ScenarioMonitor> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<ScenarioMonitor>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<ScenarioMonitor> process() {
        Page<ScenarioMonitor> page = scenarioMonitorRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          assembleScenarioMonitorCount(page);
        }
        return page;
      }
    }.execute();
  }

  @Override
  public ScenarioMonitor checkAndFind(Long id) {
    return scenarioMonitorRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "ScenarioMonitor"));
  }

  @Override
  public List<ScenarioMonitor> checkAndFind(Collection<Long> ids) {
    List<ScenarioMonitor> monitors = scenarioMonitorRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(monitors), ids.iterator().next(), "ScenarioMonitor");
    if (ids.size() != monitors.size()) {
      for (ScenarioMonitor monitor : monitors) {
        assertResourceNotFound(ids.contains(monitor.getId()), monitor.getId(), "ScenarioMonitor");
      }
    }
    return monitors;
  }

  @Override
  public void checkExits(Long projectId, String name) {
    long count = scenarioMonitorRepo.countByProjectIdAndName(projectId, name);
    if (count > 0) {
      throw ResourceExisted.of(name, "ScenarioMonitor");
    }
  }

  @Override
  public void assembleScenarioMonitorCount(Page<ScenarioMonitor> page) {
    Set<Long> monitorIds = page.getContent().stream().map(ScenarioMonitor::getId)
        .collect(Collectors.toSet());
    Map<Long, List<ScenarioMonitorHistoryInfo>> histories =
        scenarioMonitorHistoryQuery.findInfoById(monitorIds).stream()
            .collect(Collectors.groupingBy(ScenarioMonitorHistoryInfo::getMonitorId));
    for (ScenarioMonitor monitor : page.getContent()) {
      assembleScenarioMonitorCount0(monitor, histories.get(monitor.getId()));
    }
  }

  @Override
  public void assembleAndSendScenarioMonitorFailureNoticeEvent(ScenarioMonitor monitorDb) {
    if (nonNull(monitorDb.getCreatedBy())) {
      List<NoticeType> noticeTypes = commonQuery.findTenantEventNoticeTypes(monitorDb.getTenantId())
          .get(ScenarioMonitorFailedCode);
      if (isEmpty(noticeTypes)) {
        return;
      }
      List<Long> receiveObjectIds = new ArrayList<>();
      receiveObjectIds.add(monitorDb.getCreatedBy());
      NoticeSetting noticeSetting = monitorDb.getNoticeSetting();
      if (nonNull(noticeSetting) && noticeSetting.getEnabled()) {
        Set<Long> orgUserIds = userManager.getUserIdByOrgTypeIn0(noticeSetting.getOrgType(),
            noticeSetting.getOrgs().stream().map(IdAndName::getId).collect(Collectors.toSet()));
        receiveObjectIds.addAll(orgUserIds);
      }
      String message = message(ScenarioMonitorFailed, new Object[]{monitorDb.getName(),
          monitorDb.getFailureMessage()}, PrincipalContext.getDefaultLanguage().toLocale());
      EventContent event = assembleAngusTesterUserNoticeEvent(ScenarioMonitorFailedCode,
          message, SCENARIO_MONITOR.getValue(), monitorDb.getId().toString(), monitorDb.getName(),
          noticeTypes, receiveObjectIds);
      EventSender.CommonQueue.send(event);
    }
  }
}
