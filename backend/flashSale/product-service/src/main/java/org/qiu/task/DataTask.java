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
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 商品相关定时任务
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

    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;

    /**
     * 在项目启动时初始化缓存，后续每秒更新一次缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = ONE_MINUTE_IN_MILLIS)
    public void initializeAndUpdateCache() {
        // 查询数据库中所有标记为闪购商品的数据
        List<Product> products = productMapper.selectList(
                new QueryWrapper<Product>().eq("isFlashSale", 1)
        );

        // 将查询到的闪购商品列表存入 Redis 缓存
        if (products != null && !products.isEmpty()) {
            // 过滤掉闪购活动已结束的商品
            products.removeIf(product -> {
                Integer isFlashSale = productMapper.isFlashSaleProduct(product.getProductId());
                // 如果 isFlashSale 为 null 或 0，则表示不是闪购商品
                return isFlashSale == null || isFlashSale == 0;
            });

            // 将从数据库中查询得到的闪购商品列表存入Redis缓存
            CompletableFuture.runAsync(() -> {
                products.forEach(product -> redisTemplate.opsForValue()
                        .set(Constants.FLASH_SALE_PRODUCT_KEY + product.getProductId(), product));
            });
        }
    }

}
