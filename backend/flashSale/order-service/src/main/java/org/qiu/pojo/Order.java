package org.qiu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 订单实体类
 * @TableName order
 */
@TableName(value ="`order`")
@Data
public class Order implements Serializable {
    /**
     * 订单id
     */
    @TableId(value = "orderId")
    private String orderId;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private String userId;

    /**
     * 闪购活动id
     */
    @TableField(value = "activityId")
    private String activityId;

    /**
     * 商品id
     */
    @TableField(value = "productId")
    private String productId;

    /**
     * 订单总价
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 订单备注
     */
    @TableField(value = "note")
    private String note;

    /**
     * 订单状态（0：未完成 | 1：已完成）
     */
    @TableField(value = "orderStatus")
    private Integer orderStatus;

    /**
     * 支付状态（0：未支付 | 1：已支付）
     */
    @TableField(value = "payStatus")
    private Integer payStatus;

    /**
     * 运输状态（0：未发货 | 1：已发货 | 2：已收货）
     */
    @TableField(value = "shippingStatus")
    private Integer shippingStatus;

    /**
     * 订单创建时间
     */
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 订单修改时间
     */
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 订单是否已删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}