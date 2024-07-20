package org.qiu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.qiu.pojo.ReservationUser;
import org.qiu.pojo.ReservationUserQuery;
import org.qiu.result.R;
import org.qiu.service.ReservationUserService;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/8 9:05
 * @Version 1.0
 * @Since 1.0
 **/
@CrossOrigin
@RestController
@RequestMapping("/reservation_user")
public class ReservationUserController {

    @Resource
    private ReservationUserService reservationUserService;


    /**
     * 分页查询用户预约信息
     */
    @GetMapping("/page")
    public R selectByPage(@RequestParam(value = "current", required = false, defaultValue = "1") Integer current){
        IPage<ReservationUserQuery> page = reservationUserService.selectByPage(current);
        return page != null ? R.OK(page) : R.FAIL("查询预约列表失败");
    }

    /**
     * 查询预约信息
     */
    @GetMapping("/reserveInfo")
    public R getReserveInfo(@RequestParam("reservationId") String reservationId,
                            @RequestParam("userId") String userId){
        ReservationUser reservationInfo = reservationUserService.lambdaQuery()
                .eq(ReservationUser::getReservationId, reservationId)
                .eq(ReservationUser::getUserId, userId)
                .one();

        return reservationInfo != null ? R.OK(reservationInfo) : R.FAIL("查询预约信息失败");
    }

    /**
     * 预约
     */
    @PostMapping("/reserve")
    public R reserve(@RequestBody ReservationUser reservationUser){
        Boolean reserved = reservationUserService.reserve(reservationUser);
        return reserved ? R.OK("预约成功") : R.FAIL("预约失败");
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancelReserve")
    public R cancelReserve(@RequestBody ReservationUser reservationUser){
        Boolean canceled = reservationUserService.cancelReserve(reservationUser);
        return canceled ? R.OK("取消预约成功") : R.FAIL("取消预约失败");
    }


    /**
     * 修改预约
     */
    @PutMapping("/modifyReserve")
    public R modifyReserve(@RequestBody ReservationUser reservationUser){
        Boolean modified = reservationUserService.modifyReserve(reservationUser);
        return modified ? R.OK("修改预约成功") : R.FAIL("修改预约失败");
    }

}
