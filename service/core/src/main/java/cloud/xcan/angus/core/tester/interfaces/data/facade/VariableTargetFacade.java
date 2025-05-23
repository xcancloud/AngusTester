package cloud.xcan.angus.core.tester.interfaces.data.facade;

import cloud.xcan.angus.core.tester.interfaces.data.facade.vo.variable.VariableDetailVo;
import cloud.xcan.angus.core.tester.interfaces.data.facade.vo.variable.VariableTargetVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface VariableTargetFacade {

  List<IdKey<Long, Object>> add(Long targetId, String targetType, LinkedHashSet<Long> variableIds);

  void delete(Long targetId, String targetType, HashSet<Long> variableIds);

  List<VariableDetailVo> list(Long targetId, String targetType);

  List<VariableTargetVo> listTarget(Long variableId);

  Map<String, String> valuePreview(Long targetId, String targetType);
}
