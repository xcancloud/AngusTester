package cloud.xcan.angus.core.tester.domain.apis.auth;

import cloud.xcan.angus.api.commonlink.apis.ApiPermission;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.util.Collection;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ApisAuthCurrent {

  private boolean projectAuth;

  private boolean apisAuth;

  private LinkedHashSet<ApiPermission> permissions;

  public void addPermissions(Collection<ApiPermission> permissions0) {
    if (ObjectUtils.isEmpty(permissions0)) {
      return;
    }
    if (permissions == null) {
      permissions = new LinkedHashSet<>();
    }
    permissions.addAll(permissions0);
  }
}
