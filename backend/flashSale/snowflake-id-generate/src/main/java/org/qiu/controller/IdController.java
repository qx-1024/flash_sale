package org.qiu.controller;

import jakarta.annotation.Resource;
import org.qiu.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/11 17:23
 * @Version 1.0
 * @Since 1.0
 **/
@RestController
@RequestMapping("/snowflake")
public class IdController {

    @Resource
    private SnowflakeIdGenerator idGenerator;

    @GetMapping("/generateId")
    public long generateId() {
        return idGenerator.generateId();
    }
}
