package org.qiu.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="product")
public class Product implements Serializable {
    /**
     * 商品id
     */
    @TableId(value = "productId")
    private String productId;

    /**
     * 商品名称
     */
    @TableField(value = "productName")
    private String productName;

    /**
     * 是否参与闪购（0：不参与 | 1：参与）
     */
    @TableField(value = "isFlashSale")
    private Integer isFlashSale;

    /**
     * 闪购价格（单价：元/件）
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 商品库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 商品销量
     */
    @TableField(value = "sales")
    private Integer sales;

    /**
     * 商品图片
     */
    @TableField(value = "images")
    private String images;

    /**
     * 商品创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 商品更新时间
     */
    @JsonIgnore
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 商品是否删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}