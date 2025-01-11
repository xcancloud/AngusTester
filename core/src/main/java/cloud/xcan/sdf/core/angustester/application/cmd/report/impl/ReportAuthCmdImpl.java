package cloud.xcan.sdf.core.angustester.application.cmd.report.impl;

import static cloud.xcan.sdf.api.commonlink.TesterApisMessage.FORBID_AUTH_CREATOR;
import static cloud.xcan.sdf.api.commonlink.TesterApisMessage.FORBID_AUTH_CREATOR_CODE;
import static cloud.xcan.sdf.core.angustester.application.converter.ActivityConverter.toActivity;
import static cloud.xcan.sdf.core.angustester.application.converter.ReportAuthConverter.toReportAuths;
import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getUserId;

import cloud.xcan.sdf.api.commonlink.CombinedTargetType;
import cloud.xcan.sdf.core.angustester.application.cmd.activity.ActivityCmd;
import cloud.xcan.sdf.core.angustester.application.cmd.report.ReportAuthCmd;
import cloud.xcan.sdf.core.angustester.application.query.common.CommonQuery;
import cloud.xcan.sdf.core.angustester.application.query.report.ReportAuthQuery;
import cloud.xcan.sdf.core.angustester.application.query.report.ReportQuery;
import cloud.xcan.sdf.core.angustester.domain.activity.ActivityType;
import cloud.xcan.sdf.core.angustester.domain.report.Report;
import cloud.xcan.sdf.core.angustester.domain.report.ReportRepo;
import cloud.xcan.sdf.core.angustester.domain.report.auth.ReportAuth;
import cloud.xcan.sdf.core.angustester.domain.report.auth.ReportAuthRepo;
import cloud.xcan.sdf.core.angustester.domain.report.auth.ReportPermission;
import cloud.xcan.sdf.core.biz.Biz;
import cloud.xcan.sdf.core.biz.BizAssert;
import cloud.xcan.sdf.core.biz.BizTemplate;
import cloud.xcan.sdf.core.biz.cmd.CommCmd;
import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import cloud.xcan.sdf.spec.experimental.IdKey;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class ReportAuthCmdImpl extends CommCmd<ReportAuth, Long> implements ReportAuthCmd {

  @Resource
  private ReportAuthRepo reportAuthRepo;

  @Resource
  private ReportAuthQuery reportAuthQuery;

  @Resource
  private ReportQuery reportQuery;

  @Resource
  private ReportRepo reportRepo;

  @Resource
  private CommonQuery commonQuery;

  @Resource
  private ActivityCmd activityCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(ReportAuth auth) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Report reportDb;
      String authObjectName;

      @Override
      protected void checkParams() {
        // Check the report exists
        reportDb = reportQuery.checkAndFind(auth.getReportId());
        // Check the add creator permissions
        BizAssert.assertTrue(!reportDb.getCreatedBy().equals(auth.getAuthObjectId()),
            FORBID_AUTH_CREATOR_CODE, FORBID_AUTH_CREATOR);
        // Check the user have report authorization permissions
        reportAuthQuery.checkGrantAuth(getUserId(), auth.getReportId());
        // Check the authorization object exists
        authObjectName = commonQuery.checkAndGetAuthName(auth.getAuthObjectType(),
            auth.getAuthObjectId());
        // Check the for duplicate authorizations
        reportAuthQuery.checkRepeatAuth(auth.getReportId(), auth.getAuthObjectId(),
            auth.getAuthObjectType());
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add grant permission activity
        if (!auth.isCreatorAuth()) {
          activityCmd.add(toActivity(CombinedTargetType.REPORT, reportDb,
              ActivityType.AUTH, authObjectName));
        }
        return insert(auth);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(ReportAuth auth) {
    new BizTemplate<Void>() {
      ReportAuth authDb;
      Report reportDb;
      String authObjectName;

      @Override
      protected void checkParams() {
        // Check the functional test report authorization existed
        authDb = reportAuthQuery.checkAndFind(auth.getId());
        // Check the modify creator permissions
        BizAssert.assertTrue(!authDb.getCreatorFlag(), FORBID_AUTH_CREATOR_CODE,
            FORBID_AUTH_CREATOR);
        // Check the functional test report exists
        reportDb = reportQuery.checkAndFind(authDb.getReportId());
        // Check the current user have functional test report authorization permissions
        reportAuthQuery.checkGrantAuth(getUserId(), authDb.getReportId());
        // Check the authorization object exists
        authObjectName = commonQuery.checkAndGetAuthName(authDb.getAuthObjectType(),
            authDb.getAuthObjectId());
      }

      @Override
      protected Void process() {
        // Replace authorization
        authDb.setAuths(auth.getAuths());
        reportAuthRepo.save(authDb);

        // Add modify permission activity
        if (!authDb.isCreatorAuth()) {
          activityCmd.add(toActivity(CombinedTargetType.REPORT, reportDb,
              ActivityType.AUTH_UPDATED, authObjectName));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long reportId, Boolean enabledFlag) {
    new BizTemplate<Void>() {
      Report reportDb;

      @Override
      protected void checkParams() {
        // Check the report existed and authed
        reportDb = reportQuery.checkAndFind(reportId);
        // Check the user have report authorization permissions
        reportAuthQuery.checkGrantAuth(getUserId(), reportId);
      }

      @Override
      protected Void process() {
        reportRepo.updateAuthFlagById(reportId, enabledFlag);

        // Enable permission control activity
        activityCmd.add(toActivity(CombinedTargetType.REPORT, reportDb,
            enabledFlag ? ActivityType.AUTH_ENABLED : ActivityType.AUTH_DISABLED));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      ReportAuth authDb;
      Report reportDb;

      @Override
      protected void checkParams() {
        // Check the report auth exists
        authDb = reportAuthQuery.checkAndFind(id);
        // Check the modify creator permissions
        BizAssert.assertTrue(!authDb.getCreatorFlag(), FORBID_AUTH_CREATOR_CODE,
            FORBID_AUTH_CREATOR);
        // Check the report exists
        reportDb = reportQuery.checkAndFind(authDb.getReportId());
        // Check the user have report authorization permissions
        reportAuthQuery.checkGrantAuth(getUserId(), authDb.getReportId());
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
        activityCmd.add(toActivity(CombinedTargetType.REPORT, reportDb,
            ActivityType.AUTH_CANCEL, authObjectName));

        // Delete report permission
        reportAuthRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  public void addCreatorAuth(Long reportId, Set<Long> creatorIds) {
    batchInsert(toReportAuths(creatorIds, reportId, ReportPermission.ALL, true));
  }

  @Override
  public void deleteByReportId(Collection<Long> reportIds) {
    reportAuthRepo.deleteByReportIdIn(reportIds);
  }

  @Override
  protected BaseRepository<ReportAuth, Long> getRepository() {
    return this.reportAuthRepo;
  }
}
