package cloud.xcan.angus.core.tester.domain.scenario.auth;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ScenarioAuthCurrent {

  private boolean scenarioAuth;

  private LinkedHashSet<ScenarioPermission> permissions;

  public void addPermissions(Collection<ScenarioPermission> permissions0) {
    if (isEmpty(permissions0)) {
      return;
    }
    if (permissions == null) {
      permissions = new LinkedHashSet<>();
    }
    permissions.addAll(permissions0);
  }

}
