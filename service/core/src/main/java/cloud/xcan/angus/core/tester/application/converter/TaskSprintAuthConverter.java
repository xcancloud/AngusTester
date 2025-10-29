package cloud.xcan.angus.core.tester.application.converter;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.core.tester.domain.issue.sprint.TaskSprintPermission;
import cloud.xcan.angus.core.tester.domain.issue.sprint.auth.TaskSprintAuth;
import cloud.xcan.angus.idgen.UidGenerator;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class TaskSprintAuthConverter {

  @NotNull
  public static List<TaskSprintAuth> toTaskSprintAuths(Set<Long> creatorIds, Long sprintId
      , UidGenerator uidGenerator) {
    return creatorIds.stream()
        .map(creatorId -> new TaskSprintAuth().setId(uidGenerator.getUID())
            .setSprintId(sprintId)
            .setAuthObjectType(AuthObjectType.USER)
            .setAuthObjectId(creatorId)
            .setAuths(TaskSprintPermission.ALL)
            .setCreator(true))
        .toList();
  }

}
