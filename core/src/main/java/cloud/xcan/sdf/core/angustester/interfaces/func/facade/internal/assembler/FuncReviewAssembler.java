package cloud.xcan.sdf.core.angustester.interfaces.func.facade.internal.assembler;

import static java.util.Objects.nonNull;

import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlanStatus;
import cloud.xcan.sdf.core.angustester.domain.func.review.FuncReview;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review.FuncReviewAddDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review.FuncReviewFindDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review.FuncReviewReplaceDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review.FuncReviewSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review.FuncReviewUpdateDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.vo.review.FuncReviewDetailVo;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import cloud.xcan.sdf.core.jpa.criteria.SearchCriteriaBuilder;
import java.util.Set;

public class FuncReviewAssembler {

  public static FuncReview addDtoToDomain(FuncReviewAddDto dto) {
    return new FuncReview()
        .setName(dto.getName())
        .setPlanId(dto.getPlanId())
        .setStatus(FuncPlanStatus.PENDING)
        .setOwnerId(dto.getOwnerId())
        .setParticipantIds(dto.getParticipantIds())
        .setAttachments(dto.getAttachments())
        .setDescription(dto.getDescription())
        .setCaseIds(dto.getCaseIds());
  }

  public static FuncReview updateDtoToDomain(FuncReviewUpdateDto dto) {
    return new FuncReview()
        .setId(dto.getId())
        .setName(dto.getName())
        //.setPlanId(dto.getPlanId())
        //.setStatus(dto.getStatus())
        .setOwnerId(dto.getOwnerId())
        .setParticipantIds(dto.getParticipantIds())
        .setAttachments(dto.getAttachments())
        .setDescription(dto.getDescription());
  }

  public static FuncReview replaceDtoToDomain(FuncReviewReplaceDto dto) {
    return new FuncReview()
        .setId(dto.getId())
        .setName(dto.getName())
        .setPlanId(nonNull(dto.getId()) ? null : dto.getPlanId())
        .setStatus(nonNull(dto.getId()) ? null : FuncPlanStatus.PENDING)
        .setOwnerId(dto.getOwnerId())
        .setParticipantIds(dto.getParticipantIds())
        .setAttachments(dto.getAttachments())
        .setDescription(dto.getDescription());
  }

  public static FuncReviewDetailVo toDetailVo(FuncReview review) {
    return new FuncReviewDetailVo()
        .setId(review.getId())
        .setName(review.getName())
        .setProjectId(review.getProjectId())
        .setPlanId(review.getPlanId())
        .setStatus(review.getStatus())
        .setOwnerId(review.getOwnerId())
        .setOwnerName(review.getOwnerName())
        .setOwnerAvatar(review.getOwnerAvatar())
        .setParticipants(review.getParticipants())
        .setAttachments(review.getAttachments())
        .setDescription(review.getDescription())
        .setTenantId(review.getTenantId())
        .setCreatedBy(review.getCreatedBy())
        .setCreatedDate(review.getCreatedDate())
        .setLastModifiedBy(review.getLastModifiedBy())
        .setLastModifiedDate(review.getLastModifiedDate())
        .setCaseNum(review.getCaseNum())
        .setProgress(review.getProgress());
  }

  public static GenericSpecification<FuncReview> getSpecification(FuncReviewFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "name", "ownerId", "createdBy", "createdDate", "lastModifiedBy")
        .matchSearchFields("name", "description")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(FuncReviewSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "name", "ownerId", "createdBy", "createdDate", "lastModifiedBy")
        .matchSearchFields("name", "description")
        .build();
  }
}
