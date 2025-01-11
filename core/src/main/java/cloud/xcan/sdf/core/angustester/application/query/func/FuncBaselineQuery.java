package cloud.xcan.sdf.core.angustester.application.query.func;

import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaseline;
import cloud.xcan.sdf.core.angustester.domain.func.baseline.FuncBaselineInfo;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FuncBaselineQuery {

  FuncBaseline detail(Long id);

  Page<FuncBaselineInfo> find(GenericSpecification<FuncBaselineInfo> spec, PageRequest pageable);

  FuncBaseline checkAndFind(Long id);

  FuncBaselineInfo checkAndFindInfo(Long id);

  List<FuncBaseline> checkAndFind(Collection<Long> ids);

  List<FuncBaseline> getBaselineCreatedSummaries(Long projectId, Long planId,
      LocalDateTime createdDateStart, LocalDateTime createdDateEnd, AuthObjectType creatorOrgType,
      Long creatorOrgId);
}
