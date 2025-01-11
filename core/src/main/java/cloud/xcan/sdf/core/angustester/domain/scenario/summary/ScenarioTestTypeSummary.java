package cloud.xcan.sdf.core.angustester.domain.scenario.summary;

import cloud.xcan.angus.model.script.configuration.ScriptType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioTestTypeSummary {

  private ScriptType type;

  private long total;

}
