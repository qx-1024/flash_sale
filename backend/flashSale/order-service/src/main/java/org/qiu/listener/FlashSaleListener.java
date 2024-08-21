package org.qiu.listener;

import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.BuyInfo;
import org.qiu.pojo.Order;
import org.qiu.pojo.Product;
import org.qiu.service.OrderService;
import org.qiu.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    @Resource
    private OrderService orderService;

    @Resource
    private ProductService productService;

    /**
     * 监听队列，生成订单
     */
    @Transactional
    @RabbitListener(queues = Constants.FLASH_SALE_QUEUE_NAME)
    public void onFlashSaleEvent(BuyInfo buyInfo){
        Order order = new Order();
        String productId = buyInfo.getProductId();
        String note = buyInfo.getNote();

        Product product = productService.getById(productId);
        String activityId = productService.getActivityId(productId);

        order.setOrderId(buyInfo.getOrderId());
        order.setProductId(productId);
        order.setUserId(buyInfo.getUserId());
        order.setPayStatus(buyInfo.getPayStatus());
        order.setAmount(product.getPrice());
        order.setActivityId(activityId);

        if (note == null || note.isEmpty()) {
            order.setNote("无");
        } else {
            order.setNote(note);
        }

        product.setStock(product.getStock() - 1);
        productService.updateById(product);
        orderService.save(order);
    }

}
