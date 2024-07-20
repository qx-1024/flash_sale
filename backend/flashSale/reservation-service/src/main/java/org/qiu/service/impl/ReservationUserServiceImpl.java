package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.mapper.ReservationUserMapper;
import org.qiu.pojo.*;
import org.qiu.service.ReservationService;
import org.qiu.service.ReservationUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/19 15:08
 * @Version 1.0
 * @Since 1.0
 **/
@Service
public class ReservationUserServiceImpl extends MPJBaseServiceImpl<ReservationUserMapper, ReservationUser>
        implements ReservationUserService {

    @Resource
    private IdClient idClient;

    @Resource
    private ReservationService reservationService;

    @Resource
    private ReservationUserMapper reservationUserMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 预约
     * @param reservationUser   预约信息【预约活动id、用户id】
     * @return                  预约结果
     */
    @Override
    public Boolean reserve(ReservationUser reservationUser) {
        if (reservationUser == null){
            return false;
        }

        // 查询是否已经预约
        String reservationId = reservationUser.getReservationId();
        ReservationUser ru = reservationUserMapper.selectOne(
                new MPJLambdaWrapper<ReservationUser>()
                        .eq(ReservationUser::getUserId, reservationId)
                        .eq(ReservationUser::getReservationId, reservationUser.getReservationId())
        );

        if (ru != null) {
            return false;
        }

        // 判断该预约活动是否在进行中
        Reservation reservation = (Reservation) redisTemplate.opsForValue()
                .get(Constants.RESERVATION_KEY + reservationId);

        if (reservation == null){
            reservation = reservationService.lambdaQuery()
                    .eq(Reservation::getReservationId, reservationId)
                    .one();
            if (reservation != null){
                // 将查询到的 reservation 放入缓存
                redisTemplate.opsForValue()
                        .set(Constants.RESERVATION_KEY + reservationId, reservation);
            }
        }

        if (reservation != null &&
            Constants.RESERVATION_STATUS_IN_PROGRESS.equals(reservation.getReservationStatus())
        ){
            return false;
        }

        reservationUser.setId(idClient.generateId().toString());

        // 插入预约记录
        return reservationUserMapper.insert(reservationUser) > 0;
    }

    /**
     * 取消预约
     * @param reservationUser   预约信息
     * @return                  取消预约结果
     */
    @Override
    public Boolean cancelReserve(ReservationUser reservationUser) {
        if (reservationUser == null){
            return false;
        }

        // 查询是否已经预约
        ReservationUser reservation = reservationUserMapper.selectOne(
                new MPJLambdaWrapper<ReservationUser>()
                        .eq(ReservationUser::getUserId, reservationUser.getUserId())
                        .eq(ReservationUser::getReservationId, reservationUser.getReservationId())
        );

        if (reservation != null) {
            // 删除预约记录
            return reservationUserMapper.deleteById(reservation.getId()) > 0;
        }

        return false;
    }

    /**
     * 修改预约信息
     * @param reservationUser   预约信息
     * @return                  修改结果
     */
    @Override
    public Boolean modifyReserve(ReservationUser reservationUser) {
        if (reservationUser == null){
            return false;
        }

        // 查询是否已经预约
        ReservationUser reservation = reservationUserMapper.selectOne(
                new MPJLambdaWrapper<ReservationUser>()
                        .eq(ReservationUser::getId, reservationUser.getId())
        );

        if (reservation != null) {
            // 修改预约记录
            return reservationUserMapper.updateById(reservationUser) > 0;
        }

        return false;
    }

    /**
     * 分页查询预约信息列表
     * @param current       当前页码
     * @return              预约信息列表
     */
    @Override
    public IPage<ReservationUserQuery> selectByPage(Integer current) {
        return reservationUserMapper.selectJoinPage(
                new Page<>(current, Constants.DEFAULT_PAGE_SIZE),
                ReservationUserQuery.class,
                new MPJLambdaWrapper<ReservationUser>()
                        .selectAll(ReservationUser.class)
                        .select(Reservation::getReservationName)
                        .leftJoin(Reservation.class, Reservation::getReservationId, ReservationUser::getReservationId)
                        .select(User::getRealName)
                        .leftJoin(User.class, User::getUserId, ReservationUser::getUserId)
        );
    }

}
