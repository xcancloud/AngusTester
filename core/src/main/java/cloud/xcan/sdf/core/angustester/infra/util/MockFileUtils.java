package cloud.xcan.sdf.core.angustester.infra.util;

import static cloud.xcan.sdf.core.pojo.principal.PrincipalContext.getTenantId;
import static cloud.xcan.sdf.core.utils.CoreUtils.randomUUID;
import static cloud.xcan.sdf.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.sdf.api.commonlink.TesterConstant;
import cloud.xcan.sdf.core.utils.SpringAppDirUtils;
import java.io.File;

public class MockFileUtils {

  public static boolean isAngusFile(String file) {
    return file.contains("specification") && file.contains("angus/");
  }

  public static File getImportTmpPath(String fileName) {
    SpringAppDirUtils utils = new SpringAppDirUtils();
    String tmpPath = utils.getTmpDir() + TesterConstant.IMPORT_MOCK_SERVICE_DIR + getTenantId()
        + File.separator + randomUUID() + File.separator + nullSafe(fileName, "");
    File file = new File(tmpPath);
    file.mkdirs();
    return file;
  }

  public static File getExportTmpPath(String fileName) {
    SpringAppDirUtils utils = new SpringAppDirUtils();
    String tmpPath = utils.getTmpDir() + TesterConstant.IMPORT_MOCK_SERVICE_DIR + getTenantId()
        + File.separator + randomUUID() + File.separator + nullSafe(fileName, "");
    File file = new File(tmpPath);
    if (file.isDirectory()) {
      file.mkdirs();
    } else {
      file.getParentFile().mkdirs();
    }
    return file;
  }


}
