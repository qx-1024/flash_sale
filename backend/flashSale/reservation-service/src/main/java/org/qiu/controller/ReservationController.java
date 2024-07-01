package org.qiu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.qiu.pojo.Reservation;
import org.qiu.pojo.ReservationQuery;
import org.qiu.pojo.ReservationUser;
import org.qiu.result.R;
import org.qiu.service.ReservationService;
import org.qiu.service.ReservationUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 预约接口
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 21:12
 * @Version 1.0
 * @Since 1.0
 **/
@CrossOrigin
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @Resource
    private ReservationUserService reservationUserService;

    /**
     * 查询预约活动列表
     * @return  预约活动列表
     */
    @GetMapping("/list")
    public R selectAll(){
        List<Reservation> reservations = reservationService.list();
        return reservations != null ? R.OK(reservations) : R.FAIL("查询预约列表失败");
    }

    /**
     * 根据商品 id 查询预约活动信息
     * @param productId 商品 id
     * @return          预约活动信息
     */
    @GetMapping("/allowReservation")
    public R allowReservation(@RequestParam("productId") String productId){
        Boolean allowed = reservationService.allowReservation(productId);
        return allowed ? R.OK("查询预约活动信息成功") : R.FAIL("查询预约活动信息失败");
    }

    /**
     * 用户预约闪购活动
     */
    @PostMapping("/reserve")
    public R reserve(@RequestBody ReservationUser reservationUser){
        Boolean reserved = reservationUserService.reserve(reservationUser);
        return reserved ? R.OK("预约成功") : R.FAIL("预约失败");
    }

    /**
     * TODO 根据商品 id 查询对应的预约活动信息
     * @param productId     商品 id
     * @return              预约活动信息
     */
    @GetMapping("/getReservationByProductId")
    public R getReservationId(@RequestParam("productId") String productId){
        Reservation reservation = reservationService.selectByProductId(productId);
        return reservation != null ? R.OK(reservation) : R.FAIL("查询预约活动信息失败");
    }

    /**
     * 查询进行中的预约
     * @return  进行中的预约
     */
    @GetMapping("/ongoingReservations")
    public R selectOngoingReservations(){
        List<Reservation> reservations = reservationService.lambdaQuery()
                .eq(Reservation::getReservationStatus, 2)
                .list();
        return reservations != null ? R.OK(reservations) : R.FAIL("查询进行中预约失败");
    }

    /**
     * 查询已结束的预约
     * @return  已结束的预约
     */
    @GetMapping("/endedReservations")
    public R selectEndedReservations(){
        List<Reservation> reservations = reservationService.lambdaQuery()
                .eq(Reservation::getReservationStatus, 1)
                .list();
        return reservations != null ? R.OK(reservations) : R.FAIL("查询已结束预约失败");
    }

    /**
     * 查询未开始的预约
     * @return  未开始的预约
     */
    @GetMapping("/unstartedReservations")
    public R selectUnstartedReservations(){
        List<Reservation> reservations = reservationService.lambdaQuery()
                .eq(Reservation::getReservationStatus, 0)
                .list();
        return reservations != null ? R.OK(reservations) : R.FAIL("查询未开始预约失败");
    }

    /**/
    /**/
    /**/
    /**/
    /**/
    /**/


    /**
     * 查询预约数量
     */
    @GetMapping("/count")
    public R getReservationCount(){
        long count = reservationService.count();
        return count != 0 ? R.OK(count) : R.FAIL("查询预约数量失败");
    }

    /**
     * 查询预约数量前五的预约活动
     */
    @GetMapping("/top5")
    public R getTop5Reservations(){
        List<Reservation> reservationList = reservationService.selectTop5();
        return reservationList != null ? R.OK(reservationList) : R.FAIL("查询预约数量失败");
    }

    /**
     * 查询预约列表
     * @param current       页码
     * @return              预约分页列表
     */
    @GetMapping("/page")
    public R selectByPage(@RequestParam(value = "current", required = false, defaultValue = "1") Integer current){
        IPage<ReservationQuery> page = reservationService.selectByPage(current);
        return page != null ? R.OK(page) : R.FAIL("查询预约列表失败");
    }

    /**
     * 根据 id 查询预约详情
     * @param reservationId     预约 id
     * @return                  预约详情
     */
    @GetMapping("/one")
    public R selectOne(@RequestParam("reservationId") String reservationId){
        Reservation reservation = reservationService.getById(reservationId);
        return reservation != null ? R.OK(reservation) : R.FAIL("查询预约详情失败");
    }

    /**
     * 新增预约活动信息
     * @param reservation   预约活动信息
     * @return              新增结果
     */
    @PostMapping("/save")
    public R saveReservation(@RequestBody Reservation reservation){
        int saved = reservationService.saveReservation(reservation);
        return switch (saved) {
            case -1 -> R.FAIL("该闪购活动已开始或已结束");
            case -2 -> R.FAIL("预约结束时间必须在闪购活动开始时间之前");
            case -3 -> R.FAIL("该闪购活动的预约活动已存在");
            default -> saved == 1 ? R.OK("新增预约信息成功") : R.FAIL("新增预约信息失败");
        };
    }

    /**
     * 根据 id 更新预约信息
     * @param reservation   预约信息
     * @return              更新结果
     */
    @PutMapping("/update")
    public R update(@RequestBody Reservation reservation){
        boolean updated = reservationService.updateById(reservation);
        return updated ? R.OK("更新预约信息成功") : R.FAIL("更新预约信息失败");
    }

    /**
     * 根据 id 删除预约信息
     * @param reservationId     预约 id
     * @return                  删除结果
     */
    @DeleteMapping("/delete")
    public R delete(@RequestParam("reservationId") String reservationId){
        boolean deleted = reservationService.removeById(reservationId);
        return deleted ? R.OK("删除预约信息成功") : R.FAIL("删除预约信息失败");
    }

    /**
     * 批量删除预约信息
     * @param reservationIds    预约 id 列表
     * @return                  删除结果
     */
    @DeleteMapping("/batchDelete")
    public R deleteBatch(@RequestParam("reservationIds") List<String> reservationIds){
        boolean deleted = reservationService.removeByIds(reservationIds);
        return deleted ? R.OK("批量删除预约信息成功") : R.FAIL("批量删除预约信息失败");
    }
}
