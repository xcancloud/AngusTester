package cloud.xcan.sdf.api.angustester.script.vo;

import cloud.xcan.angus.model.script.configuration.ScriptType;
import cloud.xcan.sdf.model.script.ScriptSource;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author xiaolong.liu
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScriptInfoListVo {

  private Long id;

  private Long projectId;

  private Long serviceId;

  private String name;

  private ScriptType type;

  private ScriptSource source;

  private Long sourceId;

  private String sourceName;

  private Boolean authFlag;

  private String plugin;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  private LocalDateTime lastModifiedDate;

}
