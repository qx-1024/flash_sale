package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 闪购信息实体类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/21 16:46
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyInfo {

    private String orderId;

    private String userId;

    private String productId;

    private String note;

    private int payStatus;
}
