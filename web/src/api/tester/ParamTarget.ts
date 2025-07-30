import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/target';
  }

  getParamsVariableValue (apiId: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${apiId}/API/parameter/data/value`);
  }

  addVariable (targetId: string, targetType: string, ids: string[]) {
    return http.post(`${baseUrl}/${targetId}/${targetType}/variable`, ids);
  }

  deleteVariable (targetId: string, targetType: string, ids: string[], axiosConf = {}): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${targetId}/${targetType}/variable`, ids, axiosConf);
  }

  getVariable (targetId: string, targetType: string, axiosConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${targetId}/${targetType}/variable`, null, axiosConf);
  }

  addDataSet (targetId: string, targetType: string, ids: string[], axiosConf = {}) : Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${targetId}/${targetType}/dataset`, ids, axiosConf);
  }

  deleteDataSet (targetId: string, targetType: string, ids: string[], axiosConf = {}):Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${targetId}/${targetType}/dataset`, ids, axiosConf);
  }

  getDataSet (targetId: string, targetType: string, axiosConf = {}) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${targetId}/${targetType}/dataset`, null, axiosConf);
  }
}
