package org.qiu.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 17:48
 * @Version 1.0
 * @Since 1.0
 **/
@FeignClient("snowflake-id-generate")
public interface IdClient {
    @GetMapping("/snowflake/generateId")
    Long generateId();
}
