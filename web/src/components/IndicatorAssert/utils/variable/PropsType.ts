import { Extraction, ExtractionMethod } from '../extract/PropsType';

export type TargetType = 'API' | 'DIR' | 'PROJECT' | 'SCENARIO' | 'SERVICE' | 'TASK' | 'VARIABLE'

export type Scope = 'CURRENT' | 'GLOBAL'

export type VariableItem = {
  id: string;
  targetId: string;
  targetType: {
    value: TargetType;
    message: string
  };
  targetName: string;
  elementName: string;
  inherited: string;
  scope: {
    value: Scope;
    message: string;
  };
  name: string;
  description: string;
  enabled: boolean;
  value: string;
  extraction: Extraction & {
    method: {
      message: string;
      value: ExtractionMethod
    }
  };
  lastModifiedBy: string;
  lastModifiedDate: string;
}
