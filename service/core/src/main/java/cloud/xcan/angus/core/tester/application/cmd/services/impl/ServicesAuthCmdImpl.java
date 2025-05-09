package cloud.xcan.angus.core.tester.application.cmd.services.impl;

import static cloud.xcan.angus.api.commonlink.CombinedTargetType.SERVICE;
import static cloud.xcan.angus.api.commonlink.TesterApisMessage.FORBID_AUTH_CREATOR;
import static cloud.xcan.angus.api.commonlink.TesterApisMessage.FORBID_AUTH_CREATOR_CODE;
import static cloud.xcan.angus.core.tester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.angus.core.tester.application.converter.ServicesAuthConverter.toServicesCreatorAuth;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.activity.ActivityCmd;
import cloud.xcan.angus.core.tester.application.cmd.services.ServicesAuthCmd;
import cloud.xcan.angus.core.tester.application.query.common.CommonQuery;
import cloud.xcan.angus.core.tester.application.query.services.ServicesAuthQuery;
import cloud.xcan.angus.core.tester.application.query.services.ServicesQuery;
import cloud.xcan.angus.core.tester.domain.activity.ActivityType;
import cloud.xcan.angus.core.tester.domain.apis.ApisRepo;
import cloud.xcan.angus.core.tester.domain.services.Services;
import cloud.xcan.angus.core.tester.domain.services.ServicesRepo;
import cloud.xcan.angus.core.tester.domain.services.auth.ServicesAuth;
import cloud.xcan.angus.core.tester.domain.services.auth.ServicesAuthRepo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class ServicesAuthCmdImpl extends CommCmd<ServicesAuth, Long> implements ServicesAuthCmd {

  @Resource
  private ServicesAuthRepo servicesAuthRepo;

  @Resource
  private ServicesAuthQuery servicesAuthQuery;

  @Resource
  private ServicesQuery servicesQuery;

  @Resource
  private ServicesRepo servicesRepo;

  @Resource
  private ApisRepo apisRepo;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Override
  public IdKey<Long, Object> add(ServicesAuth auth) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Services serviceDb;
      String authObjectName;

      @Override
      protected void checkParams() {
        // Check the project exists
        serviceDb = servicesQuery.checkAndFind(auth.getServiceId());
        // Check the add creator permissions
        BizAssert.assertTrue(!serviceDb.getCreatedBy().equals(auth.getAuthObjectId()),
            FORBID_AUTH_CREATOR_CODE, FORBID_AUTH_CREATOR);
        // Check the current user have project authorization permissions
        servicesAuthQuery.checkGrantAuth(getUserId(), auth.getServiceId());
        // Check the authorization object exists
        authObjectName = commonQuery
            .checkAndGetAuthName(auth.getAuthObjectType(), auth.getAuthObjectId());
        // Check the for duplicate authorizations
        servicesAuthQuery.checkRepeatAuth(auth.getServiceId(), auth.getAuthObjectId(),
            auth.getAuthObjectType(), false);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add grant permission activity
        if (!auth.isCreatorAuth()) {
          activityCmd.add(toActivity(SERVICE, serviceDb, ActivityType.AUTH, authObjectName));
        }

        return batchInsert(List.of(auth), "authObjectId").get(0);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(ServicesAuth auth) {
    new BizTemplate<Void>() {
      ServicesAuth authDb;
      Services serviceDb;
      String authObjectName;

      @Override
      protected void checkParams() {
        // Check the ServicesAuth existed
        authDb = servicesAuthQuery.checkAndFind(auth.getId());
        // Check the modify creator permissions
        BizAssert.assertTrue(!authDb.getCreator(), FORBID_AUTH_CREATOR_CODE,
            FORBID_AUTH_CREATOR);
        // Check the project exists
        serviceDb = servicesQuery.checkAndFind(authDb.getServiceId());
        // Check the current user have project authorization permissions
        servicesAuthQuery.checkGrantAuth(getUserId(), authDb.getServiceId());
        // Check the authorization object exists
        authObjectName = commonQuery.checkAndGetAuthName(authDb.getAuthObjectType(),
            authDb.getAuthObjectId());
      }

      @Override
      protected Void process() {
        // Replace authorization
        authDb.setAuths(auth.getAuths());
        servicesAuthRepo.save(authDb);

        // Add modification permission activity
        if (!authDb.isCreatorAuth()) {
          activityCmd.add(toActivity(SERVICE, serviceDb, ActivityType.AUTH_UPDATED,
              authObjectName));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      ServicesAuth authDb;
      Services serviceDb;

      @Override
      protected void checkParams() {
        // Check the project auth exists
        authDb = servicesAuthQuery.checkAndFind(id);
        // Check the modify creator permissions
        BizAssert.assertTrue(!authDb.getCreator(), FORBID_AUTH_CREATOR_CODE,
            FORBID_AUTH_CREATOR);
        // Check the project exists
        serviceDb = servicesQuery.checkAndFind(authDb.getServiceId());
        // Check the current user have project authorization permissions
        servicesAuthQuery.checkGrantAuth(getUserId(), authDb.getServiceId());
      }

      @Override
      protected Void process() {
        // Get if authorization object name
        String authObjectName = "";
        try {
          authObjectName = commonQuery.checkAndGetAuthName(authDb.getAuthObjectType(),
              authDb.getAuthObjectId());
        } catch (Exception e) {
          // NOOP: Authorization can also be cancelled after the authorization object is deleted
        }

        // Add deleted permission activity, must be deleted before
        if (Objects.nonNull(id)) {
          activityCmd.add(toActivity(SERVICE, serviceDb, ActivityType.AUTH_CANCEL, authObjectName));
        }

        // Delete project permission
        servicesAuthRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long serviceId, Boolean enabled) {
    new BizTemplate<Void>() {
      Services serviceDb;

      @Override
      protected void checkParams() {
        // Check and get exists
        serviceDb = servicesQuery.checkAndFind(serviceId);

        // Check the to have grant permission
        servicesAuthQuery.checkGrantAuth(getUserId(), serviceId);
      }

      @Override
      protected Void process() {
        servicesRepo.updateAuthById(serviceId, enabled);
        apisRepo.updateServiceAuthByServiceId(serviceId, enabled);

        // Enable permission control activity
        activityCmd.add(toActivity(SERVICE, serviceDb, enabled
            ? ActivityType.AUTH_ENABLED : ActivityType.AUTH_DISABLED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void apisEnabled(Long serviceId, Boolean enabled) {
    new BizTemplate<Void>() {
      Services serviceDb;

      @Override
      protected void checkParams() {
        // Check and get exists
        serviceDb = servicesQuery.checkAndFind(serviceId);

        // Check the to have grant permission
        servicesAuthQuery.checkGrantAuth(getUserId(), serviceId);
      }

      @Override
      protected Void process() {
        apisRepo.updateServiceAuthByServiceId(serviceId, enabled);

        // Enable permission control activity
        activityCmd.add(toActivity(CombinedTargetType.API, serviceDb,
            enabled ? ActivityType.AUTH_ENABLED : ActivityType.AUTH_DISABLED));
        return null;
      }
    }.execute();
  }

  @Override
  public void addCreatorAuth(Long serviceId, Set<Long> creatorIds) {
    // Allow modification of new authorization
    servicesAuthRepo.deleteByServiceIdAndCreator(serviceId, true);

    // Save creator authorization
    List<ServicesAuth> projectAuths = creatorIds.stream()
        .map(creatorId -> toServicesCreatorAuth(creatorId, serviceId, uidGenerator))
        .collect(Collectors.toList());
    batchInsert(projectAuths, "authObjectId");
  }

  @Override
  public void moveCreatorAuth(Long serviceId, Long creatorId) {
    // Allow modification of new authorization
    servicesAuthRepo.deleteByServiceIdAndAuthObjectIdAndCreator(serviceId, creatorId, true);

    // Save creator authorization
    insert(toServicesCreatorAuth(creatorId, serviceId, uidGenerator), "authObjectId");
  }

  @Override
  public void deleteAllByProject(Collection<Long> serviceIds) {
    servicesAuthRepo.deleteByServiceIdIn(serviceIds);
  }

  @Override
  protected BaseRepository<ServicesAuth, Long> getRepository() {
    return this.servicesAuthRepo;
  }
}




