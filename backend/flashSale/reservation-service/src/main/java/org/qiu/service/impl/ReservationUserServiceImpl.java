package org.qiu.service.impl;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.mapper.ReservationUserMapper;
import org.qiu.pojo.ReservationUser;
import org.qiu.service.ReservationUserService;
import org.springframework.stereotype.Service;

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
    private ReservationUserMapper reservationUserMapper;


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
        ReservationUser reservation = reservationUserMapper.selectOne(
                new MPJLambdaWrapper<ReservationUser>()
                        .eq(ReservationUser::getUserId, reservationUser.getUserId())
                        .eq(ReservationUser::getReservationId, reservationUser.getReservationId())
        );

        if (reservation == null){
            reservationUser.setId(idClient.generateId().toString());

            // 插入预约记录
            return reservationUserMapper.insert(reservationUser) > 0;
        }

        return false;
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

}
