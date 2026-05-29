package com.x.ai.controller;

import com.x.ai.model.DatabaseQueryRequest;
import com.x.ai.model.DatabaseQueryResponse;
import com.x.ai.service.DatabaseQueryService;
import com.x.ai.service.DatabaseSchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询控制器
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {
    
    private final DatabaseQueryService queryService;
    private final DatabaseSchemaService schemaService;
    
    public DatabaseController(
            DatabaseQueryService queryService,
            DatabaseSchemaService schemaService) {
        this.queryService = queryService;
        this.schemaService = schemaService;
    }
    
    /**
     * 执行自然语言数据库查询
     */
    @PostMapping("/query")
    public ResponseEntity<DatabaseQueryResponse> executeQuery(@RequestBody DatabaseQueryRequest request) {
        try {
            DatabaseQueryResponse response = queryService.executeQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DatabaseQueryResponse errorResponse = new DatabaseQueryResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("服务器错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取数据库表列表
     */
    @GetMapping("/tables")
    public ResponseEntity<List<Map<String, Object>>> getTables() {
        try {
            List<Map<String, Object>> tables = schemaService.getTableNames();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }
    
    /**
     * 获取指定表的列信息
     */
    @GetMapping("/table/{tableName}/columns")
    public ResponseEntity<List<Map<String, Object>>> getTableColumns(@PathVariable String tableName) {
        try {
            List<Map<String, Object>> columns = schemaService.getTableColumns(tableName);
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }
    
    /**
     * 获取完整的数据库 Schema 信息
     */
    @GetMapping("/schema")
    public ResponseEntity<String> getSchemaInfo() {
        try {
            String schemaInfo = schemaService.getDatabaseSchemaInfo();
            return ResponseEntity.ok(schemaInfo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取 Schema 信息失败: " + e.getMessage());
        }
    }
}
