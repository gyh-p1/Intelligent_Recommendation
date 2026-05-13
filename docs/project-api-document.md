# 旅游景点推荐智能助手 Agent 全栈项目接口文档

## 1. 文档说明

本文档根据项目功能脑图整理，面向 `travel-h5` 前端项目与 `travel-server` 后端项目的接口联调、后端开发和测试验收。

后端技术栈按要求调整为：

- Spring Boot
- Redis

除后端技术栈外，项目功能、前端技术栈、接口能力和项目亮点保持与原方案一致。

## 2. 项目概述

### 2.1 项目名称

旅游景点推荐智能助手 Agent 全栈项目

### 2.2 项目组成

| 项目 | 说明 |
| --- | --- |
| `travel-h5` | 移动端 H5 前端项目 |
| `travel-server` | 后端服务项目 |

### 2.3 核心业务目标

用户在首页选择目的地城市、预算和旅行天数后，可以获得 AI 生成的景点推荐与旅行建议；用户也可以进入 AI 对话页面，通过流式响应实时获取旅行问答、行程规划和景点推荐内容。

## 3. 技术栈

### 3.1 前端技术栈

| 技术 | 用途 |
| --- | --- |
| Vue 3 | 前端框架 |
| Vite | 构建工具 |
| Vant | 移动端 UI 组件库 |
| Axios | HTTP 请求 |
| Fetch Stream | AI 对话流式响应、打字机效果 |

### 3.2 后端技术栈

| 技术 | 用途 |
| --- | --- |
| Spring Boot | 后端 Web 服务、REST API、SSE 流式接口 |
| Redis | 会话缓存、对话上下文缓存、热门目的地缓存、接口限流、临时推荐结果缓存 |
| Spring Web | HTTP 接口与 SSE 响应 |
| Spring Validation | 请求参数校验 |
| Jackson | JSON 序列化与反序列化 |
| Spring Data Redis | Redis 数据访问 |
| Lombok，可选 | 简化 DTO、VO、实体类代码 |
| AI SDK 或 HTTP Client | 调用大语言模型，支持多模型切换 |

## 4. 前端功能模块

### 4.1 首页 Home

| 功能 | 说明 |
| --- | --- |
| 旅游规划表单 | 选择目的地城市、输入预算、选择天数 |
| 快捷入口 | 跳转 AI 对话、个人中心 |
| 热门目的地 | 快速选择热门城市，例如北京、上海、成都、杭州 |
| 城市选择器 | 使用 Vant Picker 组件 |

### 4.2 AI 对话 Chat

| 功能 | 说明 |
| --- | --- |
| 流式响应 | 基于 fetchStream 实现打字机效果 |
| 常见问题快捷标签 | 用户点击标签后自动发送问题 |
| 消息气泡 | 用户消息右侧蓝色对齐，AI 消息左侧灰色对齐 |
| 导航处理 | 聊天页面隐藏底部 Tabbar |

### 4.3 个人中心 Profile

| 功能 | 说明 |
| --- | --- |
| 用户信息展示 | 展示头像、昵称 |
| 功能菜单 | 我的收藏、历史记录、设置 |
| 关于我们 | 展示项目介绍或团队信息弹窗 |

## 5. 后端核心功能

| 功能 | 接口 |
| --- | --- |
| 景点推荐 | `POST /api/travel/recommend` |
| AI 对话流式响应 | `POST /api/travel/chat` |
| 热门目的地 | `GET /api/travel/hot-cities` |
| 常见问题标签 | `GET /api/travel/chat/quick-questions` |
| 用户信息 | `GET /api/user/profile` |
| 我的收藏 | `GET /api/user/favorites` |
| 添加收藏 | `POST /api/user/favorites` |
| 取消收藏 | `DELETE /api/user/favorites/{favoriteId}` |
| 历史记录 | `GET /api/user/history` |
| 清空历史记录 | `DELETE /api/user/history` |
| 用户设置 | `GET /api/user/settings`、`PUT /api/user/settings` |

## 6. 通用接口约定

### 6.1 基础路径

```text
http://localhost:8080
```

生产环境可通过网关或 Nginx 转发为：

```text
https://your-domain.com
```

### 6.2 请求头

| Header | 必填 | 说明 |
| --- | --- | --- |
| `Content-Type: application/json` | 是 | 普通 JSON 接口使用 |
| `Accept: application/json` | 否 | 普通 JSON 接口使用 |
| `Accept: text/event-stream` | 是 | AI 流式对话接口使用 |
| `Authorization: Bearer {token}` | 否 | 如接入登录鉴权时使用 |
| `X-Client-Id` | 建议 | 匿名用户设备标识，用于缓存对话上下文、收藏和历史记录 |

### 6.3 通用响应结构

普通 JSON 接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 6.4 通用状态码

| code | 说明 |
| --- | --- |
| `200` | 请求成功 |
| `400` | 请求参数错误 |
| `401` | 未登录或 token 无效 |
| `403` | 无权限 |
| `404` | 资源不存在 |
| `429` | 请求过于频繁 |
| `500` | 服务器内部错误 |
| `503` | AI 服务暂不可用 |

### 6.5 分页参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `page` | number | 否 | `1` | 页码 |
| `pageSize` | number | 否 | `10` | 每页数量 |

分页响应结构：

```json
{
  "list": [],
  "total": 0,
  "page": 1,
  "pageSize": 10
}
```

## 7. Redis 设计

### 7.1 Redis 使用场景

| 场景 | 说明 |
| --- | --- |
| 对话上下文缓存 | 保存用户最近多轮对话，提升 AI 连贯性 |
| 推荐结果缓存 | 对相同城市、预算、天数组合进行短期缓存 |
| 热门目的地缓存 | 缓存热门城市列表，减少数据库或配置读取 |
| 常见问题缓存 | 缓存 AI 对话快捷标签 |
| 接口限流 | 对推荐接口和聊天接口做频率限制 |
| 用户临时数据 | 匿名用户收藏、历史记录、设置可先存 Redis |

### 7.2 Redis Key 设计

| Key | 类型 | TTL | 说明 |
| --- | --- | --- | --- |
| `travel:recommend:{hash}` | String | 30 分钟 | 景点推荐结果缓存 |
| `travel:chat:session:{sessionId}` | List | 24 小时 | AI 对话上下文 |
| `travel:hot:cities` | String/List | 24 小时 | 热门目的地 |
| `travel:quick:questions` | String/List | 24 小时 | 常见问题快捷标签 |
| `travel:rate:recommend:{clientId}` | String | 60 秒 | 推荐接口限流计数 |
| `travel:rate:chat:{clientId}` | String | 60 秒 | 聊天接口限流计数 |
| `travel:user:{clientId}:favorites` | Hash/List | 30 天 | 用户收藏 |
| `travel:user:{clientId}:history` | List | 30 天 | 历史记录 |
| `travel:user:{clientId}:settings` | String | 30 天 | 用户设置 |

## 8. 首页相关接口

### 8.1 获取热门目的地

获取首页热门目的地城市列表。

```http
GET /api/travel/hot-cities
```

#### 请求参数

无。

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "cityCode": "beijing",
      "cityName": "北京",
      "province": "北京市",
      "cover": "https://example.com/images/beijing.jpg",
      "tags": ["历史文化", "亲子", "城市漫游"]
    },
    {
      "cityCode": "shanghai",
      "cityName": "上海",
      "province": "上海市",
      "cover": "https://example.com/images/shanghai.jpg",
      "tags": ["都市", "美食", "夜景"]
    }
  ]
}
```

#### 字段说明

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `cityCode` | string | 城市编码 |
| `cityName` | string | 城市名称 |
| `province` | string | 所属省份 |
| `cover` | string | 城市封面图 |
| `tags` | string[] | 城市标签 |

### 8.2 查询城市列表

供 Vant Picker 城市选择器使用。

```http
GET /api/travel/cities
```

#### Query 参数

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 城市关键词 |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "label": "北京",
      "value": "beijing",
      "province": "北京市"
    },
    {
      "label": "杭州",
      "value": "hangzhou",
      "province": "浙江省"
    }
  ]
}
```

## 9. 景点推荐接口

### 9.1 生成景点推荐

根据用户选择的目的地城市、预算和旅行天数，生成旅游景点推荐、行程安排和提示建议。

```http
POST /api/travel/recommend
```

#### 请求头

| Header | 必填 | 示例 |
| --- | --- | --- |
| `Content-Type` | 是 | `application/json` |
| `X-Client-Id` | 建议 | `h5_8f2c9a001` |

#### 请求体

```json
{
  "city": "杭州",
  "cityCode": "hangzhou",
  "budget": 1500,
  "days": 3,
  "travelers": 2,
  "preferences": ["自然风光", "美食", "轻松"],
  "startDate": "2026-06-01"
}
```

#### 请求字段说明

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `city` | string | 是 | 目的地城市名称 |
| `cityCode` | string | 否 | 城市编码 |
| `budget` | number | 是 | 总预算，单位：元 |
| `days` | number | 是 | 旅行天数 |
| `travelers` | number | 否 | 出行人数，默认 `1` |
| `preferences` | string[] | 否 | 偏好标签 |
| `startDate` | string | 否 | 出发日期，格式 `yyyy-MM-dd` |

#### 参数校验规则

| 字段 | 规则 |
| --- | --- |
| `city` | 不能为空，长度不超过 50 |
| `budget` | 必须大于 0，建议不超过 100000 |
| `days` | 必须大于 0，建议不超过 30 |
| `travelers` | 必须大于 0，建议不超过 20 |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "requestId": "rec_202605111857001",
    "city": "杭州",
    "summary": "适合 3 天轻松游，重点安排西湖、灵隐寺、京杭大运河与本地美食体验。",
    "budgetAdvice": {
      "totalBudget": 1500,
      "estimatedCost": 1380,
      "currency": "CNY",
      "details": [
        {
          "name": "交通",
          "amount": 300
        },
        {
          "name": "餐饮",
          "amount": 360
        },
        {
          "name": "门票",
          "amount": 220
        },
        {
          "name": "住宿",
          "amount": 500
        }
      ]
    },
    "spots": [
      {
        "spotId": "hz_xihu",
        "name": "西湖",
        "level": "5A",
        "address": "杭州市西湖区龙井路1号",
        "description": "杭州代表性景区，适合步行、骑行和乘船游览。",
        "recommendedDuration": "3-4小时",
        "ticketPrice": 0,
        "tags": ["自然风光", "城市地标"],
        "reason": "预算友好，路线成熟，适合第一次到杭州的用户。"
      }
    ],
    "itinerary": [
      {
        "day": 1,
        "title": "西湖经典路线",
        "items": [
          {
            "time": "09:00",
            "activity": "游览西湖苏堤、白堤"
          },
          {
            "time": "14:00",
            "activity": "前往灵隐寺"
          }
        ]
      }
    ],
    "tips": [
      "西湖周边建议步行或骑行，节假日提前规划交通。",
      "热门餐厅建议错峰前往。"
    ],
    "cached": false,
    "createdAt": "2026-05-11 18:57:00"
  }
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `requestId` | string | 本次推荐请求 ID |
| `city` | string | 推荐城市 |
| `summary` | string | 推荐摘要 |
| `budgetAdvice` | object | 预算建议 |
| `spots` | array | 推荐景点列表 |
| `itinerary` | array | 分日行程 |
| `tips` | string[] | 出行提示 |
| `cached` | boolean | 是否来自 Redis 缓存 |
| `createdAt` | string | 生成时间 |

#### 失败响应示例

```json
{
  "code": 400,
  "message": "预算必须大于 0",
  "data": null
}
```

## 10. AI 对话接口

### 10.1 获取常见问题快捷标签

用于聊天页顶部或输入框上方的快捷问题。

```http
GET /api/travel/chat/quick-questions
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    "帮我规划杭州 3 日游",
    "北京有哪些适合亲子的景点？",
    "成都 1500 元预算怎么玩？",
    "上海两天一夜路线推荐"
  ]
}
```

### 10.2 AI 对话流式响应

AI 对话接口使用 SSE 或 fetch stream 返回流式内容，用于前端实现打字机效果。

```http
POST /api/travel/chat
```

#### 请求头

| Header | 必填 | 示例 |
| --- | --- | --- |
| `Content-Type` | 是 | `application/json` |
| `Accept` | 是 | `text/event-stream` |
| `X-Client-Id` | 建议 | `h5_8f2c9a001` |

#### 请求体

```json
{
  "sessionId": "chat_202605111857001",
  "message": "帮我规划杭州 3 日游，预算 1500 元，想轻松一点。",
  "city": "杭州",
  "model": "default",
  "stream": true
}
```

#### 请求字段说明

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `sessionId` | string | 否 | 会话 ID，不传时后端生成 |
| `message` | string | 是 | 用户输入内容 |
| `city` | string | 否 | 当前对话关联城市 |
| `model` | string | 否 | 模型标识，支持后端多模型切换 |
| `stream` | boolean | 否 | 是否流式响应，默认 `true` |

#### SSE 响应格式

```text
event: start
data: {"sessionId":"chat_202605111857001","messageId":"msg_001"}

event: message
data: {"content":"当然，"}

event: message
data: {"content":"下面是杭州 3 日轻松游路线："}

event: done
data: {"sessionId":"chat_202605111857001","messageId":"msg_001","finishReason":"stop"}
```

#### 异常事件格式

```text
event: error
data: {"code":503,"message":"AI 服务暂不可用，请稍后再试"}
```

#### 前端处理建议

前端使用 `fetch` 读取 `ReadableStream`，逐段解析 SSE 内容，并将 `event: message` 中的 `content` 追加到 AI 消息气泡中。

#### 后端处理建议

Spring Boot 可使用 `SseEmitter` 或 WebFlux `Flux<ServerSentEvent<?>>` 实现流式输出；每次对话结束后，将用户消息和 AI 回复写入 Redis：

```text
travel:chat:session:{sessionId}
```

## 11. 个人中心接口

### 11.1 获取用户信息

```http
GET /api/user/profile
```

#### 请求头

| Header | 必填 | 说明 |
| --- | --- | --- |
| `X-Client-Id` | 建议 | 匿名用户设备标识 |
| `Authorization` | 否 | 登录后使用 |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "u_001",
    "nickname": "旅行用户",
    "avatar": "https://example.com/avatar/default.png",
    "favoriteCount": 8,
    "historyCount": 15
  }
}
```

### 11.2 更新用户信息

```http
PUT /api/user/profile
```

#### 请求体

```json
{
  "nickname": "爱旅行的小明",
  "avatar": "https://example.com/avatar/u_001.png"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 12. 收藏接口

### 12.1 获取我的收藏

```http
GET /api/user/favorites?page=1&pageSize=10
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "favoriteId": "fav_001",
        "type": "spot",
        "targetId": "hz_xihu",
        "title": "西湖",
        "description": "杭州代表性景区，适合步行、骑行和乘船游览。",
        "cover": "https://example.com/images/xihu.jpg",
        "createdAt": "2026-05-11 18:57:00"
      }
    ],
    "total": 1,
    "page": 1,
    "pageSize": 10
  }
}
```

### 12.2 添加收藏

```http
POST /api/user/favorites
```

#### 请求体

```json
{
  "type": "spot",
  "targetId": "hz_xihu",
  "title": "西湖",
  "description": "杭州代表性景区，适合步行、骑行和乘船游览。",
  "cover": "https://example.com/images/xihu.jpg"
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "favoriteId": "fav_001"
  }
}
```

### 12.3 取消收藏

```http
DELETE /api/user/favorites/{favoriteId}
```

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `favoriteId` | string | 是 | 收藏 ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 13. 历史记录接口

### 13.1 获取历史记录

```http
GET /api/user/history?page=1&pageSize=10
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "historyId": "his_001",
        "type": "recommend",
        "title": "杭州 3 日游推荐",
        "summary": "西湖、灵隐寺、京杭大运河轻松路线",
        "createdAt": "2026-05-11 18:57:00"
      },
      {
        "historyId": "his_002",
        "type": "chat",
        "title": "AI 对话：成都 1500 元预算怎么玩？",
        "summary": "推荐锦里、宽窄巷子、熊猫基地等路线",
        "createdAt": "2026-05-11 18:50:00"
      }
    ],
    "total": 2,
    "page": 1,
    "pageSize": 10
  }
}
```

### 13.2 清空历史记录

```http
DELETE /api/user/history
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 14. 设置接口

### 14.1 获取用户设置

```http
GET /api/user/settings
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "streamEnabled": true,
    "defaultModel": "default",
    "theme": "light",
    "cacheEnabled": true
  }
}
```

### 14.2 更新用户设置

```http
PUT /api/user/settings
```

#### 请求体

```json
{
  "streamEnabled": true,
  "defaultModel": "default",
  "theme": "light",
  "cacheEnabled": true
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 15. 关于我们接口

### 15.1 获取关于我们信息

```http
GET /api/about
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "projectName": "旅游景点推荐智能助手 Agent 全栈项目",
    "version": "1.0.0",
    "description": "基于 Vue 3、Vite、Vant、Spring Boot、Redis 和大语言模型能力构建的移动端旅游推荐应用。",
    "features": [
      "AI Agent 智能旅游项目",
      "调用 LLM，支持多模型切换",
      "流式响应，实时显示 AI 回复",
      "移动端适配，路由懒加载",
      "使用 AI 编辑器 Trae 进行开发"
    ]
  }
}
```

## 16. 后端 Controller 建议

### 16.1 Controller 划分

| Controller | 负责接口 |
| --- | --- |
| `TravelController` | 景点推荐、城市列表、热门目的地 |
| `ChatController` | AI 对话、快捷问题 |
| `UserController` | 用户信息、设置 |
| `FavoriteController` | 收藏列表、添加收藏、取消收藏 |
| `HistoryController` | 历史记录、清空历史 |
| `AboutController` | 关于我们 |

### 16.2 Service 划分

| Service | 说明 |
| --- | --- |
| `TravelRecommendService` | 组装推荐 Prompt、调用 AI、处理推荐结果 |
| `ChatService` | 处理对话上下文、流式响应、模型切换 |
| `RedisCacheService` | 封装 Redis 缓存、TTL、Key 生成 |
| `RateLimitService` | 接口限流 |
| `UserService` | 用户信息与设置 |
| `FavoriteService` | 收藏管理 |
| `HistoryService` | 历史记录管理 |

## 17. AI Prompt 输入建议

### 17.1 推荐接口 Prompt 变量

| 变量 | 说明 |
| --- | --- |
| `city` | 目的地城市 |
| `budget` | 预算 |
| `days` | 天数 |
| `travelers` | 出行人数 |
| `preferences` | 用户偏好 |
| `startDate` | 出发日期 |

### 17.2 推荐接口输出格式要求

推荐接口建议要求 AI 返回结构化 JSON，便于前端渲染：

```json
{
  "summary": "",
  "budgetAdvice": {},
  "spots": [],
  "itinerary": [],
  "tips": []
}
```

### 17.3 对话接口上下文策略

后端从 Redis 中读取最近若干轮对话作为上下文，建议保留最近 `10` 条消息，避免 Prompt 过长。

## 18. 限流建议

| 接口 | 限流规则 |
| --- | --- |
| `POST /api/travel/recommend` | 每个 `clientId` 每分钟最多 10 次 |
| `POST /api/travel/chat` | 每个 `clientId` 每分钟最多 20 次 |
| `GET /api/travel/hot-cities` | 每个 `clientId` 每分钟最多 60 次 |

限流命中响应：

```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "data": null
}
```

## 19. 前后端联调说明

### 19.1 首页推荐流程

1. 前端调用 `GET /api/travel/hot-cities` 渲染热门目的地。
2. 用户选择城市、预算和天数。
3. 前端调用 `POST /api/travel/recommend`。
4. 后端先查询 Redis 推荐缓存。
5. 缓存命中时直接返回。
6. 缓存未命中时调用 AI 生成推荐结果。
7. 后端写入 Redis 缓存与用户历史记录。
8. 前端渲染推荐景点、预算建议、分日行程和出行提示。

### 19.2 AI 对话流程

1. 前端进入聊天页，隐藏底部 Tabbar。
2. 前端调用 `GET /api/travel/chat/quick-questions` 渲染快捷问题。
3. 用户输入问题或点击快捷标签。
4. 前端调用 `POST /api/travel/chat` 并读取流式响应。
5. 后端从 Redis 读取会话上下文。
6. 后端调用 AI 模型并通过 SSE 分段返回。
7. 前端逐段追加 AI 回复，实现打字机效果。
8. 对话完成后，后端保存本轮上下文和历史记录。

## 20. 验收标准

| 模块 | 验收点 |
| --- | --- |
| 首页 | 可以选择城市、输入预算、选择天数，并成功获取推荐 |
| 热门目的地 | 北京、上海、成都、杭州等城市可快速选择 |
| 城市选择器 | Vant Picker 可正常展示与回填城市 |
| 景点推荐 | 返回推荐摘要、景点列表、预算建议、分日行程和提示 |
| AI 对话 | 支持流式输出，前端展示打字机效果 |
| 快捷问题 | 点击后自动发送问题 |
| 消息气泡 | 用户消息右侧蓝色，AI 消息左侧灰色 |
| 个人中心 | 展示头像、昵称、收藏、历史记录、设置、关于我们 |
| Redis 缓存 | 推荐结果、会话上下文、热门城市可缓存 |
| 限流 | 高频请求返回 `429` |

## 21. 接口清单汇总

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/travel/hot-cities` | 获取热门目的地 |
| `GET` | `/api/travel/cities` | 查询城市列表 |
| `POST` | `/api/travel/recommend` | 生成景点推荐 |
| `GET` | `/api/travel/chat/quick-questions` | 获取常见问题快捷标签 |
| `POST` | `/api/travel/chat` | AI 对话流式响应 |
| `GET` | `/api/user/profile` | 获取用户信息 |
| `PUT` | `/api/user/profile` | 更新用户信息 |
| `GET` | `/api/user/favorites` | 获取我的收藏 |
| `POST` | `/api/user/favorites` | 添加收藏 |
| `DELETE` | `/api/user/favorites/{favoriteId}` | 取消收藏 |
| `GET` | `/api/user/history` | 获取历史记录 |
| `DELETE` | `/api/user/history` | 清空历史记录 |
| `GET` | `/api/user/settings` | 获取用户设置 |
| `PUT` | `/api/user/settings` | 更新用户设置 |
| `GET` | `/api/about` | 获取关于我们信息 |

