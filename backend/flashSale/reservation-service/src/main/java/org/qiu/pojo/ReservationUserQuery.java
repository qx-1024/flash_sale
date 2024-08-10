package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户预约查询对象
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/8 9:14
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUserQuery extends ReservationUser{
    private String reservationName;
    private String realName;
}
