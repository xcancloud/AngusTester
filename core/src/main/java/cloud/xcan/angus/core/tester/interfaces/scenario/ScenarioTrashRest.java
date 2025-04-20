package cloud.xcan.angus.core.tester.interfaces.scenario;


import cloud.xcan.angus.core.tester.interfaces.scenario.facade.ScenarioTrashFacade;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.dto.trash.ScenarioTrashSearchDto;
import cloud.xcan.angus.core.tester.interfaces.scenario.facade.vo.trash.ScenarioTrashDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ScenarioTrash")
@Validated
@RestController
@RequestMapping("/api/v1/scenario/trash")
public class ScenarioTrashRest {

  @Resource
  private ScenarioTrashFacade trashScenarioFacade;

  @Operation(description = "Clear the trash of scenario or dir", operationId = "scenario:trash:clear")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cleared successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void clear(
      @Parameter(name = "id", description = "Trash id", required = true) @PathVariable("id") Long id) {
    trashScenarioFacade.clear(id);
  }

  @Operation(description = "Clear all the trash of scenario and dir ", operationId = "scenario:trash:clear:all")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cleared successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void clearAll(
      @RequestParam("projectId") @Parameter(name = "projectId", description = "Project id") Long projectId) {
    trashScenarioFacade.clearAll(projectId);
  }

  @Operation(description = "Back the scenario or dir from the trash", operationId = "scenario:trash:back")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Backed successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/back")
  public ApiLocaleResult<?> back(
      @Parameter(name = "id", description = "Trash id", required = true) @PathVariable("id") Long id) {
    trashScenarioFacade.back(id);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Back all the scenario and dir from trash", operationId = "scenario:trash:back:all")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Backed successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/back")
  public ApiLocaleResult<?> backAll(
      @RequestParam("projectId") @Parameter(name = "projectId", description = "Project id") Long projectId) {
    trashScenarioFacade.backAll(projectId);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the number of all scenario and dir trash", operationId = "scenario:trash:count")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Query number succeeded")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/count")
  public ApiLocaleResult<Long> count(
      @RequestParam("projectId") @Parameter(name = "projectId", description = "Project id") Long projectId) {
    return ApiLocaleResult.success(trashScenarioFacade.count(projectId));
  }

  @Operation(description = "Fulltext search the trash of scenario or dir", operationId = "scenario:trash:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<ScenarioTrashDetailVo>> search(
      @Valid ScenarioTrashSearchDto dto) {
    return ApiLocaleResult.success(trashScenarioFacade.search(dto));
  }

}
