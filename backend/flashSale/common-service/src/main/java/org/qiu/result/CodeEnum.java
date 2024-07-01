package org.qiu.result;

import lombok.*;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/19 15:59
 * @Version 1.0
 * @Since 1.0
 **/
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public enum CodeEnum {
    OK(200, "成功"),
    FALL(500, "失败"),

    // token 不存在
    TOKEN_NOT_EXIST(901, "token 不存在"),
    // token 过期
    TOKEN_EXPIRED(902, "token 已过期"),
    // token 无效
    TOKEN_INVALID(903, "token 无效"),

    USER_LOGOUT(200, "退出成功");


    ;


    private int code;
    @NonNull
    private String msg;
}
