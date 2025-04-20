package cloud.xcan.angus.core.tester.application.cmd.task.impl;


import static cloud.xcan.angus.api.commonlink.CombinedTargetType.TASK;
import static cloud.xcan.angus.core.tester.application.converter.ActivityConverter.toActivity;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.activity.ActivityCmd;
import cloud.xcan.angus.core.tester.application.cmd.task.TaskRemarkCmd;
import cloud.xcan.angus.core.tester.application.query.task.TaskQuery;
import cloud.xcan.angus.core.tester.application.query.task.TaskRemarkQuery;
import cloud.xcan.angus.core.tester.domain.activity.Activity;
import cloud.xcan.angus.core.tester.domain.activity.ActivityType;
import cloud.xcan.angus.core.tester.domain.task.TaskInfo;
import cloud.xcan.angus.core.tester.domain.task.remark.TaskRemark;
import cloud.xcan.angus.core.tester.domain.task.remark.TaskRemarkRepo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XiaoLong Liu
 */
@Biz
public class TaskRemarkCmdImpl extends CommCmd<TaskRemark, Long> implements TaskRemarkCmd {

  @Resource
  private TaskRemarkRepo taskRemarkRepo;

  @Resource
  private TaskRemarkQuery taskRemarkQuery;

  @Resource
  private TaskQuery taskQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Override
  public IdKey<Long, Object> add(TaskRemark remark) {
    return new BizTemplate<IdKey<Long, Object>>() {
      TaskInfo taskDb;

      @Override
      protected void checkParams() {
        // Check the task exists
        taskDb = taskQuery.checkAndFindInfo(remark.getTaskId());

        // Check the remarks quota
        taskRemarkQuery.checkAddQuota(remark.getTaskId(), 1);

        // Allow anyone to remark
      }

      @Override
      protected IdKey<Long, Object> process() {
        IdKey<Long, Object> idKey = insert(remark);

        Activity activity = toActivity(TASK, taskDb, ActivityType.TASK_REMARK_ADD);
        activityCmd.add(activity);

        taskQuery.assembleAndSendModifyNoticeEvent(taskDb, activity);
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      TaskRemark taskRemarkDb;
      TaskInfo taskDb;

      @Override
      protected void checkParams() {
        taskRemarkDb = taskRemarkQuery.checkAndFind(id);
        taskDb = taskQuery.checkAndFindInfo(taskRemarkDb.getTaskId());
      }

      @Override
      protected Void process() {
        // Delete remark
        taskRemarkRepo.deleteById(id);

        activityCmd.add(toActivity(TASK, taskDb, ActivityType.TASK_REMARK_DELETE));
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TaskRemark, Long> getRepository() {
    return this.taskRemarkRepo;
  }
}
