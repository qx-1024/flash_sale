package org.qiu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import org.qiu.pojo.Reservation;
import org.qiu.pojo.ReservationQuery;

import java.util.List;

/**
* @author Qiu
* @description 针对表【reservation】的数据库操作Service
* @createDate 2024-05-27 17:59:06
*/
public interface ReservationService extends MPJBaseService<Reservation> {

    int saveReservation(Reservation reservation);

    List<Reservation> selectTop5();

    IPage<ReservationQuery> selectByPage(Integer current);

    Boolean allowReservation(String productId);

    Reservation selectByProductId(String productId);
}
