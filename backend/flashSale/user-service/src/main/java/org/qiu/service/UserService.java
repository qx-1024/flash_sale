package org.qiu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiu.pojo.User;

/**
* @author Qiu
* @description 针对表【user】的数据库操作Service
* @createDate 2024-05-19 09:09:27
*/
public interface UserService extends IService<User> {

    String login(String username, String password, String code);

    Page<User> selectByPage(Integer current, Integer pageSize);

    User selectOne(String id);

    int saveUser(User user);

    int updateUserPassWord(String username, String password, String confirmPassword);

    void getCode(HttpServletRequest request, HttpServletResponse response, DefaultKaptcha captchaProducer) throws Exception;

    boolean updateUser(User user);
}
