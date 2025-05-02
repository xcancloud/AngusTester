package cloud.xcan.angus.core.tester.interfaces.ctrl;

import static cloud.xcan.angus.remoting.common.node.ServerDiscovery.DISCOVERY_ENDPOINT_SUFFIX;
import static cloud.xcan.angus.remoting.common.node.ServerDiscovery.DISCOVERY_PING_ENDPOINT_SUFFIX;

import cloud.xcan.angus.core.tester.interfaces.ctrl.facade.CtrlFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remoting.common.node.DiscoveryNodeDto;
import cloud.xcan.angus.remoting.common.node.DiscoveryNodeVo;
import cloud.xcan.angus.remoting.common.router.ChannelRouter;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Election based on number of tasks being processed and ctrl node resource usage.
 */
@Tag(name = "Ctrl", description = "AngusCtrl Controller Status API - Controller health checks, leadership election, and agent connectivity monitoring")
@Validated
@RestController
@RequestMapping(value = "/api/v1/ctrl")
public class CtrlRest {

  @Resource
  private CtrlFacade ctrlFacade;

  @Operation(description = "Ping an controller for bind validator.", operationId = "ctrl:ping")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ping successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(DISCOVERY_PING_ENDPOINT_SUFFIX)
  public Principal ping() {
    return PrincipalContext.get();
  }

  @Operation(description = "Query and elect controller.", operationId = "ctrl:discovery")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(DISCOVERY_ENDPOINT_SUFFIX)
  public /*ApiLocaleResult<DiscoveryNodeVo>*/ DiscoveryNodeVo discovery(DiscoveryNodeDto dto) {
    return ctrlFacade.discovery(dto);
  }

  @Operation(description = "Query the connections info of controller.", operationId = "ctrl:connections:info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/connections/info")
  public ApiLocaleResult<List<ChannelRouter>> connectionsInfo() {
    return ApiLocaleResult.success(ctrlFacade.connectionsInfo());
  }

}
