package cloud.xcan.sdf.core.angustester.domain.task.count;

import cloud.xcan.sdf.api.commonlink.user.UserInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgressOverview extends AbstractOverview{

  private Map<Long, UserInfo> assignees = new HashMap<>();

  private ProgressCount totalOverview = new ProgressCount();

  private LinkedHashMap<Long, ProgressCount> assigneesOverview = new LinkedHashMap<>();

  private String[] dataDetailTitles = new String[]{};
  private List<ProgressDetail> dataDetails = new ArrayList<>();

}
