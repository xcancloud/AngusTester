package cloud.xcan.sdf.core.angustester.interfaces.func.facade.dto.review;

import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_NAME_LENGTH_X2;
import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_REMARK_LENGTH_X10;
import static cloud.xcan.sdf.spec.experimental.BizConstant.MAX_ATTACHMENT_NUM_X2;

import cloud.xcan.sdf.api.pojo.Attachment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.LinkedHashSet;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@ApiModel
@Setter
@Getter
@Accessors(chain = true)
public class FuncReviewUpdateDto {

  @NotNull
  @ApiModelProperty(value = "Plan id", example = "1", required = true)
  private Long id;

  //@NotNull
  //@ApiModelProperty(value = "Plan id", example = "1", required = true)
  //private Long planId;

  @Length(max = DEFAULT_NAME_LENGTH_X2)
  @ApiModelProperty(value = "Review name, Brief Overview of the review, supporting up to 200 characters.", example = "Example review")
  private String name;

  @ApiModelProperty(value = "Review owner id", example = "1")
  private Long ownerId;

  @ApiModelProperty(value = "Review participant ids")
  private LinkedHashSet<Long> participantIds;

  @Size(max = MAX_ATTACHMENT_NUM_X2)
  @ApiModelProperty(value =
      "Review attachments. Additional documents and information, such as requirement specifications, reference materials and standards, "
          + "system architecture diagrams, testing specifications, technical documents, etc.")
  private List<Attachment> attachments;

  @Length(max = DEFAULT_REMARK_LENGTH_X10)
  @ApiModelProperty(value =
      "Other review information. This is the other description of the testing review. Additional details such as testing strategies, "
          + "risk assessment, and management. ")
  private String description;

}
