import {http} from '@xcan-angus/tools';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/script';
  }

  // 获取脚本列表
  loadScriptList (params = {}, axiosConfig = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/search`, params, axiosConfig);
  }

  // 获取脚本列表操作权限
  loadScriptListAuth (scriptIds:string[]): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/auth/current?admin=true`, { scriptIds });
  }

  // 添加
  add (params = {}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}`, params);
  }

  // 更新
  update (params = {}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}`, params);
  }

  // 删除
  delete (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}`, { ids });
  }

  clone (id: string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/clone`);
  }

  loadDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }

  import (params: FormData): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/import`, params);
  }

  importDemo (): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/example/import`);
  }

  toggleEnabled (scriptId: string, enabled) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/${scriptId}/auth/enabled?enabled=${enabled}`);
  }

  deleteAuth (authId: string) : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/auth/${authId}`);
  }

  putAuth (authId: string, params: { permissions: string[] }) : Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/auth/${authId}`, params);
  }

  addAuth (authId: string, params: { permissions: string[], authObjectType: string }) : Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${authId}/auth`, params);
  }

  getAuth (params = {}, axiosConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/auth`, params, axiosConf);
  }
}
