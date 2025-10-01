import { EnumMessage, EvalWorkloadMethod, Priority, ReviewStatus } from '@xcan-angus/infra';
import { CaseStepView, CaseTestResult } from '@/enums/enums';
import { AttachmentInfo, ProgressInfo, TagInfo } from '@/types/types';
import { TaskInfo } from '@/views/issue/types';

export type PlanProps = {
  projectId: string;
  userInfo: { id: string; };
  notify: string;
  planId?: string;
}

export type CaseInfo = {
  id: string;
  name: string;
  code: string;
  description: string;
  projectId: string;
  planId: string;
  planName: string;
  moduleId: string;
  moduleName: string;
  softwareVersion: string;
  priority: EnumMessage<Priority>;
  deadlineDate: string;
  overdue: boolean;
  evalWorkloadMethod: EnumMessage<EvalWorkloadMethod>;
  evalWorkload: number;
  actualWorkload: number;
  review: boolean;
  reviewerId: string;
  reviewerName: string;
  reviewDate: string;
  reviewStatus: EnumMessage<ReviewStatus>;
  reviewRemark: string;
  reviewNum: string;
  reviewFailNum: string;
  testerId: string;
  testerName: string;
  developerId: string;
  developerName: string;
  testNum: string;
  testFailNum: string;
  testResult: EnumMessage<CaseTestResult>;
  testRemark: string;
  testResultHandleDate: string;
  favourite: boolean;
  follow: boolean;
  tenantId?: string;
  createdBy: string;
  createdByName: string;
  avatar: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;
  tags: TagInfo[];
}

export type CaseTestStep = {
  step: string;
  expectedResult: string;
}

export type CaseDetail = {
  id: string;
  name: string;
  code: string;
  description: string;
  projectId: string;
  planId: string;
  planName: string;
  moduleId: string;
  moduleName: string;
  softwareVersion: string;
  priority: EnumMessage<Priority>;
  precondition: string;
  stepView: EnumMessage<CaseStepView>;
  steps: CaseTestStep[];
  deadlineDate: string;
  overdue: boolean;
  evalWorkloadMethod: EnumMessage<EvalWorkloadMethod>;
  evalWorkload: number;
  actualWorkload: number;
  review: boolean;
  reviewerId: string;
  reviewerName: string;
  reviewDate: string;
  reviewStatus: EnumMessage<ReviewStatus>;
  reviewRemark: string;
  reviewNum: string;
  reviewFailNum: string;
  testerId: string;
  testerName: string;
  developerId: string;
  developerName: string;
  testNum: string;
  testFailNum: string;
  testResult: EnumMessage<CaseTestResult>;
  testRemark: string;
  testResultHandleDate: string;
  attachments: AttachmentInfo[];
  tags: TagInfo[];
  refTaskInfos: TaskInfo[];
  refCaseInfos: CaseInfo[];
  allVersionCaseVos: Map<string, CaseDetail>;
  progress: ProgressInfo;
  commentNum: number;
  activityNum: number;
  favourite: boolean;
  follow: boolean;
  tenantId: string;
  createdBy: string;
  createdByName: string;
  avatar: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;
}
