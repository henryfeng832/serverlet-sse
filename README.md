# SSE Long Connection Progress Demo

基于 Java 8 + Servlet 3.1 的 Server-Sent Events (SSE) 长连接进度演示。服务端模拟 5 秒处理任务，每秒向客户端推送进度，完成后返回最终结果。

## 技术栈

- **Java 8** — 语言版本
- **Servlet 3.1** (`javax.servlet-api`) — SSE 响应输出
- **Maven** — 构建管理
- **Tomcat 7 Maven Plugin** — 内嵌运行，无需单独安装 Tomcat
- **HTML + EventSource API** — 前端接收 SSE 事件流

## 项目结构

```
serverlet-sse/
├── .devcontainer/
│   └── devcontainer.json      # GitHub Codespaces 配置
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/sse/
│       │       └── ProgressServlet.java   # SSE 服务端
│       └── webapp/
│           └── index.html                 # 前端页面
├── pom.xml
└── README.md
```

## 本地运行

需要 **JDK 8+** 和 **Maven 3+**。

```bash
mvn tomcat7:run
```

启动后访问 [http://localhost:8080/sse-demo/](http://localhost:8080/sse-demo/)

## GitHub Codespaces 在线运行

1. 将本项目推送至 GitHub 仓库
2. 进入仓库页面，点击 **Code → Open with Codespaces → New codespace**
3. 等待容器初始化完成（自动安装 Java 8 + Maven）
4. 在终端执行启动命令：

```bash
mvn tomcat7:run
```

5. 右下角弹出端口通知时，点击 **Open in Browser**

## 工作原理

### SSE 协议格式

服务端以 `text/event-stream` 格式输出，每个事件包含 `event` 和 `data` 两个字段，事件之间用空行分隔：

```
event: progress
data: {"step": 1, "total": 5, "message": "处理中，已完成步骤 1/5"}

event: progress
data: {"step": 2, "total": 5, "message": "处理中，已完成步骤 2/5"}

...

event: complete
data: {"code": 0, "message": "处理完成", "result": "这是最终的处理结果"}
```

### 处理流程

```
客户端                       服务端
  │                           │
  │── GET /progress ─────────>│
  │                           │-- 建立 SSE 连接
  │<── event: progress (1/5) ─│    sleep 1s
  │<── event: progress (2/5) ─│    sleep 1s
  │<── event: progress (3/5) ─│    sleep 1s
  │<── event: progress (4/5) ─│    sleep 1s
  │<── event: progress (5/5) ─│    sleep 1s
  │<── event: complete ───────│    处理结束
  │                           │
```

### 关键实现点

- **响应头**：设置 `Content-Type: text/event-stream` + `Cache-Control: no-cache`，禁用缓冲
- **逐条推送**：每执行一步，通过 `writer.write()` + `writer.flush()` 实时推送到客户端
- **断连检测**：`writer.checkError()` 检测客户端是否断开，避免无效写入
- **前端**：`EventSource.addEventListener('progress', ...)` 订阅进度事件，`addEventListener('complete', ...)` 处理完成事件

## 自定义

在 `ProgressServlet.java` 的 `for` 循环内替换你的实际处理逻辑即可。循环次数控制总步骤数，`Thread.sleep(1000)` 控制推送间隔。
