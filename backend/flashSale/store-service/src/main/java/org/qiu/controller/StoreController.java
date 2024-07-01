package org.qiu.controller;

import jakarta.annotation.Resource;
import org.qiu.result.R;
import org.qiu.service.StoreService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 图片上传
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 8:40
 * @Version 1.0
 * @Since 1.0
 **/
@RequestMapping("/store")
@RestController
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("/upload")
    public R upload(@RequestPart MultipartFile file,
                    @RequestPart String type) throws Exception {
        String str = storeService.uploadImage(file, type);
        return R.OK(str);
    }
}
