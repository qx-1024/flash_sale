package org.qiu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import org.qiu.pojo.ReservationQuery;
import org.qiu.pojo.ReservationUser;
import org.qiu.pojo.ReservationUserQuery;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/6/19 15:07
 * @Version 1.0
 * @Since 1.0
 **/
public interface ReservationUserService extends MPJBaseService<ReservationUser> {

    Boolean reserve(ReservationUser reservationUser);

    Boolean cancelReserve(ReservationUser reservationUser);

    Boolean modifyReserve(ReservationUser reservationUser);

    IPage<ReservationUserQuery> selectByPage(Integer current);
}
