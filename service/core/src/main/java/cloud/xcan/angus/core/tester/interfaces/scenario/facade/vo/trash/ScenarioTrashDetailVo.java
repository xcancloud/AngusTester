package cloud.xcan.angus.core.tester.interfaces.scenario.facade.vo.trash;



import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ScenarioTrashDetailVo {

  private Long id;

  private Long projectId;

  private String targetName;

  private Long targetId;

  private Long createdBy;

  private String createdByName;

  private String createdByAvatar;

  private Long deletedBy;

  private String deletedByName;

  private String deletedByAvatar;

  private LocalDateTime deletedDate;

}



