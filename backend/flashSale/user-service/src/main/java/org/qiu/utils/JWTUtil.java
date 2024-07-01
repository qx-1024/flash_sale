package org.qiu.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.qiu.constant.Constants;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @Description: JWT 工具类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/30 8:11
 * @Version 1.0
 * @Since 1.0
 **/
public class JWTUtil {

    /**
     * 创建 token
     * @param userId    用户 id
     * @return          token
     */
    public static String createToken(String userId){
        // 设置有效期 30 分钟
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,Constants.TOKEN_EXPIRE_TIME);
        // 设置算法
        Algorithm algorithm = Algorithm.HMAC256(Constants.TOKEN_SECRET);
        // 设置 header
        HashMap<String, Object> header = new HashMap<>();
        header.put("typ","JWT");
        header.put("alg","HS256");
        // 返回 token
        return JWT.create()
                .withHeader(header)
                .withExpiresAt(calendar.getTime())
                .withClaim("userId",userId)
                .sign(algorithm);
    }

    /**
     * 解析 token，获取用户 id
     * @param token token
     * @return      用户 id
     */
    public static String parseToken(String token){
        return JWT.decode(token).getClaim("userId").asString();
    }
}
