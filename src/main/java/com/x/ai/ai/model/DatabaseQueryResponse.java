package com.x.ai.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseQueryResponse {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 生成的 SQL 语句
     */
    private String sql;
    
    /**
     * 错误信息（如果失败）
     */
    private String error;
    
    /**
     * 数据列名列表
     */
    private List<String> columns;
    
    /**
     * 数据行列表
     */
    private List<Map<String, Object>> rows;
    
    /**
     * ECharts 配置（当需要图表展示时）
     */
    private Map<String, Object> echartsOption;
    
    /**
     * 总行数
     */
    private int totalRows;
}
