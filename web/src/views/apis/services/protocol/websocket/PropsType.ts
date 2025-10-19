import { API_EXTENSION_KEY } from '@/utils/apis';

const { valueKey, enabledKey } = API_EXTENSION_KEY;

export interface FormData {
  name?: string;
  [valueKey]?: string;
  description?: string;
  [enabledKey]?: boolean;
  in: string;
  schema: Record<string, any>;
  $ref?: string;
}

export const getDefaultForm = (form = {}) => {
  return {
    name: '',
    [valueKey]: '',
    description: '',
    in: 'query',
    [enabledKey]: true,
    schema: {
      type: 'string'
    },
    ...form
  };
};

export interface Message {
  type: 'receive'|'send'|'connect'|'close'|'sendErr'|'connectErr'|'closeErr',
  date: string,
  size: string,
  content?: string,
  showContent?: boolean;
  key: string;
}
