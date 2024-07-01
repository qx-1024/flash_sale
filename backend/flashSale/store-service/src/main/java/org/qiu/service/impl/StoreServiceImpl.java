package org.qiu.service.impl;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import org.qiu.clients.IdClient;
import org.qiu.config.MinIOInfo;
import org.qiu.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 存储系统
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/23 9:17
 * @Version 1.0
 * @Since 1.0
 **/
@Service
public class StoreServiceImpl implements StoreService {
    @Resource
    private MinioClient minioClient;

    @Resource
    private MinIOInfo minIOInfo;

    @Resource
    private IdClient idClient;

    /**
     * 上传图片
     * @param file  上传的图片文件
     * @return      图片的访问地址
     */
    @Override
    public String uploadImage(MultipartFile file, String type) throws Exception {
        // 检查文件是否为空
        if (file == null || file.getSize() == 0) {
            return "文件上传异常：文件不能为空";
        }

        // 检查桶是否存在，不存在则创建
        checkBucket();

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        String key = idClient.generateId().toString();
        String suffix = "";
        if (originalFilename != null){
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 获取当前时间
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateStr = dateFormat.format(now);

        // 示例：product_20240101_213871263871249872.jpg
        String objectName = String.format("%s_%s_%s%s", type, dateStr , key, suffix);

        // 获取文件输入流
        InputStream inputStream = file.getInputStream();

        // 上传文件
        uploadFile(objectName, inputStream, contentType, file.getSize());

        // 生成预签名URL
        String url = getUrl(objectName);

        // 关闭文件输入流
        inputStream.close();
        return url;
    }

    /**
     * 上传文件
     * @param objectName    文件名
     * @param inputStream   文件输入流
     * @param contentType   文件类型
     * @param size          文件大小
     */
    private void uploadFile(String objectName, InputStream inputStream,
                            String contentType, Long size) throws Exception{
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(minIOInfo.getBucket())
                        .object(objectName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    /**
     * 生成预签名URL
     * @param objectName    文件名
     * @return              预签名URL
     */
    private String getUrl(String objectName) throws Exception{
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs
                        .builder()
                        .bucket(minIOInfo.getBucket())
                        .object(objectName)
                        .method(Method.GET)
                        .build()
        );
    }


    /**
     * 检查桶是否存在，不存在则创建
     */
    private void checkBucket() throws Exception{
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(minIOInfo.getBucket())
                        .build()
        );
        if (!bucketExists){
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(minIOInfo.getBucket())
                            .build()
            );
        }
    }
}
