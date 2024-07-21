package org.qiu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiu.constant.Constants;
import org.qiu.pojo.User;
import org.qiu.pojo.UserQuery;
import org.qiu.result.R;
import org.qiu.service.UserService;
import org.qiu.utils.JWTUtil;
import org.qiu.utils.VerificationUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 用户相关接口
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/19 10:31
 * @Version 1.0
 * @Since 1.0
 **/
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private DefaultKaptcha captchaProducer;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录验证码图片
     */
    @GetMapping("/loginValidateCode")
    public void loginValidateCode(HttpServletRequest request,
                                  HttpServletResponse response)
            throws Exception {
        String code = VerificationUtil.validateCode(request, response,
                captchaProducer, Constants.SESSION_KEY);
        redisTemplate.opsForValue().set(Constants.CAPTCHA_CODE_KEY + code, code,
                        Constants.VERIFY_CODE_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 用户登录
     * @param account   账号
     * @param password  密码
     * @return          是否登录成功
     */
    @GetMapping("/login")
    public R login(@RequestParam("account") String account,
                   @RequestParam("password") String password,
                   @RequestParam("code") String code) {
        String token = userService.login(account, password, code);
        return token != null ? R.OK("登录成功", token) : R.FAIL("登录失败");
    }

    /**
     * 用户退出登录
     * @param request   请求对象
     * @return          是否退出成功
     */
    @GetMapping("/logout")
    public R logout(HttpServletRequest request){
        String token = request.getHeader(Constants.TOKEN_HEADER);
        String userId = JWTUtil.parseToken(token);
        // 清除 Redis 中的 token 以及当前登录的用户信息
        redisTemplate.delete(Constants.TOKEN_KEY + userId);
        redisTemplate.delete(Constants.CURRENT_LOGIN_USER + userId);
        return R.OK("用户已退出登录");
    }

    /**
     * 获取当前登录的用户
     * @param request   请求对象
     * @return          用户信息对象
     */
    @GetMapping("/currentUser")
    public R currentUser(HttpServletRequest request) {
        String token = request.getHeader(Constants.TOKEN_HEADER);
        String userId = JWTUtil.parseToken(token);

        User user = (User) redisTemplate.opsForValue().get(Constants.CURRENT_LOGIN_USER + userId);

        if (user == null) {
            user = userService.selectOne(userId);
        }

        return user != null ? R.OK(user) : R.FAIL("用户未登录");
    }

    /**
     * 查询用户数量
     */
    @GetMapping("/count")
    public R getUserCount(){
        long count = userService.count();
        return count > 0 ? R.OK(count) : R.FAIL("查询用户数量失败");
    }

    /**
     * 查询男性用户数量
     */
    @GetMapping("/male")
    public R getMaleCount(){
        int maleCount = userService.lambdaQuery()
                .eq(User::getGender, 0)
                .list()
                .size();
        return maleCount > 0 ? R.OK(maleCount) : R.FAIL("查询男性用户数量失败");
    }

    /**
     * 查询女性用户数量
     */
    @GetMapping("/female")
    public R getFemaleCount(){
        int femaleCount = userService.lambdaQuery()
                .eq(User::getGender, 1)
                .list()
                .size();
        return femaleCount > 0 ? R.OK(femaleCount) : R.FAIL("查询女性用户数量失败");
    }

    /**
     * 查询 VIP 用户数量
     */
    @GetMapping("/vip")
    public R getVipCount(){
        int vipCount = userService.lambdaQuery()
                .eq(User::getUserType, 1)
                .list()
                .size();
        return vipCount > 0 ? R.OK(vipCount) : R.FAIL("查询 vip 用户数量失败");
    }

    /**
     * 分页查询用户信息
     * @param current   页码
     * @return          用户信息分页对象
     */
    @GetMapping("/page")
    public R selectByPage(@RequestParam("current") Integer current) {
        Page<User> page = userService.selectByPage(current, Constants.DEFAULT_PAGE_SIZE);
        return page != null ? R.OK(page) : R.FAIL("查询用户信息失败");
    }

    /**
     * 根据 id 查询用户信息
     * @param userId     用户id
     * @return           用户信息对象
     */
    @GetMapping("/one")
    public R selectOne(@RequestParam("userId") String userId) {
        User user = userService.selectOne(userId);
        return user != null ? R.OK(user) : R.FAIL("用户不存在");
    }

    /**
     * 创建用户【注册】
     * @param user  用户信息对象
     * @return      是否创建成功
     */
    @PostMapping("/save")
    public R createUser(@RequestBody User user) {
        int saved = userService.saveUser(user);
        return saved > 0 ? R.OK("新增用户成功") : R.FAIL("新增用户失败");
    }

    /**
     * 更新用户密码
     * @return    是否更新成功
     */
    @PutMapping("/updatePassWord")
    public R updateUserPassWord (@RequestBody UserQuery userQuery)  {
        int updated = userService.updateUserPassWord(
                userQuery.getAccount(),
                userQuery.getPassword(),
                userQuery.getConfirmPassword()
        );
        return updated > 0 ? R.OK("更新用户密码成功") : R.FAIL("更新用户密码失败");
    }

    /**
     * 更新用户信息
     * @param user  用户信息对象
     * @return      是否更新成功
     */
    @PutMapping("/update")
    public R updateUser (@RequestBody User user){
        boolean updated = userService.updateById(user);
        return updated ? R.OK("更新用户信息成功") : R.FAIL("更新用户信息失败");
    }

    /**
     * 根据 id 删除指定用户【逻辑删除】
     * @param userId    用户id
     * @return          是否删除成功
     */
    @DeleteMapping("/delete")
    public R deleteUser(@RequestParam("userId") String userId) {
        boolean removed = userService.removeById(userId);
        return removed ? R.OK("删除用户成功") : R.FAIL("删除用户失败");
    }



    /**/
    /**/
    /**/
    /**/
    /**/
    /**/

    // TODO: 未使用
    /**
     * 根据 id 批量删除用户【逻辑删除】
     * @param userIds   用户id集合
     * @return          是否删除成功
     */
    @DeleteMapping("/batchDelete")
    public R batchDeleteUser(@RequestParam("userIds") List<String> userIds) {
        boolean removed = userService.removeByIds(userIds);
        return removed ? R.OK("批量删除用户成功") : R.FAIL("批量删除用户失败");
    }

}
