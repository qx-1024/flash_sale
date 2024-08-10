package org.qiu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 预约活动实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="reservation")
public class Reservation implements Serializable {
    /**
     * 预约活动 id
     */
    @TableId(value = "reservationId")
    private String reservationId;

    /**
     * 预约的闪购活动 id
     */
    @TableField(value = "activityId")
    private String activityId;

    /**
     * 预约的闪购活动名称
     */
    @TableField(exist = false)
    private String activityName;

    /**
     * 预约名称
     */
    @TableField(value = "reservationName")
    private String reservationName;

    /**
     * 预约状态（0：预约未开始 | 1：预约已结束 | 2：预约进行中）
     */
    @TableField(value = "reservationStatus")
    private Integer reservationStatus;


    // 标记不在数据库表中
    @TableField(exist = false)
    private Integer count;

    /**
     * 预约活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "startTime")
    private LocalDateTime startTime;

    /**
     * 预约活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "endTime")
    private LocalDateTime endTime;

    /**
     * 预约活动创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 预约活动更新时间
     */
    @JsonIgnore
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 预约活动是否删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}