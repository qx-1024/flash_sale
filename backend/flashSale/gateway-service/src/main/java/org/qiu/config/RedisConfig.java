package org.qiu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;

/**
 * @Description: Redis 配置类，加载 lua 脚本文件
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/11 15:15
 * @Version 1.0
 * @Since 1.0
 **/
@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> rateLimitScript() throws IOException {
        // 加载 Lua 脚本文件
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("request_limit.lua"));
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(scriptSource);
        script.setResultType(Long.class);
        return script;
    }
}
