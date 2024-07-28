package org.qiu.constant;

/**
 * @Description: 全局常量
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/29 20:44
 * @Version 1.0
 * @Since 1.0
 **/
public class Constants {
    // 验证码的 Redis Key
    public static final String CAPTCHA_CODE_KEY = "flash:login:captcha:";

    // 当前登录用户
    public static final String CURRENT_LOGIN_USER = "flash:login:current:";

    // token 过期时间【默认 30 分钟】
    public static final int TOKEN_EXPIRE_TIME = 30;

    // 当前登录用户信息有效时间
    public static final int CURRENT_LOGIN_USER_EXPIRE_TIME = 30;

    // 验证码的过期时间
    public static final long VERIFY_CODE_EXPIRE_TIME = 1L;

    // 获取验证码的 IP 的过期时间
    public static final long IP_COOLDOWN_DURATION = 60L;



    // 分页查询默认每页条数
    public static final int DEFAULT_PAGE_SIZE = 8;

    // 预约活动状态，0：未开始
    public static final Integer RESERVATION_STATUS_NOT_STARTED = 0;

    // 预约活动状态，1：已结束
    public static final Integer RESERVATION_STATUS_FINISHED = 1;

    // 预约活动状态，2：进行中
    public static final Integer RESERVATION_STATUS_IN_PROGRESS = 2;

    // 闪购活动状态，0：未开始
    public static final Integer ACTIVITY_STATUS_NOT_STARTED = 0;

    // 闪购活动状态，1：已结束
    public static final Integer ACTIVITY_STATUS_FINISHED = 1;

    // 闪购活动状态，2：进行中
    public static final Integer ACTIVITY_STATUS_IN_PROGRESS = 2;



    // token header
    public static final String TOKEN_HEADER = "Authorization";

    // token private key
    public static final String TOKEN_SECRET = "aBcDeFgH1I2J3K4L5mNoPq6R7S8T9U!@#$%&*";

    // Session Key
    public static final String SESSION_KEY = "flash:vertify:session:";

    // token in redis Key
    public static final String TOKEN_KEY = "flash:login:token:";


    // flash sale product key
    public static final String FLASH_SALE_PRODUCT_KEY = "flash:product:list";

    // reservation list key
    public static final String RESERVATION_KEY = "flash:reservation:";

    // activity list key
    public static final String ACTIVITY_KEY = "flash:activity:";

    // order list key
    public static final String ORDER_KEY = "flash:order:";

    // flash sale order queue name
    public static final String FLASH_SALE_QUEUE_NAME = "flash.work.queue";


    // request key(Used to record the number of requests within a second)
    public static final String REQUEST_KEY = "flash:request:";

    public static final String USER_LOGIN_URI = "/user/login";
    public static final String USER_LOGOUT_URI = "/user/logout";
    public static final String USER_REGISTER_URI = "/user/save";
    public static final String USER_GET_CAPTCHA_URI = "/user/loginValidateCode";

    public static final String ADMIN_URI = "/admin";
    public static final String ADMIN_ID = "51483200459251712";
}
