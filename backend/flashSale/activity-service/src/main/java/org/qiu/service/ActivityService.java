package org.qiu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseService;
import org.qiu.pojo.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.qiu.pojo.ActivityQuery;

/**
* @author Qiu
* @description 针对表【activity】的数据库操作Service
* @createDate 2024-05-27 17:58:44
*/
public interface ActivityService extends MPJBaseService<Activity> {

    int saveActivity(Activity activity);

    IPage<ActivityQuery> selectByPage(Integer current);
}
