package org.qiu.mapper;

import org.qiu.pojo.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
* @author Qiu
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-06-05 16:25:06
* @Entity org.qiu.pojo.Order
*/
public interface OrderMapper extends BaseMapper<Order> {

    double getTotal();

    String getUserName(String userId);

    String getActivityName(String activityId);

    String getProductName(String productId);

    BigDecimal getAmount(String productId);

    String getActivityId(String productId);
}




