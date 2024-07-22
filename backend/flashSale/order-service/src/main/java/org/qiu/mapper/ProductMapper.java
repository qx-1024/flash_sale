package org.qiu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.qiu.pojo.Product;

import java.math.BigDecimal;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Mapper
* @createDate 2024-05-27 17:58:56
* @Entity org.qiu.pojo.Product
*/
public interface ProductMapper extends BaseMapper<Product> {

    BigDecimal getPrice(String productId);

    String getActivityId(String productId);
}




