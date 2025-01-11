package cloud.xcan.sdf.core.angustester.application.query.apis.impl;

import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.SERVICE_SHARE_EXPIRED;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.SERVICE_SHARE_TOKEN_ERROR_T;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertUnauthorized;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static java.lang.String.format;

import cloud.xcan.sdf.api.gm.setting.SettingUserDoorRemote;
import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisShareQuery;
import cloud.xcan.sdf.core.angustester.application.query.services.ServicesSchemaQuery;
import cloud.xcan.sdf.core.angustester.domain.apis.share.ApisShare;
import cloud.xcan.sdf.core.angustester.domain.apis.share.ApisShareRepo;
import cloud.xcan.sdf.core.angustester.domain.services.schema.SchemaFormat;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import cloud.xcan.sdf.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.sdf.core.pojo.principal.PrincipalContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SummaryQueryRegister(name = "ApisShare", table = "apis_share",
    groupByColumns = {"created_date", "target_type"}
)
@Biz
public class ApisShareQueryImpl implements ApisShareQuery {

  @Resource
  private ApisShareRepo apisShareRepo;

  @Resource
  private ServicesSchemaQuery servicesSchemaQuery;

  @Resource
  private SettingUserDoorRemote settingUserDoorRemote;

  @Override
  public ApisShare detail(Long id) {
    return new BizTemplate<ApisShare>() {
      ApisShare shareDb;

      @Override
      protected void checkParams() {
        // Check the share exits
        shareDb = checkAndFind(id);
      }

      @Override
      protected ApisShare process() {
        shareDb.setUrl(format("%s?id=%s&pat=%s", shareDb.getBaseUrl(),
            shareDb.getId(), shareDb.getPat()));
        return shareDb;
      }
    }.execute();
  }

  @Override
  public Page<ApisShare> find(GenericSpecification<ApisShare> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<ApisShare>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<ApisShare> process() {
        return apisShareRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public ApisShare view(Long id, String pat) {
    return new BizTemplate<ApisShare>(false) {
      ApisShare shareDb;

      @Override
      protected void checkParams() {
        // Check the share exits
        shareDb = checkAndFind(id);
        // Check the spt(public token) authorization
        assertUnauthorized(pat.equals(shareDb.getPat()), SERVICE_SHARE_TOKEN_ERROR_T);
        // Check the share is valid
        assertTrue(shareDb.isNotExpired(), SERVICE_SHARE_EXPIRED);
      }

      @Override
      protected ApisShare process() {
        String openapi = servicesSchemaQuery.openapiDetail(shareDb.getServicesId(),
            shareDb.getApisIds(), SchemaFormat.json, false, true);
        shareDb.setOpenapi(openapi);
        // Relay optTenantId for door api
        PrincipalContext.setOptTenantId(shareDb.getTenantId());
        shareDb.setApiProxy(settingUserDoorRemote.proxyDetail().orElseContentThrow());
        return shareDb;
      }
    }.execute();
  }

  @Override
  public ApisShare checkAndFind(Long id) {
    return apisShareRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "ApisShare"));
  }

  @Override
  public List<ApisShare> checkAndFind(Collection<Long> ids) {
    List<ApisShare> shares = apisShareRepo.findAllById(ids);
    if (ids.size() != shares.size()) {
      ids.removeAll(shares.stream().map(ApisShare::getId).collect(Collectors.toSet()));
      assertResourceNotFound(isEmpty(ids), ids, "ApisShare");
    }
    return shares;
  }

}
