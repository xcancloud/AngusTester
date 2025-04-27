package cloud.xcan.angus.core.tester.interfaces.analysis.facade;


import cloud.xcan.angus.api.gm.analysis.dto.CustomizationSummaryDto;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryDefinitionVo;
import java.util.List;
import java.util.Map;

public interface AnalysisCustomizationFacade {

  SummaryQueryDefinitionVo definitions();

  Object summary(CustomizationSummaryDto dto);

  Map<String, Object> summary(List<CustomizationSummaryDto> dto);

}
