
import { BasicAssertionType, EnumMessage, HttpMethod, AssertionCondition } from '@xcan-angus/infra';

export type ApiInfo = {
  projectId: string;
  apisId: string;
  id: string;
  endpoint: string;
  server?: { [key: string]: any };
  description: string;
  summary: string;
  method: EnumMessage<HttpMethod>;
  parameters: {
    name: string;
    in: string;
    description: string;
    enabled:boolean;
  }[];
  requestBody: {
    $ref:string;
    description: string;
    content: {
      [key:string]: {
        schema: {[key:string]:any};
        exampleSetFlag: boolean;
        'x-xc-value': string;
      }
    };
    required: boolean;
  };
  authentication:{
    type: string;
    enabled: boolean;
    'x-xc-value': string;
    'x-scheme': string;
    $ref?:string;
  };
  assertions: {
    name: string;
    enabled: boolean;
    type: EnumMessage<BasicAssertionType>;
    expected: string;
    assertionCondition: EnumMessage<AssertionCondition>;
    expression: string;
    description: string;
    parameterName: string;
    condition: string;
    extraction: {
      method: { value: string; message: string; };
      expression: string;
      matchItem: string;
      defaultValue: string;
      location: string;
      parameterName: string;
    };
  }[];
  resolvedRefModels: { [key: string]: string };
  availableServers: {
    url: string;
    description?: string;
    variables?: {
      [key: string]: {
        default: string;
        description: string;
        enum: string[];
      }
    };
  }[];
}
