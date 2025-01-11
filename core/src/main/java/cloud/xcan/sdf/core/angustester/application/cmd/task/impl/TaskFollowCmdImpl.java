package cloud.xcan.sdf.core.angustester.application.cmd.task.impl;

import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.TASK;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.TASK_FOLLOW_REPEATED;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static java.util.Objects.isNull;

import cloud.xcan.sdf.api.message.http.ResourceExisted;
import cloud.xcan.sdf.core.angustester.application.cmd.activity.ActivityCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.task.TaskFollowCmd;
import cloud.xcan.sdf.core.angustester.application.query.task.TaskQuery;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.task.TaskInfo;
import cloud.xcan.sdf.core.angustester.domain.task.follow.TaskFollow;
import cloud.xcan.sdf.core.angustester.domain.task.follow.TaskFollowRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.cmd.CommCmd;
import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.spec.experimental.IdKey;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class TaskFollowCmdImpl extends CommCmd<TaskFollow, Long> implements TaskFollowCmd {

  @Resource
  private TaskFollowRepo taskFollowRepo;

  @Resource
  private TaskQuery taskQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(TaskFollow follow) {
    return new BizTemplate<IdKey<Long, Object>>() {
      TaskInfo taskDb = null;

      @Override
      protected void checkParams() {
        // Check the task exists
        taskDb = taskQuery.checkAndFindInfo(follow.getTaskId());
        //  Check is not repeated
        if (taskFollowRepo.countByTaskIdAndCreatedBy(follow.getTaskId(), getUserId()) > 0) {
          throw ResourceExisted.of(TASK_FOLLOW_REPEATED, new Object[]{});
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        follow.setProjectId(taskDb.getProjectId());
        IdKey<Long, Object> idKey = insert(follow);

        // Add follow task activity
        activityCmd.add(toActivity(TASK, taskDb, ActivityType.FOLLOW));
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancel(Long id) {
    new BizTemplate<Void>() {
      TaskInfo taskDb;

      @Override
      protected void checkParams() {
        // Check the task existed
        taskDb = taskQuery.checkAndFindInfo(id);
      }

      @Override
      protected Void process() {
        if (taskFollowRepo.deleteByTaskIdAndCreatedBy(id, getUserId()) > 0) {
          // Add cancel follow api activity
          activityCmd.add(toActivity(TASK, taskDb, ActivityType.FOLLOW_CANCEL));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancelAll(Long projectId) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Void process() {
        if (isNull(projectId)) {
          taskFollowRepo.deleteByCreatedBy(getUserId());
        } else {
          taskFollowRepo.deleteByProjectIdAndCreatedBy(projectId, getUserId());
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TaskFollow, Long> getRepository() {
    return this.taskFollowRepo;
  }
}
