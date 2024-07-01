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

    @Override
    public Boolean reserve(ReservationUser reservationUser) {
        reservationUser.setId(idClient.generateId().toString());

        // 查询是否已经预约【确保一个用户一次预约】
        ReservationUser reservation = reservationUserMapper.selectOne(
                new MPJLambdaWrapper<ReservationUser>()
                        .eq(ReservationUser::getUserId, reservationUser.getUserId())
                        .eq(ReservationUser::getReservationId, reservationUser.getReservationId())
        );

        if (reservation == null){
            return reservationUserMapper.insert(reservationUser) > 0;
        }

        return false;
    }
}
