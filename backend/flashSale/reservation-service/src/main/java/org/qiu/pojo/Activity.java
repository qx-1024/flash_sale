package org.qiu.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 闪购活动实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="activity")
public class Activity implements Serializable {
    /**
     * 闪购活动id
     */
    @TableId(value = "activityId")
    private String activityId;

    /**
     * 闪购商品id
     */
    @TableField(value = "productId")
    private String productId;

    /**
     * 闪购活动名称
     */
    @TableField(value = "activityName")
    private String activityName;

    /**
     * 闪购活动状态（0：闪购未开始 | 1：闪购已结束 | 2：闪购进行中）
     */
    @TableField(value = "activityStatus")
    private Integer activityStatus;

    /**
     * 闪购开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "startTime")
    private LocalDateTime startTime;

    /**
     * 闪购结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "endTime")
    private LocalDateTime endTime;

    /**
     * 闪购活动创建时间
     */
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 闪购活动更新时间
     */
    @JsonIgnore
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 闪购活动是否删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}