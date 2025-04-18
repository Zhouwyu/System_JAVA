package com.geekzhou.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_revision")
public class OrderRevision {

    @TableId(value = "revision_id", type = IdType.AUTO)
    private Long revisionId;

    @TableField("order_no")
    private String orderNo;

    @TableField("revision_number")
    private Integer revisionNumber;

    @TableField(value = "revision_data", typeHandler = JacksonTypeHandler.class)
    private Object revisionData; // 使用Object类型接收JSON数据

    @TableField("revision_type")
    private String revisionType;

    @TableField("operator")
    private String operator;

    @TableField("revision_remark")
    private String revisionRemark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
