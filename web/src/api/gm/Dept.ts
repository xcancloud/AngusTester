import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Dept {
  constructor (prefix: string) {
    baseUrl = prefix + '/dept';
  }

  getDeptList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }
}
