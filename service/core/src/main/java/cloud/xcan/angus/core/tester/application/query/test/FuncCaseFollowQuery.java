package cloud.xcan.angus.core.tester.application.query.test;

import cloud.xcan.angus.core.tester.domain.test.follow.FuncCaseFollowP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FuncCaseFollowQuery {

  Page<FuncCaseFollowP> list(Long projectId, String name, PageRequest pageable);

  Long count(Long projectId);

}




