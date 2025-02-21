package cloud.xcan.sdf.core.angustester.application.query.apis.impl;

import static cloud.xcan.sdf.api.commonlink.TesterConstant.MAX_API_CASE_NUM;
import static cloud.xcan.sdf.api.search.SearchCriteria.equal;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.APIS_CASE_NAME_REPEATED_T;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.APIS_CASE_OVER_LIMIT_CODE;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.APIS_CASE_OVER_LIMIT_T;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.sdf.spec.experimental.BizConstant.DEFAULT_NAME_LENGTH_X4;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import cloud.xcan.angus.model.element.http.ApisCaseType;
import cloud.xcan.sdf.api.manager.UserManager;
import cloud.xcan.sdf.api.message.http.ResourceExisted;
import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.application.converter.ApisConverter;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisCaseQuery;
import cloud.xcan.sdf.core.angustester.application.query.services.ServicesCompQuery;
import cloud.xcan.sdf.core.angustester.domain.apis.Apis;
import cloud.xcan.sdf.core.angustester.domain.apis.ApisBaseInfo;
import cloud.xcan.sdf.core.angustester.domain.apis.ApisBaseInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.apis.cases.ApisCase;
import cloud.xcan.sdf.core.angustester.domain.apis.cases.ApisCaseInfo;
import cloud.xcan.sdf.core.angustester.domain.apis.cases.ApisCaseInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.apis.cases.ApisCaseRepo;
import cloud.xcan.sdf.core.angustester.domain.services.comp.ServicesComp;
import cloud.xcan.sdf.core.angustester.infra.util.RefResolver;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.exception.QuotaException;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class ApisCaseQueryImpl implements ApisCaseQuery {

  private static final Logger log = LoggerFactory.getLogger(ApisCaseQueryImpl.class);
  @Resource
  private ApisCaseRepo apisCaseRepo;

  @Resource
  private ApisCaseInfoRepo apisCaseInfoRepo;

  @Resource
  private ApisBaseInfoRepo apisBaseInfoRepo;

  @Resource
  private ServicesCompQuery servicesCompQuery;

  @Resource
  private UserManager userManager;

  @Override
  public ApisCase detail(Long id) {
    return new BizTemplate<ApisCase>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected ApisCase process() {
        ApisCase apisCase = apisCaseRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "ApisCase"));

        // Set tags
        List<ApisCase> cases = List.of(apisCase);

        // Set apis deleted flag
        setApisAndServiceInfo(cases);

        // Set user name and avatar
        userManager.setUserNameAndAvatar(cases, "createdBy");
        return apisCase;
      }
    }.execute();
  }

  @Override
  public Page<ApisCaseInfo> list(GenericSpecification<ApisCaseInfo> spec, Pageable pageable) {
    return new BizTemplate<Page<ApisCaseInfo>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Page<ApisCaseInfo> process() {
        Set<SearchCriteria> criteria = spec.getCriterias();
        criteria.add(equal("apisDeletedFlag", false));

        // Assemble mainClass table
        Page<ApisCaseInfo> page = apisCaseInfoRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          // Set apis deleted flag
          setInfoApisNameAndDeleted(page.getContent());
          // Set user name and avatar
          userManager.setUserNameAndAvatar(page.getContent(), "createdBy");
        }
        return page;
      }
    }.execute();
  }

  @Override
  public List<ApisCase> findByApisId(Long apisId) {
    return apisCaseRepo.findByApisId(apisId);
  }

  @Override
  public List<ApisCase> findByServicesIdAndType(Long servicesId, ApisCaseType caseType) {
    return apisCaseRepo.findByServicesIdAndType(servicesId, caseType);
  }

  @Override
  public Long countByServiceId(Long servicesId) {
    return apisCaseRepo.countByServicesId(servicesId);
  }

  @Override
  public void checkCaseNameExists(Apis apisDb, List<ApisCase> cases) {
    List<String> existedNames = apisCaseRepo.findNamesByNameInAndApisId(
        cases.stream().map(ApisCase::getName).collect(Collectors.toSet()), apisDb.getId());
    if (!existedNames.isEmpty()) {
      throw ResourceExisted.of(APIS_CASE_NAME_REPEATED_T, new Object[]{existedNames.get(0)});
    }
  }

  @Override
  public void checkAndSafeUpdateNameExists(Apis apisDb, List<ApisCase> cases) {
    Set<String> updateNames = cases.stream().map(ApisCase::getName).collect(Collectors.toSet());
    if (isNotEmpty(updateNames)) {
      Map<String, ApisCaseInfo> caseInfosMap = apisCaseInfoRepo.findAllByNameInAndApisId(
              updateNames, apisDb.getId()).stream()
          .collect(Collectors.toMap(ApisCaseInfo::getName, x -> x));
      if (isEmpty(caseInfosMap)) {
        return;
      }
      for (ApisCase case0 : cases) {
        if (caseInfosMap.containsKey(case0.getName())
            && !caseInfosMap.get(case0.getName()).getId().equals(case0.getId())) {
          throw ResourceExisted.of(APIS_CASE_NAME_REPEATED_T, new Object[]{case0.getName()});
        }
      }
    }
  }

  @Override
  public ApisCase checkAndFind(Long id) {
    return apisCaseRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "ApisCase"));
  }

  @Override
  public ApisCaseInfo checkAndFindInfo(Long id) {
    return apisCaseInfoRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "ApisCase"));
  }

  @Override
  public List<ApisCase> checkAndFind(Collection<Long> ids) {
    List<ApisCase> cases = apisCaseRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(cases), ids.iterator().next(), "ApisCase");
    if (ids.size() != cases.size()) {
      for (ApisCase case0 : cases) {
        assertResourceNotFound(ids.contains(case0.getId()), case0.getId(), "ApisCase");
      }
    }
    return cases;
  }

  @Override
  public List<ApisCaseInfo> checkAndFindInfo(Collection<Long> ids) {
    List<ApisCaseInfo> cases = apisCaseInfoRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(cases), ids.iterator().next(), "ApisCase");
    if (ids.size() != cases.size()) {
      for (ApisCaseInfo case0 : cases) {
        assertResourceNotFound(ids.contains(case0.getId()), case0.getId(), "ApisCase");
      }
    }
    return cases;
  }

  @Override
  public void checkExistedCaseType(Long apisId, List<ApisCase> cases){
    if (isNotEmpty(cases)) {
      for (ApisCase aCase : cases) {
        assertResourceExisted(!aCase.getType().isUnique()
            || !apisCaseInfoRepo.existsByApisIdAndType(apisId, aCase.getType()),
            String.format("Apis[%s] %s type case is Existed", apisId, aCase.getType().getValue()));
      }
    }
  }

  @Override
  public void checkUpdateNameExists(Long planId, String name, Long id) {
    assertResourceExisted(isEmpty(name)
            || apisCaseRepo.countByApisIdAndNameAndIdNot(planId, name, id) < 1,
        APIS_CASE_NAME_REPEATED_T, new Object[]{name});
  }

  @Override
  public void checkCaseQuota(int incr, Long apisId) {
    int count = apisCaseRepo.countByApisId(apisId);
    if (count + incr > MAX_API_CASE_NUM) {
      throw QuotaException.of(APIS_CASE_OVER_LIMIT_CODE, APIS_CASE_OVER_LIMIT_T,
          new Object[]{MAX_API_CASE_NUM});
    }
  }

  /**
   * Set case apis deleted status
   */
  @Override
  public void setInfoApisNameAndDeleted(List<ApisCaseInfo> cases) {
    Set<Long> apisIds = cases.stream().map(ApisCaseInfo::getApisId).collect(Collectors.toSet());
    if (isNotEmpty(apisIds)) {
      List<ApisBaseInfo> existedApis = apisBaseInfoRepo.findAllById(apisIds);
      if (isNotEmpty(existedApis)) {
        Map<Long, ApisBaseInfo> apisMap = existedApis.stream()
            .collect(Collectors.toMap(ApisBaseInfo::getId, x -> x));
        Set<Long> existedIds = apisMap.keySet();
        // Set apis name ann deletion status
        cases.forEach(c -> {
          if (nonNull(c.getApisId())) {
            if (existedIds.contains(c.getApisId())) {
              c.setApisDeletedFlag(false);
              c.setApisSummary(apisMap.get(c.getApisId()).getSummary());
            } else {
              c.setApisDeletedFlag(true);
            }
          }
        });
      }
    }
  }

  /**
   * Set case apis deleted status
   */
  @Override
  public void setApisAndServiceInfo(List<ApisCase> cases) {
    Set<Long> apisIds = cases.stream().map(ApisCase::getApisId).collect(Collectors.toSet());
    if (isNotEmpty(apisIds)) {
      List<ApisBaseInfo> existedApis = apisBaseInfoRepo.findAllById(apisIds);
      if (isNotEmpty(existedApis)) {
        Map<Long, ApisBaseInfo> apisMap = existedApis.stream()
            .collect(Collectors.toMap(ApisBaseInfo::getId, x -> x));
        Set<Long> existedIds = apisMap.keySet();
        // Set apis name ann deletion status
        cases.forEach(c -> {
          if (nonNull(c.getApisId())) {
            if (existedIds.contains(c.getApisId())) {
              c.setApisDeletedFlag(false);
              c.setApisSummary(apisMap.get(c.getApisId()).getSummary());
              c.setApisServiceId(apisMap.get(c.getApisId()).getServiceId());
            } else {
              c.setApisDeletedFlag(true);
            }
          }
        });
        // Set project name info -> Do by @NameJoin
      }
    }
  }

  @NotNull
  @Override
  public Map<String, String> findCaseAllRef(ApisCase caseDb) {
    Map<String, String> allRefModels = new HashMap<>();
    try {
      Set<String> refs = RefResolver.findPropertyValues(Json31.pretty(caseDb), "$ref");
      if (isNotEmpty(refs)) {
        Map<String, String> compModelMap = servicesCompQuery.findByServiceId(caseDb.getServicesId())
            .stream().collect(Collectors.toMap(ServicesComp::getRef, ServicesComp::getModel));
        ApisConverter.findAllRef0(allRefModels, refs, compModelMap);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return allRefModels;
  }

  @Override
  public void setAndGetRefAuthentication(ApisCase case0) {
    if (case0.isAuthSchemaRef()) {
      ServicesComp comp = servicesCompQuery.detailByRef(case0.getServicesId(),
          case0.getAuthentication().get$ref());
      if (isNull(comp)){
        log.warn("ServicesComp `{}` not found", case0.getAuthentication().get$ref());
        return;
      }
      SecurityScheme auth = comp.toComponent(SecurityScheme.class);
      case0.setRefAuthentication(auth);
    }
  }

  @Override
  public void setSafeCloneName(ApisCase apisCase) {
    String saltName = randomAlphanumeric(3);
    String clonedName = apisCaseRepo.existsByApisIdAndName(
        apisCase.getApisId(), apisCase.getName() + "-Copy")
        ? apisCase.getName() + "-Copy." + saltName : apisCase.getName() + "-Copy";
    clonedName = clonedName.length() > DEFAULT_NAME_LENGTH_X4 ? clonedName.substring(0,
        DEFAULT_NAME_LENGTH_X4 - 3) + saltName : clonedName;
    apisCase.setName(clonedName);
  }

}
