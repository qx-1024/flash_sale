package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 预约活动查询对象
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/10 9:33
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationQuery extends Reservation{
    private String activityName;
}
