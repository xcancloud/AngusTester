import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class User {
  constructor (prefix: string) {
    baseUrl = prefix + '/user';
  }

  getUserList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }
}
