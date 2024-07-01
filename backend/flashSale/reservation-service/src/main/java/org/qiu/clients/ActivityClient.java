package org.qiu.clients;

import org.qiu.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/10 10:27
 * @Version 1.0
 * @Since 1.0
 **/
@FeignClient("activity-service")
public interface ActivityClient {

    @GetMapping("/activity/one")
    R selectActivityById(@RequestParam("activityId") String activityId);

}
