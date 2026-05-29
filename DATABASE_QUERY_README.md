# AI 智能数据库查询系统

## 功能概述

这是一个基于 Spring AI 的智能数据库查询系统，支持以下功能：

1. **自然语言转 SQL**：用户可以用自然语言提问，AI 自动生成对应的 SQL 查询语句
2. **安全执行查询**：系统会自动验证 SQL 安全性，只允许 SELECT 查询
3. **多种展示方式**：
   - 表格展示：以 HTML 表格形式展示查询结果
   - 图表展示：使用 ECharts 生成可视化图表（柱状图、折线图、饼图、散点图）

## 技术栈

- **后端**：Spring Boot 3.2.0 + Spring AI 0.8.1
- **数据库**：PostgreSQL
- **AI 模型**：阿里百炼 (DashScope) / Ollama 本地模型
- **前端**：原生 HTML + JavaScript + ECharts 5.4.3
- **连接池**：HikariCP

## 配置说明

### 数据库配置

在 `application.yml` 中已配置 PostgreSQL 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.1.130:5432/my_ai?currentSchema=public
    username: postgres
    password: iot.2024
    driver-class-name: org.postgresql.Driver
```

### AI 模型配置

系统支持两种 AI 模型：

1. **阿里百炼（默认）**：
   ```yaml
   spring:
     ai:
       dashscope:
         api-key: sk-c9d4ffeb57684a58b1a63bbd8edf8108
         chat:
           options:
             model: qwen-plus
   ```

2. **Ollama 本地模型**：
   ```yaml
   spring:
     ai:
       ollama:
         base-url: http://192.168.1.150:11434
         chat:
           options:
             model: qwen2.5-coder:7b-instruct-q4_K_M
   ```

## 使用方法

### 1. 启动应用

```bash
mvn clean spring-boot:run
```

应用将在 `http://localhost:8000` 启动。

### 2. 访问界面

打开浏览器访问 `http://localhost:8000`，可以看到两个模式：

- **💬 聊天模式**：普通的 AI 对话功能
- **🗄️ 数据库查询**：智能数据库查询功能

### 3. 使用数据库查询

1. 切换到"数据库查询"模式
2. 选择展示方式：
   - **表格**：直接显示数据表格
   - **图表**：可选择柱状图、折线图、饼图或散点图
3. 输入自然语言问题，例如：
   - "查询所有用户的姓名和邮箱"
   - "统计每个部门的员工数量"
   - "显示最近一个月的销售趋势"
   - "查询销售额最高的前10个产品"
4. 点击"查询"按钮
5. 系统会：
   - 自动获取数据库 Schema 信息
   - 使用 AI 生成 SQL 语句
   - 验证 SQL 安全性
   - 执行查询
   - 以表格或图表形式展示结果

## API 接口

### 1. 执行数据库查询

```
POST /api/database/query
Content-Type: application/json

{
  "question": "查询所有用户",
  "displayType": "table",  // 或 "chart"
  "chartType": "bar"       // 当 displayType 为 chart 时有效：bar/line/pie/scatter
}
```

响应示例：

```json
{
  "success": true,
  "sql": "SELECT * FROM users LIMIT 100",
  "columns": ["id", "name", "email"],
  "rows": [
    {"id": 1, "name": "张三", "email": "zhangsan@example.com"},
    {"id": 2, "name": "李四", "email": "lisi@example.com"}
  ],
  "totalRows": 2,
  "echartsOption": {...}  // 当 displayType 为 chart 时返回
}
```

### 2. 获取表列表

```
GET /api/database/tables
```

### 3. 获取表的列信息

```
GET /api/database/table/{tableName}/columns
```

### 4. 获取完整 Schema 信息

```
GET /api/database/schema
```

## 安全特性

1. **SQL 注入防护**：
   - 只允许 SELECT 查询
   - 禁止 INSERT、UPDATE、DELETE、DROP 等危险操作
   - AI 生成的 SQL 会经过安全验证

2. **查询限制**：
   - 自动添加 LIMIT 100，防止返回过多数据
   - 连接池管理，防止资源耗尽

## 架构设计

### 核心组件

1. **DatabaseSchemaService**：
   - 从 PostgreSQL information_schema 获取表和列信息
   - 为 AI 提供数据库结构上下文

2. **DatabaseQueryService**：
   - 使用 AI 将自然语言转换为 SQL
   - 验证 SQL 安全性
   - 执行查询并处理结果
   - 生成 ECharts 配置

3. **DatabaseController**：
   - 提供 REST API 接口
   - 处理请求和响应

4. **前端界面**：
   - 双模式切换（聊天/数据库查询）
   - 表格渲染
   - ECharts 图表渲染

## 示例查询

以下是一些可以使用自然语言查询的示例：

- "列出所有员工的姓名和部门"
- "统计每个城市的用户数量"
- "显示本月销售额最高的产品"
- "查询年龄大于30岁的用户"
- "按月份统计订单数量"
- "找出工资最高的前5名员工"

## 注意事项

1. 确保 PostgreSQL 数据库可访问
2. 确保 AI 服务可用（阿里百炼 API 或 Ollama 服务）
3. 数据库需要有实际的表和数据才能查询
4. 复杂的自然语言问题可能需要更强大的 AI 模型

## 扩展建议

1. **添加更多图表类型**：如面积图、雷达图等
2. **支持多轮对话**：记住上下文，支持追问
3. **查询历史**：保存用户的查询历史
4. **导出功能**：支持导出 CSV、Excel 等格式
5. **权限控制**：添加用户认证和授权
6. **性能优化**：添加查询缓存、分页等功能
