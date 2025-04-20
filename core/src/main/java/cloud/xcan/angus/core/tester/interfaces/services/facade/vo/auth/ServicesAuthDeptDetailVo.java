package cloud.xcan.angus.core.tester.interfaces.services.facade.vo.auth;

import cloud.xcan.angus.remote.NameJoinField;

public class ServicesAuthDeptDetailVo extends ServicesAuthDetailVo {

  @NameJoinField(id = "authObjectId", repository = "commonDeptRepo")
  private String name;

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
