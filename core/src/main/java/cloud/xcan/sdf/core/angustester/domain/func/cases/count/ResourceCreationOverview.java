package cloud.xcan.sdf.core.angustester.domain.func.cases.count;

import cloud.xcan.sdf.api.commonlink.user.UserInfo;
import cloud.xcan.sdf.core.angustester.domain.task.count.AbstractOverview;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceCreationOverview extends AbstractOverview {

  private Map<Long, UserInfo> creators = new HashMap<>();

  private ResourceCreationCount totalOverview = new ResourceCreationCount();

  private Map<Long, ResourceCreationCount> creatorOverview = new LinkedHashMap<>();

  private String[] dataDetailTitles = new String[]{};
  private List<ResourceCreationDetail> dataDetails = new ArrayList<>();

}
