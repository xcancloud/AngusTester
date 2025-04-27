package cloud.xcan.angus.core.tester.application.cmd.activity;

import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import cloud.xcan.angus.core.tester.domain.activity.Activity;
import java.util.Collection;
import java.util.List;

public interface ActivityCmd {

  void add(Activity activity);

  void addAll(Collection<Activity> activities);

  void deleteByTarget(CombinedTargetType targetType, List<Long> targetIds);

  void deleteByTaskIds(List<Long> taskIds);

}




