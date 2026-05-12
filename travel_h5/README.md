# travel_h5

旅游景点推荐智能助手 H5 前端项目，基于 Vue 3、TypeScript、Vite 和 Vant 构建。

## 技术栈

- Vue 3
- TypeScript
- Vite
- Vue Router
- Vant
- Axios

## 本地开发

```bash
npm install
npm run dev
```

默认开发服务端口为 `5173`。Vite 开发代理会把 `/api` 请求转发到 `http://localhost:8080`，配置见 `vite.config.ts`。

## 常用命令

```bash
npm run dev      # 启动本地开发服务
npm run build    # 类型检查并打包生产文件
npm run preview  # 本地预览生产包
```

## 目录说明

```text
travel_h5/
  public/          静态资源
  src/
    api/           接口请求封装
    router/        路由配置
    types/         TypeScript 类型
    utils/         工具函数
    views/         页面组件
  index.html       Vite 入口 HTML
  package.json     项目脚本和依赖
  vite.config.ts   Vite 配置
```

## 提交说明

下面这些目录是本地安装、缓存或构建生成的内容，不需要提交到 GitHub：

- `node_modules/`
- `dist/`
- `.npm-cache/`
- `.vite/`
- `.cache/`

提交源码时保留 `package.json` 和 `package-lock.json`，其他人克隆项目后运行 `npm install` 就能重新安装依赖。
