import { Method } from '@/views/apis/services/apiHttp/interface';
import { i18n } from '@xcan-angus/infra';

const t = i18n.getI18n()?.global?.t || ((value: string):string => value);

/**
 * Drawer types enum for type safety
 * Defines all possible drawer keys used in the application
 */
export enum DrawerType {
  // API related drawers
  API_INFO = 'apiInfo',
  PERFORMANCE = 'performance',
  ACTIVITY = 'activity',
  TEST_INFO = 'testInfo',
  SHARE_LIST = 'shareList',
  PROXY = 'proxy',
  CODE = 'code',
  API_MOCK = 'apiMock',
  CASE = 'case',

  // Service related drawers
  SERVICE_INFO = 'serviceInfo',
  OPENAPI = 'openapi',
  SYNC_CONFIG = 'syncConfig',
  SECURITY = 'security',
  SERVER_CONFIG = 'serverConfig',
  TAG = 'tag',
  COMPONENT = 'componnet',
  SOCKET_CONFIG = 'socketConfig'
}

export type NavItem = {
  icon: string,
  name: string,
  value: string,
  auth?: 'MODIFY' | 'GRANT' | 'VIEW ' | 'SHARE',
  disabled?: boolean,
}

export interface DataSourceType {
  endpoint: string,
  method: Method,
  summary: string,
  favourite: boolean,
  id: string,
  show: boolean,
  auth:boolean,
  protocol: {
    value: string;
    message: string;
  };
  status?: { value: string };
  serviceId?: string;
  serviceName?: string;
}

export interface StateType {
  allData: Array<DataSourceType>,
  dataSource: Array<DataSourceType>,
  id: string,
  name: string,
  order: Record<string, string | undefined>,
  serviceId: string,
  showGroupList: boolean,
  drawerComp: Array<NavItem>,
  groupedBy?: string,
  type?: 'API' | 'WEBSOCKET';
  serviceAuth: boolean
}
export const navs: NavItem[] = [
  {
    icon: 'icon-fuwuxinxi',
    name: t('service.apis.navs.apiInfo'),
    value: DrawerType.API_INFO,
    disabled: true,
    auth: 'MODIFY'
  },
  {
    icon: 'icon-zhibiao',
    name: t('service.apis.navs.performance'),
    value: DrawerType.PERFORMANCE,
    disabled: true,
    auth: 'MODIFY'
  },
  {
    icon: 'icon-zhihangceshi',
    name: t('service.apis.navs.testInfo'),
    value: DrawerType.TEST_INFO,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-yongliku',
    name: t('common.useCase'),
    value: DrawerType.CASE,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-lishijilu',
    name: t('service.apis.navs.activity'),
    value: DrawerType.ACTIVITY,
    auth: 'VIEW ',
    disabled: true
  },
  {
    icon: 'icon-jiekoudaili',
    name: t('service.apis.navs.agent'),
    value: DrawerType.PROXY,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-daimashitu',
    name: t('service.apis.navs.code'),
    value: DrawerType.CODE,
    auth: 'VIEW ',
    disabled: true
  },
  {
    icon: 'icon-mockjiedian',
    name: t('service.apis.navs.apiMock'),
    value: DrawerType.API_MOCK,
    auth: 'VIEW ',
    disabled: true
  }
];

export const serviceNav: NavItem[] = [
  {
    icon: 'icon-fuwuxinxi',
    name: t('service.apis.serviceNav.projectInfo'),
    value: DrawerType.SERVICE_INFO,
    auth: 'GRANT',
    disabled: true
  },
  {
    icon: 'icon-wendangxinxi',
    name: t('service.apis.serviceNav.openapi'),
    value: DrawerType.OPENAPI,
    auth: 'GRANT',
    disabled: true
  },
  {
    icon: 'icon-peizhifuwutongbu',
    name: t('service.apis.serviceNav.syncConfig'),
    value: DrawerType.SYNC_CONFIG,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-renzhengtou',
    name: t('service.apis.serviceNav.security'),
    value: DrawerType.SECURITY,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-host',
    name: t('service.apis.serviceNav.serverConfig'),
    value: DrawerType.SERVER_CONFIG,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-lishijilu',
    name: t('service.apis.serviceNav.activity'),
    auth: 'VIEW ',
    value: DrawerType.ACTIVITY,
    disabled: true
  },
  {
    icon: 'icon-jiekoudaili',
    name: t('service.apis.serviceNav.agent'),
    value: DrawerType.PROXY,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-biaoqian',
    name: t('common.tag'),
    value: DrawerType.TAG,
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-zujian',
    name: t('service.apis.serviceNav.componnet'),
    value: DrawerType.COMPONENT,
    auth: 'MODIFY',
    disabled: true
  }
];

export const socketNavs: NavItem[] = [
  {
    icon: 'icon-fuwuxinxi',
    name: t('service.apis.socketNavs.apiInfo'),
    value: DrawerType.API_INFO,
    auth: 'MODIFY'
  },
  {
    icon: 'icon-lishijilu',
    name: t('service.apis.socketNavs.activity'),
    value: DrawerType.ACTIVITY,
    auth: 'VIEW ',
    disabled: true
  },
  {
    icon: 'icon-jiekoudaili',
    name: t('service.apis.socketNavs.agent'),
    value: DrawerType.PROXY,
    auth: 'MODIFY',
    disabled: true
  }
];
