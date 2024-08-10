package org.qiu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: Minio 配置信息
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 8:29
 * @Version 1.0
 * @Since 1.0
 **/
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinIOInfo {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
