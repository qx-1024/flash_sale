package org.qiu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.mapper.ReservationMapper;
import org.qiu.pojo.Product;
import org.qiu.pojo.Reservation;
import org.qiu.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/13 9:07
 * @Version 1.0
 * @Since 1.0
 **/
@EnableScheduling       // 开启定时任务
@Component
public class DataTask {

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private ProductService productService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * 在项目启动时初始化缓存，后续每天中午12点更新一次缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = ONE_DAY_IN_MILLIS)
    public void initializeAndUpdateCache() {
        List<String> ids = productService.listObjs(
                new QueryWrapper<Product>()
                        .lambda()
                        .select(Product::getProductId)
        );

        ids.forEach(id -> {
            redisTemplate.opsForValue()
                    .set(
                            Constants.RESERVATION_LIST_KEY + id,
                            reservationMapper.reservationStatus(id)
                    );
        });
    }
}
