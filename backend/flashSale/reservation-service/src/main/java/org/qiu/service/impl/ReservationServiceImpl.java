package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.mapper.ActivityMapper;
import org.qiu.pojo.*;
import org.qiu.service.ReservationService;
import org.qiu.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Qiu
* @description 针对表【reservation】的数据库操作Service实现
* @createDate 2024-05-27 17:59:06
*/
@Service
public class ReservationServiceImpl extends MPJBaseServiceImpl<ReservationMapper, Reservation>
    implements ReservationService{

    @Resource
    private IdClient idClient;

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增预约活动信息【手动生成 id】
     */
    @Override
    public int saveReservation(Reservation reservation) {
        // 预约活动信息校验：确保开始时间早于结束时间
        if (reservation.getStartTime().isAfter(reservation.getEndTime())) {
            return -4;
        }

        // 生成预约活动 ID
        reservation.setReservationId(idClient.generateId().toString());

        // 根据闪购活动 ID 查询是否有对应预约活动，确保同一个闪购活动不会重复添加预约活动
        Reservation r = reservationMapper.selectOne(
                new MPJLambdaWrapper<Reservation>().eq(Reservation::getActivityId, reservation.getActivityId())
        );

        // 该闪购活动的预约活动已存在
        if (r != null){
            return -3;
        }

        // 根据闪购活动 ID 查询对应的闪购活动信息
        Activity activity = activityMapper.selectOne(
            new MPJLambdaWrapper<Activity>().eq(Activity::getActivityId, reservation.getActivityId())
        );

        if (activity != null) {
            // 判断闪购活动是否未开始
            if (activity.getActivityStatus() != 0) {
                return -1;
            }

            // 判断预约的结束时间是否在闪购活动的开始时间之前
            if (reservation.getEndTime().isAfter(activity.getStartTime())) {
                return -2;
            }
        }

        return reservationMapper.insert(reservation);
    }

    /**
     * 查询预约量前五的预约活动
     */
    @Override
    public List<Reservation> selectTop5() {
        return reservationMapper.selectTop5();
    }

    /**
     * 分页查询预约信息【使用 activityId 查询到 activityName】
     */
    @Override
    public IPage<ReservationQuery> selectByPage(Integer current) {
        return reservationMapper.selectJoinPage(
                new Page<>(current, Constants.DEFAULT_PAGE_SIZE),
                ReservationQuery.class,
                new MPJLambdaWrapper<Reservation>()
                        .selectAll(Reservation.class)
                        .select(Activity::getActivityName)
                        .leftJoin(Activity.class, Activity::getActivityId, Reservation::getActivityId)
        );
    }

    /**
     * 根据商品 ID 查询对应的预约活动是否允许预约
     */
    @Override
    public Boolean allowReservation(String productId) {
        Reservation reservation = null;
        Reservation cacheData = (Reservation) redisTemplate.opsForValue().get(Constants.RESERVATION_LIST_KEY + productId);

        if (cacheData == null) {
            reservation = reservationMapper.reservationStatus(productId);
        } else {
            reservation = cacheData;
        }

        LocalDateTime now = LocalDateTime.now();

        // 判断当前时间是否在预约活动时间范围内
        return reservation != null && (now.isAfter(reservation.getStartTime()) && now.isBefore(reservation.getEndTime()));
    }

    /**
     * 根据商品 ID 查询预约活动信息
     */
    @Override
    public Reservation selectByProductId(String productId) {
        return reservationMapper.selectReservationByProductId(productId);
    }

    /**
     * 查询最近一周每天的预约量
     */
    @Override
    public List<WeekReservation> selectWeekReservations() {
        return reservationMapper.selectWeekReservations();
    }

    /**
     * 查询正在预约的商品
     */
    @Override
    public List<Product> selectProductWithOnGoingReservation() {
        List<String> ids = reservationMapper.selectProductIdWithOnGoingReservation();

        if (!CollectionUtils.isEmpty(ids)) {
            return ids.stream()
                    .filter(id -> reservationMapper.selectProduct(id) != null)
                    .map(id -> reservationMapper.selectProduct(id))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }


}
