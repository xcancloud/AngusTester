package cloud.xcan.sdf.core.angustester.application.converter;

import static cloud.xcan.sdf.api.commonlink.TesterConstant.MAX_ACTIVITY_LENGTH;
import static cloud.xcan.sdf.spec.utils.DateUtils.DATE_TIME_FMT;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.sdf.api.commonlink.CombinedTargetType;
import cloud.xcan.sdf.api.commonlink.user.UserBase;
import cloud.xcan.sdf.core.angustester.domain.activity.Activity;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.func.cases.FuncCase;
import cloud.xcan.sdf.core.angustester.domain.tag.Tag;
import cloud.xcan.sdf.spec.locale.MessageHolder;
import java.util.List;
import java.util.stream.Collectors;

public class FuncActivityConverter extends ActivityConverter {

  public static Activity toModifyCaseActivity(boolean replaceFlag,
      boolean hasModifyTester, boolean hasModifyCaseTags, boolean hasModifyAttachments,
      FuncCase case0, FuncCase caseDb, UserBase testerDb, List<Tag> caseTagsDb) {
    Activity mainActivity = toActivity(CombinedTargetType.FUNC_CASE, caseDb, ActivityType.UPDATED);
    StringBuilder activity = new StringBuilder();
    StringBuilder safeActivity = new StringBuilder();

    boolean hasChanged = false;
    // Required params
    if (isNotEmpty(case0.getName()) && !case0.getName().equals(caseDb.getName())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.NAME_UPDATED.getDescMessageKey(), new Object[]{"", case0.getName()}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    }

    // Required params
    if (isNotEmpty(case0.getPriority())
        && !case0.getPriority().equals(caseDb.getPriority())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.PRIORITY.getDescMessageKey(), new Object[]{"", case0.getPriority()}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    }

    // Required params
    if (hasModifyTester && nonNull(caseDb.getTesterId())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.FUNC_TESTER.getDescMessageKey(),
          new Object[]{"", testerDb.getFullname()}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    }

    if (hasModifyCaseTags) {
      if (isNotEmpty(case0.getTagTargets())) {
        activity.append("<br/>").append(MessageHolder.message(
            ActivityType.TAG.getDescMessageKey(), new Object[]{"",
                caseTagsDb.stream().map(Tag::getName).collect(Collectors.joining(","))}));
        hasChanged = true;
        if (activity.length() < MAX_ACTIVITY_LENGTH) {
          safeActivity = activity;
        }
      } else {
        if (replaceFlag && isEmpty(case0.getTagTargets()) && isNotEmpty(caseDb.getTagTargets())) {
          activity.append("<br/>").append(MessageHolder.message(
              ActivityType.TAG_CLEAR.getDescMessageKey(), new Object[]{""}));
          hasChanged = true;
          if (activity.length() < MAX_ACTIVITY_LENGTH) {
            safeActivity = activity;
          }
        }
      }
    }

    // Required params
    if (isNotEmpty(case0.getDeadlineDate())
        && !case0.getDeadlineDate().equals(caseDb.getDeadlineDate())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.DEADLINE.getDescMessageKey(), new Object[]{"",
              case0.getDeadlineDate().format(DATE_TIME_FMT)}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    }

    if (hasModifyAttachments) {
      if (isNotEmpty(case0.getAttachments())) {
        activity.append("<br/>").append(MessageHolder.message(
            ActivityType.ATTACHMENT_UPDATED.getDescMessageKey(), new Object[]{"",
                case0.getAttachments().stream().map(x ->
                    "<a data-type=\"attachment\" data-href=\"" + x.getUrl() + "\">" + x.getName()
                        + "</a>").collect(Collectors.joining(","))}));
        hasChanged = true;
        if (activity.length() < MAX_ACTIVITY_LENGTH) {
          safeActivity = activity;
        }
      } else {
        if (replaceFlag && isEmpty(case0.getAttachments()) && isNotEmpty(caseDb.getAttachments())) {
          activity.append("<br/>").append(MessageHolder.message(
              ActivityType.ATTACHMENT_CLEAR.getDescMessageKey(), new Object[]{""}));
          hasChanged = true;
          if (activity.length() < MAX_ACTIVITY_LENGTH) {
            safeActivity = activity;
          }
        }
      }
    }

    if (isNotEmpty(case0.getEvalWorkload())
        && !case0.getEvalWorkload().equals(caseDb.getEvalWorkload())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.EVAL_WORKLOAD.getDescMessageKey(),
          new Object[]{caseDb.getName(), caseDb.getEvalWorkloadMethod(), case0.getEvalWorkload()}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    } else {
      if (replaceFlag && isNull(case0.getEvalWorkload()) && nonNull(caseDb.getEvalWorkload())) {
        activity.append("<br/>").append(MessageHolder.message(
            ActivityType.EVAL_WORKLOAD_CLEAR.getDescMessageKey(),
            new Object[]{caseDb.getName(), caseDb.getEvalWorkloadMethod()}));
        hasChanged = true;
        if (activity.length() < MAX_ACTIVITY_LENGTH) {
          safeActivity = activity;
        }
      }
    }

    if (isNotEmpty(case0.getActualWorkload())
        && !case0.getActualWorkload().equals(caseDb.getActualWorkload())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.EVAL_WORKLOAD.getDescMessageKey(),
          new Object[]{caseDb.getName(), caseDb.getEvalWorkloadMethod(),
              case0.getActualWorkload()}));
      hasChanged = true;
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    } else {
      if (replaceFlag && isNull(case0.getActualWorkload()) && nonNull(caseDb.getActualWorkload())) {
        activity.append("<br/>").append(MessageHolder.message(
            ActivityType.EVAL_WORKLOAD_CLEAR.getDescMessageKey(),
            new Object[]{caseDb.getName(), caseDb.getEvalWorkloadMethod()}));
        hasChanged = true;
        if (activity.length() < MAX_ACTIVITY_LENGTH) {
          safeActivity = activity;
        }
      }
    }

    if (isNotEmpty(case0.getDescription())
        && !case0.getDescription().equals(caseDb.getDescription())) {
      activity.append("<br/>").append(MessageHolder.message(
          ActivityType.DESCRIPTION_UPDATED.getDescMessageKey(), new Object[]{""}));
      if (activity.length() < MAX_ACTIVITY_LENGTH) {
        safeActivity = activity;
      }
    } else {
      if (replaceFlag && isEmpty(case0.getDescription()) && isNotEmpty(caseDb.getDescription())) {
        activity.append("<br/>").append(MessageHolder.message(
            ActivityType.DESCRIPTION_CLEAR.getDescMessageKey(), new Object[]{""}));
        hasChanged = true;
        if (activity.length() < MAX_ACTIVITY_LENGTH) {
          safeActivity = activity;
        }
      }
    }

    if (!hasChanged) {
      return mainActivity;
    }
    mainActivity.setDescription(mainActivity.getDescription() + safeActivity.toString());
    mainActivity.setDetail(mainActivity.getDetail() + safeActivity.toString());
    return mainActivity;
  }

}
