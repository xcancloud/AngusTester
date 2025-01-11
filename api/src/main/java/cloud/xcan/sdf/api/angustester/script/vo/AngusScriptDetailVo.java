package cloud.xcan.sdf.api.angustester.script.vo;


import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.model.script.configuration.ScriptType;
import cloud.xcan.sdf.api.NameJoinField;
import cloud.xcan.sdf.model.script.ScriptSource;
import io.swagger.annotations.ApiModel;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Valid
@ApiModel
@Setter
@Getter
@Accessors(chain = true)
public class AngusScriptDetailVo {

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

  private String description;

  private AngusScript content;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;

}



