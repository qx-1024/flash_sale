package org.qiu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.pojo.Order;
import org.qiu.pojo.OrderQuery;
import org.qiu.service.OrderService;
import org.qiu.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public Page<OrderQuery> getByPage(Integer current, int defaultPageSize) {
        Page<Order> page = orderMapper.selectPage(new Page<>(current, defaultPageSize), null);
        Page<OrderQuery> pageQuery = new Page<>(current, defaultPageSize);

        List<Order> orders = page.getRecords();
        List<OrderQuery> records = new ArrayList<>(orders.size());

        // 遍历原始的订单列表，并将每个 Order 转换为 OrderQuery
        for (Order order : orders) {
            OrderQuery orderQuery = new OrderQuery();

            // 手动设置 OrderQuery 额外的字段
            orderQuery.setUserName(orderMapper.getUserName(order.getUserId()));
            orderQuery.setActivityName(orderMapper.getActivityName(order.getActivityId()));
            orderQuery.setProductName(orderMapper.getProductName(order.getProductId()));

            // 使用 BeanUtils 工具类进行自动属性复制
            BeanUtils.copyProperties(order, orderQuery);

            // 添加转换后的 OrderQuery 到新的 Page 对象
            records.add(orderQuery);
        }

        // 设置分页信息
        pageQuery.setRecords(records);
        pageQuery.setTotal(page.getTotal());
        pageQuery.setSize(page.getSize());
        pageQuery.setCurrent(page.getCurrent());

        return pageQuery;
    }

    /**
     * 新增订单【异步 MQ】
     * @param order     订单信息
     * @return          新增结果
     */
    @Override
    public boolean saveOrder(Order order) {
        String orderId = idClient.generateId().toString();
        order.setOrderId(orderId);

        redisTemplate.opsForValue().set(Constants.ORDER_KEY + orderId, order);

        return orderMapper.insert(order) > 0;
    }

    /**
     * 获取已完成订单的总金额
     */
    @Override
    public double getTotal() {
        return orderMapper.getTotal();
    }
}




