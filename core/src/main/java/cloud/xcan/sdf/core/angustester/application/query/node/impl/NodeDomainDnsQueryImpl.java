package cloud.xcan.sdf.core.angustester.application.query.node.impl;

import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.DOMAIN_DNS_NAME_REPEATED_T;

import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.core.angustester.application.query.node.NodeDomainDnsQuery;
import cloud.xcan.sdf.core.angustester.domain.node.dns.NodeDomainDns;
import cloud.xcan.sdf.core.angustester.domain.node.dns.NodeDomainDnsRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.ProtocolAssert;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class NodeDomainDnsQueryImpl implements NodeDomainDnsQuery {

  @Resource
  private NodeDomainDnsRepo nodeDomainDnsRepo;

  @Override
  public NodeDomainDns find(Long id) {
    return new BizTemplate<NodeDomainDns>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected NodeDomainDns process() {
        return nodeDomainDnsRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "NodeDomainDns"));
      }
    }.execute();
  }

  @Override
  public Page<NodeDomainDns> find(Specification<NodeDomainDns> spec, Pageable pageable) {
    return new BizTemplate<Page<NodeDomainDns>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<NodeDomainDns> process() {
        return nodeDomainDnsRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public NodeDomainDns checkAndFind(Long id) {
    return nodeDomainDnsRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "NodeDomainDns"));
  }

  @Override
  public void checkAddNameExists(Long domainId, String name) {
    // Include logic deleted project
    Long nameCount = nodeDomainDnsRepo.countByDomainIdAndName(domainId, name);
    ProtocolAssert.assertResourceExisted(nameCount < 1,
        DOMAIN_DNS_NAME_REPEATED_T, new Object[]{name});
  }

  @Override
  public void checkUpdateNameExists(Long id, Long domainId, String name) {
    // Include logic deleted project
    Long nameCount = nodeDomainDnsRepo.countByDomainIdAndNameAndIdNot(domainId, name, id);
    ProtocolAssert.assertResourceExisted(nameCount < 1,
        DOMAIN_DNS_NAME_REPEATED_T, new Object[]{name});
  }
}




