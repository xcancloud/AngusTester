import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/mock';
  }

  loadFunction (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/functions`);
  }

  loadFunctionValue (params): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/text/data/batch`, params);
  }

  addFunctionBatch (params: {function:string, iterations: number, name:string}[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/function/batch`, params);
  }

  addMockTest (params: { iterations: 1, text:string}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/text`, params);
  }

  getFileScriptList (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/file/script`, params);
  }

  loadMockApisSample (params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis/sample`, params);
  }

  loadServiceSamples (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service/sample`);
  }

  resetInstance (id:string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/service/${id}/instance/sync`);
  }

  loadMockApisSearch (params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis`, { ...params, fullTextSearch: true });
  }

  loadMockApis (params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis`, params);
  }

  addMockApi (params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/apis`, params);
  }

  updateMockApi (params: any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/apis`, params);
  }

  loadMockApiInfo (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis/${id}`);
  }

  deleteMockApi (params: any): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/apis`, params, { paramsType: true });
  }

  cloneMockApi (id: string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/apis/${id}/clone`);
  }

  loadMockApiResponse (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis/${id}/response`);
  }

  addMockApiResponse (id: string, params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/apis/${id}/response`, params);
  }

  updateMockApiResponse (id: string, params: any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/apis/${id}/response`, params);
  }

  loadMockApiLogInfo (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/apis/log/${id}`);
  }

  syncMockApiInstance (id: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/apis/${id}/instance/sync`);
  }

  importDemoMockApi (mockServiceId:string) {
    return http.post(`${baseUrl}/service/${mockServiceId}/example/apis/import`);
  }

  loadServices (params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service`, { ...params, fullTextSearch: true });
  }

  addService (params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service`, params);
  }

  addAngusService (params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/association/services`, params);
  }

  addFileService (params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/file/import`, params);
  }

  patchService (params: any): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/service`, params);
  }

  loadServiceInfo (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service/${id}`);
  }

  loadServiceApiIds (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service/${id}/association/apis/id`);
  }

  addServicesMockService (mockServiceId: string, projectId:string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/service/association/${mockServiceId}/${projectId}`);
  }

  cancelServiceMock (id: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/service/${id}/association`);
  }

  cancelMockApi (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/apis/association?`, { ids });
  }

  addApiMockServiceApi (mockApiId: string, apisId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/apis/association/${mockApiId}/${apisId}`);
  }

  postApiMockServiceApi (mockServiceId: string, apisId: string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/apis/association/${mockServiceId}/${apisId}`);
  }

  copyApiMockServiceApi (mockServiceId: string, apisId: string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/apis/copy/${apisId}/${mockServiceId}`);
  }

  deleteService (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/service`, { ids }, { paramsType: true });
  }

  deleteForceService (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/service?force=true`, { ids }, { paramsType: true });
  }

  startService (ids: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/start`, ids);
  }

  stopService (ids: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/stop`, ids);
  }

  loadMockApiLogs (serviceId: string, params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service/${serviceId}/apis/log`, { ...params, fullTextSearch: true });
  }

  loadServiceAuth (serviceId: string, params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/service/${serviceId}/auth`, params);
  }

  checkHasAuth (params: any): Promise<[Error | null, any]> {
    return http.get(
      `${baseUrl}/service/${params.serviceId}/auth/${params.permission}/check`,
      { userId: params.userId }
    );
  }

  addServiceAuth (id: string, params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/${id}/auth`, params);
  }

  patchEnabled (id: string, enabled: boolean): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/service/${id}/auth/enabled`, { enabled: enabled }, { paramsType: true });
  }

  putServiceAuth (id: string, params: any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/service/auth/${id}`, params);
  }

  deleteServiceAuth (id: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/service/auth/${id}`);
  }

  mockData (params: {configuration: Record<string, any>; mockData: Record<string, any>}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/data/script`, params);
  }

  importService (formData: FormData): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/service/import`, formData);
  }

  execMockData (params: {configuration: Record<string, any>; mockData: Record<string, any>, plugin: string, scriptId: string, projectId: string}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/data/execution`, params);
  }

  getGenerateScriptContent (params: Record<string, any>): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/data/script/content`, params);
  }
}
