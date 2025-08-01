import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/ai';
  }

  getChartResult (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/chat/result`, params);
  }
}
