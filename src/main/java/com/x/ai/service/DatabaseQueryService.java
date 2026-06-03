package com.x.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.ai.model.DatabaseQueryRequest;
import com.x.ai.model.DatabaseQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据库查询服务 - AI SQL 生成和执行
 */
@Slf4j
@Service
public class DatabaseQueryService {
    
    private final ChatClient dashScopeChatClient;
    private final ChatClient ollamaChatClient;
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseSchemaService schemaService;
    private final ObjectMapper objectMapper;
    
    public DatabaseQueryService(
            @Qualifier("chatClient") ChatClient dashScopeChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            JdbcTemplate jdbcTemplate,
            DatabaseSchemaService schemaService) {
        this.dashScopeChatClient = dashScopeChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.jdbcTemplate = jdbcTemplate;
        this.schemaService = schemaService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 执行自然语言数据库查询
     */
    public DatabaseQueryResponse executeQuery(DatabaseQueryRequest request) {
        try {
            // 1. 获取数据库 Schema 信息
            String schemaInfo = schemaService.getDatabaseSchemaInfo();
            
            // 2. 使用 AI 生成 SQL
            String sql = generateSQL(request.getQuestion(), schemaInfo, request.getModel());
            log.info("生成的 SQL: {}", sql);
            
            // 3. 验证 SQL 安全性（只允许 SELECT）
            if (!isSafeSQL(sql)) {
                return createErrorResponse("SQL 不安全或格式不正确，请检查生成的 SQL 是否包含 FROM 子句\n\n生成的 SQL: " + sql);
            }
            
            // 4. 执行 SQL 查询
            List<Map<String, Object>> rows;
            try {
                rows = executeSQL(sql);
            } catch (Exception e) {
                log.error("SQL 执行失败: {}, SQL: {}", e.getMessage(), sql);
                return createErrorResponse("SQL 执行失败: " + e.getMessage() + "\n\n生成的 SQL: " + sql);
            }
            
            // 5. 提取列名
            List<String> columns = extractColumns(rows);
            
            // 6. 构建响应
            DatabaseQueryResponse response = new DatabaseQueryResponse();
            response.setSuccess(true);
            response.setSql(sql);
            response.setColumns(columns);
            response.setRows(rows);
            response.setTotalRows(rows.size());
            
            // 7. 如果需要图表展示，生成 ECharts 配置
            if ("chart".equals(request.getDisplayType())) {
                Map<String, Object> echartsOption = generateEChartsOption(
                    request.getChartType(), 
                    columns, 
                    rows,
                    request.getQuestion()
                );
                response.setEchartsOption(echartsOption);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("执行数据库查询失败", e);
            return createErrorResponse("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用 AI 生成 SQL 语句
     */
    private String generateSQL(String question, String schemaInfo, String model) {
        String prompt = String.format("""
            你是一个专业的 PostgreSQL SQL 专家。根据用户的自然语言问题和数据库 Schema 信息，生成对应的 SQL 查询语句。
            
            重要规则：
            1. 只生成 SELECT 查询，不允许 INSERT、UPDATE、DELETE、DROP 等修改操作
            2. SQL 必须符合 PostgreSQL 语法规范
            3. 如果问题涉及聚合、排序、分组等，请合理使用 SQL 功能
            4. 限制返回行数，最多返回 1000 条记录（使用 LIMIT 1000）
            5. 只返回 SQL 语句本身，不要包含任何解释、注释或其他文字
            6. 不要使用 markdown 代码块标记（如 ```sql）
            7. 必须包含 FROM 子句指定表名
            8. 只能使用 Schema 信息中提供的表和列
            9. 如果需要表注释，请使用 pg_description 系统表
            10. SQL 必须在同一行，不要换行
            
            示例：
            - 查询所有表：SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'
            - 查询用户表：SELECT * FROM users LIMIT 1000
            - 统计数量：SELECT COUNT(*) FROM users
            - 查询表注释：SELECT table_name, obj_description((quote_ident(table_schema) || '.' || quote_ident(table_name))::regclass) AS table_comment FROM information_schema.tables WHERE table_schema = 'public'
            
            数据库 Schema 信息：
            %s
            
            用户问题：%s
            
            请生成完整的 SQL 语句（必须包含 FROM 子句，且在同一行）：
            """, schemaInfo, question);
        
        // 根据模型名称选择对应的 ChatClient
        ChatClient client = isOllamaModel(model) ? ollamaChatClient : dashScopeChatClient;
        
        log.info("开始生成 SQL，模型：{}", model);
        
        try {
            String result = client.prompt()
                    .system("你是一个专业的 PostgreSQL SQL 专家，擅长将自然语言转换为 SQL 查询。")
                    .user(prompt)
                    .options(org.springframework.ai.chat.prompt.ChatOptions.builder()
                            .model(model)
                            .temperature(0.1)  // 降低随机性，让输出更稳定
                            .build())
                    .call()
                    .content();
            
            log.info("AI 返回的原始结果：{}", result);
            
            // 清理结果，提取纯 SQL
            return cleanSQL(result);
        } catch (Exception e) {
            log.error("AI 生成 SQL 失败", e);
            throw new RuntimeException("AI 生成 SQL 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 判断是否为 Ollama 模型
     */
    private boolean isOllamaModel(String model) {
        // Ollama 模型名称通常包含 ':' 分隔符（如 qwen2.5-coder:7b）
        return model != null && model.contains(":");
    }
    
    /**
     * 清理 SQL 字符串，移除多余的内容
     */
    private String cleanSQL(String sql) {
        if (sql == null || sql.isEmpty()) {
            throw new RuntimeException("AI 未生成有效的 SQL");
        }
        
        log.info("清理前的 SQL: {}", sql);
        
        // 移除 markdown 代码块标记
        sql = sql.replaceAll("```sql\\s*", "").replaceAll("```\\s*$", "");
        
        // 移除首尾空白
        sql = sql.trim();
        
        // 将多行 SQL 合并为一行（替换换行符为空格）
        sql = sql.replaceAll("\\s+", " ").trim();
        
        // 移除注释
        sql = sql.replaceAll("--[^\\n]*", "").trim();
        
        log.info("清理后的 SQL: {}", sql);
        
        // 验证 SQL 是否完整
        if (!sql.toUpperCase().contains("FROM")) {
            throw new RuntimeException("生成的 SQL 不完整，缺少 FROM 子句：" + sql);
        }
        
        return sql;
    }
    
    /**
     * 验证 SQL 安全性
     */
    private boolean isSafeSQL(String sql) {
        if (sql == null || sql.isEmpty()) {
            return false;
        }
        
        String upperSQL = sql.toUpperCase().trim();
        
        // 只允许 SELECT
        if (!upperSQL.startsWith("SELECT")) {
            return false;
        }
        
        // 必须包含 FROM 子句
        if (!upperSQL.contains("FROM")) {
            return false;
        }
        
        // 禁止危险关键字
        String[] dangerousKeywords = {
            "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", 
            "TRUNCATE", "GRANT", "REVOKE", "EXECUTE"
        };
        
        for (String keyword : dangerousKeywords) {
            if (upperSQL.contains(" " + keyword) || upperSQL.contains("\t" + keyword)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 执行 SQL 查询
     */
    private List<Map<String, Object>> executeSQL(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new RuntimeException("SQL 执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从查询结果中提取列名
     */
    private List<String> extractColumns(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(rows.get(0).keySet());
    }
    
    /**
     * 生成 ECharts 配置
     */
    private Map<String, Object> generateEChartsOption(
            String chartType, 
            List<String> columns, 
            List<Map<String, Object>> rows,
            String question) {
        
        Map<String, Object> option = new LinkedHashMap<>();
        
        // 标题
        Map<String, Object> title = new LinkedHashMap<>();
        title.put("text", question);
        title.put("left", "center");
        option.put("title", title);
        
        // 工具提示
        Map<String, Object> tooltip = new LinkedHashMap<>();
        tooltip.put("trigger", "axis");
        option.put("tooltip", tooltip);
        
        // 图例
        Map<String, Object> legend = new LinkedHashMap<>();
        legend.put("data", columns.subList(1, Math.min(columns.size(), 4)));
        legend.put("top", "10%");
        option.put("legend", legend);
        
        // 网格
        Map<String, Object> grid = new LinkedHashMap<>();
        grid.put("left", "3%");
        grid.put("right", "4%");
        grid.put("bottom", "3%");
        grid.put("containLabel", true);
        option.put("grid", grid);
        
        // 根据图表类型生成不同的配置
        switch (chartType.toLowerCase()) {
            case "bar":
                generateBarChart(option, columns, rows);
                break;
            case "line":
                generateLineChart(option, columns, rows);
                break;
            case "pie":
                generatePieChart(option, columns, rows);
                break;
            case "scatter":
                generateScatterChart(option, columns, rows);
                break;
            default:
                generateBarChart(option, columns, rows);
        }
        
        return option;
    }
    
    /**
     * 生成柱状图配置
     */
    private void generateBarChart(Map<String, Object> option, List<String> columns, List<Map<String, Object>> rows) {
        if (columns.isEmpty() || rows.isEmpty()) {
            return;
        }
        
        String xAxisColumn = columns.get(0);
        
        // X 轴
        Map<String, Object> xAxis = new LinkedHashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", rows.stream()
            .map(row -> String.valueOf(row.get(xAxisColumn)))
            .toList());
        option.put("xAxis", xAxis);
        
        // Y 轴
        Map<String, Object> yAxis = new LinkedHashMap<>();
        yAxis.put("type", "value");
        option.put("yAxis", yAxis);
        
        // 数据系列
        List<Map<String, Object>> series = new ArrayList<>();
        for (int i = 1; i < columns.size(); i++) {
            String column = columns.get(i);
            Map<String, Object> serie = new LinkedHashMap<>();
            serie.put("name", column);
            serie.put("type", "bar");
            serie.put("data", rows.stream()
                .map(row -> {
                    Object value = row.get(column);
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }
                    return 0;
                })
                .toList());
            series.add(serie);
        }
        option.put("series", series);
    }
    
    /**
     * 生成折线图配置
     */
    private void generateLineChart(Map<String, Object> option, List<String> columns, List<Map<String, Object>> rows) {
        if (columns.isEmpty() || rows.isEmpty()) {
            return;
        }
        
        String xAxisColumn = columns.get(0);
        
        // X 轴
        Map<String, Object> xAxis = new LinkedHashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", rows.stream()
            .map(row -> String.valueOf(row.get(xAxisColumn)))
            .toList());
        option.put("xAxis", xAxis);
        
        // Y 轴
        Map<String, Object> yAxis = new LinkedHashMap<>();
        yAxis.put("type", "value");
        option.put("yAxis", yAxis);
        
        // 数据系列
        List<Map<String, Object>> series = new ArrayList<>();
        for (int i = 1; i < columns.size(); i++) {
            String column = columns.get(i);
            Map<String, Object> serie = new LinkedHashMap<>();
            serie.put("name", column);
            serie.put("type", "line");
            serie.put("smooth", true);
            serie.put("data", rows.stream()
                .map(row -> {
                    Object value = row.get(column);
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }
                    return 0;
                })
                .toList());
            series.add(serie);
        }
        option.put("series", series);
    }
    
    /**
     * 生成饼图配置
     */
    private void generatePieChart(Map<String, Object> option, List<String> columns, List<Map<String, Object>> rows) {
        if (columns.size() < 2 || rows.isEmpty()) {
            return;
        }
        
        String nameColumn = columns.get(0);
        String valueColumn = columns.get(1);
        
        // 饼图不需要坐标轴
        option.remove("xAxis");
        option.remove("yAxis");
        option.remove("grid");
        
        // 数据系列
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", String.valueOf(row.get(nameColumn)));
            Object value = row.get(valueColumn);
            if (value instanceof Number) {
                item.put("value", ((Number) value).doubleValue());
            } else {
                item.put("value", 0);
            }
            data.add(item);
        }
        
        Map<String, Object> serie = new LinkedHashMap<>();
        serie.put("name", valueColumn);
        serie.put("type", "pie");
        serie.put("radius", "50%");
        serie.put("data", data);
        serie.put("emphasis", Map.of(
            "itemStyle", Map.of(
                "shadowBlur", 10,
                "shadowOffsetX", 0,
                "shadowColor", "rgba(0, 0, 0, 0.5)"
            )
        ));
        
        List<Map<String, Object>> series = new ArrayList<>();
        series.add(serie);
        option.put("series", series);
    }
    
    /**
     * 生成散点图配置
     */
    private void generateScatterChart(Map<String, Object> option, List<String> columns, List<Map<String, Object>> rows) {
        if (columns.size() < 2 || rows.isEmpty()) {
            return;
        }
        
        String xColumn = columns.get(0);
        String yColumn = columns.get(1);
        
        // X 轴
        Map<String, Object> xAxis = new LinkedHashMap<>();
        xAxis.put("type", "value");
        xAxis.put("name", xColumn);
        option.put("xAxis", xAxis);
        
        // Y 轴
        Map<String, Object> yAxis = new LinkedHashMap<>();
        yAxis.put("type", "value");
        yAxis.put("name", yColumn);
        option.put("yAxis", yAxis);
        
        // 数据系列
        List<List<Object>> data = rows.stream()
            .map(row -> {
                List<Object> point = new ArrayList<>();
                Object xValue = row.get(xColumn);
                Object yValue = row.get(yColumn);
                
                if (xValue instanceof Number) {
                    point.add(((Number) xValue).doubleValue());
                } else {
                    point.add(0);
                }
                
                if (yValue instanceof Number) {
                    point.add(((Number) yValue).doubleValue());
                } else {
                    point.add(0);
                }
                
                return point;
            })
            .toList();
        
        Map<String, Object> serie = new LinkedHashMap<>();
        serie.put("type", "scatter");
        serie.put("symbolSize", 10);
        serie.put("data", data);
        
        List<Map<String, Object>> series = new ArrayList<>();
        series.add(serie);
        option.put("series", series);
    }
    
    /**
     * 创建错误响应
     */
    private DatabaseQueryResponse createErrorResponse(String error) {
        DatabaseQueryResponse response = new DatabaseQueryResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
}
