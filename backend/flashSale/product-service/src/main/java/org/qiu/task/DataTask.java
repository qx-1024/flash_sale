package org.qiu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.mapper.ProductMapper;
import org.qiu.pojo.Product;
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
 * @Date: 2024/7/12 14:49
 * @Version 1.0
 * @Since 1.0
 **/
@EnableScheduling       // 开启定时任务
@Component
public class DataTask {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    // 时间常量
    private static final long TWELVE_HOUR = 12 * 60 * 60 * 1000;

    /**
     * 在项目启动时初始化缓存，后续每秒更新一次缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = TWELVE_HOUR)
    public void initializeAndUpdateCache() {
        // 查询数据库中所有标记为闪购商品的数据
        List<Product> products = productMapper.selectList(
                new QueryWrapper<Product>().eq("isFlashSale", 1)
        );

        // 将查询到的闪购商品列表存入 Redis 缓存
        redisTemplate.opsForValue().set(Constants.FLASH_SALE_PRODUCT_KEY, products);
    }

}
