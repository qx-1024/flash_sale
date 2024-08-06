package org.qiu.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: ID 生成器客户端
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/19 11:17
 * @Version 1.0
 * @Since 1.0
 **/
@FeignClient("snowflake-id-generate")
public interface IdClient {

    @GetMapping("/snowflake/generateId")
    Long generateId();
}
