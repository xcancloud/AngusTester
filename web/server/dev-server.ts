const gatewayApiRouters = [
  '/gm/',
  '/defender/',
  '/storage/'
];

const fileApiRouters = ['/storage/'];
const proxy = {};
gatewayApiRouters.every(item => {
  if (fileApiRouters.includes(item)) {
    proxy[item] = {
      target: 'http://dev-files.xcan.cloud',
      changeOrigin: true
    };
  } else {
    proxy[item] = {
      target: 'http://dev-apis.xcan.cloud',
      changeOrigin: true
    };
  }
  return true;
});

export default {
  host: '0.0.0.0',
  allowedHosts: ['dev-host.xcan.cloud'],
  port: 80,
  strictPort: true,
  open: false,
  proxy
};
