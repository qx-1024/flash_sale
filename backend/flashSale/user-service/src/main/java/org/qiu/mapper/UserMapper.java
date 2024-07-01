package org.qiu.mapper;

import org.qiu.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Qiu
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-05-27 16:52:01
* @Entity org.qiu.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {
    User selectByAccount(String account);
}




