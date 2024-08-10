package org.qiu.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.pojo.Activity;
import org.qiu.service.ActivityService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 闪购活动相关定时任务
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/7/20 14:37
 * @Version 1.0
 * @Since 1.0
 **/
@Component
@EnableScheduling
public class DataTask {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ActivityService activityService;

    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;


    /**
     * 加载闪购活动信息到缓存
     */
    @Scheduled(initialDelay = 0, fixedRate = ONE_MINUTE_IN_MILLIS)
    public void initializeAndUpdateCache() {
        List<Activity> activities = activityService.lambdaQuery().list();

        activities.forEach(activity -> {
            redisTemplate.opsForValue().set(Constants.ACTIVITY_KEY + activity.getActivityId(), activity);
        });
    }

    /**
     * 更新活动状态
     */
    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void updateReservationStatus(){
        List<Activity> activities = activityService.lambdaQuery().list();

        activities.forEach(activity -> {
            LocalDateTime startTime = activity.getStartTime();
            LocalDateTime endTime = activity.getEndTime();
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(startTime)){
                activity.setActivityStatus(Constants.ACTIVITY_STATUS_NOT_STARTED);
            }

            if (now.isAfter(endTime)){
                activity.setActivityStatus(Constants.ACTIVITY_STATUS_FINISHED);
            }

            if (now.isAfter(startTime) && now.isBefore(endTime)){
                activity.setActivityStatus(Constants.ACTIVITY_STATUS_IN_PROGRESS);
            }

            activityService.updateById(activity);
        });
    }

}
