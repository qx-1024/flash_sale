package org.qiu.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        // 配置消费者数量
        factory.setConcurrentConsumers(4);  // 最小消费者数量
        factory.setMaxConcurrentConsumers(8);  // 最大消费者数量
        
        // 每次只处理一条消息
        factory.setPrefetchCount(1);
        
        // 手动确认消息
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        // 配置消息转换器
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        
        // 设置消费者标签策略
        factory.setConsumerTagStrategy(queue -> "flash_sale_consumer_" + queue);
        
        return factory;
    }
} 