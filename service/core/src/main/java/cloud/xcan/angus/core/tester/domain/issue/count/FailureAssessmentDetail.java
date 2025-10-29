package cloud.xcan.angus.core.tester.domain.issue.count;

import static java.lang.String.valueOf;

import cloud.xcan.angus.core.tester.domain.issue.BugLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FailureAssessmentDetail extends FailureAssessmentCount implements DataDetailBase {

  private String name;

  @JsonIgnore
  @Schema(description = "The number of failures by different levels", hidden = true)
  private LinkedHashMap<BugLevel, Integer> failureLevelCount = new LinkedHashMap<>();
  @JsonIgnore
  @Schema(description = "The rate of failures by different levels", hidden = true)
  private LinkedHashMap<BugLevel, Double> failureLevelRate = new LinkedHashMap<>();

  /**
   * The number and rate of failures by different levels.
   */
  private long criticalNum;
  private double criticalRate;
  private long majorNum;
  private double majorRate;
  private long minorNum;
  private double minorRate;
  private long trivialNum;
  private double trivialRate;

  @Override
  public String[] toValues() {
    return new String[]{
        name,
        valueOf(totalNum), valueOf(totalWorkload),
        valueOf(bugNum), valueOf(failureNum), valueOf(failureWorkload),
        valueOf(oneTimeFailureNum), valueOf(oneTimeFailureRate),
        valueOf(twoTimeFailureNum), valueOf(twoTimeFailureRate),
        valueOf(failureCompletedNum), valueOf(failureCompletedRate),
        valueOf(failureOverdueNum), valueOf(failureOverdueRate),
        valueOf(failureTotalTime), valueOf(failureAvgTime),
        valueOf(failureMinTime), valueOf(failureMaxTime),
        valueOf(criticalNum), valueOf(criticalRate),
        valueOf(majorNum), valueOf(majorRate),
        valueOf(minorNum), valueOf(minorRate),
        valueOf(trivialNum), valueOf(trivialRate)
    };
  }
}
