package cloud.xcan.sdf.core.angustester.application.query.script.impl;

import static cloud.xcan.sdf.core.angustester.application.converter.ScriptConverter.countCreationScript;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.SCRIPT_PROPERTIES_CONSTRAINT_ERROR;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.sdf.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.sdf.core.jpa.criteria.CriteriaUtils.findInfoScope;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.isJobOrDoorApi;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.isUserAction;
import static cloud.xcan.sdf.core.utils.CoreUtils.getCommonResourcesStatsFilter;
import static cloud.xcan.sdf.spec.experimental.BizConstant.ANGUS_SCRIPT_LENGTH;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.model.script.configuration.ScriptType;
import cloud.xcan.angus.parser.AngusParser;
import cloud.xcan.angus.parser.models.AngusParseResult;
import cloud.xcan.angus.parser.models.ParseOptions;
import cloud.xcan.sdf.api.InfoScope;
import cloud.xcan.sdf.api.commonlink.CombinedTargetType;
import cloud.xcan.sdf.model.script.ScriptSource;
import cloud.xcan.sdf.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.sdf.api.enums.AuthObjectType;
import cloud.xcan.sdf.api.manager.SettingTenantQuotaManager;
import cloud.xcan.sdf.api.manager.UserManager;
import cloud.xcan.sdf.api.message.http.ResourceExisted;
import cloud.xcan.sdf.api.message.http.ResourceNotFound;
import cloud.xcan.sdf.api.search.SearchCriteria;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisQuery;
import cloud.xcan.sdf.core.angustester.application.query.common.CommonQuery;
import cloud.xcan.sdf.core.angustester.application.query.project.ProjectMemberQuery;
import cloud.xcan.sdf.core.angustester.application.query.scenario.ScenarioQuery;
import cloud.xcan.sdf.core.angustester.application.query.script.ScriptAuthQuery;
import cloud.xcan.sdf.core.angustester.application.query.script.ScriptQuery;
import cloud.xcan.sdf.core.angustester.domain.apis.ApisBaseInfo;
import cloud.xcan.sdf.core.angustester.domain.scenario.Scenario;
import cloud.xcan.sdf.core.angustester.domain.script.Script;
import cloud.xcan.sdf.core.angustester.domain.script.ScriptInfo;
import cloud.xcan.sdf.core.angustester.domain.script.ScriptInfoListRepo;
import cloud.xcan.sdf.core.angustester.domain.script.ScriptInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.script.ScriptRepo;
import cloud.xcan.sdf.core.angustester.domain.script.count.ScriptCount;
import cloud.xcan.sdf.core.angustester.domain.script.count.ScriptResourcesCreationCount;
import cloud.xcan.sdf.core.angustester.domain.script.tag.ScriptTag;
import cloud.xcan.sdf.core.angustester.domain.script.tag.ScriptTagRepo;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.ProtocolAssert;
import cloud.xcan.sdf.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.sdf.core.jpa.criteria.GenericSpecification;
import cloud.xcan.sdf.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.sdf.spec.locale.MessageHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@SummaryQueryRegister(name = "Script", table = "script", groupByColumns = {/*"created_date", */
    "type", "source"})
@Biz
public class ScriptQueryImpl implements ScriptQuery {

  @Resource
  private ScriptRepo scriptRepo;

  @Resource
  private ScriptInfoRepo scriptInfoRepo;

  @Resource
  private ScriptInfoListRepo scriptInfoListRepo;

  @Resource
  private ScriptTagRepo scriptTagRepo;

  @Resource
  private ScriptAuthQuery scriptAuthQuery;

  @Resource
  private ScenarioQuery scenarioQuery;

  @Resource
  private ApisQuery apisQuery;

  @Resource
  private ProjectMemberQuery projectMemberQuery;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private UserManager userManager;

  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  @Resource
  private Validator validator;

  @Override
  public Script detail(Long id) {
    return new BizTemplate<Script>() {
      @Override
      protected void checkParams() {
        scriptAuthQuery.checkViewAuth(getUserId(), id);
      }

      @Override
      protected Script process() {
        Script script = checkAndFind(id);

        setScriptTag(script);

        setScriptSourceName(script);

        // Note: For ctrl check permission
        script.setPermissions(scriptAuthQuery.getUserAuth(id, getUserId()));
        return script;
      }
    }.execute();
  }

  @Override
  public Script info(Long id) {
    return new BizTemplate<Script>() {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          scriptAuthQuery.checkViewAuth(getUserId(), id);
        }
      }

      @Override
      protected Script process() {
        Script script = checkAndFind(id);

        // Note: For angusctrl check permission
        script.setPermissions(scriptAuthQuery.getUserAuth(id, getUserId()));
        return script;
      }
    }.execute();
  }

  @Override
  public List<ScriptInfo> infos(Set<Long> ids) {
    return new BizTemplate<List<ScriptInfo>>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected List<ScriptInfo> process() {
        List<ScriptInfo> scripts = scriptInfoRepo.findAllById(ids);
        setScriptSourceName(scripts);
        return scripts;
      }
    }.execute();
  }

  @Override
  public Script angusDetail(Long id) {
    return new BizTemplate<Script>() {
      @Override
      protected void checkParams() {
        scriptAuthQuery.checkViewAuth(getUserId(), id);
      }

      @Override
      protected Script process() {
        Script script = checkAndFind(id);

        setScriptSourceName(script);

        if (isNotEmpty(script.getContent())) {
          script.setAngusScript(checkAndParse(script.getContent(), false));
        }
        return script;
      }
    }.execute();
  }

  @Override
  public ScriptCount countStatistics(Set<SearchCriteria> criterias) {
    return new BizTemplate<ScriptCount>() {
      @Override
      protected void checkParams() {
        // NOOP: Check view permission
      }

      @Override
      protected ScriptCount process() {
        // Set authorization conditions when you are not an administrator or only query yourself
        commonQuery.checkAndSetAuthObjectIdCriteria(criterias);

        return scriptInfoListRepo.count(criterias);
      }
    }.execute();
  }

  @Override
  public ScriptResourcesCreationCount creationStatistics(Long projectId,
      AuthObjectType creatorObjectType, Long creatorObjectId, LocalDateTime createdDateStart,
      LocalDateTime createdDateEnd) {
    return new BizTemplate<ScriptResourcesCreationCount>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected ScriptResourcesCreationCount process() {
        final ScriptResourcesCreationCount result = new ScriptResourcesCreationCount();

        // Find all when condition is null, else find by condition
        Set<Long> createdBys = isNull(creatorObjectType) ? null
            : userManager.getUserIdByOrgType0(creatorObjectType, creatorObjectId);

        Set<SearchCriteria> allFilters = getCommonResourcesStatsFilter(
            projectId, createdDateStart, createdDateEnd, createdBys);

        // Number of statistical scenario
        List<ScriptInfo> scripts = scriptInfoRepo.findAllByFilters(allFilters);
        countCreationScript(result, scripts);
        return result;
      }
    }.execute();
  }

  @Override
  public Page<ScriptInfo> find(GenericSpecification<ScriptInfo> spec, Pageable pageable) {
    return new BizTemplate<Page<ScriptInfo>>() {
      @Override
      protected void checkParams() {
        if (!isJobOrDoorApi()) {
          // Check the project member permission
          projectMemberQuery.checkMember(spec.getCriterias());
        }
      }

      @Override
      protected Page<ScriptInfo> process() {
        safeScenarioQuery(spec.getCriterias());

        // Set authorization conditions when you are not an administrator or only query yourself
        commonQuery.checkAndSetAuthObjectIdCriteria(spec.getCriterias());
        Page<ScriptInfo> pages = scriptInfoListRepo.find(spec.getCriterias(), pageable,
            ScriptInfo.class, null);
        if (pages.isEmpty()) {
          return pages;
        }

        // For AngusCtrl remote query
        InfoScope infoScope = findInfoScope(spec.getCriterias(), InfoScope.DETAIL);
        if (InfoScope.DETAIL.equals(infoScope)) {
          setScriptInfoTag(pages.getContent());
          setScriptSourceName(pages.getContent());
          //setScriptCurrentPermission(pages.getContent());
        }
        return pages;
      }
    }.execute();
  }

  @Override
  public Page<ScriptInfo> infoList(GenericSpecification<ScriptInfo> spec, Pageable pageable) {
    return new BizTemplate<Page<ScriptInfo>>() {
      @Override
      protected void checkParams() {
        if (!isJobOrDoorApi()) {
          // Check the project member permission
          projectMemberQuery.checkMember(spec.getCriterias());
        }
      }

      @Override
      protected Page<ScriptInfo> process() {
        safeScenarioQuery(spec.getCriterias());

        if (isUserAction()) {
          // Set authorization conditions when you are not an administrator or only query yourself
          commonQuery.checkAndSetAuthObjectIdCriteria(spec.getCriterias());

          Page<ScriptInfo> pages = scriptInfoListRepo.find(spec.getCriterias(), pageable,
              ScriptInfo.class, null);
          if (pages.isEmpty()) {
            return pages;
          }
          setScriptSourceName(pages.getContent());
        }
        return scriptInfoRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public void safeScenarioQuery(Set<SearchCriteria> criterias) {
    // Convert the scenario query from associated table to the main table query
    String source = findFirstValue(criterias, "source");
    String sourceTargetId = findFirstValue(criterias, "sourceTargetId");
    if (isNotEmpty(source) && isNotEmpty(sourceTargetId) &&
        CombinedTargetType.SCENARIO.getValue().equals(source)) {
      CriteriaUtils.findAndRemove(criterias, "sourceTargetId");
      criterias.add(SearchCriteria.equal("scenarioId", sourceTargetId));
    }
  }

  @Override
  public List<ScriptInfo> findInfoBySource(ScriptSource source, Long sourceTargetId) {
    return scriptInfoRepo.findBySourceIdAndSource(sourceTargetId, source);
  }

  @Override
  public List<Script> findBySource(ScriptSource source, Long sourceTargetId) {
    return scriptRepo.findBySourceIdAndSource(sourceTargetId, source);
  }

  @Override
  public Set<Long> findIdsBySource(ScriptSource source, Collection<Long> sourceTargetIds) {
    return scriptRepo.findIdsBySourceIdInAndSourceAndType(sourceTargetIds, source.getValue());
  }

  @Override
  public Set<Long> findIdsBySourceAndTypeIn(ScriptSource source, Collection<Long> sourceTargetIds,
      Collection<ScriptType> testTypes) {
    return scriptRepo.findIdsBySourceIdInAndSourceAndType(sourceTargetIds, source.getValue(),
        testTypes.stream().map(ScriptType::getValue).collect(Collectors.toList()));
  }

  @Override
  public Script findScriptByScenarioId(Long scenarioId) {
    List<Script> scripts = scriptRepo.findBySourceIdAndSource(scenarioId, ScriptSource.SCENARIO);
    if (scripts.size() > 1) {
      log.error("Scenario[{}] and script are not one-on-one", scenarioId);
    }
    return isEmpty(scripts) ? null : scripts.get(0);
  }

  @Override
  public ScriptInfo findScriptInfoByScenarioId(Long scenarioId) {
    List<ScriptInfo> scripts = scriptInfoRepo.findBySourceIdAndSource(scenarioId,
        ScriptSource.SCENARIO);
    if (scripts.size() > 1) {
      log.error("Scenario[{}] and script are not one-on-one", scenarioId);
    }
    return isEmpty(scripts) ? null : scripts.get(0);
  }

  @Override
  public Script findBySourceAndScriptType(ScriptSource source, Long sourceTargetId,
      ScriptType scriptType) {
    return scriptRepo.findBySourceIdAndSourceAndType(sourceTargetId, source, scriptType);
  }

  @Override
  public Script checkAndFind(Long id) {
    return scriptRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Script"));
  }

  @Override
  public ScriptInfo checkAndFindInfo(Long id) {
    return scriptInfoRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Script"));
  }

  @Override
  public List<ScriptInfo> checkAndFindInfos(Collection<Long> ids) {
    List<ScriptInfo> scriptDbs = scriptInfoRepo.findAllById(ids);
    Set<Long> scriptDbIds = scriptDbs.stream().map(ScriptInfo::getId).collect(Collectors.toSet());
    Set<Long> retainsIds = new HashSet<>(ids);
    retainsIds.removeAll(scriptDbIds);
    assertResourceNotFound(isEmpty(retainsIds), retainsIds, "Script");
    return scriptDbs;
  }

  @Override
  public AngusScript checkAndParse(String content, boolean validation) {
    if (isEmpty(content)) {
      return null;
    }

    AngusParseResult parse = AngusParser.getInstance().readContents(content,
        ParseOptions.newBuilder().validate(false).build() // Close default validation
    );

    // Check the parsing validation passed
    ProtocolAssert.assertTrue(nonNull(parse.getScript()), isNotEmpty(parse.getMessages())
        ? parse.getMessages().get(0) : null);

    // Check the script content is valid
    if (validation) {
      checkAngusScript(parse.getScript());
    }
    return parse.getScript();
  }

  @Override
  public String checkAndSerialize(AngusScript script, boolean validation) {
    if (isEmpty(script)) {
      return null;
    }

    if (validation) {
      checkAngusScript(script);
    }

    String content = null;
    try {
      content = AngusParser.YAML_MAPPER.writeValueAsString(script);
    } catch (JsonProcessingException e) {
      ProtocolAssert.assertTrue(false, "Script format exception: " + e.getMessage());
    }

    checkScriptLength(content);
    return content;
  }

  @Override
  public void checkAngusScript(AngusScript script) {
    // Verify parameter constraints
    Set<ConstraintViolation<AngusScript>> constraints = validator.validate(script);
    if (!constraints.isEmpty()) {
      ConstraintViolation<AngusScript> constraint = constraints.iterator().next();
      ProtocolAssert.assertTrue(false,
          MessageHolder.message(SCRIPT_PROPERTIES_CONSTRAINT_ERROR) +
              ": [" + constraint.getPropertyPath() + "] " + constraint.getMessage());
    }
  }

  @Override
  public void checkQuota(int incr) {
    long count = scriptRepo.count();
    settingTenantQuotaManager.checkTenantQuota(QuotaResource.AngusTesterScript, null, incr + count);
  }

  @Override
  public void checkScriptLength(String script) {
    if (isNotEmpty(script)) {
      // @Length(max = ANGUS_SCRIPT_LENGTH) // 10MB
      ProtocolAssert.assertTrue(script.length() <= ANGUS_SCRIPT_LENGTH,
          "The length of the script content cannot exceed: " + ANGUS_SCRIPT_LENGTH);
    }
  }

  @Override
  public void checkSourceResourceExist(Script script) {
    //Assert.assertNotNull(script.getSourceId(), "Script source id is null");
    if (script.getSource().isUnique() && nonNull(script.getSourceId())) {
      if (script.getSource().isApis()) {
        apisQuery.check(script.getSourceId());
      }
      if (script.getSource().isScenario()) {
        scenarioQuery.check(script.getSourceId());
      }
    }
  }

  @Override
  public void checkSourceAddScriptExist(Script script) {
    ProtocolAssert.assertNotNull(script.getType(), "Script type is null");
    ProtocolAssert.assertNotNull(script.getSource(), "Script source is null");
    if (script.getSource().isUnique()) {
      ProtocolAssert.assertNotNull(script.getSourceId(), "Script source id is null");
      if (scriptRepo.existsBySourceIdAndSourceAndType(script.getSourceId(), script.getSource(),
          script.getType())) {
        throw ResourceExisted.of(script.getType().getMessage(), script.getSource().getMessage());
      }
    }
  }

  @Override
  public void checkSourceUpdateScriptExist(Script script) {
    ProtocolAssert.assertNotNull(script.getId(), "Script id is null");
    ProtocolAssert.assertNotNull(script.getType(), "Script type id is null");
    if (script.getSource().isUnique()) {
      ProtocolAssert.assertNotNull(script.getSourceId(), "Script source id is null");
      if (scriptRepo.existsBySourceIdAndSourceAndTypeAndIdNot(script.getSourceId(),
          script.getSource(), script.getType(), script.getId())) {
        throw ResourceExisted.of(script.getType().getMessage(), script.getSource().getMessage());
      }
    }
  }

  @Override
  public boolean isAuthCtrl(Long scriptId) {
    ScriptInfo script = scriptInfoRepo.findById(scriptId).orElse(null);
    return nonNull(script) && (script.getAuthFlag());
  }

  @Override
  public void setScriptTag(Script script) {
    List<String> tags = scriptTagRepo.findByScriptId(script.getId()).stream()
        .map(ScriptTag::getName).collect(Collectors.toList());
    script.setTags(tags);
  }

  @Override
  public void setScriptInfoTag(List<ScriptInfo> scriptInfos) {
    Map<Long, List<ScriptTag>> tagsMap = scriptTagRepo
        .findByScriptIdIn(scriptInfos.stream().map(ScriptInfo::getId)
            .collect(Collectors.toSet())).stream()
        .collect(Collectors.groupingBy(ScriptTag::getScriptId));
    if (isNotEmpty(tagsMap)) {
      for (ScriptInfo scriptInfo : scriptInfos) {
        if (tagsMap.containsKey(scriptInfo.getId())) {
          scriptInfo.setTags(tagsMap.get(scriptInfo.getId()).stream()
              .map(ScriptTag::getName).collect(Collectors.toList()));
        }
      }
    }
  }

  @Override
  public void setScriptSourceName(Script script) {
    if (script.getSource().isApis() && nonNull(script.getSourceId())) {
      ApisBaseInfo apis = apisQuery.findBase0ById(script.getSourceId());
      if (nonNull(apis)) {
        script.setSourceName(apis.getSummary());
      }
    }

    if (script.getSource().isScenario() && nonNull(script.getSourceId())) {
      Scenario scenario = scenarioQuery.find0(script.getSourceId());
      if (nonNull(scenario)) {
        script.setSourceName(scenario.getName());
      }
    }
  }

  @Override
  public void setScriptSourceName(List<ScriptInfo> scriptInfos) {
    Map<ScriptSource, List<ScriptInfo>> sourceScriptMap = scriptInfos.stream()
        .filter(x -> x.getSource().isUnique())
        .collect(Collectors.groupingBy(ScriptInfo::getSource));
    if (isEmpty(sourceScriptMap)) {
      return;
    }

    if (sourceScriptMap.containsKey(ScriptSource.API)) {
      Set<Long> scriptSourceIdsMap = sourceScriptMap.get(ScriptSource.API).stream()
          .map(ScriptInfo::getSourceId).filter(Objects::nonNull).collect(Collectors.toSet());
      Map<Long, ApisBaseInfo> apisMap = apisQuery.findBase0ByIdIn(scriptSourceIdsMap)
          .stream().collect(Collectors.toMap(ApisBaseInfo::getId, x -> x));
      for (ScriptInfo script : scriptInfos) {
        if (nonNull(script.getSourceId()) && apisMap.containsKey(script.getSourceId())) {
          script.setSourceName(apisMap.get(script.getSourceId()).getSummary());
        }
      }
    }

    if (sourceScriptMap.containsKey(ScriptSource.SCENARIO)) {
      Set<Long> scriptSourceIdsMap = sourceScriptMap.get(ScriptSource.SCENARIO).stream()
          .map(ScriptInfo::getSourceId).filter(Objects::nonNull).collect(Collectors.toSet());
      Map<Long, Scenario> apisMap = scenarioQuery.find0ByIdIn(scriptSourceIdsMap)
          .stream().collect(Collectors.toMap(Scenario::getId, x -> x));
      for (ScriptInfo script : scriptInfos) {
        if (nonNull(script.getSourceId()) && apisMap.containsKey(script.getSourceId())) {
          script.setSourceName(apisMap.get(script.getSourceId()).getName());
        }
      }
    }
  }

}
