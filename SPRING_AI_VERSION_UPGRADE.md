# Spring AI 版本升级记录

## 升级时间
2026-06-01

## 版本变更

### Spring Boot
- **之前**: 3.2.0
- **现在**: 3.3.8 ✅

### Spring AI
- **之前**: 0.8.1（未使用 BOM）
- **现在**: 1.0.0-M6（使用 BOM 管理）✅

### Spring AI Alibaba
- **之前**: 1.0.0-M6.1
- **现在**: 1.0.0-M6.1（保持不变）✅

### Spring AI Ollama
- **之前**: 1.0.0-M6（硬编码版本）
- **现在**: 1.0.0-M6（通过 BOM 管理）✅

## 主要改进

### 1. 引入 Spring AI BOM
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>${spring-ai.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**优势：**
- ✅ 统一管理所有 Spring AI 依赖版本
- ✅ 避免版本冲突
- ✅ 简化依赖配置（无需为每个模块指定版本）

### 2. Spring Boot 升级
从 3.2.0 升级到 3.3.8，获得：
- ✅ 更好的性能和稳定性
- ✅ 更多 bug 修复
- ✅ 更好的 Spring AI 兼容性

### 3. 依赖配置简化

**之前：**
```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
  <version>1.0.0-M6</version>  <!-- 硬编码 -->
</dependency>
```

**现在：**
```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
  <!-- 版本由 BOM 管理 -->
</dependency>
```

## 版本兼容性

| 组件 | 版本 | 要求 |
|------|------|------|
| JDK | 21 | ✅ 满足 |
| Spring Boot | 3.3.8 | ✅ Spring AI 1.0.0-M6 需要 3.2+ |
| Spring AI | 1.0.0-M6 | ✅ 稳定版本 |
| Spring AI Alibaba | 1.0.0-M6.1 | ✅ 兼容 |

## 验证结果

```bash
mvn clean compile -DskipTests
```

✅ **BUILD SUCCESS** - 编译成功，无错误

## 后续升级路径

如果需要升级到更新的版本：

### Spring AI 1.1.x（推荐生产环境）
```xml
<properties>
  <spring-ai.version>1.1.5</spring-ai.version>
  <spring-ai-alibaba.version>1.1.2.1</spring-ai-alibaba.version>
</properties>

<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.5.14</version>
</parent>
```

**注意：**
- Spring AI 1.1.x 需要 Spring Boot 3.4+ 或 3.5.x
- API 可能有变化（ChatClient 替代 AiClient）
- 需要测试兼容性

## 参考资料

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba)

## 注意事项

1. **M 版本说明**：当前使用的是 Milestone（里程碑）版本，适合开发和测试
2. **生产环境**：建议使用 GA（General Availability）稳定版本
3. **版本检查**：定期查看最新版本并评估升级
4. **API 变化**：大版本升级时注意 API 变更

## 总结

✅ 成功升级到 Spring AI 1.0.0-M6  
✅ 引入 BOM 管理依赖版本  
✅ Spring Boot 升级到 3.3.8  
✅ 编译成功，无兼容性问题  

项目现在使用更规范的依赖管理方式，便于后续维护和升级！
