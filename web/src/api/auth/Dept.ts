import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Dept {
  constructor (prefix: string) {
    baseUrl = prefix + '/dept';
  }

  searchList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/search`, params);
  }
}
