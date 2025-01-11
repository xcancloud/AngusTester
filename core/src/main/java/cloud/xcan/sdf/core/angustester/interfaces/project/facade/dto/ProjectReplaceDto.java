package cloud.xcan.sdf.core.angustester.interfaces.project.facade.dto;

import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_NAME_LENGTH;
import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_REMARK_LENGTH_X10;
import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_URL_LENGTH_X2;

import cloud.xcan.sdf.api.commonlink.tag.OrgTargetType;
import cloud.xcan.sdf.core.angustester.domain.project.ProjectType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@ApiModel
@Setter
@Getter
@Accessors(chain = true)
public class ProjectReplaceDto {

  @ApiModelProperty("Modify project id. Create a new project when the value is null")
  private Long id;

  @ApiModelProperty(value = "Create project type, default `AGILE`", example = "AGILE")
  private ProjectType type;

  @NotBlank
  @Length(max = DEFAULT_NAME_LENGTH)
  @ApiModelProperty(value = "Project name, must be unique", example = "DemoProject", required = true)
  private String name;

  @ApiModelProperty(value = "Project avatar")
  @Length(max = DEFAULT_URL_LENGTH_X2)
  private String avatar;

  @NotNull
  @ApiModelProperty(value = "Owner id", example = "1", required = true)
  private Long ownerId;

  @NotNull
  @ApiModelProperty(value = "Project start date, Determine the start times of the research and testing activities"
      + " to ensure completion within the project cycle.", example = "2023-06-10 00:00:00", required = true)
  private LocalDateTime startDate;

  @NotNull
  @ApiModelProperty(value = "Project deadline date, Determine the end times of the research and testing activities"
      + " to ensure completion within the project cycle.", example = "2029-06-20 00:00:00", required = true)
  private LocalDateTime deadlineDate;

  @ApiModelProperty(value = "Project description")
  @Length(max = DEFAULT_REMARK_LENGTH_X10)
  private String description;

  @NotNull
  @ApiModelProperty(value = "Project member type and ids", required = true)
  private LinkedHashMap<OrgTargetType, LinkedHashSet<Long>> memberTypeIds;

}




