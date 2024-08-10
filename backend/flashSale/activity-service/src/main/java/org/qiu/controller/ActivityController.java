package org.qiu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.Activity;
import org.qiu.pojo.ActivityQuery;
import org.qiu.result.R;
import org.qiu.service.ActivityService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 闪购活动相关接口
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 21:32
 * @Version 1.0
 * @Since 1.0
 **/
@CrossOrigin
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 获取闪购活动总数
     */
    @GetMapping("/count")
    public R getActivityCount(){
        long count = activityService.count();
        return count != 0 ? R.OK(count) : R.FAIL("查询活动数量失败");
    }

    /**
     * 分页查询闪购活动列表
     * @param current   页码
     * @return          分页结果
     */
    @GetMapping("/page")
    public R selectByPage(@RequestParam(value = "current", required = false, defaultValue = "1") Integer current){
        IPage<ActivityQuery> page = activityService.selectByPage(current);
        return page != null ? R.OK(page) : R.FAIL("查询闪购活动失败");
    }

    /**
     * 根据活动ID查询闪购活动详情
     * @param activityId    闪购活动ID
     * @return              闪购活动详情
     */
    @GetMapping("/one")
    public R selectActivityById(@RequestParam("activityId") String activityId){
        Activity activity = (Activity) redisTemplate.opsForValue().get(Constants.ACTIVITY_KEY + activityId);

        if (activity == null) {
            activity = activityService.getById(activityId);
            if (activity == null) {
                // 如果 activity 为 null，则表明没有找到记录
                return R.FAIL("查询闪购活动失败");
            } else {
                // 缓存 activity 到 Redis
                redisTemplate.opsForValue().set(Constants.ACTIVITY_KEY + activityId, activity);
            }
        }

        return R.OK(activity);
    }

    /**
     * 新增闪购活动
     * @param activity      闪购活动信息
     * @return              新增结果
     */
    @PostMapping("/save")
    public R save(@RequestBody Activity activity){
        int saved = activityService.saveActivity(activity);

        if (saved == -1){
            return R.FAIL("活动时间设置有误");
        }

        return saved == 1 ? R.OK("新增闪购活动成功") : R.FAIL("新增闪购活动失败");
    }

    /**
     * 更新闪购活动信息
     * @param activity      闪购活动信息
     * @return              更新结果
     */
    @PutMapping("/update")
    public R update(@RequestBody Activity activity){
        redisTemplate.opsForValue().set(Constants.ACTIVITY_KEY + activity.getActivityId(), activity);

        boolean updated = activityService.updateById(activity);

        return updated ? R.OK("更新闪购活动成功") : R.FAIL("更新闪购活动失败");
    }

    /**
     * 根据 id 删除指定闪购活动
     * @param activityId    闪购活动ID
     * @return              删除结果
     */
    @DeleteMapping("/delete")
    public R delete(@RequestParam("activityId") String activityId){
        redisTemplate.delete(Constants.ACTIVITY_KEY + activityId);

        boolean deleted = activityService.removeById(activityId);

        return deleted ? R.OK("删除闪购活动成功") : R.FAIL("删除闪购活动失败");
    }



    /**/
    /**/
    /**/
    /**/
    /**/
    /**/

    // TODO 未使用
    /**
     * 查询活动列表
     * @return  活动列表
     */
    @GetMapping("/list")
    public R selectList(){
        List<Activity> activityList = activityService.list();
        return activityList != null ? R.OK(activityList) : R.FAIL("查询活动列表失败");
    }

    // TODO 未使用
    /**
     * 查询未开始的活动列表
     * @return  未开始的活动列表
     */
    @GetMapping("/unStartedActivities")
    public R selectUnStartedActivities(){
        List<Activity> activityList = activityService.lambdaQuery()
                .eq(Activity::getActivityStatus, 0)
                .list();
        return activityList != null ? R.OK(activityList) : R.FAIL("查询未开始的闪购活动列表失败");
    }

    // TODO 未使用
    /**
     * 查询进行中的活动列表
     * @return  进行中的活动列表
     */
    @GetMapping("/ongoingActivities")
    public R selectOngoingActivities(){
        List<Activity> activityList = activityService.lambdaQuery()
                .eq(Activity::getActivityStatus, 1)
                .list();
        return activityList != null ? R.OK(activityList) : R.FAIL("查询进行中的闪购活动列表失败");
    }

    // TODO 未使用
    /**
     * 查询已结束的活动列表
     * @return  已结束的活动列表
     */
    @GetMapping("/endedActivities")
    public R selectEndedActivities(){
        List<Activity> activityList = activityService.lambdaQuery()
                .eq(Activity::getActivityStatus, 2)
                .list();
        return activityList != null ? R.OK(activityList) : R.FAIL("查询已结束的闪购活动列表失败");
    }

    // TODO 未使用
    @GetMapping("/top5")
    public R getTop5Activity(){
        List<Activity> activities = activityService.lambdaQuery().list();
        return activities != null ? R.OK(activities) : R.FAIL("查询活动列表失败");
    }

    // TODO 未使用
    /**
     * 批量删除指定闪购活动
     * @param activityIds   闪购活动ID列表
     * @return              删除结果
     */
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestParam("activityIds") List<String> activityIds){
        boolean deleted = activityService.removeByIds(activityIds);
        return deleted ? R.OK("批量删除闪购活动成功") : R.FAIL("批量删除闪购活动失败");
    }

}
