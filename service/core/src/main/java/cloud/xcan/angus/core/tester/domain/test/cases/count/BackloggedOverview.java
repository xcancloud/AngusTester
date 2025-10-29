package cloud.xcan.angus.core.tester.domain.test.cases.count;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.tester.domain.task.count.AbstractOverview;
import cloud.xcan.angus.core.tester.domain.task.count.BackloggedCount;
import cloud.xcan.angus.core.tester.domain.task.count.BackloggedDetail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BackloggedOverview extends AbstractOverview {

  private Map<Long, UserInfo> testers = new HashMap<>();

  private BackloggedCount totalOverview = new BackloggedCount();

  private Map<Long, BackloggedCount> testersOverview = new LinkedHashMap<>();

  private String[] dataDetailTitles = new String[]{};
  private List<BackloggedDetail> dataDetails = new ArrayList<>();

}
