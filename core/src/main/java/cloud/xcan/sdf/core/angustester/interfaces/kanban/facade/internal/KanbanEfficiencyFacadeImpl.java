package cloud.xcan.sdf.core.angustester.interfaces.kanban.facade.internal;

import cloud.xcan.sdf.core.angustester.application.query.kanban.KanbanEfficiencyQuery;
import cloud.xcan.sdf.core.angustester.domain.kanban.EfficiencyCaseOverview;
import cloud.xcan.sdf.core.angustester.domain.kanban.EfficiencyTaskOverview;
import cloud.xcan.sdf.core.angustester.interfaces.kanban.facade.KanbanEfficiencyFacade;
import cloud.xcan.sdf.core.angustester.interfaces.kanban.facade.dto.KanbanEfficiencyFindDto;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class KanbanEfficiencyFacadeImpl implements KanbanEfficiencyFacade {

  @Resource
  private KanbanEfficiencyQuery kanbanEfficiencyQuery;

  @Override
  public EfficiencyTaskOverview taskEfficiencyOverview(KanbanEfficiencyFindDto dto) {
    return kanbanEfficiencyQuery.taskEfficiencyOverview(dto.getCreatorObjectType(),
        dto.getCreatorObjectId(), dto.getProjectId(), dto.getPlanId(), dto.getCreatedDateStart(),
        dto.getCreatedDateEnd(), false, false, true, true);
  }

  @Override
  public EfficiencyCaseOverview caseEfficiencyOverview(KanbanEfficiencyFindDto dto) {
    return kanbanEfficiencyQuery.caseEfficiencyOverview(dto.getCreatorObjectType(),
        dto.getCreatorObjectId(), dto.getProjectId(), dto.getPlanId(), dto.getCreatedDateStart(),
        dto.getCreatedDateEnd(), true, false, false, true, true);
  }

}
