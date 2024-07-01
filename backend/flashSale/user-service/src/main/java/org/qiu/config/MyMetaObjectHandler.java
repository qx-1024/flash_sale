package org.qiu.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/28 10:09
 * @Version 1.0
 * @Since 1.0
 **/
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        setFieldValByName("isDeleted", 0, metaObject);
        setFieldValByName("createTime", ZonedDateTime.now().toLocalDateTime(), metaObject);
        setFieldValByName("updateTime", ZonedDateTime.now().toLocalDateTime(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updateTime", ZonedDateTime.now().toLocalDateTime(), metaObject);
    }
}
