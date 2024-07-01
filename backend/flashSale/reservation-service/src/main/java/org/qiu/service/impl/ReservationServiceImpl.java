package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.mapper.ActivityMapper;
import org.qiu.pojo.Activity;
import org.qiu.pojo.Product;
import org.qiu.pojo.Reservation;
import org.qiu.pojo.ReservationQuery;
import org.qiu.service.ReservationService;
import org.qiu.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 新增预约活动信息【手动生成 id】
     */
    @Override
    public int saveReservation(Reservation reservation) {
        // 生成预约活动 ID
        reservation.setReservationId(idClient.generateId().toString());

        // 根据闪购活动名称查询是否有对应预约活动，确保不会重复添加同一个预约活动
        Reservation r = reservationMapper.selectOne(
                new MPJLambdaWrapper<Reservation>()
                        .eq(Reservation::getReservationName, reservation.getReservationName())
        );

        // 该闪购活动的预约活动已存在
        if (r != null){
            return -3;
        }

        // 根据活动名称查询对应的闪购活动信息
        Activity activity = activityMapper.selectOne(
            new MPJLambdaWrapper<Activity>()
                .eq(Activity::getActivityName, reservation.getActivityName())
        );

        if (activity != null) {
            // 判断闪购活动是否未开始
            if (activity.getActivityStatus() != 0) {
                return -1;
            }

            // 设置预约活动中的活动 ID
            reservation.setActivityId(activity.getActivityId());

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
     * @param current
     * @return
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

    @Override
    public Boolean allowReservation(String productId) {
        Reservation reservation = reservationMapper.reservation(productId);
        LocalDateTime now = LocalDateTime.now();

        // 预约已结束
        return reservation == null ||
                (
                    now.isAfter(reservation.getStartTime()) &&
                    now.isBefore(reservation.getEndTime())
                );
    }

    @Override
    public Reservation selectByProductId(String productId) {
        return reservationMapper.selectReservationByProductId(productId);
    }
}
