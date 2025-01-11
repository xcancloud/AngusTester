package cloud.xcan.sdf.core.angustester.application.cmd.node;

import cloud.xcan.sdf.api.commonlink.node.AgentInstallCmd;
import cloud.xcan.sdf.core.angustester.domain.node.Node;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface NodeCmd {

  List<IdKey<Long, Object>> add(List<Node> nodes);

  List<IdKey<Long, Object>> add0(List<Node> nodes);

  void update(List<Node> nodes);

  void rename(Long id, String trim);

  void stop(HashSet<Long> ids);

  void restart(HashSet<Long> ids);

  void enabled(List<Node> nodes);

  void delete(List<Node> nodes);

  void purchase(Long orderId, Long tenantId);

  void purchase(Node node, Long nodeNum);

  void renew(Long orderId, Long originalOrderId, Long tenantId);

  void syncInstanceInfo();

  void expireAndDeleteAliYunInstances();

  AgentInstallCmd agentInstall(Long id);

  void agentRestart(Long id);

  void agentAutoInstall();

  void testConnectionNodeConfig(String ip, Integer sshPort, String username, String passd);

  boolean testConnectionNodeConfig0(String ip, Integer sshPort,
      String username, String passd);


}




