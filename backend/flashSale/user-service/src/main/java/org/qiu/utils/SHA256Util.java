package org.qiu.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: SHA256 加密算法工具类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/22 9:00
 * @Version 1.0
 * @Since 1.0
 **/
@Slf4j
public class SHA256Util {
    /**
     * SHA256 加密
     * @param input     需要加密的字符串
     * @return          加密后的字符串
     */
    public static String encrypt(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA256 加密失败", e);
        }
        return null;
    }

    /**
     * 将哈希字节数组转为 16 进制字符串
     * @param bytes 哈希字节数组
     * @return      16 进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
