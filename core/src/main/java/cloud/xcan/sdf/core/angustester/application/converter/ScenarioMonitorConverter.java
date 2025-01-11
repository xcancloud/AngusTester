package cloud.xcan.sdf.core.angustester.application.converter;

import static cloud.xcan.sdf.spec.utils.ObjectUtils.calcRate;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.sdf.spec.utils.WorkingTimeCalculator.isLast24Hour;
import static cloud.xcan.sdf.spec.utils.WorkingTimeCalculator.isLastMonth;
import static cloud.xcan.sdf.spec.utils.WorkingTimeCalculator.isLastWeek;

import cloud.xcan.angus.metrics.statistics.ListStatistics;
import cloud.xcan.sdf.core.angustester.domain.scenario.count.ScenarioMonitorCount;
import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitor;
import cloud.xcan.sdf.core.angustester.domain.scenario.monitor.ScenarioMonitorHistoryInfo;
import cloud.xcan.sdf.spec.unit.TimeValue;
import java.util.List;

public class ScenarioMonitorConverter {

  public static void setReplaceInfo(ScenarioMonitor monitorDb, ScenarioMonitor monitor) {
    monitorDb.setName(monitor.getName())
        .setDescription(monitor.getDescription())
        .setNextExecDate(monitor.getTimeSetting().getNextDate())
        .setCreatedAt(monitor.getTimeSetting().getCreatedAt())
        .setTimeSetting(monitor.getTimeSetting())
        .setServerSetting(monitor.getServerSetting())
        .setNoticeSetting(monitor.getNoticeSetting());
  }

  public static void assembleScenarioMonitorCount0(
      ScenarioMonitor monitorDb, List<ScenarioMonitorHistoryInfo> histories) {
    ScenarioMonitorCount count = new ScenarioMonitorCount();
    if (isNotEmpty(histories)) {
      count.setTotalNum(histories.size())
          .setSuccessNum(
              (int) histories.stream().filter(x -> x.getStatus().isSuccess()).count())
          .setFailureNum(
              (int) histories.stream().filter(x -> x.getStatus().isFailure()).count())
          .setSuccessRate(calcRate(count.getSuccessNum(), count.getTotalNum()))
          .setLast24HoursNum(
              (int) histories.stream().filter(x -> isLast24Hour(x.getCreatedDate())).count())
          .setLast24HoursSuccessNum(
              (int) histories.stream().filter(x -> x.getStatus().isSuccess()
                  && isLast24Hour(x.getCreatedDate())).count())
          .setLast24HoursSuccessRate(
              calcRate(count.getLast24HoursSuccessNum(), count.getLast24HoursNum()))
          .setLast7DayNum(
              (int) histories.stream().filter(x -> isLastWeek(x.getCreatedDate())).count())
          .setLast7DaySuccessNum(
              (int) histories.stream().filter(x -> x.getStatus().isSuccess()
                  && isLastWeek(x.getCreatedDate())).count())
          .setLast7DaySuccessRate(
              calcRate(count.getLast7DaySuccessNum(), count.getLast7DayNum()))
          .setLast30DayNum(
              (int) histories.stream().filter(x -> isLastMonth(x.getCreatedDate())).count())
          .setLast30DaySuccessNum(
              (int) histories.stream().filter(x -> x.getStatus().isSuccess()
                  && isLastMonth(x.getCreatedDate())).count())
          .setLast30DaySuccessRate(
              calcRate(count.getLast30DaySuccessNum(), count.getLast30DayNum()));

      ListStatistics scores = new ListStatistics();
      for (ScenarioMonitorHistoryInfo history : histories) {
        scores.addValue(nullSafe(history.getResponseDelay(), 0L));
      }
      count.setAvgDelayTime(TimeValue.ofMillisecond((long) scores.getMean()))
          .setMinDelayTime(TimeValue.ofMillisecond((long) scores.getMin()))
          .setMaxDelayTime(TimeValue.ofMillisecond((long) scores.getMax()))
          .setP50DelayTime(
              TimeValue.ofMillisecond((long) scores.getPercentile(50)))
          .setP75DelayTime(
              TimeValue.ofMillisecond((long) scores.getPercentile(75)))
          .setP90DelayTime(
              TimeValue.ofMillisecond((long) scores.getPercentile(90)));
    }
    monitorDb.setCount(count);
  }


}
