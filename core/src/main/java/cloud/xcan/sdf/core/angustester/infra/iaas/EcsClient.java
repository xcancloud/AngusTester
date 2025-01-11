package cloud.xcan.sdf.core.angustester.infra.iaas;

import cloud.xcan.sdf.api.pojo.node.NodeSpecData;
import cloud.xcan.sdf.core.angustester.domain.node.Node;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EcsClient {

  String queryMeetResourceSpecRegion(InstanceChargeType chargeType, NodeSpecData spec);

  Boolean hasAvailableResource(String regionId, InstanceChargeType chargeType, NodeSpecData spec);

  List<Node> purchaseAndRunInstances(Long orderId, LocalDateTime orderExpiredDate,
      Long tenantId, NodeSpecData spec, String regionId) throws Exception;

  void deleteInstances(String regionId, List<String> instanceIds) throws Exception;

  Map<String, Node> getInstancesDescribe(String regionId, List<String> instances) throws Exception;

  void stopInstances(String regionId, List<String> instanceIds) throws Exception;

  void restartInstances(String regionId, List<String> instanceIds) throws Exception;
}
