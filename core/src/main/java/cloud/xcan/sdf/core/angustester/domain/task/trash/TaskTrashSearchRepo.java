package cloud.xcan.sdf.core.angustester.domain.task.trash;

import cloud.xcan.sdf.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TaskTrashSearchRepo extends CustomBaseRepository<TaskTrash> {


}
