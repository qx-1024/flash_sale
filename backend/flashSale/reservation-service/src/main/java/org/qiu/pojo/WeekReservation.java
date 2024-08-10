package org.qiu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: 周预约信息查询对象
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/6 16:12
 * @Version 1.0
 * @Since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekReservation {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime date;

    private Integer count;

}
