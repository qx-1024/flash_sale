package org.qiu.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 9:16
 * @Version 1.0
 * @Since 1.0
 **/
public interface StoreService {
    String uploadImage(MultipartFile file, String type) throws Exception;
}
