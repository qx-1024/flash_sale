package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.mapper.ProductMapper;
import org.qiu.pojo.Product;
import org.qiu.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-05-27 17:58:56
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{
}




