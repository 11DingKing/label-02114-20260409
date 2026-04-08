# 女友点餐系统 💕

一个专为情侣设计的每日点餐应用，让"今天吃什么"不再是难题！

---

## How to Run

### 🐳 方式一：Docker Compose 一键启动（推荐）

```bash
# 1. 克隆项目
git clone <repository-url>
cd girlfriend-menu

# 2. 配置环境变量（可选，使用默认值可跳过）
cp .env.example .env
# 编辑 .env 文件修改数据库密码等敏感信息

# 3. 一键启动所有服务
docker-compose up --build -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看日志（可选）
docker-compose logs -f backend
```

**首次启动需要等待 2-3 分钟**，MySQL 初始化和后端编译需要时间。

### 💻 方式二：本地开发环境

#### 前置要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

```bash
# 1. 创建数据库并初始化
mysql -u root -p < backend/src/main/resources/schema.sql

# 2. 设置环境变量（或修改 application.yml）
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret

# 3. 启动后端
cd backend
mvn spring-boot:run

# 4. 启动管理后台（新终端）
cd frontend-admin
npm install
npm run dev

# 5. 启动用户端（新终端）
cd frontend-user
npm install
npm run dev
```

---

## Services

| 服务 | 端口 | 访问地址 | 描述 |
|------|------|----------|------|
| MySQL | 3306 | - | 数据库服务 |
| Backend API | 8080 | http://localhost:8080 | Spring Boot 后端 |
| Swagger UI | 8080 | http://localhost:8080/swagger-ui.html | API 接口文档 |
| Admin Frontend | 8081 | http://localhost:8081 | 管理后台（男友端） |
| User Frontend | 8082 | http://localhost:8082 | H5用户端（女友端） |

---

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 男友登录管理后台 |
| 用户 | girlfriend | love123 | 女友登录H5点餐 |

---

## 题目内容

> 我想写一个供女朋友点每天吃的饭的app或者手机网页，我主要以vue为主，需要服务器的话，我到时候买一个，你可以帮我规划一下代码吗

---

## 🧪 质检测试指南

### 一、环境验证

```bash
# 1. 确认所有容器正常运行
docker-compose ps

# 预期输出：4个容器状态都是 Up
# gf-menu-mysql    Up (healthy)
# gf-menu-backend  Up
# gf-menu-admin    Up
# gf-menu-user     Up

# 2. 检查后端健康状态
curl http://localhost:8080/api/category/list

# 预期返回：JSON 格式的分类列表
```

### 二、功能测试清单

#### 管理后台测试 (http://localhost:8081)

| 序号 | 测试项 | 操作步骤 | 预期结果 |
|------|--------|----------|----------|
| 1 | 登录功能 | 输入 admin/admin123 点击登录 | 跳转到数据概览页面 |
| 2 | 数据概览 | 查看 Dashboard 页面 | 显示统计数据和排行榜 |
| 3 | 分类管理 | 点击"分类管理"菜单 | 显示6个分类（早餐/午餐/晚餐/夜宵/甜点/饮品） |
| 4 | 新增分类 | 点击"新增分类"，填写信息保存 | 分类列表新增一条记录 |
| 5 | 菜品管理 | 点击"菜品管理"菜单 | 显示所有菜品列表（约40个） |
| 6 | 新增菜品 | 点击"新增菜品"，填写信息保存 | 菜品列表新增一条记录 |
| 7 | 搜索菜品 | 在搜索框输入"鸡"搜索 | 筛选出包含"鸡"的菜品 |
| 8 | 点餐记录 | 点击"点餐记录"菜单 | 显示历史点餐记录 |
| 9 | 心愿清单 | 点击"心愿清单"菜单 | 显示女友的心愿列表 |
| 10 | 退出登录 | 点击右上角用户名，选择退出 | 跳转到登录页面 |

#### 用户端H5测试 (http://localhost:8082)

| 序号 | 测试项 | 操作步骤 | 预期结果 |
|------|--------|----------|----------|
| 1 | 登录功能 | 输入 girlfriend/love123 点击登录 | 跳转到首页，显示问候语 |
| 2 | 分类切换 | 点击不同分类标签 | 菜品列表随分类切换 |
| 3 | 点餐功能 | 点击任意菜品卡片 | 弹出点餐确认弹窗 |
| 4 | 选择餐次 | 在弹窗中选择早餐/午餐/晚餐/夜宵 | 餐次按钮高亮显示 |
| 5 | 确认点餐 | 点击"就吃这个！"按钮 | 提示"点餐成功"，弹窗关闭 |
| 6 | 随机推荐 | 点击底部"随机"Tab，点击"开始抽取" | 骰子动画后显示随机菜品 |
| 7 | 再来一次 | 点击"再来一次"按钮 | 重新抽取显示新菜品 |
| 8 | 点餐历史 | 点击底部"记录"Tab | 显示历史点餐记录列表 |
| 9 | 心愿清单 | 点击底部"心愿"Tab | 显示心愿列表 |
| 10 | 添加心愿 | 输入想吃的菜名，点击"许愿" | 心愿列表新增一条 |
| 11 | 删除心愿 | 点击心愿项右侧的删除按钮 | 心愿被删除 |

### 三、API 接口测试

```bash
# 1. 登录接口
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. 获取分类列表
curl http://localhost:8080/api/category/list

# 3. 获取菜品列表
curl "http://localhost:8080/api/dish/list?current=1&size=10"

# 4. 随机推荐
curl "http://localhost:8080/api/dish/random?count=1"
```

### 四、运行单元测试

```bash
# 后端测试
cd backend
mvn test

# 前端管理后台测试
cd frontend-admin
npm test

# 前端用户端测试
cd frontend-user
npm test
```

### 五、常见问题排查

```bash
# 问题1：容器启动失败
docker-compose logs mysql    # 查看MySQL日志
docker-compose logs backend  # 查看后端日志

# 问题2：数据库连接失败
# 等待 MySQL 完全启动（约30秒），然后重启后端
docker-compose restart backend

# 问题3：端口被占用
# 修改 docker-compose.yml 中的端口映射
# 或停止占用端口的服务

# 问题4：清理重新开始
docker-compose down -v       # 停止并删除数据卷
docker-compose up --build -d # 重新构建启动
```

---

## 📦 初始化数据说明

系统启动后自动初始化以下数据：

| 数据类型 | 数量 | 说明 |
|----------|------|------|
| 用户 | 2 | admin(管理员) + girlfriend(用户) |
| 分类 | 6 | 早餐、午餐、晚餐、夜宵、甜点、饮品 |
| 菜品 | 40+ | 每个分类下多个真实菜品 |
| 点餐记录 | 8 | 示例历史记录 |
| 心愿清单 | 5 | 示例心愿数据 |

---

## 功能特性

### 管理后台 🔧
- 📊 数据概览：统计总点餐数、完成率、最爱菜品排行
- 📂 分类管理：增删改查菜品分类
- 🍽️ 菜品管理：管理所有菜品信息
- 📋 点餐记录：查看和管理点餐历史
- 💝 心愿清单：查看女友的心愿，标记已完成

### 用户端 H5 📱
- 🍽️ 今日点餐：浏览菜品，选择想吃的
- 🎲 随机推荐：选择困难症救星，随机抽取菜品
- 📜 点餐历史：回顾美食记忆
- 💝 心愿清单：许下想吃的菜，等他来做

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 + Vite |
| UI组件 | Element Plus (Admin) / 自定义组件 (H5) |
| 状态管理 | Pinia |
| HTTP客户端 | Axios |
| 样式 | SCSS |
| 后端框架 | Spring Boot 3 |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 认证 | JWT |
| 容器化 | Docker + Docker Compose |
| Web服务器 | Nginx |

---

## 项目结构

```
girlfriend-menu/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/menu/
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 服务层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── vo/                 # 视图对象
│   │   ├── config/             # 配置类（含 OpenAPI 配置）
│   │   └── exception/          # 异常处理
│   ├── src/main/resources/
│   │   ├── application.yml     # 应用配置（支持环境变量）
│   │   └── schema.sql          # 数据库初始化脚本
│   └── Dockerfile
├── frontend-admin/             # Vue3 管理后台
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── api/                # API接口
│   │   ├── stores/             # Pinia状态
│   │   ├── router/             # 路由配置
│   │   └── styles/             # 样式文件
│   └── Dockerfile
├── frontend-user/              # Vue3 H5用户端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── api/                # API接口
│   │   ├── utils/              # 工具函数
│   │   ├── stores/             # Pinia状态
│   │   └── styles/             # 样式文件
│   └── Dockerfile
├── docs/                       # 项目文档
│   └── project_design.md       # 设计文档
├── .env.example                # 环境变量示例
├── docker-compose.yml          # Docker编排文件
└── README.md                   # 项目说明
```

---

## 🔐 安全配置

敏感信息通过环境变量管理，不硬编码在代码中：

```bash
# .env 文件示例
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_key
```

---

## 📚 API 文档

启动后端后访问 Swagger UI 查看完整的 API 文档：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

支持在线测试所有接口，包含请求参数说明和响应示例。

---

## License

MIT License - 用爱发电 ❤️
