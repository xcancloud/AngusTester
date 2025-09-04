/**
 * Script information type
 * Represents the structure of a script entity
 */
export interface ScriptInfo {
  id?: string;
  name?: string;
  description?: string;
  content?: string;
  type?: {
    value: string;
    message: string;
  };
  plugin?: string;
  source?: {
    value: string;
    message: string;
  };
  sourceId?: string;
  sourceName?: string;
  tags?: string[];
  createdByName?: string;
  lastModifiedByName?: string;
  lastModifiedDate?: string;
}

/**
 * Form state type
 * Represents the structure of script form data
 */
export interface FormState {
  name?: string;
  type?: string;
  typeName?: string;
  description?: string;
}

/**
 * Permission key type
 * Represents available permission types for scripts
 */
export type PermissionKey = 'TEST' | 'VIEW' | 'MODIFY' | 'DELETE' | 'EXPORT' | 'COLON' | 'GRANT';

/**
 * Execution information type
 * Represents the structure of execution data
 */
export type ExecInfo = {
    actualStartDate: string;
    assocApiCaseIds: string[];
    canUpdateTestResult: boolean;
    createdBy: string;
    createdByName: string;
    createdDate: string;
    currentDuration: string;
    currentDurationProgress: string;
    currentIterations: string;
    currentIterationsProgress: string;
    duration: {
        unit: string;
        value: string;
    };
    durationProgress: boolean;
    endDate: string;
    execBy: string;
    execByName: string;
    hasOperationPermission: boolean;
    id: string;
    ignoreAssertions: boolean;
    iterations: string;
    iterationsProgress: boolean;
    lastModifiedBy: string;
    lastModifiedByName: string;
    lastModifiedDate: string;
    lastSchedulingDate: string;
    lastSchedulingResult: {
        console: string[];
        deviceId: string;
        execId: string;
        exitCode: string;
        message: string;
        results: string[];
        success: boolean;
    }[];
    meterMessage: string;
    meterStatus: string;
    name: string;
    no: string;
    plugin: string;
    priority: string;
    reportInterval: {
        unit: string;
        value: string;
    };
    sampleSummaryInfo: {
        brps: string;
        bwps: string;
        duration: string;
        endTime: string;
        errorRate: string;
        errors: string;
        extCounter1: string;
        extCounter2: string;
        extCounter3: string;
        extGauge1: string;
        extGauge2: string;
        extGauge3: string;
        finish: boolean;
        iterations: string;
        n: string;
        name: string;
        operations: string;
        ops: string;
        readBytes: string;
        startTime: string;
        threadMaxPoolSize: string;
        threadPoolActiveSize: string;
        threadPoolSize: string;
        threadRunning: boolean;
        threadTerminated: boolean;
        tps: string;
        tranMax: string;
        tranMean: string;
        tranMin: string;
        tranP50: string;
        tranP75: string;
        tranP90: string;
        tranP95: string;
        tranP99: string;
        tranP999: string;
        transactions: string;
        uploadResultBytes: string;
        uploadResultProgress: string;
        uploadResultTotalBytes: string;
        writeBytes: string;
    };
    schedulingNum: string;
    scriptId: string;
    scriptName: string;
    scriptSource: string;
    scriptSourceId: string;
    scriptSourceName: string;
    scriptType: string;
    singleTargetPipeline: boolean;
    startAtDate: string;
    startMode: string;
    status: {
        value: string;
        message: string;
    };
    syncTestResult: boolean;
    syncTestResultFailure: string;
    thread: string;
    trial: boolean;
    updateTestResult: boolean;
}
