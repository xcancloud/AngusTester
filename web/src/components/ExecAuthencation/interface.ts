
import { useI18n } from 'vue-i18n';
const { t } = useI18n();

export interface AuthItem {
  name?: string;
  type: 'http'|'apiKey'|'oauth2'|'extends'|null;
  flows?: Record<string, any>;
  in?: string;
  scheme?: string;
  $ref?: string;
  value?: string;
  apiKeys?: {name: string; in: string; value: string}[];
  oauth2?: {clientCredentials?: Record<string, string|string[]>, password?: Record<string, string|string[]>, newToken?: boolean };

}

export const inOpt = [
  {
    value: 'query',
    label: 'query'
  },
  {
    value: 'header',
    label: 'header'
  }
  // {
  //   value: 'cookie',
  //   label: 'cookie'
  // }
];

export const getAuthItem = () => {
  return {
    name: '',
    in: 'header',
    value: ''
  };
};

export const authTypeOptions = [
  {
    label: t('xcan_execAuthencation.noAuth'),
    value: null
  },
  {
    label: t('xcan_execAuthencation.basicAuth'),
    value: 'basic'
  },
  {
    label: t('xcan_execAuthencation.bearerToken'),
    value: 'bearer'
  },
  {
    label: t('xcan_execAuthencation.apiKey'),
    value: 'apiKey'
  },
  {
    label: t('xcan_execAuthencation.oauth2'),
    value: 'oauth2'
  },
  // {
  //   label: 'openIdConnect',
  //   value: 'openIdConnect'
  // },
  {
    label: t('xcan_execAuthencation.public'),
    value: 'extends'
  }
];

export const authInOpt = [
  {
    label: t('xcan_execAuthencation.sendViaBasicAuthHeader'),
    value: 'BASIC_AUTH_HEADER'
  },
  {
    label: t('xcan_execAuthencation.sendViaRequestBody'),
    value: 'REQUEST_BODY'
  }
];

export const flowAuthType = [
  // {
  //   value: 'authorizationCode',
  //   label: '授权码模式（Authorization Code）'
  // },
  {
    value: 'password',
    label: t('xcan_execAuthencation.passwordMode')
  },
  // {
  //   value: 'implicit',
  //   label: '隐式模式（Implicit）'
  // },
  {
    value: 'clientCredentials',
    label: t('xcan_execAuthencation.clientCredentialsMode')
  }
  // {
  //   value: 'authorizationCodePKCE',
  //   label: '授权码模式-带PKCE（Authorization Code PKCE）'
  // }
];

// authorizationUrl 授权URL
// tokenUrl 访问令牌URL
// refreshUrl 刷新令牌URL
// scopes 访问范围

// x-xc-oauth2-clientId
// x-xc-oauth2-clientSecret
// x-xc-oauth2-callbackUrl
// x-xc-oauth2-username
// x-xc-oauth2-password

export const authLabels = [
  {
    valueKey: 'authorizationUrl',
    label: t('xcan_execAuthencation.authorizationUrl'),
    maxLength: 400,
    required: true
  },
  {
    valueKey: 'x-xc-oauth2-callbackUrl',
    label: t('xcan_execAuthencation.callbackUrl'),
    maxLength: 400,
    required: true
  },
  {
    valueKey: 'tokenUrl',
    label: t('xcan_execAuthencation.accessTokenUrl'),
    maxLength: 400,
    required: true
  },
  {
    valueKey: 'refreshUrl',
    label: t('xcan_execAuthencation.refreshTokenUrl'),
    maxLength: 400
  },
  {
    valueKey: 'clientId',
    label: t('xcan_execAuthencation.clientId'),
    maxLength: 400,
    required: true
  },
  {
    valueKey: 'clientSecret',
    label: t('xcan_execAuthencation.clientSecret'),
    maxLength: 1024,
    required: true
  },
  {
    valueKey: 'username',
    label: t('xcan_execAuthencation.username'),
    maxLength: 400,
    required: true
  },
  {
    valueKey: 'password',
    label: t('xcan_execAuthencation.password'),
    maxLength: 1024,
    required: true
  },
  {
    valueKey: 'challengeMethod',
    label: t('xcan_execAuthencation.encryptionMethod')
  },
  {
    valueKey: 'codeVerifier',
    label: t('xcan_execAuthencation.verificationCode')
  },
  {
    valueKey: 'scopes',
    label: t('xcan_execAuthencation.scope'),
    maxLength: 200
  }
  // {
  //   valueKey: 'state',
  //   label: '状态'
  // }
];

export const encryptionTypeOpt = [
  {
    value: 'SHA-256',
    label: 'SHA-256'
  },
  {
    value: 'Plain',
    label: 'Plain'
  }
];

export const authorizationCode = [
  'authorizationUrl',
  'callbackUrl',
  'tokenUrl',
  'refreshUrl',
  'clientId',
  'clientSecret',
  'scopes'
];
export const password = [
  'tokenUrl',
  'refreshUrl',
  'clientId',
  'clientSecret',
  'username',
  'password',
  'scopes'
];
export const implicit = [
  'authorizationUrl',
  'callbackUrl',
  // 'tokenUrl',
  'refreshUrl',
  'clientId',
  'clientSecret',
  'scopes'
];
export const clientCredentials = [
  'tokenUrl',
  'refreshUrl',
  'clientId',
  'clientSecret',
  'scopes'
];
export const authorizationCodePKCE = [
  'authorizationUrl',
  'callbackUrl',
  'tokenUrl',
  'refreshUrl',
  'clientId',
  'clientSecret',
  'challengeMethod',
  'codeVerifier',
  'scopes'
];

export const flowAuthKeys = {
  authorizationCode,
  password,
  implicit,
  clientCredentials,
  authorizationCodePKCE
};

const getApiKeyData = (dataSource) => {
  // const { extensions } = dataSource;
  // const first = { name: dataSource.name, in: dataSource.in, [valueKey]: dataSource[valueKey] };
  // const others = dataSource[securityApiKeyPerfix] || [];
  const queryAuth = {};
  const queryList = dataSource.filter(item => item.in === 'query');
  queryList.forEach(item => {
    queryAuth[item.name] = item.value;
  });
  const headerAuth = {};
  const headerList = dataSource.filter(item => item.in === 'header');
  headerList.forEach(item => {
    headerAuth[item.name] = item.value;
  });
  return [headerAuth, queryAuth];
};

const getShowApiKeyData = (dataSource) => {
  const queryAuth = {};
  const queryList = [dataSource.apiKeys || []].filter(item => item.in === 'query');
  queryList.forEach(item => {
    queryAuth[item.name] = item.value;
  });
  const headerAuth = {};
  const headerList = [dataSource.apiKeys || []].filter(item => item.in === 'header');
  headerList.forEach(item => {
    headerAuth[item.name] = item.value;
  });
  return [headerAuth, queryAuth];
};

const getExecShowAuthData = async (dataSource) => {
  switch (dataSource.type) {
    case 'http':
      return [{
        Authorization: dataSource.value
      }];
    case 'apiKey':
      return getShowApiKeyData(dataSource);
    case 'extends':
      return [{}];
    case 'oauth2':
      if (dataSource.oauth2) {
        if (dataSource.newToken) {
          return [{ access_token: '' }];
        } else {
          return [{ access_token: dataSource.value }];
        }
      } else {
        return [{}];
      }
      break;
  }
};

export {
  getApiKeyData,
  getShowApiKeyData,
  getExecShowAuthData
};

export default {
  getApiKeyData,
  getShowApiKeyData,
  getExecShowAuthData
};
