package org.qiu.filters;

import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.result.CodeEnum;
import org.qiu.result.R;
import org.qiu.utils.JSONUtil;
import org.qiu.utils.JWTUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 网关全局拦截器
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/31 14:58
 * @Version 1.0
 * @Since 1.0
 **/
@Order(-1)
@Component
public class MyGlobalFilter implements GlobalFilter {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisScript<Long> rateLimitScript;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uriStr = getString(request);

        HttpHeaders headers = request.getHeaders();
        // 获取请求头中的 token
        String token = headers.getFirst(Constants.TOKEN_HEADER);


        // 同一个 IP 在一秒内仅有 5 次请求被接收，其余的请求直接拒绝
        String clientIpAddress = request.getRemoteAddress().getAddress().getHostAddress();
        String key = Constants.REQUEST_KEY + clientIpAddress;

        // 执行 Lua 脚本来进行请求限制
        Long res = redisTemplate.execute(rateLimitScript, Collections.singletonList(key), "5");

        if (res != null && res == 0) {
            // 如果超过限制，返回 429 Too Many Requests
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return response.setComplete();
        }


        // 对登录、注册、获取验证码、登出以外的请求拦截
        if (!uriStr.equals(Constants.USER_LOGIN_URI) &&
                !uriStr.equals(Constants.USER_REGISTER_URI) &&
                !uriStr.equals(Constants.USER_GET_CAPTCHA_URI) &&
                !uriStr.equals(Constants.USER_LOGOUT_URI)) {
            // token 不存在
            if (!StringUtils.hasText(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_NOT_EXIST);

                // 把 R 对象转为 json
                String json = JSONUtil.toJSON(result);

                // 响应给前端
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }


            // token 不为空，但验证未通过
            if (!JWTUtil.checkToken(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_INVALID);
                // 把 R 对象转为 json
                String json = JSONUtil.toJSON(result);
                // 响应给前端
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }


            // token 验证通过，但 redis 中不存在（过期）
            String userId = JWTUtil.parseToken(token);
            String jwt = (String) redisTemplate.opsForValue().get(Constants.TOKEN_KEY + userId);
            if (!StringUtils.hasText(jwt)) {
                R result = R.FAIL(CodeEnum.TOKEN_EXPIRED);
                // 把 R 对象转为 json
                String json = JSONUtil.toJSON(result);
                // 响应给前端
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }


            // redis 中有 token，比较前端传过来的 token 和 redis 的 token 是否相等
            if (!token.equals(jwt)) {
                R result = R.FAIL(CodeEnum.TOKEN_INVALID);
                // 把 R 对象转为 json
                String json = JSONUtil.toJSON(result);
                // 响应给前端
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }


            // 使用线程池，更新 redis 中 token 以及当前登录用户信息的过期时间
            threadPoolTaskExecutor.execute(() -> {
                redisTemplate.expire(Constants.TOKEN_KEY + userId,
                        Constants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

                redisTemplate.expire(Constants.CURRENT_LOGIN_USER + userId,
                        Constants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            });


        }
        return chain.filter(exchange);
    }

    /**
     * 获取 URI 中第一个 '/' 与 '?' 之间的字符串
     */
    private static String getString(ServerHttpRequest request) {
        String uriStr = request.getURI().toString().substring(21);
        // 如果 uriStr 中有 "?" 则截取第一个 ‘/’ 与 ‘？’ 之间的字符传，
        // 反之则只截取第一个 ‘/’之后的字符串（不需要包含‘/’）
        int firstSlashIndex = uriStr.indexOf('/');
        int questionMarkIndex = uriStr.indexOf('?');

        return (questionMarkIndex == -1) ?
                uriStr.substring(firstSlashIndex) :
                uriStr.substring(firstSlashIndex, questionMarkIndex);
    }
}
