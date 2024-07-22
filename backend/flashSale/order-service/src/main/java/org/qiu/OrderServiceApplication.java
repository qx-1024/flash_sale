package org.qiu;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.TimeZone;

@EnableFeignClients
@SpringBootApplication
@MapperScan("org.qiu.mapper")
public class OrderServiceApplication implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    /**
     * 分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusPagination() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    /**
     * 配置 RabbitMQ 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    /**
     * Redis 序列化
     */
    @Override
    public void run(String... args) throws Exception {
        // 设置 RedisTemplate 的键序列化器为 StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 创建 ObjectMapper 实例
        ObjectMapper mapper = new ObjectMapper();
        // 设置 ObjectMapper 的可见性，使其能够访问所有字段和方法
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 激活默认的类型信息，以便序列化和反序列化时包含类型信息
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.EVERYTHING);
        // 创建一个 JavaTimeModule 对象，用于支持 Java 8 时间类型的序列化和反序列化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 注册 JavaTimeModule 到 ObjectMapper 中，以便支持 Java 8 时间类型的处理
        mapper.registerModule(javaTimeModule);
        // 配置 ObjectMapper，将日期序列化为 ISO-8601 格式的字符串而不是时间戳
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 设置 RedisTemplate 的值序列化器为 Jackson2JsonRedisSerializer，使用上面配置的 ObjectMapper
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(mapper, Object.class));
        // 设置 RedisTemplate 的哈希键序列化器为 StringRedisSerializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置 RedisTemplate 的哈希值序列化器为 Jackson2JsonRedisSerializer，使用上面配置的 ObjectMapper
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(mapper, Object.class));
    }

}
