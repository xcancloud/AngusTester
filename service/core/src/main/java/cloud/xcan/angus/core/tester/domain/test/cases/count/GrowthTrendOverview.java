package cloud.xcan.angus.core.tester.domain.test.cases.count;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.tester.domain.issue.count.AbstractOverview;
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
