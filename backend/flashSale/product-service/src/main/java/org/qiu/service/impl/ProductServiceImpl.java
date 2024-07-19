package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.pojo.Product;
import org.qiu.service.ProductService;
import org.qiu.mapper.ProductMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-05-27 17:58:56
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Resource
    private IdClient idClient;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        // 初始化一个空的列表对象
        List<Product> list = null;

        // 从Redis中获取缓存的闪购商品列表
        List<Product> products = (List<Product>) redisTemplate.opsForValue().get(Constants.FLASH_SALE_PRODUCT_KEY);

        // 如果Redis中有缓存数据，则直接返回缓存数据
        if(products != null){
            list = products;
        } else {
            // 如果Redis中没有缓存数据，则从数据库中查询闪购商品列表
            list = productMapper.selectList(new QueryWrapper<Product>().eq("isFlashSale", 1));

            // 将从数据库中查询得到的闪购商品列表存入Redis缓存
            redisTemplate.opsForValue().set(Constants.FLASH_SALE_PRODUCT_KEY, list);
        }

        // 返回最终的闪购商品列表，可能是从缓存获取的或者是从数据库查询得到并存入缓存的
        return list;
    }
}




