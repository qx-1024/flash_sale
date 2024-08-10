package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/8/10 8:51
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OngoingReservation {
    private String reservationId;
    private String reservationName;
}
