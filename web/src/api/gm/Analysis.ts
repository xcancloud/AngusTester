import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class API {
  constructor (prefix: string) {
    baseUrl = prefix + '/analysis';
  }

  loadCustomizationSummary (params) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/customization/summary`, params);
  }
}
