package org.qiu.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Description: 异常处理拦截器
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/29 12:38
 * @Version 1.0
 * @Since 1.0
 **/
@Slf4j
@Order(100)
@Component
public class ExceptionHandlerFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            return chain.filter(exchange);
        } catch (Exception e) {
            // 处理异常
            return handleException(e, exchange);
        }
    }

    private Mono<Void> handleException(Exception e, ServerWebExchange exchange) {
        // 根据不同的异常类型返回不同的HTTP状态码和错误消息
        HttpStatus status;
        String errorMessage;

        if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Bad Request: " + e.getMessage();
        } else if (e instanceof NullPointerException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Null Pointer Exception: " + e.getMessage();
        } else if (e instanceof IllegalStateException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Illegal State Exception: " + e.getMessage();
        } else if (e instanceof UnsupportedOperationException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Unsupported Operation: " + e.getMessage();
        } else if (e instanceof RuntimeException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Runtime Exception: " + e.getMessage();
        } else {
            // 默认错误处理
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "An internal server error occurred: " + e.getMessage();
        }

        // 记录错误日志
        log.error(errorMessage);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // 返回错误响应体
        String responseBody = "{\"error\":\"" + errorMessage + "\"}";
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
