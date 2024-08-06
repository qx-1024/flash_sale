package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户查询对象
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/22 14:18
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQuery {
    private String account;
    private String password;
    private String confirmPassword;
}
