import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/apis';
  }

  // 克隆
  patchClone (params:string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${params}/clone`, { id: params });
  }

  // 移动
  patchMove (params: {apiIds: string[], targetProjectId: string}): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/move`, params);
  }

  del (params: { ids: string[] }): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}`, params);
  }

  loadInfo (id:string, resolveRef = false, axiosConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`, { resolveRef }, axiosConf);
  }

  // 添加 socketapi
  putApi (params:any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}`, params);
  }

  // 添加保存
  addApi (params:any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/archive`, params);
  }

  // 修改保存
  updateApi (params:any): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}`, params);
  }

  //  添加收藏
  addFavourite (params:any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${params}/favourite`, {});
  }

  // 取消收藏
  cancelFavourite (id:string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/favourite`, {});
  }

  // 保存接口性能指标
  updatePerf (params:any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/perf?id=${params.id}`, params);
  }

  delNotArchiveList (): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/archive`);
  }

  searchList (params: any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/search`, params);
  }

  // 取消接口性能指标
  cancelPerf (id:string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${id}/perf/cancel`);
  }

  // 查询接口性能指标
  loadPerf (id:string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/perf`);
  }

  addNewApi (params:any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}`, params);
  }

  // 获取接口权限
  loadApiAuthority (params: any, axiosConfig = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/auth`, params, axiosConfig);
  }

  // 更改是否有权限控制
  updateAuthFlag (params: any): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/${params.id}/auth/enabled?enabled=${params.enabled}`);
  }

  addAuth (id: string, params: any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/auth`, params);
  }

  // 增加关注
  addWatch (id:string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/follow`);
  }

  // 取消关注
  cancelWatch (id:string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/follow`, {});
  }

  // 修改权限
  updateAuth (authId: string, params: any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/auth/${authId}`, params);
  }

  // 删除授权
  delAuth (authId: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/auth/${authId}`);
  }

  // 执行测试信息
  loadTest (id:string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/tester`);
  }

  // 查询分配测试人
  loadDistrbutionTest (params:any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${params.id}/test`);
  }

  // 修改分配测试人
  putDistrubutionTest (params:any): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${params.id}/test`, params.dto);
  }

  // 重新测试
  retest (id: string): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/${id}/test/task/restart`);
  }

  // 重新打开测试任务
  reOpen (id: string): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/${id}/test/task/reopen`);
  }

  deleteTest (id: string, testTypes: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/test/task`, { testTypes });
  }

  // 查询操作权限
  loadActionAuth ({ id = '', userId = '' }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/user/${userId}/auth`);
  }

  // 查看个人权限
  loadUserAuth (id: string, admin = true): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/user/auth/current`, { admin });
  }

  // 查询
  checkAuth ({ id = '', authPermission = '', userId = '' }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/auth/${authPermission}/check`, {
      userId
    });
  }

  // 添加用例
  addCase (params: {[key: string]: any}): Promise<[Error | null, any]> {
    const { apisId, ...param } = params;
    return http.post(`${baseUrl}/${apisId}/case`, param);
  }

  addApisCase (params: any[] = []): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/case`, params);
  }

  // 详情
  loadCaseInfo (caseId: string, axiosConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/case/${caseId}`, null, axiosConf);
  }

  // 修改
  patchCase (params = {}): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/case`, params);
  }

  replaceCase (params:any[] = []): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/case`, params);
  }

  // case list API
  loadApiCases (params): Promise<[Error | null, any]> {
    const { ...param } = params;
    return http.get(`${baseUrl}/case/search`, param);
  }

  // 用例
  // 删除
  delCase (caseIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/case`, caseIds, {
      dataType: true
    });
  }

  // 克隆
  cloneCase (ids: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/case/clone`, ids);
  }

  // 执行用例
  execCase (caseIds: string[], apisId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${apisId}/case/exec`, { caseIds }, {
      paramsType: true
    });
  }

  // 启用禁用用例
  enabledCase (enabled: boolean, caseIds: string[]) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/case/enabled`, {
      enabled,
      ids: caseIds
    }, {
      paramsType: true
    });
  }

  // 保存 name
  updateCaseName (params: {id: string, name: string}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/case/${params.id}/name?name=${params.name}`);
  }

  // 保存 等级
  updateCasePriority (params: {id: string, priority: string}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/case/${params.id}/priority?priority=${params.priority}`);
  }

  // 收藏
  // 取消全部收藏 /api/v1/apis/favourite/cancelAll
  cancelAllFavourite (): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/favourite`, {});
  }

  // 获取分享历史列表
  loadShareList (params:any): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/share/search`, params);
  }

  // 分享详情
  loadShareInfo (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/share/${id}`);
  }

  // 添加分享
  addShare (params:any): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/share`, params);
  }

  // 删除分享
  delShare (id:string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/share`, { ids: [id] });
  }

  // 修改
  patchShared (params:any): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/share`, params);
  }

  // 获取关注列表
  loadWatchList (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/follow/search`, params);
  }

  // 取消全部关注
  cancelWatchAll (): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/follow`, {});
  }

  // 获取关注列表
  loadFavouriteList (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/favourite/search`, params);
  }

  loadApiInfoList (ids:string[]): Promise<[Error | null, any]> {
    return http.get(baseUrl + '/list/detail', { ids });
  }

  // 修改接口状态
  patchStatus ({ id = '', status = '' }): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/${id}/status?status=${status}`);
  }

  addMockApiByApiId (apisId: string, params:{mockServiceId:string, summary?:string}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${apisId}/association/mock/apis`, params);
  }

  getMockApiByApiId (apisId:string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${apisId}/association/mock/apis`);
  }

  // 生成测试脚本
  putApiScript (id: string, params: {duration: string; priority: string; testType: string; threads: string}[]): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${id}/test/script/generate`, params);
  }

  // 删除测试脚本
  delApiScript (id: string, testTypes: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/test/script`, { testTypes });
  }

  // 更新测试脚本
  updateApiScript (id: string, params: {duration: string; priority: string; testType: string; threads: string}[]): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${id}/test/script/update`, params);
  }

  // 获取回收站数据
  getTrashData (params: {targetType: 'API'|'SERVICE', [key: string]: any}) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/trash/search`, { ...params });
  }

  // 删除回收站数据
  delTrash (id: string) : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/trash/${id}`);
  }

  // 删除回收站所有数据
  delAllTrash <T> (params: T) : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/trash`, params);
  }

  // 还原回收站所有数据
  backAllTrash <T> (params: T, axioConf = {}) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/trash/back`, params, axioConf);
  }

  // 还原数据
  backTrash (id: string) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/trash/${id}/back`);
  }

  delUnarchived (id: string) : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/unarchived/${id}`);
  }

  delAllUnarchived () : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/unarchived`);
  }

  getDesignList <T> (params: T, axioConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/design/search`, params, axioConf);
  }

  addDesign <T> (params: T, axioConf = {}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/design`, params, axioConf);
  }

  updateDesign <T> (params: T, axioConf = {}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/design`, params, axioConf);
  }

  getDesignInfo (designId: string) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/design/${designId}`);
  }

  deleteDesign (designId: string) : Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/design/${designId}`);
  }

  exportDesign  (params: {id: string, format: 'json'|'yaml'}, axioConf = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/design/export`, params, axioConf);
  }

  cloneDesign (designId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/design/${designId}/clone`);
  }

  releaseDesign (designId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/design/${designId}/release`);
  }

  generateServiceFromDesign (designId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/design/${designId}/service/generate`);
  }

  importDesign (params: {projectId: string, name: string, content: string}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/design/import`, params);
  }

  putDesignContent (params: {id: string, openapi: string}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/design/import`, params)
  }

}
