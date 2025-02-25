package cloud.xcan.sdf.core.angustester.application.cmd.apis.impl;

import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.API_DESIGN;
import static cloud.xcan.sdf.api.commonlink.CombinedTargetType.SERVICE;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivities;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.sdf.core.angustester.domain.TesterCoreMessage.APIS_OAS_DESIGN_NOT_FOUND;
import static cloud.xcan.sdf.core.angustester.infra.util.AngusTesterUtils.convertImportFile;
import static cloud.xcan.sdf.core.angustester.infra.util.AngusTesterUtils.writeExportFile;
import static cloud.xcan.sdf.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;
import static cloud.xcan.sdf.spec.experimental.StandardCharsets.UTF_8;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.sdf.api.commonlink.apis.ApiSource;
import cloud.xcan.sdf.api.commonlink.apis.StrategyWhenDuplicated;
import cloud.xcan.sdf.core.angustester.application.cmd.activity.ActivityCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.apis.ApisDesignCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.services.ServicesCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.services.ServicesSchemaCmd;
import cloud.xcan.sdf.core.angustester.application.converter.ApisDesignConverter;
import cloud.xcan.sdf.core.angustester.application.query.apis.ApisDesignQuery;
import cloud.xcan.sdf.core.angustester.application.query.project.ProjectMemberQuery;
import cloud.xcan.sdf.core.angustester.application.query.services.ServicesAuthQuery;
import cloud.xcan.sdf.core.angustester.application.query.services.ServicesSchemaQuery;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.apis.design.ApisDesign;
import cloud.xcan.sdf.core.angustester.domain.apis.design.ApisDesignInfo;
import cloud.xcan.sdf.core.angustester.domain.apis.design.ApisDesignInfoRepo;
import cloud.xcan.sdf.core.angustester.domain.apis.design.ApisDesignRepo;
import cloud.xcan.sdf.core.angustester.domain.apis.design.ApisDesignSource;
import cloud.xcan.sdf.core.angustester.domain.services.Services;
import cloud.xcan.sdf.core.angustester.domain.services.schema.SchemaFormat;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.cmd.CommCmd;
import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.extension.angustester.api.ApiImportSource;
import cloud.xcan.sdf.model.apis.ApiStatus;
import cloud.xcan.sdf.spec.experimental.IdKey;
import cloud.xcan.sdf.spec.utils.FileUtils;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.core.util.Yaml31;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Biz
public class ApisDesignCmdImpl extends CommCmd<ApisDesign, Long> implements ApisDesignCmd {

  @Resource
  private ApisDesignRepo apisDesignRepo;

  @Resource
  private ApisDesignInfoRepo apisDesignInfoRepo;

  @Resource
  private ApisDesignQuery apisDesignQuery;

  @Resource
  private ServicesCmd servicesCmd;

  @Resource
  private ServicesSchemaCmd servicesSchemaCmd;

  @Resource
  private ServicesSchemaQuery servicesSchemaQuery;

  @Resource
  private ServicesAuthQuery servicesAuthQuery;

  @Resource
  private ProjectMemberQuery projectMemberQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Transactional(rollbackOn = Exception.class)
  @Override
  public IdKey<Long, Object> add(ApisDesign design) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), design.getProjectId());
      }

      @Override
      protected IdKey<Long, Object> process() {
        IdKey<Long, Object> idKeys = add0(design);
        activityCmd.add(toActivity(API_DESIGN, design, ActivityType.CREATED));
        return idKeys;
      }
    }.execute();
  }

  @Override
  public IdKey<Long, Object> add0(ApisDesign design) {
    return insert(design);
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void updateName(Long id, String name) {
    new BizTemplate<Void>() {
      ApisDesignInfo designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFindInfo(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
      }

      @Override
      protected Void process() {
        if (!designDb.getName().equals(name)) {
          designDb.setName(name);
          apisDesignInfoRepo.save(designDb);

          activityCmd.add(toActivity(API_DESIGN, designDb, ActivityType.NAME_UPDATED));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void replaceContent(Long id, String openapi) {
    new BizTemplate<Void>() {
      ApisDesign designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFind(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
      }

      @Override
      protected Void process() {
        designDb.setReleaseFlag(false);
        OpenAPI osa = servicesSchemaQuery.checkAndGetApisParseProvider(ApiImportSource.OPENAPI)
            .parse(openapi);
        designDb.setOpenapi(Json31.pretty(osa));
        apisDesignRepo.save(designDb);

        activityCmd.add(toActivity(API_DESIGN, designDb, ActivityType.UPDATED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void release(Long id) {
    new BizTemplate<Void>() {
      ApisDesign designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFind(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
        // Check the released content is not empty
        assertNotEmpty(designDb.getOpenapi(), APIS_OAS_DESIGN_NOT_FOUND);
      }

      @Override
      protected Void process() {
        if (designDb.hasLatestContent()) {
          servicesSchemaCmd.openapiReplace(designDb.getDesignSourceId(), false, false,
              designDb.getOpenapi(), StrategyWhenDuplicated.COVER, true, ApiSource.SYNC,
              null, false, null);
        }
        designDb.setReleaseFlag(true);
        apisDesignRepo.save(designDb);

        activityCmd.add(toActivity(API_DESIGN, designDb, ActivityType.RELEASE));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public IdKey<Long, Object> clone(Long id) {
    return new BizTemplate<IdKey<Long, Object>>() {
      ApisDesign designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFind(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
        // Check the view services permission
        if (designDb.getDesignSource().isSynchronousService()) {
          servicesAuthQuery.checkViewAuth(getUserId(), designDb.getDesignSourceId());
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // The service is not cloned
        ApisDesign newDesign = ApisDesignConverter.toClone(designDb);
        IdKey<Long, Object> idKey = insert(newDesign);

        activityCmd.add(toActivity(API_DESIGN, designDb, ActivityType.CLONE));
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void servicesGenerate(Long id) {
    new BizTemplate<Void>() {
      ApisDesign designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFind(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
      }

      @Override
      protected Void process() {
        if (designDb.getDesignSource().isSynchronousService()) {
          return null;
        }

        Services services = new Services()
            .setProjectId(designDb.getProjectId()).setName(designDb.getName())
            .setAuthFlag(false).setStatus(ApiStatus.UNKNOWN).setSource(ApiSource.EDITOR);
        servicesCmd.add(services, false);

        if (isEmpty(designDb.getOpenapi())) {
          servicesSchemaCmd.init(services);
        } else {
          servicesSchemaCmd.init(services,
              servicesSchemaQuery.checkAndGetApisParseProvider(ApiImportSource.OPENAPI)
                  .parse(designDb.getOpenapi()));
        }
        activityCmd.add(toActivity(SERVICE, services, ActivityType.CREATED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public IdKey<Long, Object> imports(Long projectId, String name, String content,
      MultipartFile file) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the project member permission
        projectMemberQuery.checkMember(projectId, getUserId());
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNotEmpty(content)) {
          OpenAPI openApi = servicesSchemaQuery.checkAndGetApisParseProvider(
                  ApiImportSource.OPENAPI)
              .parse(content);
          return addClone(openApi, projectId, name);
        }

        if (nonNull(file)) {
          try {
            File importFile = convertImportFile(file);
            OpenAPI openApi = servicesSchemaQuery.checkAndGetApisParseProvider(
                    ApiImportSource.OPENAPI)
                .parse(FileUtils.readFileToString(importFile, UTF_8));
            return addClone(openApi, projectId, name);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP
      }

      @Override
      protected Void process() {
        List<ApisDesignInfo> designs = apisDesignQuery.findbyIds(ids);
        for (ApisDesignInfo design : designs) {
          if (design.getDesignSource().isSynchronousService()
              && nonNull(design.getDesignSourceId())) {
            servicesAuthQuery.checkDeleteAuth(getUserId(), design.getDesignSourceId());
          } else {
            projectMemberQuery.checkMember(design.getProjectId(), getUserId());
          }
        }
        apisDesignInfoRepo.deleteAll(designs);

        activityCmd.addAll(toActivities(API_DESIGN, designs, ActivityType.DELETED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public File export(Long id, SchemaFormat format) {
    return new BizTemplate<File>() {
      ApisDesign designDb;

      @Override
      protected void checkParams() {
        // Check the design exists
        designDb = apisDesignQuery.checkAndFind(id);
        // Check the project member permission
        projectMemberQuery.checkMember(getUserId(), designDb.getProjectId());
      }

      @Override
      protected File process() {
        // Write exported OpenAPI to file
        String content = "";
        if (isNotEmpty(designDb.getOpenapi())) {
          content = isNotEmpty(designDb.getOpenapi()) && format.isYaml()
              ? Yaml31.pretty(servicesSchemaQuery.checkAndGetApisParseProvider(
              ApiImportSource.OPENAPI).parse(designDb.getOpenapi()))
              : designDb.getOpenapi();
        }
        return writeExportFile(designDb.getName() + "." + format.name(), content);
      }
    }.execute();
  }

  @Override
  public void deleteByServiceIdIn(Collection<Long> serviceIds) {
    apisDesignRepo.deleteByDesignSourceIdIn(
        ApisDesignSource.SYNCHRONOUS_SERVICE.getValue(), serviceIds);
  }

  private IdKey<Long, Object> addClone(OpenAPI openApi, Long projectId, String name) {
    ApisDesign design = new ApisDesign().setProjectId(projectId)
        .setName(name).setReleaseFlag(false).setOpenapiSpecVersion(openApi.getOpenapi())
        .setOpenapi(Json31.pretty(openApi))
        .setDesignSource(ApisDesignSource.FILE_IMPORT);

    IdKey<Long, Object> idKey = add0(design);
    activityCmd.add(toActivity(API_DESIGN, design, ActivityType.IMPORT));
    return idKey;
  }

  @Override
  protected BaseRepository<ApisDesign, Long> getRepository() {
    return apisDesignRepo;
  }
}
