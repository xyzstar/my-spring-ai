package com.x.ai.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库 Schema 服务 - 获取数据库表结构信息
 */
@Slf4j
@Service
public class DatabaseSchemaService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DatabaseSchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * 获取所有表的名称和描述
     */
    public List<Map<String, Object>> getTableNames() {
        String sql = """
            SELECT 
                table_name,
                obj_description((quote_ident(table_schema) || '.' || quote_ident(table_name))::regclass) as description
            FROM information_schema.tables
            WHERE table_schema = 'public'
              AND table_type = 'BASE TABLE'
            ORDER BY table_name
            """;
        
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("获取表名失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取指定表的列信息
     */
    public List<Map<String, Object>> getTableColumns(String tableName) {
        String sql = """
            SELECT 
                column_name,
                data_type,
                is_nullable,
                column_default,
                character_maximum_length,
                numeric_precision,
                ordinal_position
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = ?
            ORDER BY ordinal_position
            """;
        
        try {
            return jdbcTemplate.queryForList(sql, tableName);
        } catch (Exception e) {
            log.error("获取表 {} 的列信息失败", tableName, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取完整的数据库 Schema 信息（用于 AI 上下文）
     */
    public String getDatabaseSchemaInfo() {
        StringBuilder schemaInfo = new StringBuilder();
        schemaInfo.append("PostgreSQL 数据库 Schema 信息:\n\n");
        
        List<Map<String, Object>> tables = getTableNames();
        
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            String description = (String) table.get("description");
            
            schemaInfo.append("表名: ").append(tableName);
            if (description != null && !description.isEmpty()) {
                schemaInfo.append(" (").append(description).append(")");
            }
            schemaInfo.append("\n");
            
            List<Map<String, Object>> columns = getTableColumns(tableName);
            for (Map<String, Object> column : columns) {
                schemaInfo.append("  - ")
                    .append(column.get("column_name"))
                    .append(" (")
                    .append(column.get("data_type"));
                
                if ("character varying".equals(column.get("data_type")) || "varchar".equals(column.get("data_type"))) {
                    Integer length = (Integer) column.get("character_maximum_length");
                    if (length != null) {
                        schemaInfo.append("(").append(length).append(")");
                    }
                }
                
                schemaInfo.append(", ")
                    .append("nullable=".equals(column.get("is_nullable")) ? "可空" : "非空");
                
                Object defaultValue = column.get("column_default");
                if (defaultValue != null) {
                    schemaInfo.append(", 默认值=").append(defaultValue);
                }
                
                schemaInfo.append(")\n");
            }
            schemaInfo.append("\n");
        }
        
        return schemaInfo.toString();
    }
}
