package org.qiu.constant;

/**
 * @Description: 常量
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
    // 验证码的过期时间
    public static final long VERIFY_CODE_EXPIRE_TIME = 1L;



    // 分页查询默认每页条数
    public static final int DEFAULT_PAGE_SIZE = 8;




    // token 过期时间【默认 30 分钟】
    public static final int TOKEN_EXPIRE_TIME = 30;

    // token 算安的私钥
    public static final String TOKEN_SECRET = "aBcDeFgH1I2J3K4L5mNoPq6R7S8T9U!@#$%&*";

    // Session Key
    public static final String SESSION_KEY = "flash:vertify:session:";

    // token 存储的 Redis Key
    public static final String TOKEN_KEY = "flash:login:token:";

    // token header
    public static final String TOKEN_HEADER = "Authorization";

    // request key(Used to record the number of requests within a second)
    public static final String REQUEST_KEY = "flash:request:";

    // flash sale product key
    public static final String FLASH_SALE_PRODUCT_KEY = "flash:product:list";


    public static final String USER_LOGIN_URI = "/user/login";
    public static final String USER_LOGOUT_URI = "/user/logout";
    public static final String USER_REGISTER_URI = "/user/save";
    public static final String USER_GET_CAPTCHA_URI = "/user/loginValidateCode";

    public static final String ADMIN_URI = "/admin";
    public static final String ADMIN_ID = "51483200459251712";
}
