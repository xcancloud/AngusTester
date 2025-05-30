package cloud.xcan.angus.api.tester.script;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.tester.script.dto.ScriptFindDto;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfoListVo;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfoVo;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfosVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${xcan.service.angustester:XCAN-ANGUSTESTER.BOOT}")
public interface ScriptInnerRemote {

  @Operation(summary = "Query the detail of script", operationId = "script:detail:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/innerapi/v1/script/{id}")
  ApiLocaleResult<ScriptDetailVo> detail(
      @Parameter(name = "id", description = "Script id", required = true) @PathVariable("id") Long id);

  @Operation(summary = "Query the info of script", operationId = "script:info:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/innerapi/v1/script/{id}/info")
  ApiLocaleResult<ScriptInfoVo> info(
      @Parameter(name = "id", description = "Script id", required = true) @PathVariable("id") Long id);

  @Operation(summary = "Query the info of scripts", operationId = "script:infos:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/innerapi/v1/script/info/byids")
  ApiLocaleResult<List<ScriptInfosVo>> infos(
      @Parameter(name = "ids", description = "Script ids", required = true)
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids);

  @Operation(summary = "Query the list of script info", operationId = "script:info:list:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/innerapi/v1/script/info")
  ApiLocaleResult<PageResult<ScriptInfoListVo>> list(@SpringQueryMap ScriptFindDto dto);

}
