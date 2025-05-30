import {InputProps} from 'ant-design-vue/es/input';

declare global {
    type ChangeEvent = Parameters<InputProps['onChange']>[0]
}

declare module '@xcan-angus/vue-ui';
