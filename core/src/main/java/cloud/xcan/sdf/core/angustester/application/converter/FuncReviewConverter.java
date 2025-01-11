package cloud.xcan.sdf.core.angustester.application.converter;

import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlanStatus;
import cloud.xcan.sdf.core.angustester.domain.func.review.FuncReview;

public class FuncReviewConverter {

  public static void setReplaceInfo(FuncReview review, FuncReview reviewDb) {
    review.setProjectId(reviewDb.getProjectId())
        .setPlanId(reviewDb.getPlanId())
        .setStatus(reviewDb.getStatus())
        .setTenantId(reviewDb.getTenantId())
        .setCreatedBy(reviewDb.getCreatedBy())
        .setCreatedDate(reviewDb.getCreatedDate());
  }

  public static FuncReview clone(FuncReview reviewDb) {
    return new FuncReview().setName(reviewDb.getName())
        .setProjectId(reviewDb.getProjectId())
        .setPlanId(reviewDb.getPlanId())
        .setStatus(FuncPlanStatus.PENDING)
        .setOwnerId(reviewDb.getOwnerId())
        .setParticipantIds(reviewDb.getParticipantIds())
        .setAttachments(reviewDb.getAttachments())
        .setDescription(reviewDb.getDescription());
  }
}
