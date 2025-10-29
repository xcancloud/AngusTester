package cloud.xcan.angus.core.tester.domain.test.cases.count;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.tester.domain.issue.count.AbstractOverview;
import cloud.xcan.angus.core.tester.domain.issue.count.OverdueAssessmentCount;
import cloud.xcan.angus.core.tester.domain.issue.count.OverdueAssessmentDetail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OverdueAssessmentOverview extends AbstractOverview {

  private Map<Long, UserInfo> testers = new HashMap<>();

  private OverdueAssessmentCount totalOverview = new OverdueAssessmentCount();

  private Map<Long, OverdueAssessmentCount> testersOverview = new LinkedHashMap<>();

  private String[] dataDetailTitles = new String[]{};
  private List<OverdueAssessmentDetail> dataDetails = new ArrayList<>();

}
