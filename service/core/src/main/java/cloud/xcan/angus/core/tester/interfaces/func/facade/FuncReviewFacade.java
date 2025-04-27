package cloud.xcan.angus.core.tester.interfaces.func.facade;

import cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.FuncReviewAddDto;
import cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.FuncReviewFindDto;
import cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.FuncReviewReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.FuncReviewSearchDto;
import cloud.xcan.angus.core.tester.interfaces.func.facade.dto.review.FuncReviewUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.func.facade.vo.review.FuncReviewDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;

public interface FuncReviewFacade {

  IdKey<Long, Object> add(FuncReviewAddDto dto);

  void update(FuncReviewUpdateDto dto);

  IdKey<Long, Object> replace(FuncReviewReplaceDto dto);

  void start(Long id);

  void end(Long id);

  void block(Long id);

  IdKey<Long, Object> clone(Long id);

  void reviewReset(HashSet<Long> ids);

  void reviewRestart(HashSet<Long> ids);

  void delete(Long id);

  FuncReviewDetailVo detail(Long id);

  PageResult<FuncReviewDetailVo> list(FuncReviewFindDto dto);

  PageResult<FuncReviewDetailVo> search(FuncReviewSearchDto dto);


}
