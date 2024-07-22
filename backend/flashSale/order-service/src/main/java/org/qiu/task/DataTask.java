package org.qiu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.Order;
import org.qiu.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/21 9:14
 * @Version 1.0
 * @Since 1.0
 **/
@EnableScheduling
@Component
public class DataTask {

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 时长常量
    private static final long TWELVE_HOUR = 12 * 60 * 60 * 1000;


    /**
     * 在项目启动时初始化缓存，后续在每分钟更新一次缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = TWELVE_HOUR)
    public void initializeAndUpdateCache() {
        List<Order> orderList = orderService.lambdaQuery().list();

        orderList.forEach(order -> {
            redisTemplate.opsForValue().set(Constants.ORDER_KEY + order.getOrderId(), order);
        });
    }

    /**
     * 在项目启动时执行一次，后续在每一个整点执行
     */
    /*@PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void updateReservationStatus(){

    }*/

}
