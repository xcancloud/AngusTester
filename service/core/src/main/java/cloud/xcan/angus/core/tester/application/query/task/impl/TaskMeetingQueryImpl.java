package cloud.xcan.angus.core.tester.application.query.task.impl;

import static cloud.xcan.angus.core.tester.application.converter.TaskMeetingConverter.getMeetingCreatorResourcesFilter;

import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.application.query.task.TaskMeetingQuery;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeeting;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeetingRepo;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeetingSearchRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class TaskMeetingQueryImpl implements TaskMeetingQuery {

  @Resource
  private TaskMeetingRepo taskMeetingRepo;

  @Resource
  private TaskMeetingSearchRepo taskMeetingSearchRepo;

  @Resource
  private UserManager userManager;

  @Override
  public TaskMeeting detail(Long id) {
    return new BizTemplate<TaskMeeting>() {

      @Override
      protected TaskMeeting process() {
        return checkAndFind(id);
      }
    }.execute();
  }

  @Override
  public Page<TaskMeeting> list(GenericSpecification<TaskMeeting> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<TaskMeeting>>() {
      @Override
      protected Page<TaskMeeting> process() {
        return fullTextSearch
            ? taskMeetingSearchRepo.find(spec.getCriteria(), pageable, TaskMeeting.class, match)
            : taskMeetingRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<TaskMeeting> findBySprintId(Long sprintId) {
    return taskMeetingRepo.findBySprintId(sprintId);
  }

  @Override
  public TaskMeeting checkAndFind(Long id) {
    return taskMeetingRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "TaskMeeting"));
  }

  @Override
  public List<TaskMeeting> getMeetingCreatedSummaries(Long projectId, Long sprintId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd, AuthObjectType creatorOrgType,
      Long creatorOrgId) {
    // Find all when condition is null, else find by condition
    Set<Long> creatorIds = Objects.isNull(creatorOrgType) ? null
        : userManager.getUserIdByOrgType0(creatorOrgType, creatorOrgId);
    Set<SearchCriteria> allFilters = getMeetingCreatorResourcesFilter(projectId, sprintId,
        createdDateStart, createdDateEnd, creatorIds);
    return taskMeetingRepo.findAllByFilters(allFilters);
  }
}
