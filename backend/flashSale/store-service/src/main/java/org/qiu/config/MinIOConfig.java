package org.qiu.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: Minio 配置类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 8:28
 * @Version 1.0
 * @Since 1.0
 **/
@Configuration
public class MinIOConfig {
    @Resource
    private MinIOInfo minIOInfo;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minIOInfo.getEndpoint())
                .credentials(minIOInfo.getAccessKey(), minIOInfo.getSecretKey())
                .build();
    }
}
