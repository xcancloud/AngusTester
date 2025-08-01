import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class User {
  constructor (prefix: string) {
    baseUrl = prefix + '/user';
  }

  getUserList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }
}
