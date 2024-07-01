package org.qiu;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class GatewayServiceApplication implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    /**
     * Redis 序列化：解决乱码问题，例如：\xAC\xED\x00\x05t\x00\x04pmnm
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        ObjectMapper mapper = new ObjectMapper();

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING);

        redisTemplate.setValueSerializer(
                new Jackson2JsonRedisSerializer<Object>(mapper, Object.class)
        );
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(
                new Jackson2JsonRedisSerializer<Object>(mapper, Object.class)
        );
    }

}
