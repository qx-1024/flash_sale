package org.qiu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/10 17:30
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityQuery extends Activity {
    private String productName;
}
