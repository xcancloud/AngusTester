package cloud.xcan.angus.core.tester.interfaces.issue.facade;

import cloud.xcan.angus.api.enums.Priority;
import cloud.xcan.angus.api.enums.Result;
import cloud.xcan.angus.core.tester.domain.issue.TaskType;
import cloud.xcan.angus.core.tester.interfaces.test.facade.vo.FuncCaseListVo;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskAddDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskAssigneeReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskAttachmentReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskConfirmerReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskDescriptionReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskFindDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskImportDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskMoveDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskTagReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.dto.TaskWorkloadReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.vo.TaskDetailVo;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.vo.TaskInfoVo;
import cloud.xcan.angus.core.tester.interfaces.issue.facade.vo.TaskListVo;
import cloud.xcan.angus.core.tester.interfaces.version.facade.dto.SoftwareVersionRefReplaceDto;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface TaskFacade {

  IdKey<Long, Object> add(TaskAddDto dto);

  void update(Long id, TaskUpdateDto dto);

  void replace(Long id, TaskReplaceDto dto);

  void rename(Long id, String name);

  void move(TaskMoveDto dto);

  void replaceType(Long id, String type);

  void replaceBugLevel(Long id, String bugLevel);

  void replaceMissingBug(Long id, Boolean missingBug);

  void replaceAssignee(Long id, TaskAssigneeReplaceDto dto);

  void replaceConfirmer(Long id, TaskConfirmerReplaceDto dto);

  void replaceTag(Long id, TaskTagReplaceDto dto);

  void replaceDeadline(Long id, LocalDateTime deadlineDate);

  void replacePriority(Long id, Priority priority);

  void replaceSoftwareVersion(Long id, SoftwareVersionRefReplaceDto version);

  void replaceEvalWorkload(Long id, TaskWorkloadReplaceDto dto);

  void replaceActualWorkload(Long id, TaskWorkloadReplaceDto dto);

  void replaceDescription(Long id, TaskDescriptionReplaceDto dto);

  void replaceAttachment(Long id, TaskAttachmentReplaceDto dto);

  void start(Long id);

  void cancel(Long id);

  void processed(Long id);

  void confirm(Long id, Result result, BigDecimal evalWorkload, BigDecimal actualWorkload);

  void complete(Long id, BigDecimal evalWorkload, BigDecimal actualWorkload);

  void subtaskSet(Long id, HashSet<Long> subTaskIds);

  void subtaskCancel(Long id, HashSet<Long> subTaskIds);

  List<TaskInfoVo> notAssociatedSubtask(Long id, Long moduleId);

  void taskAssocAdd(Long id, HashSet<Long> assocTaskIds);

  void taskAssocCancel(Long id, HashSet<Long> assocTaskIds);

  List<TaskInfoVo> notAssociatedTask(Long id, Long moduleId, TaskType taskType);

  void caseAssocAdd(Long id, HashSet<Long> assocCaseIds);

  void caseAssocCancel(Long id, HashSet<Long> assocCaseIds);

  List<FuncCaseListVo> notAssociatedCase(Long id, Long moduleId);

  void restart(Long id);

  void reopen(Long id);

  List<IdKey<Long, Object>> imports(TaskImportDto dto);

  List<IdKey<Long, Object>> importExample(Long projectId);

  void delete(Collection<Long> ids);

  TaskDetailVo detail(Long id);

  PageResult<TaskListVo> list(boolean export, TaskFindDto dto);

  ResponseEntity<Resource> export(TaskFindDto dto, HttpServletResponse response);

}
