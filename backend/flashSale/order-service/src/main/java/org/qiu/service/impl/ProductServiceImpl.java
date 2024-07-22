package org.qiu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.mapper.ProductMapper;
import org.qiu.pojo.Product;
import org.qiu.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-05-27 17:58:56
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Resource
    private ProductMapper productMapper;

    @Override
    public BigDecimal getPrice(String productId) {
        return productMapper.getPrice(productId);
    }

    @Override
    public String getActivityId(String productId) {
        return productMapper.getActivityId(productId);
    }
}




