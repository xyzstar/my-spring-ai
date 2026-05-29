package com.x.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseQueryRequest {
    
    /**
     * 用户的自然语言问题
     */
    private String question;
    
    /**
     * 使用的 AI 模型
     */
    private String model;
    
    /**
     * 展示类型：table（表格）或 chart（图表）
     */
    private String displayType = "table";
    
    /**
     * 图表类型（当 displayType 为 chart 时有效）
     * 支持：bar（柱状图）、line（折线图）、pie（饼图）、scatter（散点图）
     */
    private String chartType = "bar";
}
