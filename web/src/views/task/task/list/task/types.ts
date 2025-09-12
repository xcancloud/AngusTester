import { Priority } from '@xcan-angus/infra';
import { TaskType, TestType, BugLevel } from '@/enums/enums';
import { TaskInfo } from '@/views/task/types';

export type EditFormState = {
  assigneeId: string | undefined;
  deadlineDate: string | undefined;
  name: string | undefined;
  priority: Priority;
  projectId: string | undefined;
  sprintId: string | undefined;
  taskType: TaskType;
  moduleId?: string | undefined;
  confirmerId?: string | undefined;
  attachments?: {
    name: string;
    url: string;
  }[] | undefined;
  targetParentId?: string | undefined;
  targetId?: string | undefined;
  parentTaskId?: string | undefined;
  tagIds?: string[] | undefined;
  refTaskIds?: string[] | undefined;
  refCaseIds?: string[] | undefined;
  description?: string | undefined;
  evalWorkload?: string | undefined;
  actualWorkload?: string | undefined;
  testType?: TestType;
  bugLevel?: BugLevel;
  testerId?: string;
  missingBug?: boolean;
  softwareVersion?: string;

  // Temp for edit
  userInfo?: { id: string; };
  appInfo?: { id: string; };
}

export type TaskInfoProps = {
  projectId: string;
  userInfo: { id: string; fullName: string};
  appInfo: { id: string; };
  dataSource: TaskInfo;
  taskId?: string;
  loading?: boolean;
  notify?: string;
}
