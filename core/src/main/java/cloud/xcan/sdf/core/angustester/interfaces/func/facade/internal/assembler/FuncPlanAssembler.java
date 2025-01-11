package cloud.xcan.sdf.core.angustester.interfaces.func.facade.internal.assembler;

import static cloud.xcan.sdf.spec.utils.ObjectUtils.nullSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlan;
import cloud.xcan.sdf.core.angustester.domain.func.plan.FuncPlanStatus;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanAddDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanFindDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanReplaceDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanSearchDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.plan.FuncPlanUpdateDto;
import cloud.xcan.sdf.core.angustester.interfaces.func.facade.vo.plan.FuncPlanDetailVo;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import cloud.xcan.sdf.core.jpa.criteria.SearchCriteriaBuilder;
import java.util.Set;

public class FuncPlanAssembler {

  public static FuncPlan addDtoToDomain(FuncPlanAddDto dto) {
    return new FuncPlan()
        .setProjectId(dto.getProjectId())
        .setName(dto.getName())
        .setAuthFlag(nullSafe(dto.getAuthFlag(), false))
        .setStatus(FuncPlanStatus.PENDING)
        .setStartDate(dto.getStartDate())
        .setDeadlineDate(dto.getDeadlineDate())
        .setOwnerId(dto.getOwnerId())
        .setTesterResponsibilities(dto.getTesterResponsibilities())
        .setTestingScope(dto.getTestingScope())
        .setTestingObjectives(dto.getTestingObjectives())
        .setAcceptanceCriteria(dto.getAcceptanceCriteria())
        .setOtherInformation(dto.getOtherInformation())
        .setAttachments(dto.getAttachments())
        .setCasePrefix(dto.getCasePrefix())
        .setReviewFlag(dto.getReviewFlag())
        .setEvalWorkloadMethod(dto.getEvalWorkloadMethod())
        .setDeletedFlag(false);
  }

  public static FuncPlan updateDtoToDomain(FuncPlanUpdateDto dto) {
    return new FuncPlan()
        .setId(dto.getId())
        //.setProjectId(dto.getProjectId())
        .setName(dto.getName())
        //.setStatus(dto.getStatus())
        .setStartDate(dto.getStartDate())
        .setDeadlineDate(dto.getDeadlineDate())
        .setOwnerId(dto.getOwnerId())
        .setTesterResponsibilities(dto.getTesterResponsibilities())
        .setTestingScope(dto.getTestingScope())
        .setTestingObjectives(dto.getTestingObjectives())
        .setAcceptanceCriteria(dto.getAcceptanceCriteria())
        .setOtherInformation(dto.getOtherInformation())
        .setAttachments(dto.getAttachments())
        // Not allowed modification
        //.setCasePrefix(dto.getCasePrefix())
        .setReviewFlag(dto.getReviewFlag())
        .setEvalWorkloadMethod(dto.getEvalWorkloadMethod());
  }

  public static FuncPlan replaceDtoToDomain(FuncPlanReplaceDto dto) {
    return new FuncPlan()
        .setId(dto.getId())
        // Modifying directory not allowed
        .setProjectId(nonNull(dto.getId()) ? null : dto.getProjectId())
        .setName(dto.getName())
        .setAuthFlag(nonNull(dto.getId()) ? null : nullSafe(dto.getAuthFlag(), false))
        .setStatus(nonNull(dto.getId()) ? null : FuncPlanStatus.PENDING)
        .setStartDate(dto.getStartDate())
        .setDeadlineDate(dto.getDeadlineDate())
        .setOwnerId(dto.getOwnerId())
        .setTesterResponsibilities(dto.getTesterResponsibilities())
        .setTestingScope(dto.getTestingScope())
        .setTestingObjectives(dto.getTestingObjectives())
        .setAcceptanceCriteria(dto.getAcceptanceCriteria())
        .setOtherInformation(dto.getOtherInformation())
        .setAttachments(dto.getAttachments())
        // Modifying prefix not allowed
        .setCasePrefix(nonNull(dto.getId()) ? null : dto.getCasePrefix())
        .setReviewFlag(dto.getReviewFlag())
        .setEvalWorkloadMethod(dto.getEvalWorkloadMethod())
        .setDeletedFlag(nonNull(dto.getId()) ? null : false);
  }

  public static FuncPlanDetailVo toDetailVo(FuncPlan plan) {
    return new FuncPlanDetailVo()
        .setId(plan.getId())
        .setProjectId(plan.getProjectId())
        .setName(plan.getName())
        .setStatus(plan.getStatus())
        .setStartDate(plan.getStartDate())
        .setDeadlineDate(plan.getDeadlineDate())
        .setOwnerId(plan.getOwnerId())
        .setOwnerName(plan.getOwnerName())
        .setOwnerAvatar(plan.getOwnerAvatar())
        .setTesterResponsibilities(plan.getTesterResponsibilities())
        .setMembers(plan.getMembers())
        .setTestingScope(plan.getTestingScope())
        .setTestingObjectives(plan.getTestingObjectives())
        .setAcceptanceCriteria(plan.getAcceptanceCriteria())
        .setOtherInformation(plan.getOtherInformation())
        .setAttachments(plan.getAttachments())
        .setCasePrefix(plan.getCasePrefix())
        .setReviewFlag(plan.getReviewFlag())
        .setEvalWorkloadMethod(plan.getEvalWorkloadMethod())
        .setAuthFlag(plan.getAuthFlag())
        .setTenantId(plan.getTenantId())
        .setCreatedBy(plan.getCreatedBy())
        .setCreatedDate(plan.getCreatedDate())
        .setLastModifiedBy(plan.getLastModifiedBy())
        .setLastModifiedDate(plan.getLastModifiedDate())
        .setCaseNum(plan.getCaseNum())
        .setValidCaseNum(plan.getValidCaseNum())
        .setProgress(plan.getProgress());
  }

  public static GenericSpecification<FuncPlan> getSpecification(FuncPlanFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "startDate", "deadlineDate")
        .orderByFields("id", "name", "ownerId", "createdBy", "createdDate", "lastModifiedBy"
            , "startDate", "deadlineDate")
        .matchSearchFields("name", "other_information")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(FuncPlanSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "startDate", "deadlineDate")
        .orderByFields("id", "name", "ownerId", "createdBy", "createdDate", "lastModifiedBy"
            , "startDate", "deadlineDate")
        .matchSearchFields("name", "other_information")
        .build();
  }
}
