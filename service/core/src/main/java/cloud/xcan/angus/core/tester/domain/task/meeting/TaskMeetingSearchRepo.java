package cloud.xcan.angus.core.tester.domain.task.meeting;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TaskMeetingSearchRepo extends CustomBaseRepository<TaskMeeting> {

}
