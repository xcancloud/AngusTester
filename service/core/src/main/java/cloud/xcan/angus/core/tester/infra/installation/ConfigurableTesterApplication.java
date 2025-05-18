package cloud.xcan.angus.core.tester.infra.installation;


import static cloud.xcan.angus.api.commonlink.CtrlConstant.EXEC_MAX_FREE_NODES;
import static cloud.xcan.angus.api.commonlink.CtrlConstant.EXEC_MAX_FREE_THREADS;
import static cloud.xcan.angus.api.commonlink.CtrlConstant.MAX_FREE_CONCURRENT_TASK;
import static cloud.xcan.angus.api.commonlink.CtrlConstant.MAX_FREE_USER;
import static cloud.xcan.angus.api.enums.RedisDeployment.SINGLE;
import static cloud.xcan.angus.api.enums.SupportedDbType.MYSQL;
import static cloud.xcan.angus.core.spring.env.AbstractEnvLoader.appDir;
import static cloud.xcan.angus.core.spring.env.AbstractEnvLoader.appEdition;
import static cloud.xcan.angus.core.spring.env.AbstractEnvLoader.appHomeDir;
import static cloud.xcan.angus.core.spring.env.AbstractEnvLoader.appName;
import static cloud.xcan.angus.core.spring.env.AbstractEnvLoader.envs;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getFinalTenantId;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getFinalTenantName;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getGMWebsite;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getInstallGMHost;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getInstallGMPort;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getInstallTesterHost;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getProductInfo;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.getTesterWebsite;
import static cloud.xcan.angus.core.spring.env.ConfigurableApplicationAndEnvLoader.localDCaches;
import static cloud.xcan.angus.core.spring.env.EnvHelper.getEnum;
import static cloud.xcan.angus.core.spring.env.EnvHelper.getInt;
import static cloud.xcan.angus.core.spring.env.EnvHelper.getString;
import static cloud.xcan.angus.core.spring.env.EnvKeys.DATABASE_TYPE;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_EMAIL;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_ENCRYPTED_PASSWORD;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_FULL_NAME;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_PASSWORD;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_USERNAME;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_ADMIN_USER_ID;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_APIS_URL_PREFIX;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_APP_EXPIRATION_DATE;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_APP_OPEN_DATE;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_DB_HOST;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_DB_NAME;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_DB_PASSWORD;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_DB_PORT;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_DB_USER;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_HOST;
import static cloud.xcan.angus.core.spring.env.EnvKeys.GM_PORT;
import static cloud.xcan.angus.core.spring.env.EnvKeys.INSTALL_APPS;
import static cloud.xcan.angus.core.spring.env.EnvKeys.INSTALL_TYPE;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_DEPLOYMENT;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_HOST;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_NODES;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_PASSWORD;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_PORT;
import static cloud.xcan.angus.core.spring.env.EnvKeys.REDIS_SENTINEL_MASTER;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TENANT_ID;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TENANT_NAME;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_APIS_SERVER_URL;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_DB_HOST;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_DB_NAME;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_DB_PASSWORD;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_DB_PORT;
import static cloud.xcan.angus.core.spring.env.EnvKeys.TESTER_DB_USER;
import static cloud.xcan.angus.core.spring.env.EnvKeys.VITE_GM_URL_PREFIX;
import static cloud.xcan.angus.core.spring.env.EnvKeys.VITE_TESTER_URL_PREFIX;
import static cloud.xcan.angus.core.utils.CoreUtils.getResourceFileContent;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.getMaxFreeOpenDate;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.getMaxTrialOpenDate;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAIN_APP_SERVICES;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_ADMIN_PASSWORD;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_ADMIN_USERNAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_ADMIN_USER_ID;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_HOST;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_MYSQL_DB;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_MYSQL_PASSWORD;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_MYSQL_PORT;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_MYSQL_USER;
import static cloud.xcan.angus.spec.experimental.BizConstant.PrivateAppConfig.DEFAULT_REDIS_PORT;
import static cloud.xcan.angus.spec.experimental.BizConstant.TESTER_SERVICE;
import static cloud.xcan.angus.spec.process.ProcessCommand.PLATFORM;
import static cloud.xcan.angus.spec.utils.DateUtils.formatByDateTimePattern;
import static cloud.xcan.angus.spec.utils.JsonUtils.toJson;
import static cloud.xcan.angus.spec.utils.NetworkUtils.getHostName;
import static cloud.xcan.angus.spec.utils.ObjectUtils.convertToMap;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static java.lang.System.nanoTime;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import cloud.xcan.angus.api.enums.InstallType;
import cloud.xcan.angus.api.enums.RedisDeployment;
import cloud.xcan.angus.api.enums.SupportedDbType;
import cloud.xcan.angus.api.pojo.Pair;
import cloud.xcan.angus.core.app.ProductInfo;
import cloud.xcan.angus.core.jdbc.FullSQLException;
import cloud.xcan.angus.core.jdbc.JDBCUtils;
import cloud.xcan.angus.core.spring.env.ConfigurableApplication;
import cloud.xcan.angus.core.utils.checker.DatabaseChecker;
import cloud.xcan.angus.core.utils.checker.RedisChecker;
import cloud.xcan.angus.spec.experimental.Assert;
import cloud.xcan.angus.spec.properties.repo.PropertiesRepo;
import cloud.xcan.angus.spec.utils.FileUtils;
import cloud.xcan.angus.spec.utils.crypto.Base64Utils;
import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.core.env.ConfigurableEnvironment;
import org.typelevel.dcache.DCache;

public class ConfigurableTesterApplication implements ConfigurableApplication {

  private final Set<String> installApps = new HashSet<>();

  private SupportedDbType databaseType;
  public static ProductInfo productInfo;
  private Connection gmConnection;
  private Connection testerConnection;

  public ConfigurableTesterApplication() {
  }

  @Override
  public void doConfigureApplication(ConfigurableEnvironment environment, Properties envs)
      throws Exception {
    // Whether to start the installation; skip execution if it is not enabled installation or a private edition.
    if (appEdition.isPrivatization()) {
      productInfo = getProductInfo();

      installApps.addAll(Arrays.stream(getString(INSTALL_APPS, "").trim().split(","))
          .map(String::trim).toList());

      rewriteEnvByBusiness();

      if (isNotEmpty(installApps) && installApps.contains(TESTER_SERVICE)) {
        System.out.println("---> Configure application starting <----");
        installApplication();
        System.out.println("---> Configure application completed <----");
      }
    }
  }

  private void rewriteEnvByBusiness() {
    // Used by eureka
    envs.put(GM_HOST, getInstallGMHost());
    envs.put(GM_PORT, getInstallGMPort());

    envs.put(GM_APIS_URL_PREFIX, getGMWebsite());
    envs.put(TESTER_APIS_SERVER_URL, getTesterWebsite());

    InstallType installType = getEnum(INSTALL_TYPE, InstallType.class, InstallType.SHARED);
    if (installType.isShared()) {
      envs.put(TESTER_DB_HOST, getString(GM_DB_HOST));
      envs.put(TESTER_DB_PORT, getString(GM_DB_PORT));
      envs.put(TESTER_DB_NAME, getString(GM_DB_NAME));
      envs.put(TESTER_DB_USER, getString(GM_DB_USER));
      envs.put(TESTER_DB_PASSWORD, getString(GM_DB_PASSWORD));
    }
  }

  private void installApplication() throws Exception {
    // Configure all license info
    Pair<String, DCache> mainAppDCache = localDCaches.getOrDefault(TESTER_SERVICE, new Pair<>());
    Long tenantId = getFinalTenantId(mainAppDCache.value);

    addEnvForInstallSql(mainAppDCache.getValue());

    initDatabaseAndRedis();

    initDatabase();

    modifyApplicationConfiguration(tenantId, mainAppDCache);

    removeInstalledApplication();
  }

  private void initDatabaseAndRedis() {
    // Database configuration check
    databaseType = getEnum(DATABASE_TYPE, SupportedDbType.class, MYSQL);
    try {
      gmConnection = DatabaseChecker.checkConnection(databaseType,
          getString(GM_DB_HOST, DEFAULT_HOST), getInt(GM_DB_PORT, DEFAULT_MYSQL_PORT),
          getString(GM_DB_NAME, DEFAULT_MYSQL_DB), getString(GM_DB_USER, DEFAULT_MYSQL_USER),
          getString(GM_DB_PASSWORD, DEFAULT_MYSQL_PASSWORD)
      );
      System.out.println("---> The AngusGM database configuration is correct.");
    } catch (Exception e) {
      System.err.printf("---> The AngusGM database configuration is incorrect, cause: \n\t%s\n.",
          e.getMessage());
      throw new IllegalStateException(e.getMessage());
    }

    boolean needInstall = appName.equalsIgnoreCase(TESTER_SERVICE) && installApps.contains(appName);
    InstallType installType = getEnum(INSTALL_TYPE, InstallType.class, InstallType.SHARED);
    if (needInstall && installType.isStandalone()) {
      try {
        testerConnection = DatabaseChecker.checkConnection(databaseType,
            getString(TESTER_DB_HOST, DEFAULT_HOST), getInt(TESTER_DB_PORT, DEFAULT_MYSQL_PORT),
            getString(TESTER_DB_NAME, DEFAULT_MYSQL_DB),
            getString(TESTER_DB_USER, DEFAULT_MYSQL_USER),
            getString(TESTER_DB_PASSWORD, DEFAULT_MYSQL_PASSWORD)
        );
        System.out.println("---> The AngusTester database configuration is correct.");
      } catch (Exception e) {
        System.err.printf(
            "---> The AngusTester database configuration is incorrect, cause: \n\t%s\n.",
            e.getMessage());
        throw new IllegalStateException(e.getMessage());
      }
    } else {
      testerConnection = gmConnection;
    }

    // Redis configuration check
    RedisDeployment deployment = getEnum(REDIS_DEPLOYMENT, RedisDeployment.class, SINGLE);
    try {
      RedisChecker.checkConnection(deployment,
          getString(REDIS_HOST, DEFAULT_HOST), getInt(REDIS_PORT, DEFAULT_REDIS_PORT),
          getString(REDIS_PASSWORD), getString(REDIS_SENTINEL_MASTER),
          getString(REDIS_NODES)
      );
      System.out.println("---> The Redis configuration is correct.");
    } catch (Exception e) {
      System.err.printf("---> The Redis configuration is incorrect, cause: \n\t%s\n.",
          e.getMessage());
      throw new IllegalStateException(e.getMessage());
    }
  }

  private void modifyApplicationConfiguration(Long tenantId, Pair<String, DCache> mainAppDCache)
      throws Exception {
    // Configure the openapi2p client of store
    // saveStoreClient(dCache, gmDbConn);

    saveLicense(localDCaches);

    // Configure the installation info of main application.
    saveMainApplicationInstallation(tenantId, mainAppDCache.getKey(), mainAppDCache.getValue());

    // Configure the open info of AngusGM
    saveApplicationOpen(tenantId, mainAppDCache.getValue());

    // Configure the main node
    Long nodeId = saveMainNode(tenantId, getInstallTesterHost());
    // Configure the main node and agent auth
    saveMainNodeInfo(tenantId, nodeId);

    // Configure the website of AngusGM
    updateApplicationWebsite();

    // Config application statics resources
    // Note: Move first and then configure static resources, keeping the original static resource configuration template unchanged
    saveWebStaticsEnv();
  }

  private void initDatabase() throws Exception {
    String db = databaseType.getValue().toLowerCase();
    String edition = appEdition.getValue().toLowerCase();
    Map<String, String> variables = convertToMap(envs);

    String gmCommonSchemaSql = getResourceFileContent(
        String.format("installation/common/%s/gm_schema.sql", db));
    if (isNotEmpty(gmCommonSchemaSql)) {
      JDBCUtils.executeScript(gmConnection, gmCommonSchemaSql, emptyMap());
      System.out.println("---> Schema `common/gm_schema.sql` Installation Completed.");
    }

    String testerCommonSchemaSql = getResourceFileContent(
        String.format("installation/common/%s/tester_schema.sql", db));
    Assert.assertHasText(testerCommonSchemaSql, "Common gm_schema.sql is not found");
    JDBCUtils.executeScript(testerConnection, testerCommonSchemaSql, emptyMap());
    System.out.println("---> Schema `common/tester_schema.sql` Installation Completed.");

    String gmEditionSchemaSql = getResourceFileContent(
        String.format("installation/%s/%s/gm_schema.sql", edition, db));
    if (isNotEmpty(gmEditionSchemaSql)) {
      JDBCUtils.executeScript(gmConnection, gmEditionSchemaSql, emptyMap());
      System.out.println("---> Schema `edition/gm_schema.sql` Installation Completed.");
    }

    String testerEditionSchemaSql = getResourceFileContent(
        String.format("installation/%s/%s/tester_schema.sql", edition, db));
    if (isNotEmpty(testerEditionSchemaSql)) {
      JDBCUtils.executeScript(testerConnection, testerEditionSchemaSql, emptyMap());
      System.out.println("---> Schema `edition/tester_schema.sql` Installation Completed.");
    }

    String gmCommonDataSql = getResourceFileContent(
        String.format("installation/common/%s/gm_data.sql", db));
    if (isNotEmpty(gmCommonDataSql)) {
      JDBCUtils.executeScript(gmConnection, gmCommonDataSql, variables);
      System.out.println("---> Data `common/gm_data.sql` Installation Completed.");
    }

    String testerCommonDataSql = getResourceFileContent(
        String.format("installation/common/%s/tester_data.sql", db));
    Assert.assertHasText(testerCommonDataSql, "Common gm_data.sql is not found");
    JDBCUtils.executeScript(testerConnection, testerCommonDataSql, variables);
    System.out.println("---> Data `common/tester_data.sql` Installation Completed.");

    String gmEditionDataSql = getResourceFileContent(
        String.format("installation/%s/%s/gm_data.sql", edition, db));
    if (isNotEmpty(gmEditionDataSql)) {
      JDBCUtils.executeScript(gmConnection, gmEditionDataSql, variables);
      System.out.println("---> Data `edition/gm_data.sql` Installation Completed.");
    }

    String testerEditionDataSql = getResourceFileContent(
        String.format("installation/%s/%s/tester_data.sql", edition, db));
    if (isNotEmpty(testerEditionDataSql)) {
      JDBCUtils.executeScript(testerConnection, testerEditionDataSql, variables);
      System.out.println("---> Data `edition/tester_data.sql` Installation Completed.");
    }
  }

  private void saveLicense(Map<String, Pair<String, DCache>> licences) throws Exception {
    // @formatter:off
    for (Pair<String, DCache> pair : licences.values()) {
      String licenseNo = pair.getKey();
      DCache dCache = pair.getValue();
      File file = new File(appDir.getLicenceDir(appHomeDir) + licenseNo + ".lic");
      String licenseBase64Content = Base64Utils.encode(FileUtils.readFileToByteArray(file));
      JDBCUtils.executeUpdate(gmConnection, "DELETE FROM license_installed WHERE goods_code=? AND goods_version=? ",
          Arrays.asList(dCache.getPco(), dCache.getVer()));
      JDBCUtils.executeUpdate(gmConnection,
          "INSERT license_installed(id, main, install_edition_type, "
              + "license_no, main_license_no, provider, issuer, holder_id, holder,"
              + "goods_edition_type, goods_id, goods_type, goods_code, goods_name,"
              + "goods_version, order_no, subject, info, signature, "
              + "issued_date, begin_date, end_date, content) "
              + "VALUES (?, ?, ?, " + "?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?,"
              + "?, ?, ?, ?, ?," + "?, ?, ?, ? )",
          Arrays.asList(nanoTime(), MAIN_APP_SERVICES.contains(dCache.getPco()) ? 1 : 0, appEdition.getValue(),
              licenseNo, dCache.getMln(), dCache.getPro().getName(), dCache.getIss().getName(), dCache.getHid(), removeStart(dCache.getHol().getName(),"CN="),
              dCache.getVty(), dCache.getGid(), dCache.getPty(), dCache.getPco(), dCache.getPna(),
              dCache.getVer(), dCache.getOno(), dCache.getSub(), dCache.getInf(), dCache.getPsi(),
              formatByDateTimePattern(dCache.getIda()), formatByDateTimePattern(dCache.getNbe()), formatByDateTimePattern(dCache.getNaf()), licenseBase64Content)
      );
    }
    // @formatter:on
  }

  private void saveMainApplicationInstallation(Long tenantId, String licenseNo,
      DCache mainAppDCache) throws Exception {
    // @formatter:off
    JDBCUtils.executeUpdate(gmConnection, "DELETE FROM app_installed WHERE code=? AND version=? ",
        Arrays.asList(productInfo.getCode(), productInfo.getVersion()));
    JDBCUtils.executeUpdate(gmConnection,
        "INSERT app_installed(id, goods_id, edition_type, type, code, name, version, "
            + "icon_text, tags, introduction, information, features, "
            + "charge, order_no, platform, issuer, license_no, expired_date, "
            + "install_type, install_status, install_message, install_main_app, install_app_code, install_instance_id, "
            + "online_install, uninstallable, uninstall, tenant_id, created_by, created_by_name, created_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?," + " ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?," + "?, ?, ?, ?, ?, ?, ?)",
        Arrays.asList(nanoTime(), productInfo.getId(), appEdition.getValue(), productInfo.getType(), productInfo.getCode(), productInfo.getName(), productInfo.getVersion(),
            productInfo.getBase64Icon(), toJson(productInfo.getTags()), productInfo.getIntroduction(), productInfo.getInformation(), toJson(productInfo.getFeatures()),
            !appEdition.isCommunity(), "", PLATFORM.getName(), "https://www.xcan.cloud", stringSafe(licenseNo), nonNull(mainAppDCache) ? formatByDateTimePattern(mainAppDCache.getNaf()) : null,
            "INSTALLED", "NORMAL", "Success", 1, null, null,
            0, 0, 0, tenantId, -1, null, formatByDateTimePattern(new Date()))
    );

    // Update license quota, TODO When multiple main applications are used, the highest quota value should be update
    JDBCUtils.executeUpdate(gmConnection, "UPDATE c_setting_tenant_quota SET quota=? WHERE name=?",
        Arrays.asList(nonNull(mainAppDCache) ? mainAppDCache.getTco() : EXEC_MAX_FREE_THREADS, "AngusTesterConcurrency")
    );
    JDBCUtils.executeUpdate(gmConnection, "UPDATE c_setting_tenant_quota SET quota=? WHERE name=?",
        Arrays.asList(nonNull(mainAppDCache) ? mainAppDCache.getTta() : MAX_FREE_CONCURRENT_TASK, "AngusTesterConcurrentTask")
    );
    JDBCUtils.executeUpdate(gmConnection, "UPDATE c_setting_tenant_quota SET quota=? WHERE name=?",
        Arrays.asList(nonNull(mainAppDCache) ? mainAppDCache.getTno() : EXEC_MAX_FREE_NODES, "AngusTesterNode")
    );
    JDBCUtils.executeUpdate(gmConnection, "UPDATE c_setting_tenant_quota SET quota=? WHERE name=?",
        Arrays.asList(nonNull(mainAppDCache) ? mainAppDCache.getCam() : MAX_FREE_USER, "User")
    );
    // @formatter:on
  }

  private void saveApplicationOpen(long tenantId, DCache mainAppDCache) throws Exception {
    // @formatter:off
    JDBCUtils.executeUpdate(gmConnection, "DELETE FROM app_open WHERE app_code=? AND version=? ",
        Arrays.asList(productInfo.getCode(), productInfo.getVersion()));
    JDBCUtils.executeUpdate(gmConnection,
        "INSERT app_open(id, app_id, edition_type, app_code, app_type, version, "
            + "client_id, user_id, tenant_id, open_date, expiration_date, expiration_deleted, op_client_open, created_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ?, ?, ?)",
        Arrays.asList(nanoTime(), productInfo.getAppId(), appEdition.getValue(), productInfo.getCode(), "CLOUD_APP", productInfo.getVersion(),
            "xcan_tp", -1L, tenantId,
            nonNull(mainAppDCache) ? formatByDateTimePattern(mainAppDCache.getNbe()) : formatByDateTimePattern(new Date()),
            nonNull(mainAppDCache) ? formatByDateTimePattern(mainAppDCache.getNaf()) : formatByDateTimePattern((appEdition.isPrivatizationFree() ? getMaxFreeOpenDate() : getMaxTrialOpenDate())),
            0, 0, formatByDateTimePattern(new Date()))
    );
    // @formatter:off
  }

  private Long saveMainNode(Long tenantId, String ip) throws FullSQLException {
    // @formatter:off
    Long nodeId = JDBCUtils.executeQuery(testerConnection, "SELECT * FROM node WHERE ip=? LIMIT 1",
        singletonList(ip), (rs) -> rs.next() ? rs.getLong("id") : null);
    if (nonNull(nodeId)) {
      // Existing? Only add node roles
      JDBCUtils.executeUpdate(testerConnection,
          "DELETE FROM node_role WHERE node_id =? AND role=?", Arrays.asList(nodeId, "MANAGEMENT"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "MANAGEMENT"));
      JDBCUtils.executeUpdate(testerConnection,
          "DELETE FROM node_role WHERE node_id =? AND role=?", Arrays.asList(nodeId, "CONTROLLER"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "CONTROLLER"));
      // TODO Execution and Mock service nodes should be separate
      JDBCUtils.executeUpdate(testerConnection,
          "DELETE FROM node_role WHERE node_id =? AND role=?", Arrays.asList(nodeId, "EXECUTION"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "EXECUTION"));
      JDBCUtils.executeUpdate(testerConnection,
          "DELETE FROM node_role WHERE node_id =? AND role=?", Arrays.asList(nodeId, "MOCK_SERVICE"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "MOCK_SERVICE"));
    } else {
      // Not Existing! insert node and roles
      nodeId = nanoTime();
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node(id, name, ip, public_ip, region_id, domain, spec, "
              + "source, free, enabled, username, password, ssh_port, "
              + "instance_id, instance_name, instance_expired_date, deleted, "
              + "order_id, charge_type, install_agent, sync, ext_search_merge, "
              + "tenant_id, created_by, created_date, last_modified_by, last_modified_date) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ?, "
              + "?, ?, ?, ?, " + " ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?)",
          Arrays.asList(nodeId, nullSafe(getHostName(), "INSTALLATION_APP") , ip, null, null, null, null,
              "OWN_NODE", 0, 1, null, null, 22, null, null, null, 0,
              null, null, 0, 0, String.valueOf(nodeId),
              tenantId, -1L, formatByDateTimePattern(new Date()), -1L, formatByDateTimePattern(new Date())
          )
      );
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "MANAGEMENT"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "CONTROLLER"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "EXECUTION"));
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_role(id, node_id, role) VALUES (?, ?, ?)", Arrays.asList(nanoTime(), nodeId, "MOCK_SERVICE"));
    }
    // @formatter:on
    return nodeId;
  }

  private void saveMainNodeInfo(Long tenantId, Long nodeId) throws Exception {
    // @formatter:off
    Long nodeId0 = JDBCUtils.executeQuery(testerConnection, "SELECT * FROM node_info WHERE id=?",
        singletonList(nodeId), (rs) -> rs.next() ? rs.getLong("id") : null);
    if (isNull(nodeId0)){
      JDBCUtils.executeUpdate(testerConnection,
          "INSERT node_info(id, tenant_id, info, os, agent_installed, agent_auth, last_modified_date) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?)",
          Arrays.asList(nodeId, tenantId, null, null, 0, null, formatByDateTimePattern(new Date()))
      );
    }
    // @formatter:on
  }

  private void updateApplicationWebsite() throws Exception {
    // @formatter:on
    JDBCUtils.executeUpdate(gmConnection, "UPDATE app SET url=? WHERE code=?",
        Arrays.asList(getTesterWebsite(), TESTER_SERVICE)
    );
    // @formatter:off
  }

  private void saveWebStaticsEnv() throws Exception {
    String staticPath = appDir.getStaticDir(appHomeDir);
    PropertiesRepo.ofPrivateStatics(staticPath)
        .save(VITE_GM_URL_PREFIX, getGMWebsite())
        .save(VITE_TESTER_URL_PREFIX, getTesterWebsite())
        .saveToDisk();
  }

  private void removeInstalledApplication() throws Exception {
    String confPath = appDir.getConfDir(appHomeDir);
    PropertiesRepo repo = PropertiesRepo.ofPrivate(confPath);

    Set<String> remoteApps = new HashSet<>(installApps);
    remoteApps.remove(TESTER_SERVICE);
    repo.save(INSTALL_APPS, String.join(",", remoteApps)).saveToDisk();
  }

  private void addEnvForInstallSql(DCache mainDCache) {
    envs.put(TENANT_ID, getFinalTenantId(mainDCache).toString());
    envs.put(TENANT_NAME, getFinalTenantName(mainDCache));

    //envs.put(GM_APP_OPEN_DATE, formatByDateTimePattern(new Date()));
    //envs.put(GM_APP_EXPIRATION_DATE, formatByDateTimePattern(getMaxFreeOpenDate()));

    envs.put(GM_ADMIN_USER_ID, DEFAULT_ADMIN_USER_ID);
    envs.put(GM_ADMIN_FULL_NAME, getString(GM_ADMIN_FULL_NAME, "User" + randomNumeric(6)));
    envs.put(GM_ADMIN_EMAIL, getString(GM_ADMIN_EMAIL, ""));
    envs.put(GM_ADMIN_USERNAME, getString(GM_ADMIN_USERNAME, DEFAULT_ADMIN_USERNAME));

    String adminUserPassword = getString(GM_ADMIN_PASSWORD, DEFAULT_ADMIN_PASSWORD);
    if (adminUserPassword.length() < 6) {
      throw new IllegalStateException("Password must be at least 6 characters");
    }
    envs.put(GM_ADMIN_ENCRYPTED_PASSWORD,
        createDelegatingPasswordEncoder().encode(adminUserPassword));
  }

}
