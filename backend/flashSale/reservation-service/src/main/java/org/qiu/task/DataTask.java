package org.qiu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.mapper.ReservationMapper;
import org.qiu.pojo.Product;
import org.qiu.pojo.Reservation;
import org.qiu.service.ProductService;
import org.qiu.service.ReservationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 预约相关定时任务
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/13 9:07
 * @Version 1.0
 * @Since 1.0
 **/
@EnableScheduling       // 开启定时任务
@Component
public class DataTask {

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private ReservationService reservationService;

    @Resource
    private ProductService productService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 时长常量
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;


    /**
     * 加载预约活动是否允许预约的信息
     * 在项目启动时初始化缓存，后续在每分钟更新一次缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = ONE_MINUTE_IN_MILLIS)
    public void initializeAndUpdateCache() {
        List<String> ids = productService.listObjs(
                new QueryWrapper<Product>().lambda().select(Product::getProductId)
        );

        ids.forEach(id -> {
            // use to allowReservation function in ReservationServiceImpl
            redisTemplate.opsForValue()
                    .set(
                            Constants.RESERVATION_KEY + id,
                            reservationMapper.reservationStatus(id)
                    );
        });
    }

    /**
     * 更新预约状态
     * 在项目启动时执行一次，后续在每一个整点执行
     */
    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void updateReservationStatus(){
        List<Reservation> reservations = reservationService.lambdaQuery().list();

        reservations.forEach(reservation -> {
            LocalDateTime startTime = reservation.getStartTime();
            LocalDateTime endTime = reservation.getEndTime();
            LocalDateTime now = LocalDateTime.now();

            // 预约未开始
            if (now.isBefore(startTime)) {
                reservation.setReservationStatus(Constants.RESERVATION_STATUS_NOT_STARTED);
            }

            // 预约已结束
            if (now.isAfter(endTime)){
                reservation.setReservationStatus(Constants.RESERVATION_STATUS_FINISHED);
            }

            // 预约进行中
            if (now.isAfter(startTime) && now.isBefore(endTime)){
                reservation.setReservationStatus(Constants.RESERVATION_STATUS_IN_PROGRESS);
            }

            reservationService.updateById(reservation);
        });
    }

    /**
     * 更新缓存中预约专区的商品列表信息
     */
    @Scheduled(initialDelay = 0, fixedRate = ONE_MINUTE_IN_MILLIS)
    public void updateCache(){
        List<String> ids = reservationMapper.selectProductIdWithOnGoingReservation();

        List<Product> products = null;
        if (!CollectionUtils.isEmpty(ids)) {
            products = ids.stream()
                    .filter(id -> reservationMapper.selectProduct(id) != null)
                    .map(id -> reservationMapper.selectProduct(id))
                    .toList();

            products.forEach(product -> {
                redisTemplate.opsForValue().set(
                        Constants.FLASH_RESERVE_PRODUCT_KEY + product.getProductId(), product
                );
            });
        }
    }

}
