package cloud.xcan.sdf.core.angustester.interfaces.mock.facade.vo.apis;


import cloud.xcan.sdf.api.NameJoinField;
import cloud.xcan.sdf.extension.angustester.api.ApiImportSource;
import cloud.xcan.sdf.core.angustester.domain.mock.apis.MockApisSource;
import cloud.xcan.sdf.model.remoting.vo.MockApiResponseInfoVo;
import cloud.xcan.sdf.spec.http.HttpMethod;
import io.swagger.annotations.ApiModel;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@ApiModel
@Setter
@Getter
@Accessors(chain = true)
public class MockApisDetailVo {

  private Long id;

  private Long projectId;

  private String summary;

  private String description;

  private MockApisSource source;

  private ApiImportSource importSource;

  private HttpMethod method;

  private String endpoint;

  private Long mockServiceId;

  private String mockServiceName;

  private String mockServiceDomainUrl;

  private String mockServiceHostUrl;

  private Long assocProjectId;

  @NameJoinField(id = "assocProjectId", repository = "projectRepo")
  private String assocProjectName;

  private Long assocApisId;

  //@NameJoinField(id = "assocApisId", repository = "apisBaseInfoRepo")
  private String assocApisName;

  private long requestNum;

  private long pushbackNum;

  private long simulateErrorNum;

  private long successNum;

  private long exceptionNum;

  private Boolean inconsistentOperationFlag;

  private Boolean assocApisDeletedFlag;

  private HttpMethod apisMethod;

  private String apisEndpoint;

  private List<MockApiResponseInfoVo> responses;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;

}
