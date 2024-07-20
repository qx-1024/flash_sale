package org.qiu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qiu.constant.Constants;
import org.qiu.clients.IdClient;
import org.qiu.mapper.UserMapper;
import org.qiu.pojo.User;
import org.qiu.service.UserService;
import org.qiu.utils.JWTUtil;
import org.qiu.utils.SHA256Util;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author Qiu
* @description 针对表【user】的数据库表的 Service 实现
* @createDate 2024-05-19 09:09:27
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private IdClient idClient;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户登录
     * @param account       账号
     * @param password      密码
     * @param code          验证码
     * @return              token
     */
    @Override
    public String login(String account, String password, String code) {
        // 查询 Redis，验证 code 是否存在？是否未过期？
        String redis_code = (String) redisTemplate.opsForValue()
                .get(Constants.CAPTCHA_CODE_KEY + code);
        boolean code_exist = redis_code != null;

        // 根据用户名查询用户
        User user = userMapper.selectByAccount(account);
        // 对用户传入的密码进行加密
        password = SHA256Util.encrypt(password);
        boolean password_correct = user != null && user.getPassword().equals(password);

        if (code_exist && password_correct) {
            String userId = user.getUserId();
            String token = JWTUtil.createToken(userId);

            // 删除 Redis 中的验证码
            redisTemplate.delete(Constants.CAPTCHA_CODE_KEY + code);

            // 保存用户 token 到 Redis
            redisTemplate.opsForValue().set(Constants.TOKEN_KEY + userId, token,
                    Constants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

            // 返回 token
            return token;
        }
        return null;
    }

    /**
     * 根据分页参数查询用户列表，并脱敏手机号
     * @param current       页码
     * @param pageSize      每页数量
     * @return              脱敏后的用户列表
     */
    @Override
    public Page<User> selectByPage(Integer current, Integer pageSize) {
        Page<User> page = userMapper.selectPage(new Page<>(current, pageSize), null);
        List<User> userList = page.getRecords();
        for (User user : userList) {
            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber.length() == 11) { // 确保手机号长度为 11 位
                String desensitizedPhoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
                user.setPhoneNumber(desensitizedPhoneNumber);
            }

            user.setPassword("********"); // 脱敏密码
        }
        return page;
    }

    /**
     * 根据 id 查询用户，并脱敏手机号
     * @param id    用户 id
     * @return      脱敏后的用户对象
     */
    @Override
    public User selectOne(String id) {
        User user = userMapper.selectById(id);
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber.length() == 11) { // 确保手机号长度为 11 位
            String desensitizedPhoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
            user.setPhoneNumber(desensitizedPhoneNumber);

            user.setPassword("********************"); // 脱敏密码
        }
        return user;
    }

    /**
     * 新增用户【注册】
     * @param user  用户对象
     * @return      新增结果
     */
    @Override
    public int saveUser(User user) {
        // 生成 id
        user.setUserId(idClient.generateId().toString());
        // 密码加密（使用 sha256 加密算法）
        user.setPassword(SHA256Util.encrypt(user.getPassword()));
        return userMapper.insert(user);
    }

    /**
     * 更新用户密码
     * @param account               账号
     * @param password              新密码
     * @param confirmPassword       确认密码
     * @return                      更新结果
     */
    @Override
    public int updateUserPassWord(String account, String password, String confirmPassword) {
        // 双保险：确保用户输入的密码无误
        if (!password.equals(confirmPassword)){
            return 0;
        }
        // 根据账号查询用户
        User user = userMapper.selectByAccount(account);
        if (user != null) {
            // 对新密码进行加密并保存到用户对象中
            user.setPassword(SHA256Util.encrypt(password));
        }
        // 更新数据库中对应的用户的密码
        return user != null ? userMapper.updateById(user) : 0;
    }

}