package cloud.xcan.angus.core.tester.domain.test.summary;

import cloud.xcan.angus.core.tester.domain.test.cases.count.FuncTesterCount;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class FuncTesterWorkSummary {

  private Long testerId;

  private String testerName;

  private String testerAvatar;

  private FuncTesterCount count;

  private Map<String, List<FuncCaseSummary>> groupByDay = new HashMap<>();

}
