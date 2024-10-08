package org.qiu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiu.constant.Constants;
import org.qiu.clients.IdClient;
import org.qiu.mapper.UserMapper;
import org.qiu.pojo.User;
import org.qiu.pojo.UserOption;
import org.qiu.result.CodeEnum;
import org.qiu.result.R;
import org.qiu.service.UserService;
import org.qiu.utils.JSONUtil;
import org.qiu.utils.JWTUtil;
import org.qiu.utils.SHA256Util;
import org.qiu.utils.VerificationUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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

        if( redis_code == null ) {
            return null;
        }

        // 根据用户名查询用户
        User user = userMapper.selectByAccount(account);

        // 对用户传入的密码进行加密
        password = SHA256Util.encrypt(password);
        boolean password_correct = user != null && user.getPassword().equals(password);

        if (password_correct) {
            String userId = user.getUserId();
            String token = JWTUtil.createToken(userId);

            CompletableFuture.runAsync(() -> {
                ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

                // 删除 Redis 中的验证码
                redisTemplate.delete(Constants.CAPTCHA_CODE_KEY + code);

                // 保存用户 token 到 Redis
                valueOps.set(Constants.TOKEN_KEY + userId, token,
                        Constants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

                // 保存用户 ID 到 Redis，表示用户处于登录状态
                valueOps.set(Constants.CURRENT_LOGIN_USER + userId, user,
                        Constants.CURRENT_LOGIN_USER_EXPIRE_TIME, TimeUnit.MINUTES);
            });

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

    /**
     * 更新用户信息
     * @param user   用户对象
     * @return       更新结果
     */
    @Override
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }

        // 从数据库中查询用户信息，包括手机号、密码的原始值
        User originalUser = userMapper.selectById(user.getUserId());

        // 检查并还原脱敏后的手机号
        if (isMaskedPhoneNumber(user.getPhoneNumber())) {
            user.setPhoneNumber(originalUser.getPhoneNumber());
        }

        // 检查并还原脱敏后的密码
        if (Constants.ED.equals(user.getPassword())) {
            user.setPassword(originalUser.getPassword());
        }

        return userMapper.updateById(user) > 0;
    }

    @Override
    public List<UserOption> allUserName() {
        return userMapper.allUserName();
    }

    /**
     * 获取验证码
     */
    @Override
    public void getCode(HttpServletRequest request, HttpServletResponse response, DefaultKaptcha captchaProducer)
            throws Exception {
        // 获取客户端的IP地址
        String clientIp = getClientIp(request);

        // 检查该IP地址是否在冷却时间内
        boolean isCoolingDown = redisTemplate.opsForValue().get(clientIp) != null;

        if (isCoolingDown) {
            // IP地址在冷却时间内，不允许获取验证码
            R result = R.FAIL(CodeEnum.FORBIDDEN_GET_CODE);

            String json = JSONUtil.toJSON(result);

            response.setContentType("application/json;charset=UTF-8"); // 设置响应头为JSON格式
            response.getWriter().write(json);

            return ;
        }

        // 生成验证码
        String code = VerificationUtil.validateCode(request, response, captchaProducer, Constants.SESSION_KEY);

        // 异步操作 Redis
        CompletableFuture.runAsync(() -> {
            // 将验证码存储到Redis中
            redisTemplate.opsForValue().set(Constants.CAPTCHA_CODE_KEY + code, code,
                    Constants.VERIFY_CODE_EXPIRE_TIME, TimeUnit.MINUTES);

            // 存储该IP地址最后一次获取验证码的时间戳
            redisTemplate.opsForValue().set(clientIp, String.valueOf(System.currentTimeMillis()),
                    Constants.IP_COOLDOWN_DURATION, TimeUnit.SECONDS);
        });

    }

    /**
     * 获取客户端的IP地址
     * @param request HTTP请求对象
     * @return 客户端的IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        // Try to get the IP from X-Forwarded-For header, which is the most common case.
        String ip = request.getHeader(Constants.HEADER_X_FORWARDED_FOR);

        if (ip == null || ip.isEmpty() || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            // If X-Forwarded-For is not present or unknown, try Proxy-Client-IP.
            ip = request.getHeader(Constants.HEADER_PROXY_CLIENT_IP);
        }

        if (ip == null || ip.isEmpty() || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            // If Proxy-Client-IP is not present or unknown, try WL-Proxy-Client-IP.
            ip = request.getHeader(Constants.HEADER_WL_PROXY_CLIENT_IP);
        }

        if (ip == null || ip.isEmpty() || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            // If all else fails, get the remote address directly.
            ip = request.getRemoteAddr();

            // In some cases, getRemoteAddr() might still return "0:0:0:0:0:0:0:1" for IPv6 localhost,
            // so we check and convert it to "127.0.0.1" if necessary.
            if (ip.equals("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
        }

        // If the X-Forwarded-For header was used, we split by commas and take the first IP.
        if (Constants.HEADER_X_FORWARDED_FOR.equalsIgnoreCase(request.getHeaderNames().nextElement())) {
            String[] ips = ip.split(",");
            if (ips.length > 0) {
                ip = ips[0].trim();
            }
        }

        return ip;
    }

    /**
     * 验证电话号码是否已被正确脱敏
     * @param phoneNumber 电话号码字符串
     * @return 如果电话号码被正确脱敏，则返回 true；否则返回 false
     */
    private boolean isMaskedPhoneNumber(String phoneNumber) {
        // 正则表达式匹配格式：前三位数字，接着四个星号，最后四位数字
        return phoneNumber.matches("^\\d{3}\\*\\*\\*\\*\\d{4}$");
    }
}