package cloud.xcan.sdf.core.angustester.application.query.scenario;

import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitor;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ScenarioMonitorQuery {

  ScenarioMonitor detail(Long id);

  Page<ScenarioMonitor> find(GenericSpecification<ScenarioMonitor> spec, PageRequest pageable);

  void assembleScenarioMonitorCount(Page<ScenarioMonitor> page);

  ScenarioMonitor checkAndFind(Long id);

  List<ScenarioMonitor> checkAndFind(Collection<Long> ids);

  void checkExits(Long projectId, String name);

  void assembleAndSendScenarioMonitorFailureNoticeEvent(ScenarioMonitor monitorDb);

}
