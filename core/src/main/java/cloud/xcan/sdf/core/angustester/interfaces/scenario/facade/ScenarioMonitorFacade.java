package cloud.xcan.sdf.core.angustester.interfaces.scenario.facade;

import cloud.xcan.sdf.api.PageResult;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.monitor.ScenarioMonitorAddDto;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.monitor.ScenarioMonitorFindDto;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.monitor.ScenarioMonitorReplaceDto;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.monitor.ScenarioMonitorSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.dto.monitor.ScenarioMonitorUpdateDto;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.vo.monitor.ScenarioMonitorDetailVo;
import cloud.xcan.sdf.core.angustester.interfaces.scenario.facade.vo.monitor.ScenarioMonitorListVo;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.Collection;

public interface ScenarioMonitorFacade {

  IdKey<Long, Object> add(ScenarioMonitorAddDto dto);

  void update(ScenarioMonitorUpdateDto dto);

  IdKey<Long, Object> replace(ScenarioMonitorReplaceDto dto);

  void runNow(Long id);

  void delete(Collection<Long> ids);

  ScenarioMonitorDetailVo detail(Long id);

  PageResult<ScenarioMonitorListVo> list(ScenarioMonitorFindDto dto);

  PageResult<ScenarioMonitorListVo> search(ScenarioMonitorSearchDto dto);

}
