package cloud.xcan.angus.core.tester.infra.search;

import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeeting;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeetingSearchRepo;
import org.springframework.stereotype.Repository;

@Repository
public class TaskMeetingSearchRepoMysql extends SimpleSearchRepository<TaskMeeting> implements
    TaskMeetingSearchRepo {

}

