package cloud.xcan.angus.core.tester.interfaces.project;


import cloud.xcan.angus.core.tester.interfaces.project.facade.ProjectTrashFacade;
import cloud.xcan.angus.core.tester.interfaces.project.facade.dto.trash.ProjectTrashFindto;
import cloud.xcan.angus.core.tester.interfaces.project.facade.vo.trash.ProjectTrashDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ProjectTrash", description = "Project Recycle Bin Management - Temporary storage for "
    + "deleted projects with restore capabilities and permanent deletion controls")
@Validated
@RestController
@RequestMapping("/api/v1/project/trash")
public class ProjectTrashRest {

  @Resource
  private ProjectTrashFacade projectTrashFacade;

  @Operation(summary = "Clear the trash of project", operationId = "project:trash:clear")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cleared successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void clear(
      @Parameter(name = "id", description = "Trash id", required = true) @PathVariable("id") Long id) {
    projectTrashFacade.clear(id);
  }

  @Operation(summary = "Clear all the trash of project ", operationId = "project:trash:clear:all")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Cleared successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping()
  public void clearAll() {
    projectTrashFacade.clearAll();
  }

  @Operation(summary = "Back the project from the trash", operationId = "project:trash:back")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Backed successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/back")
  public ApiLocaleResult<?> back(
      @Parameter(name = "id", description = "Trash id", required = true) @PathVariable("id") Long id) {
    projectTrashFacade.back(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Back all the project from trash", operationId = "project:trash:back:all")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Backed successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/back")
  public ApiLocaleResult<?> backAll() {
    projectTrashFacade.backAll();
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the count of all project trash", operationId = "project:trash:count")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Query count succeeded")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/count")
  public ApiLocaleResult<Long> count() {
    return ApiLocaleResult.success(projectTrashFacade.count());
  }

  @Operation(summary = "Query trash list of the project", operationId = "project:trash:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ProjectTrashDetailVo>> list(
      @Valid @ParameterObject ProjectTrashFindto dto) {
    return ApiLocaleResult.success(projectTrashFacade.list(dto));
  }

}
