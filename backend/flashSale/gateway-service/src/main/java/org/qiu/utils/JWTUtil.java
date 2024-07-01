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

    /**
     * 校验 token
     * @param token token
     * @return      true 校验通过，false 校验失败
     */
    public static Boolean checkToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(Constants.TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            // 如果验证成功，没有抛出异常，则返回 true
            verifier.verify(token);
            return true;
        } catch (SignatureVerificationException e){
            throw new RuntimeException("token 校验失败，无效签名");
        } catch (TokenExpiredException e){
            throw new RuntimeException("token 校验失败，token 已过期");
        } catch (AlgorithmMismatchException e){
            throw new RuntimeException("token 校验失败，token 算法不匹配");
        } catch (Exception e){
            throw new RuntimeException("token 校验失败，token 无效");
        }
    }

}
