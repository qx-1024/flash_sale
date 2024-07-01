package org.qiu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.pojo.Order;
import org.qiu.service.OrderService;
import org.qiu.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Qiu
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-06-05 16:25:06
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

    @Resource
    private IdClient idClient;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public boolean saveOrder(Order order) {
        String orderId = idClient.generateId().toString();
        order.setOrderId(orderId);
        return orderMapper.insert(order) > 0;
    }
}




