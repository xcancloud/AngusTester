package cloud.xcan.sdf.core.angustester.application.cmd.comment.impl;

import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.FUNC_CASE;
import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.TASK;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivity;

import cloud.xcan.sdf.api.commonlink.CombinedTargetType;
import cloud.xcan.sdf.core.angustester.application.cmd.activity.ActivityCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.comment.CommentCmd;
import cloud.xcan.sdf.core.angustester.application.query.comment.CommentQuery;
import cloud.xcan.sdf.core.angustester.application.query.common.CommonQuery;
import cloud.xcan.sdf.core.angustester.application.query.func.FuncCaseQuery;
import cloud.xcan.sdf.core.angustester.application.query.task.TaskQuery;
import cloud.xcan.sdf.core.angustester.domain.CombinedTarget;
import cloud.xcan.sdf.core.angustester.domain.activity.Activity;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.comment.Comment;
import cloud.xcan.sdf.core.angustester.domain.comment.CommentRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.cmd.CommCmd;
import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.spec.utils.ObjectUtils;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class CommentCmdImpl extends CommCmd<Comment, Long> implements CommentCmd {

  @Resource
  private CommentRepo commentRepo;

  @Resource
  private CommentQuery commentQuery;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private TaskQuery taskQuery;

  @Resource
  private FuncCaseQuery funcCaseQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Comment add(Comment comment) {
    return new BizTemplate<Comment>() {
      int commentNum;
      CombinedTargetType targetType;
      CombinedTarget combinedTarget;

      @Override
      protected void checkParams() {
        commentNum = commentQuery.checkQuota(comment);
        targetType = CombinedTargetType.valueOf(comment.getTargetType());
        combinedTarget = commonQuery.checkAndGetCombinedTarget(targetType, comment.getTargetId(),
            false);
      }

      @Override
      protected Comment process() {
        comment.setId(uidGenerator.getUID());
        if (comment.isRootComment()) {
          comment.setLevel(1);
        } else {
          Comment pComment = commentQuery.checkAndFind(comment.getPid());
          comment.setLevel(pComment.getLevel() + 1);
        }
        insert0(comment);

        // Calculate the number of latest comments for web
        comment.setTotalCommentNum(commentNum + 1);

        // Add activity and comment notice event
        if (targetType.isTask()) {
          Activity activity = toActivity(TASK, combinedTarget, ActivityType.COMMENT_ADD,
              comment.getContent());
          activityCmd.add(activity);
          taskQuery.assembleAndSendModifyNoticeEvent(combinedTarget.getTaskInfo(), activity);
        } else if (targetType.isFuncCase()) {
          Activity activity = toActivity(FUNC_CASE, combinedTarget, ActivityType.COMMENT_ADD,
              comment.getContent());
          activityCmd.add(activity);
          funcCaseQuery.assembleAndSendModifyNoticeEvent(combinedTarget.getCaseInfo(), activity);
        }
        return comment;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Comment commentDb;

      @Override
      protected void checkParams() {
        commentDb = commentQuery.checkAndFind(id);
        commentQuery.checkDeletePermission(commentDb);
      }

      @Override
      protected Void process() {
        commentRepo.deleteById(id);

        List<Comment> subComments = commentRepo.findAllByPid(id);
        deleteSubComment(subComments);

        // Add activity and comment notice event
        CombinedTargetType targetType = CombinedTargetType.valueOf(commentDb.getTargetType());
        try {
          CombinedTarget combinedTarget = commonQuery.checkAndGetCombinedTarget(targetType,
              commentDb.getTargetId(), false);
          if (targetType.isTask()) {
            Activity activity = toActivity(TASK, combinedTarget, ActivityType.COMMENT_DELETE,
                commentDb.getContent());
            activityCmd.add(activity);
            taskQuery.assembleAndSendModifyNoticeEvent(combinedTarget.getTaskInfo(), activity);
          } else if (targetType.isFuncCase()) {
            Activity activity = toActivity(FUNC_CASE, combinedTarget, ActivityType.COMMENT_DELETE,
                commentDb.getContent());
            activityCmd.add(activity);
            funcCaseQuery.assembleAndSendModifyNoticeEvent(combinedTarget.getCaseInfo(), activity);
          }
        } catch (Exception e) {
          // NOOP: Target resource not found
        }
        return null;
      }
    }.execute();
  }

  private void deleteSubComment(List<Comment> subComments) {
    commentRepo.deleteAll(subComments);
    for (Comment comment : subComments) {
      List<Comment> allByPid = commentRepo.findAllByPid(comment.getId());
      if (ObjectUtils.isNotEmpty(allByPid)) {
        deleteSubComment(allByPid);
      }
    }
  }

  @Override
  protected BaseRepository<Comment, Long> getRepository() {
    return this.commentRepo;
  }
}
