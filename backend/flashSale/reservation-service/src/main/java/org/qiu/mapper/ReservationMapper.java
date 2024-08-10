package org.qiu.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.qiu.pojo.*;

import java.util.List;

/**
* @author Qiu
* @description 针对表【reservation】的数据库操作Mapper
* @createDate 2024-05-27 17:59:06
* @Entity org.qiu.pojo.Reservation
*/
public interface ReservationMapper extends MPJBaseMapper<Reservation> {

    List<Top5Query> selectTop5();

    Reservation reservationStatus(String productId);

    Reservation selectReservationByProductId(String productId);

    List<WeekReservation> selectWeekReservations();

    List<String> selectProductIdWithOnGoingReservation();

    Product selectProduct(String id);

    List<OngoingReservation> getOngoingReservations();
}




