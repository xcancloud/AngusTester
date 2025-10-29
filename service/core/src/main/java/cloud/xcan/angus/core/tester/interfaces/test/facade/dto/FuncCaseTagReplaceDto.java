package cloud.xcan.angus.core.tester.interfaces.test.facade.dto;

import static cloud.xcan.angus.api.commonlink.TesterConstant.MAX_TAGS_NUM;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FuncCaseTagReplaceDto {

  @Size(max = MAX_TAGS_NUM)
  @Schema(description = "Tag identifiers for test case categorization with clear functionality")
  private LinkedHashSet<Long> tagIds;

}
