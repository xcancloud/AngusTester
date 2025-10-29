package cloud.xcan.angus.core.tester.infra.search;

import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import cloud.xcan.angus.core.tester.domain.issue.trash.TaskTrash;
import cloud.xcan.angus.core.tester.domain.issue.trash.TaskTrashSearchRepo;
import org.springframework.stereotype.Repository;

@Repository
public class TaskTrashSearchRepoMysql extends SimpleSearchRepository<TaskTrash> implements
    TaskTrashSearchRepo {

}
