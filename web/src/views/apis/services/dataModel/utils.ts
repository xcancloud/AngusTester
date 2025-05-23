import { type NavItem } from '../services/apiGroup/PropsType';

export const serviceNavItem: NavItem[] = [
  {
    icon: 'icon-fuwuxinxi',
    name: '服务信息',
    value: 'projectInfo', // 1
    auth: 'GRANT',
    disabled: true
  },
  {
    icon: 'icon-wendangxinxi',
    name: '文档信息',
    value: 'openapi',
    auth: 'GRANT',
    disabled: true
  },
  {
    icon: 'icon-peizhifuwutongbu',
    name: '同步配置',
    value: 'syncConfig', // 6
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-renzhengtou',
    name: '安全方案配置',
    value: 'security', // 7
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-host',
    name: '服务器配置',
    value: 'serverConfig', // 8
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-zhibiao',
    name: '测试指标',
    value: 'performance', // 2
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-zhihangceshi',
    name: '测试信息',
    value: 'testInfo', // 3
    auth: 'VIEW',
    disabled: true
  },
  {
    icon: 'icon-lishijilu',
    name: '活动',
    auth: 'VIEW',
    value: 'activity',
    disabled: true
  },
  {
    icon: 'icon-bianliang',
    name: '变量',
    value: 'variable', // 4
    auth: 'MODIFY',
    disabled: true
  },
  // {
  //   icon: 'icon-fenxiang',
  //   name: '分享记录',
  //   value: 'shareList', // 5
  //   auth: 'MODIFY',
  //   disabled: true
  // },
  {
    icon: 'icon-jiekoudaili',
    name: '代理',
    value: 'agent',
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-biaoqian',
    name: '标签',
    value: 'tag', // 9
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-zujian',
    name: '组件',
    value: 'componnet', // componnet 10
    auth: 'MODIFY',
    disabled: true
  },
  {
    icon: 'icon-mockjiedian',
    name: 'Mock服务',
    value: 'serviceMock', // 11
    auth: 'VIEW',
    disabled: true
  }
];

// const schemaObj = JSON.parse("{\"type\":\"array\",\"items\":{\"type\":\"object\",\"nullable\":false,\"deprecated\":false,\"items\":{\"type\":\"object\",\"properties\":{\"ids\":{\"type\":\"string\",\"nullable\":false,\"deprecated\":false}}}}}");

export const parseSchemaObjToArr = (obj, requiredKeys: string[] = []) => {
  const result: any[] = [];
  if (obj.type === 'object') {
    Object.keys(obj.properties || {}).forEach(key => {
      const attrValue = obj.properties[key];
      let children: any[] = [];
      if (attrValue.type === 'array' && (attrValue.items?.type || attrValue.items?.$ref)) {
        children = parseSchemaObjToArr(attrValue, attrValue.required);
      }
      if (attrValue.type === 'object' && attrValue.properties) {
        children = parseSchemaObjToArr(attrValue, attrValue.required);
      }
      result.push({
        name: key,
        ...attrValue,
        required: requiredKeys.includes(key),
        children: children.length ? children : undefined,
        properties: undefined,
        items: undefined
      });
    });
  } else if (obj.type === 'array') {
    let children: any[] = [];
    if (obj.items?.type === 'array' && (obj.items.items?.type || obj.items.items?.$ref)) {
      children = parseSchemaObjToArr(obj.items, obj.items?.required);
    }
    if (obj.items?.type === 'object' && obj.items.properties) {
      children = parseSchemaObjToArr(obj.items, obj.items?.required);
    }
    result.push({
      name: 'items',
      ...obj,
      ...(obj.items || {}),
      type: obj.items?.type,
      children: children.length ? children : undefined,
      properties: undefined,
      items: undefined
    });
  } else {
    return [{
      ...obj,
      required: requiredKeys.includes(obj),
      properties: undefined,
      items: undefined
    }];
  }
  return result;
};

export const parseSchemaArrToObj = (arr, type) => {
  let result: {[key: string]: any} = {};
  if (arr.length === 1 && type === 'array') {
    delete arr[0].required;
    result = {
      ...arr[0]
    };

    if (arr[0].children?.length) {
      if (arr[0].type === 'array') {
        result.items = parseSchemaArrToObj(arr[0].children, arr[0].type);
      }
      if (arr[0].type === 'object') {
        result = parseSchemaArrToObj(arr[0].children, arr[0].type);
      }
    }
    result.required = [];
    delete result.children;
    delete result.name;
  } else if (arr.length === 1 && type !== 'object') {
    delete arr[0].children;
    delete arr[0].required;
    return {
      ...arr[0]
    };
  } else {
    result.type = 'object';
    result.properties = {};
    result.required = [];
    arr.forEach(attrItem => {
      if (attrItem.required === true) {
        result.required.push(attrItem.name);
      }
      delete attrItem.required;
      if (['array', 'object'].includes(attrItem.type)) {
        if (attrItem.type === 'array') {
          result.properties[attrItem.name] = {
            ...attrItem,
            items: attrItem.children?.length ? parseSchemaArrToObj(attrItem.children, attrItem.type) : undefined
          };
        } else {
          result.properties[attrItem.name] = {
            ...attrItem,
            ...(attrItem.children ? parseSchemaArrToObj(attrItem.children, attrItem.type) : {})
          };
        }
        delete result.properties[attrItem.name].children;
        delete result.properties[attrItem.name].name;
      } else {
        result.properties[attrItem.name] = {
          ...attrItem
        };
      }
    });
  }
  return result;
};

export const schemaTypeDependenceMap = {
  object: ['type', 'types', 'nullable', 'deprecated', 'minLength', 'maxLength', 'minimum', 'maximum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required'],
  array: ['examples', 'type', 'types', 'nullable', 'default', 'deprecated', 'enum', 'minLength', 'maxLength', 'minimum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required'],
  string: ['examples', 'type', 'types', 'nullable', 'default', 'deprecated', 'enum', 'minLength', 'maxLength', 'minimum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required'],
  number: ['examples', 'type', 'types', 'nullable', 'default', 'deprecated', 'enum', 'minLength', 'maxLength', 'minimum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required'],
  integer: ['examples', 'type', 'types', 'nullable', 'default', 'deprecated', 'enum', 'minLength', 'maxLength', 'minimum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required'],
  boolean: ['examples', 'type', 'types', 'nullable', 'default', 'deprecated', 'enum', 'minLength', 'maxLength', 'minimum', 'minItems', 'maxItems', 'pattern', 'description', 'format', 'required']
};
