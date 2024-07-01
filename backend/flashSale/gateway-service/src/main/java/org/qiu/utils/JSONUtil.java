package org.qiu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description: JSON 工具类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/30 10:17
 * @Version 1.0
 * @Since 1.0
 **/
public class JSONUtil {
    // 对象映射工具类， 可以实现 java对象 <----> json对象 的相互转化
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 把 Java 对象转 json
     */
    public static String toJSON(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 把 json 转 Java 对象
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
