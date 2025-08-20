package cloud.xcan.angus.core.tester.application.query.activity.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.core.tester.application.converter.ActivityConverter;
import cloud.xcan.angus.core.tester.application.query.activity.ActivityQuery;
import cloud.xcan.angus.core.tester.application.query.project.ProjectQuery;
import cloud.xcan.angus.core.tester.domain.activity.Activity;
import cloud.xcan.angus.core.tester.domain.activity.ActivityListRepo;
import cloud.xcan.angus.core.tester.domain.activity.ActivityRepo;
import cloud.xcan.angus.core.tester.domain.activity.ActivitySearchRepo;
import cloud.xcan.angus.core.tester.domain.activity.summary.ActivitySummary;
import cloud.xcan.angus.core.tester.domain.project.Project;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of activity query operations for activity management and reporting.
 *
 * <p>This class provides comprehensive functionality for querying and retrieving
 * activity data, including pagination, full-text search, and summary generation.</p>
 *
 * <p>It handles activity data enrichment with project names and user information,
 * supporting both detailed activity queries and summary statistics.</p>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Activity pagination with specification-based filtering</li>
 *   <li>Full-text search capabilities for activity content</li>
 *   <li>Activity data enrichment (project names, user information)</li>
 *   <li>Activity summary generation by target</li>
 *   <li>Activity count statistics by main target</li>
 *   <li>Summary query registration for reporting</li>
 * </ul></p>
 *
 * @author XiaoLong Liu
 */
@Biz
@SummaryQueryRegister(name = "Activity", table = "activity", groupByColumns = {"opt_date",
    "target_type"})
public class ActivityQueryImpl implements ActivityQuery {

  @Resource
  private ActivityRepo activityRepo;

  @Resource
  private ActivityListRepo activityListRepo;

  @Resource
  private ActivitySearchRepo activitySearchRepo;

  @Resource
  private ProjectQuery projectQuery;

  @Resource
  private UserManager userManager;

  /**
   * Finds activities with pagination, filtering, and optional full-text search.
   *
   * <p>This method retrieves activities based on specification criteria with support
   * for pagination and optional full-text search capabilities.</p>
   *
   * <p>The method automatically enriches activity data with project names and
   * user information for enhanced display.</p>
   *
   * @param spec the specification for filtering activities
   * @param pageable the pagination and sorting parameters
   * @param fullTextSearch whether to use full-text search
   * @param match the full-text search match fields
   * @return a page of activities with enriched data
   */
  @Override
  public Page<Activity> find(GenericSpecification<Activity> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Activity>>() {

      @Override
      protected Page<Activity> process() {
        // Execute activity query with full-text search or standard search
        Page<Activity> page = fullTextSearch
            ? activitySearchRepo.find(spec.getCriteria(), pageable, Activity.class, match)
            : activityListRepo.find(spec.getCriteria(), pageable, Activity.class, null);

        // Enrich activity data with project names and user information if content exists
        if (page.hasContent()) {
          setProjectName(page);
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }

  /**
   * Enriches activities with project names for enhanced display.
   *
   * <p>This method retrieves project information for all activities in the page
   * and sets the project name for each activity. Activities without associated
   * projects are marked with "--".</p>
   *
   * @param page the page of activities to enrich with project names
   */
  @Override
  public void setProjectName(Page<Activity> page) {
    // Retrieve project information for all activities in the page
    Map<Long, Project> projectMap = projectQuery.find0ById(page.getContent()
            .stream().map(Activity::getProjectId).collect(Collectors.toSet()))
        .stream().collect(Collectors.toMap(Project::getId, p -> p));

    // Set project name for each activity or mark as unknown if project not found
    for (Activity activity : page.getContent()) {
      if (projectMap.containsKey(activity.getProjectId())) {
        activity.setProjectName(projectMap.get(activity.getProjectId()).getName());
      } else {
        activity.setProjectName("--");
      }
    }
  }

  /**
   * Finds activity summaries for a specific target with user information enrichment.
   *
   * <p>This method retrieves all activities for a given target and converts them
   * to summary format with enriched user information.</p>
   *
   * <p>The method returns null if no activities are found for the target.</p>
   *
   * @param targetType the type of target to search for
   * @param targetId the ID of the target
   * @return list of activity summaries or null if no activities found
   */
  @Override
  public List<ActivitySummary> findSummaryByTarget(CombinedTargetType targetType, Long targetId) {
    // Retrieve all activities for the specified target
    List<Activity> activities = activityRepo.findByTargetTypeAndTargetId(targetType, targetId);
    if (isEmpty(activities)) {
      return null;
    }

    // Enrich activities with user information
    userManager.setUserNameAndAvatar(activities, "userId", "fullName", "avatar");

    // Convert activities to summary format
    return activities.stream().map(ActivityConverter::toActivitySummary)
        .toList();
  }

  /**
   * Gets the total number of activities for a main target.
   *
   * <p>This method counts all activities associated with a main target,
   * providing statistics for activity volume analysis.</p>
   *
   * @param id the main target ID
   * @return the total number of activities for the main target
   */
  @Override
  public int getActivityNumByMainTarget(Long id) {
    // Count all activities associated with the main target
    return activityRepo.countAllByMainTargetId(id);
  }

}
