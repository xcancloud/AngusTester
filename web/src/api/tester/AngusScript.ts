import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/angus';
  }

  loadDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/script/${id}`);
  }
}
