package cloud.xcan.angus.core.tester.application.cmd.project;

import cloud.xcan.angus.core.tester.domain.ExampleDataType;
import cloud.xcan.angus.core.tester.domain.project.Project;
import cloud.xcan.angus.core.tester.domain.project.ProjectType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;
import javax.annotation.Nullable;

public interface ProjectCmd {

  IdKey<Long, Object> add(Project project);

  IdKey<Long, Object> add0(Project project);

  void update(Project project);

  IdKey<Long, Object> replace(Project project);

  IdKey<Long, Object> importExample(String name, ProjectType type, Set<ExampleDataType> dataTypes);

  IdKey<Long, Object> importProjectExample(@Nullable String name, ProjectType type,
      Project project);

  void delete(Long id);

}
