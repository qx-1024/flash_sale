package org.qiu.service;

import org.qiu.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Qiu
* @description 针对表【order】的数据库操作Service
* @createDate 2024-06-05 16:25:06
*/
public interface OrderService extends IService<Order> {

    boolean saveOrder(Order order);

    double getTotal();
}
