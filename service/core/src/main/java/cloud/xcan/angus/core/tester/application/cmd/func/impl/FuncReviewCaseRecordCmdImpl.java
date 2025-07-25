package cloud.xcan.angus.core.tester.application.cmd.func.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.tester.application.cmd.func.FuncReviewCaseRecordCmd;
import cloud.xcan.angus.core.tester.domain.func.review.record.FuncReviewCaseRecord;
import cloud.xcan.angus.core.tester.domain.func.review.record.FuncReviewCaseRecordRepo;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * Command implementation for functional review case records.
 * <p>
 * Provides methods for adding and deleting review case records.
 * <p>
 * Ensures batch operations and repository access.
 */
@Biz
public class FuncReviewCaseRecordCmdImpl extends CommCmd<FuncReviewCaseRecord, Long>
    implements FuncReviewCaseRecordCmd {

  @Resource
  private FuncReviewCaseRecordRepo funcReviewCaseRecordRepo;

  /**
   * Add a batch of review case records.
   * <p>
   * Batch inserts review case records for persistence.
   */
  @Override
  public void add0(List<FuncReviewCaseRecord> records) {
    batchInsert0(records);
  }

  /**
   * Delete review case records by review case IDs.
   * <p>
   * Removes all records associated with the given review case IDs.
   */
  @Override
  public void deleteByReviewCaseIdIn(Collection<Long> reviewCaseIds) {
    funcReviewCaseRecordRepo.deleteByReviewCaseIdIn(reviewCaseIds);
  }

  /**
   * Get the repository for functional review case records.
   * <p>
   * Used by the base command class for generic operations.
   */
  @Override
  protected BaseRepository<FuncReviewCaseRecord, Long> getRepository() {
    return funcReviewCaseRecordRepo;
  }
}
