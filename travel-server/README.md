# travel-server

旅游景点推荐智能助手后端服务，基于 Spring Boot 和 Redis 初始化，提供 H5 前端所需的 REST API 与 SSE 流式对话接口。

## 技术栈

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Validation
- Spring Data Redis
- Jackson
- Lombok

## 本地启动

```bash
mvn spring-boot:run
```

Windows PowerShell:

```powershell
mvn spring-boot:run
```

服务默认启动在：

```text
http://localhost:8080
```

Redis 默认连接：

```text
localhost:6379
```

当前接口即使 Redis 未启动也可以用于基础联调；Redis 相关缓存和限流会自动降级到本地内存。

## 已初始化接口

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/travel/hot-cities` | 热门目的地 |
| GET | `/api/travel/cities` | 城市列表 |
| POST | `/api/travel/recommend` | 生成景点推荐 |
| GET | `/api/travel/chat/quick-questions` | 快捷问题 |
| POST | `/api/travel/chat` | SSE 流式对话 |
| GET | `/api/user/profile` | 用户信息 |
| PUT | `/api/user/profile` | 更新用户信息 |
| GET | `/api/user/favorites` | 收藏列表 |
| POST | `/api/user/favorites` | 添加收藏 |
| DELETE | `/api/user/favorites/{favoriteId}` | 删除收藏 |
| GET | `/api/user/history` | 历史记录 |
| DELETE | `/api/user/history` | 清空历史记录 |
| GET | `/api/user/settings` | 用户设置 |
| PUT | `/api/user/settings` | 更新用户设置 |
| GET | `/api/about` | 关于项目 |

## 构建验证

```bash
mvn test
```

Windows PowerShell:

```powershell
mvn test
```
