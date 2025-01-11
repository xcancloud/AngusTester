package cloud.xcan.sdf.core.angustester.domain.task.summary;

import cloud.xcan.sdf.core.angustester.domain.task.sprint.TaskSprintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskSprintStatusSummary {

  private TaskSprintStatus status;

  private long total;

}
