package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.pojo.BuyInfo;
import org.qiu.pojo.Product;
import org.qiu.service.ProductService;
import org.qiu.mapper.ProductMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-05-27 17:58:56
*/
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Resource
    private IdClient idClient;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public int saveProduct(Product product) {
        // 设置商品 ID
        String productId = idClient.generateId().toString();
        product.setProductId(productId);

        int inserted = productMapper.insert(product);

        // 新增商品信息到缓存
        redisTemplate.opsForValue().set(
                Constants.FLASH_SALE_PRODUCT_KEY + productId,
                product
        );

        return inserted;
    }

    @Override
    public List<Product> getFlashSaleProductList() {
        // 从 Redis 中获取缓存的闪购商品列表
        List<Product> products = (List<Product>) redisTemplate.opsForValue().get(Constants.FLASH_SALE_PRODUCT_KEY);

        if (products != null) {
            return products;
        }

        // 如果 Redis 中没有缓存数据，则从数据库中查询闪购商品列表
        products = productMapper.selectList(new QueryWrapper<Product>().eq("isFlashSale", 1));

        if (products != null && !products.isEmpty()) {
            // 过滤掉闪购活动已结束的商品
            products.removeIf(product -> {
                Integer isFlashSale = productMapper.isFlashSaleProduct(product.getProductId());
                // 如果 isFlashSale 为 null 或 0，则表示不是闪购商品
                return isFlashSale == null || isFlashSale == 0;
            });

            // 将从数据库中查询得到的闪购商品列表存入Redis缓存
            List<Product> finalProducts = products;
            CompletableFuture.runAsync(() -> {
                finalProducts.forEach(product -> redisTemplate.opsForValue()
                        .set(Constants.FLASH_SALE_PRODUCT_KEY + product.getProductId(), product));
            });
        }

        return products;
    }

    @Override
    public String buy(BuyInfo buyInfo) {
        // 已支付，可以生成订单
        if (buyInfo.getPayStatus() == 1) {
            String orderId = idClient.generateId().toString();
            buyInfo.setOrderId(orderId);

            rabbitTemplate.convertAndSend(Constants.FLASH_SALE_QUEUE_NAME, buyInfo);

            return orderId;
        }
        return null;
    }
}




