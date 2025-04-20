package cloud.xcan.angus.core.tester.interfaces.module.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ModuleVo {

  private Long id;

  private String name;

  private Long projectId;

  private Long pid;

  private Integer sequence;

  private boolean hasEditPermission = false;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

}
