package org.qiu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.Order;
import org.qiu.pojo.OrderQuery;
import org.qiu.result.R;
import org.qiu.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 订单相关接口
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/5 16:29
 * @Version 1.0
 * @Since 1.0
 **/
@RequestMapping("/order")
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 查询订单成交总金额
     */
    @GetMapping("/amountTotal")
    public R selectTotal() {
        double total = orderService.getTotal();
        return R.OK(total);
    }

    /**
     * 分页查询订单信息
     * @param current   页码
     * @return          订单信息列表
     */
    @GetMapping("/page")
    public R selectPage(@RequestParam("current") Integer current) {
        Page<OrderQuery> page = orderService.getByPage(current, Constants.DEFAULT_PAGE_SIZE);
        return page != null ? R.OK(page) : R.FAIL("分页查询订单失败");
    }

    /**
     * 查询订单信息详情
     * @param orderId   订单ID
     * @return          订单信息详情
     */
    @GetMapping("/one")
    public R selectOne(@RequestParam("orderId") Long orderId) {
        // 从缓存中获取订单信息
        Order order = (Order) redisTemplate.opsForValue().get(Constants.ORDER_KEY + orderId);

        if (order == null) {
            order = orderService.lambdaQuery().eq(Order::getOrderId, orderId).one();
            if (order != null) {
                 redisTemplate.opsForValue().set(Constants.ORDER_KEY + orderId, order);
            }
        }

        return order != null ? R.OK(order) : R.FAIL("查询订单详情失败");
    }

    /**
     * 查询订单总数
     */
    @GetMapping("/count")
    public R selectCount() {
        long count = orderService.count();
        return count != 0 ? R.OK(count) : R.FAIL("查询订单数量失败");
    }

    /**
     * 新增订单
     * @param order 订单信息
     * @return      新增结果
     */
    @PostMapping("/save")
    public R save(@RequestBody Order order) {
        boolean result = orderService.saveOrder(order);
        return result ? R.OK() : R.FAIL("保存订单失败");
    }

    /**
     * 更新订单
     * @param order 订单信息
     * @return      更新结果
     */
    @PutMapping("/update")
    public R update(@RequestBody Order order) {
        boolean result = orderService.updateById(order);

        redisTemplate.opsForValue().set(Constants.ORDER_KEY + order.getOrderId(), order);

        return result ? R.OK() : R.FAIL("更新订单失败");
    }

    /**
     * 根据 id 删除订单
     * @param orderId   订单ID
     * @return          删除结果
     */
    @DeleteMapping("/delete")
    public R delete(@RequestParam("orderId") Long orderId) {
        boolean result = orderService.removeById(orderId);

        redisTemplate.delete(Constants.ORDER_KEY + orderId);

        return result ? R.OK() : R.FAIL("删除订单失败");
    }



    /**/
    /**/
    /**/
    /**/
    /**/
    /**/

    // TODO：未使用
    /**
     * 查询订单信息列表
     */
    @GetMapping("/list")
    public R selectList() {
        List<Order> orderList = orderService.lambdaQuery().list();
        return orderList != null ? R.OK(orderList) : R.FAIL("查询订单列表失败");
    }

    // TODO：未使用
    /**
     * 批量删除订单
     * @param orderIds  订单ID列表
     * @return          删除结果
     */
    @GetMapping("/batchDelete")
    public R batchDelete(@RequestParam("orderIds") List<Long> orderIds) {
        boolean result = orderService.removeByIds(orderIds);
        return result ? R.OK() : R.FAIL("批量删除订单失败");
    }

}
