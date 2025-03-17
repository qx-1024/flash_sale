package org.qiu.listener;

import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.BuyInfo;
import org.qiu.pojo.Order;
import org.qiu.pojo.Product;
import org.qiu.service.OrderService;
import org.qiu.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;


/**
 * @Description: 闪购监听 -- 生成订单【消费者】
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/21 16:40
 * @Version 1.0
 * @Since 1.0
 **/
@Component
public class FlashSaleListener {

    private static final Logger log = LoggerFactory.getLogger(FlashSaleListener.class);

    @Resource
    private OrderService orderService;

    @Resource
    private ProductService productService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 监听队列，生成订单
     */
    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = Constants.FLASH_SALE_QUEUE_NAME)
    public void onFlashSaleEvent(BuyInfo buyInfo) {
        String orderKey = "order:processed:" + buyInfo.getOrderId();
        
        // 1. 幂等性检查
        if (Boolean.TRUE.equals(redisTemplate.hasKey(orderKey))) {
            log.info("订单已处理，跳过重复消息：{}", buyInfo.getOrderId());
            return;
        }

        try {
            // 2. 库存检查和扣减（使用乐观锁）
            Product product = productService.getById(buyInfo.getProductId());
            if (product == null || product.getStock() <= 0) {
                log.error("商品库存不足：{}", buyInfo.getProductId());
                throw new RuntimeException("商品库存不足");
            }

            boolean updateSuccess = productService.update()
                .setSql("stock = stock - 1")
                .eq("productId", product.getProductId())
                .gt("stock", 0)
                .update();

            if (!updateSuccess) {
                log.error("库存扣减失败：{}", buyInfo.getProductId());
                throw new RuntimeException("库存扣减失败");
            }

            // 3. 创建订单
            Order order = new Order();
            String activityId = productService.getActivityId(buyInfo.getProductId());
            
            order.setOrderId(buyInfo.getOrderId());
            order.setProductId(buyInfo.getProductId());
            order.setUserId(buyInfo.getUserId());
            order.setPayStatus(buyInfo.getPayStatus());
            order.setAmount(product.getPrice());
            order.setActivityId(activityId);
            order.setNote(buyInfo.getNote() == null || buyInfo.getNote().isEmpty() ? "无" : buyInfo.getNote());

            // 4. 保存订单
            orderService.save(order);

            // 5. 标记订单处理完成（设置30分钟过期，足够覆盖MQ的重试时间）
            redisTemplate.opsForValue().set(orderKey, "1", 30, TimeUnit.MINUTES);
            
            log.info("订单处理成功：{}", buyInfo.getOrderId());
            
        } catch (Exception e) {
            log.error("订单处理失败：{}", buyInfo.getOrderId(), e);
            throw e; // 抛出异常触发消息重试
        }
    }

}
