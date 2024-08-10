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
 * 用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user")
public class User implements Serializable {
    /**
     * 用户 id
     */
    @TableId(value = "userId")
    private String userId;

    /**
     * 用户真实姓名
     */
    @TableField(value = "realName")
    private String realName;

    /**
     * 用户昵称
     */
    @TableField(value = "nickname")
    private String nickName;

    /**
     * 用户账号
     */
    @TableField(value = "account")
    private String account;

    /**
     * 用户密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户手机号
     */
    @TableField(value = "phoneNumber")
    private String phoneNumber;

    /**
     * 用户地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 用户类型（0：普通用户 | 1：VIP 用户）
     */
    @TableField(value = "userType")
    private Integer userType;

    /**
     * 用户性别（0：男 | 1：女 | 2：未知）
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 用户账号创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 用户账号更新时间
     */
    @JsonIgnore
    @TableField(value = "updateTime", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 用户是否删除（0：未删除 | 1：已删除）
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = "isDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}