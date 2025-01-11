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
public class GrowthTrendOverview extends AbstractOverview {

  private Map<Long, UserInfo> testers = new HashMap<>();

  private GrowthTrendCount totalOverview = new GrowthTrendCount();

  private Map<Long, GrowthTrendCount> testersOverview = new LinkedHashMap<>();

  private String[] dataDetailTitles = new String[]{};
  private List<GrowthTrendDetail> dataDetails = new ArrayList<>();

}
