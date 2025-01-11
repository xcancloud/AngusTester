package cloud.xcan.sdf.core.angustester.application.cmd.task.impl;

import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.TASK;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.TASK_FAVOURITE_REPEATED;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static java.util.Objects.isNull;

import cloud.xcan.sdf.api.message.http.ResourceExisted;
import cloud.xcan.sdf.core.angustester.application.cmd.activity.ActivityCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.task.TaskFavouriteCmd;
import cloud.xcan.sdf.core.angustester.application.query.task.TaskQuery;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.task.TaskInfo;
import cloud.xcan.sdf.core.angustester.domain.task.favorite.TaskFavourite;
import cloud.xcan.sdf.core.angustester.domain.task.favorite.TaskFavouriteRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.cmd.CommCmd;
import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.spec.experimental.IdKey;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class TaskFavouriteCmdImpl extends CommCmd<TaskFavourite, Long> implements
    TaskFavouriteCmd {

  @Resource
  private TaskFavouriteRepo taskFavoriteRepo;

  @Resource
  private TaskQuery taskQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Override
  public IdKey<Long, Object> add(TaskFavourite favorite) {
    return new BizTemplate<IdKey<Long, Object>>() {
      TaskInfo taskDb;

      @Override
      protected void checkParams() {
        // Check the task existed
        taskDb = taskQuery.checkAndFindInfo(favorite.getTaskId());

        // Check the is not repeated
        if (taskFavoriteRepo.countByTaskIdAndCreatedBy(favorite.getTaskId(), getUserId()) > 0) {
          throw ResourceExisted.of(TASK_FAVOURITE_REPEATED, new Object[]{});
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        favorite.setProjectId(taskDb.getProjectId());
        IdKey<Long, Object> idKey = insert(favorite);

        // Add favourite task activity
        activityCmd.add(toActivity(TASK, taskDb, ActivityType.FAVOURITE));
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
        if (taskFavoriteRepo.deleteByTaskIdAndCreatedBy(id, getUserId()) > 0) {
          //Add cancel favourite api activity
          activityCmd.add(toActivity(TASK, taskDb, ActivityType.FAVOURITE_CANCEL));
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
          taskFavoriteRepo.deleteByCreatedBy(getUserId());
        } else {
          taskFavoriteRepo.deleteByProjectIdAndCreatedBy(projectId, getUserId());
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TaskFavourite, Long> getRepository() {
    return this.taskFavoriteRepo;
  }

}
