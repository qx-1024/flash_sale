package org.qiu.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: 用户预约实体类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/19 14:58
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservation_user")
public class ReservationUser {

    /**
     * 预约 id
     */
    @TableId("id")
    private String id;

    /**
     * 预约活动 id
     */
    @TableField("reservationId")
    private String reservationId;

    /**
     * 预约用户 id
     */
    @TableField("userId")
    private String userId;

    /**
     * 预约创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 预约更新时间
     */
    @JsonIgnore
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 预约是否删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;
}
