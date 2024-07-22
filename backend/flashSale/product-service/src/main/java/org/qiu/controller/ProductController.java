package org.qiu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.BuyInfo;
import org.qiu.pojo.Product;
import org.qiu.result.R;
import org.qiu.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 商品相关接口
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 18:09
 * @Version 1.0
 * @Since 1.0
 **/
@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询参与闪购的商品列表【热点数据--闪购专区】
     */
    @GetMapping("/flashSaleProductList")
    public R selectFlashSaleProductList(){
        List<Product> list = productService.getFlashSaleProductList();
        return list != null ? R.OK(list) : R.FAIL("查询闪购商品列表失败");
    }

    /**
     * 查询销量前五的商品
     * @return      商品列表
     */
    @GetMapping("/top5")
    public R selectTop5(){
        List<Product> list = productService.lambdaQuery()
                .orderByDesc(Product::getSales)
                .last("limit 5")
                .list();
        return list != null ? R.OK(list) : R.FAIL("查询商品列表失败");
    }

    /**
     * 查询商品数量
     */
    @GetMapping("/count")
    public R getProductCount(){
        long count = productService.count();
        return count != 0 ? R.OK(count) : R.FAIL("查询商品数量失败");
    }

    /**
     * 分页查询商品列表
     * @param current       当前页码
     * @return              商品分页列表
     */
    @GetMapping("/page")
    public R selectByPage(@RequestParam(value = "current", required = false, defaultValue = "1") Integer current){
        if(current == null) current = 1;
        Page<Product> page = productService.page(new Page<>(current, Constants.DEFAULT_PAGE_SIZE));
        return page != null ? R.OK(page) : R.FAIL("查询商品列表失败");
    }

    /**
     * 根据商品 ID 查询商品【热点数据--前台用户查询商品详情】
     * @param productId     商品ID
     * @return              商品
     */
    @GetMapping("/one")
    public R selectOne(@RequestParam("productId") String productId){
        Product product = (Product) redisTemplate.opsForValue().get(Constants.FLASH_SALE_PRODUCT_KEY + productId);

        if ( product == null) {
            product = productService.getById(productId);
        }

        return product != null ? R.OK(product) : R.FAIL("查询商品失败");
    }


    /**
     * 用户闪购【采用异步消费的方式生成订单信息】
     * @param buyInfo   闪购信息：用户ID、商品ID
     * @return          闪购结果
     */
    @PostMapping("/buy")
    public R buy(@RequestBody BuyInfo buyInfo){
        boolean bought = productService.buy(buyInfo);
        return bought ? R.OK("购买成功") : R.FAIL("购买失败");
    }

    /**
     * 新增商品
     * @param product   商品
     * @return          新增结果
     */
    @PostMapping("/save")
    public R save(@RequestBody Product product){
        int saved = productService.saveProduct(product);

        // 新增到缓存
        redisTemplate.opsForValue().set(
                Constants.FLASH_SALE_PRODUCT_KEY + product.getProductId(),
                product
        );

        return saved == 1 ? R.OK("新增商品成功") : R.FAIL("新增商品失败");
    }

    /**
     * 更新商品
     * @param product   商品
     * @return          更新结果
     */
    @PutMapping("/update")
    public R update(@RequestBody Product product){
        boolean updated = productService.updateById(product);

        // 更新缓存
        redisTemplate.opsForValue().set(
                Constants.FLASH_SALE_PRODUCT_KEY + product.getProductId(),
                product
        );

        return updated ? R.OK("更新商品成功") : R.FAIL("更新商品失败");
    }

    /**
     * 根据 id 删除指定商品
     * @param productId     商品ID
     * @return              删除结果
     */
    @DeleteMapping("/delete")
    public R delete(@RequestParam("productId") String productId){
        boolean deleted = productService.removeById(productId);

        Boolean deleteCache = redisTemplate.delete(Constants.FLASH_SALE_PRODUCT_KEY + productId);

        return deleted && Boolean.TRUE.equals(deleteCache) ? R.OK("删除商品成功") : R.FAIL("删除商品失败");
    }




    /**/
    /**/
    /**/
    /**/
    /**/
    /**/

    // TODO 未使用
    /**
     * 查询所有商品
     */
    @GetMapping("/list")
    public R selectList(){
        List<Product> productList = productService.list();
        return productList != null ? R.OK(productList) : R.FAIL("查询商品列表失败");
    }

    // TODO 未使用
    /**
     * 批量删除商品
     * @param productIds    商品ID列表
     * @return              删除结果
     */
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestParam("productIds") List<String> productIds){
        boolean deleted = productService.removeByIds(productIds);
        return deleted ? R.OK("批量删除商品成功") : R.FAIL("批量删除商品失败");
    }

}
