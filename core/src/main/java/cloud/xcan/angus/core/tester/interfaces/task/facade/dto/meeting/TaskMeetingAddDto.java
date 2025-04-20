package cloud.xcan.angus.core.tester.interfaces.task.facade.dto.meeting;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.tester.domain.task.meeting.TaskMeetingType;
import cloud.xcan.angus.validator.EditorContentLength;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid

@Setter
@Getter
@Accessors(chain = true)
public class TaskMeetingAddDto {

  @NotNull
  @Schema(description = "Project id", requiredMode = RequiredMode.REQUIRED)
  private Long projectId;

  @Schema(description = "Meeting sprint id")
  private Long sprintId;

  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Meeting subject.", requiredMode = RequiredMode.REQUIRED)
  private String subject;

  @NotNull
  @Schema(description = "Meeting type, Specify the type of meeting for identification and management purposes.", requiredMode = RequiredMode.REQUIRED)
  private TaskMeetingType type;

  @NotEmpty
  @Schema(description = "Meeting date, record the specific date of the meeting", requiredMode = RequiredMode.REQUIRED)
  private String date;

  @NotEmpty
  @Schema(description = "Meeting start and time, record the start and end times of the meeting", requiredMode = RequiredMode.REQUIRED)
  private String time;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "Meeting location, record the meeting venue or the link for online meetings")
  private String location;

  @NotNull
  @Schema(description = "Meeting moderator, specify the host of the meeting", requiredMode = RequiredMode.REQUIRED)
  private UserInfo moderator;

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Meeting participants, list the names of all participants", requiredMode = RequiredMode.REQUIRED)
  private List<UserInfo> participants;

  @NotEmpty
  @EditorContentLength(max = 6000)
  @Schema(description = "Meeting content, record the content of the meeting", requiredMode = RequiredMode.REQUIRED)
  private String content;
}
