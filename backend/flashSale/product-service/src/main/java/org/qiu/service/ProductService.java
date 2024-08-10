package org.qiu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.qiu.pojo.BuyInfo;
import org.qiu.pojo.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Qiu
* @description 针对表【product】的数据库操作Service
* @createDate 2024-05-27 17:58:56
*/
public interface ProductService extends IService<Product> {

    int saveProduct(Product product);

    List<Product> getFlashSaleProductList();

    String buy(BuyInfo buyInfo);

}
