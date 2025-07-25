package cloud.xcan.angus.core.tester.application.cmd.data.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.data.DatasourceCmd;
import cloud.xcan.angus.core.tester.application.converter.MockDatasourceConverter;
import cloud.xcan.angus.core.tester.application.query.common.CommonQuery;
import cloud.xcan.angus.core.tester.application.query.data.DatasourceQuery;
import cloud.xcan.angus.core.tester.application.query.project.ProjectMemberQuery;
import cloud.xcan.angus.core.tester.domain.data.datasource.Datasource;
import cloud.xcan.angus.core.tester.domain.data.datasource.DatasourceRepo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command implementation for managing datasources.
 * <p>
 * Provides methods for adding, replacing, testing, and deleting datasources.
 * Handles permission checks, name uniqueness, quota validation, and connection testing.
 */
@Biz
@Slf4j
public class DatasourceCmdImpl extends CommCmd<Datasource, Long> implements DatasourceCmd {

  @Resource
  private DatasourceRepo datasourceRepo;
  @Resource
  private DatasourceQuery datasourceQuery;
  @Resource
  private ProjectMemberQuery projectMemberQuery;
  @Resource
  private CommonQuery commonQuery;

  /**
   * Add a new datasource.
   * <p>
   * Validates project membership, name uniqueness, and quota. Inserts the datasource.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Datasource datasource) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the member permission
        projectMemberQuery.checkMember(getUserId(), datasource.getProjectId());

        // Check the name exists
        datasourceQuery.checkNameExists(datasource.getName());

        // Check the mock datasource quota
        commonQuery.checkTenantQuota(QuotaResource.AngusTesterMockDatasource, null,
            datasourceRepo.countAllByTenantId(getOptTenantId()) + 1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(datasource);
      }
    }.execute();
  }

  /**
   * Replace (add or update) a datasource.
   * <p>
   * Adds a new datasource if ID is null, otherwise updates the existing datasource.
   * Validates database immutability and name uniqueness.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(Datasource datasource) {
    return new BizTemplate<IdKey<Long, Object>>() {
      Datasource datasourceDb;

      @Override
      protected void checkParams() {
        if (nonNull(datasource.getId())) {
          // Check the datasource exists
          datasourceDb = datasourceQuery.checkAndFind(datasource.getId());

          // Check the database is not allowed to be modified
          ProtocolAssert.assertTrue(datasource.getDatabase().equals(datasourceDb.getDatabase()),
              "Database is not allowed to be modified");

          // Check the name exists
          if (!datasourceDb.getName().equals(datasource.getName())) {
            datasourceQuery.checkNameExists(datasource.getName());
          }
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        if (isNull(datasource.getId())) {
          return add(datasource);
        }

        // Save replaced datasource
        MockDatasourceConverter.setReplaceInfo(datasource, datasourceDb);
        datasourceRepo.save(datasourceDb);
        return new IdKey<Long, Object>().setId(datasourceDb.getId()).setKey(datasourceDb.getName());
      }
    }.execute();
  }

  /**
   * Test datasource connection by configuration.
   * <p>
   * Attempts to connect to the datasource using provided configuration and sets connection status.
   */
  @Override
  public Datasource testByConfig(Datasource datasource) {
    return new BizTemplate<Datasource>() {


      @Override
      protected Datasource process() {
        boolean connSuccess = true;
        String failureMessage = null;
        try {
          datasourceQuery.connDatabase(datasource);
        } catch (Exception e) {
          connSuccess = false;
          failureMessage = e.getMessage();
        }
        datasource.setConnSuccess(connSuccess).setConnFailureMessage(failureMessage);
        return datasource;
      }
    }.execute();
  }

  /**
   * Test datasource connection by ID.
   * <p>
   * Finds the datasource by ID, attempts to connect, and sets connection status.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Datasource testById(Long id) {
    return new BizTemplate<Datasource>() {
      Datasource datasourceDb;

      @Override
      protected void checkParams() {
        datasourceDb = datasourceQuery.checkAndFind(id);
      }

      @Override
      protected Datasource process() {
        boolean connSuccess = true;
        String failureMessage = null;
        try {
          datasourceQuery.connDatabase(datasourceDb);
        } catch (Exception e) {
          connSuccess = false;
          failureMessage = e.getMessage();
        }
        datasourceDb.setConnSuccess(connSuccess).setConnFailureMessage(failureMessage);
        return datasourceDb;
      }
    }.execute();
  }

  /**
   * Delete a datasource by ID.
   * <p>
   * Deletes the datasource from the repository.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
      }

      @Override
      protected Void process() {
        datasourceRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  /**
   * Get the repository for Datasource entity.
   * <p>
   * @return the DatasourceRepo instance
   */
  @Override
  protected BaseRepository<Datasource, Long> getRepository() {
    return this.datasourceRepo;
  }
}

