package org.qiu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.constant.Constants;
import org.qiu.pojo.Activity;
import org.qiu.pojo.ActivityQuery;
import org.qiu.pojo.Product;
import org.qiu.service.ActivityService;
import org.qiu.mapper.ActivityMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
* @author Qiu
* @description 针对表【activity】的数据库操作Service实现
* @createDate 2024-05-27 17:58:44
*/
@Service
public class ActivityServiceImpl extends MPJBaseServiceImpl<ActivityMapper, Activity>
    implements ActivityService{

    @Resource
    private IdClient idClient;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增闪购活动
     * @param activity      闪购活动对象
     * @return              受影响的行数
     */
    @Override
    public int saveActivity(Activity activity) {
        // 设置闪购活动 id
        String activityId = idClient.generateId().toString();
        activity.setActivityId(activityId);

        redisTemplate.opsForValue().set(Constants.ACTIVITY_KEY + activityId, activity);

        return activityMapper.insert(activity);
    }

    /**
     * 分页查询闪购活动【使用 productId 查询到 productName】
     * @param current    当前页码
     * @return           分页查询结果
     */
    @Override
    public IPage<ActivityQuery> selectByPage(Integer current) {
        return activityMapper.selectJoinPage(
                new Page<>(current, Constants.DEFAULT_PAGE_SIZE),
                ActivityQuery.class,
                new MPJLambdaWrapper<Activity>()
                        .selectAll(Activity.class)
                        .select(Product::getProductName)
                        .leftJoin(Product.class, Product::getProductId, Activity::getProductId)
        );
    }
}




