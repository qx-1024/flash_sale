package org.qiu.service.impl;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import net.coobird.thumbnailator.Thumbnails;
import org.qiu.clients.IdClient;
import org.qiu.config.MinIOInfo;
import org.qiu.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
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

    private static final int SIZE_10 = 1024 * 1024 * 10;    // 10M
    private static final int SIZE_5 = 1024 * 1024 * 5;      // 5M
    private static final int SIZE_3 = 1024 * 1024 * 3;      // 3M
    private static final int SIZE_1 = 1024 * 1024;          // 1M

    private static final float QUALITY_5 = 0.5f;            // 压缩质量0.5
    private static final float QUALITY_3 = 0.3f;            // 压缩质量0.3
    private static final float QUALITY_2 = 0.2f;            // 压缩质量0.2
    private static final float QUALITY_1 = 0.1f;            // 压缩质量0.1

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

        // 判断文件大小是否小于10M
        long size = file.getSize();
        if (size > SIZE_10) {
            return "文件上传异常：文件大小不能超过10M";
        }

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
        String objectName = String.format("%s_%s_%s%s", type, dateStr, key, suffix);

        // 压缩图片
        InputStream inputStream = file.getInputStream();

        // 上传文件
        uploadFile(objectName, inputStream, contentType, file.getSize());

        // 生成预签名URL
        String url = getUrl(objectName);
        System.out.println("上传成功，预签名URL：" + url);

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
        String bucketName = minIOInfo.getBucket();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(bucketName)
                        .build()
        );
        if (!bucketExists){
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );
        }
    }

    /**
     * 根据文件大小进行压缩【Thumbnailator】
     */
    public InputStream compressImageByThumbnails(InputStream inputStream,
                                                 Long size,
                                                 String format) throws IOException {
        double quality = 1.0;

        // 根据不同的图片大小选择压缩质量
        if (size < SIZE_1) {
            quality = QUALITY_5;
        } else if (size < SIZE_3) {
            quality = QUALITY_3;
        } else if (size < SIZE_5) {
            quality = QUALITY_2;
        } else if (size < SIZE_10) {
            quality = QUALITY_1;
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 使用 Thumbnails 库进行图片压缩
            Thumbnails.of(inputStream)
                    .scale(1f)
                    .outputQuality(quality)
                    .outputFormat(format)
                    .toOutputStream(outputStream);

            // 将压缩后的结果转换为 InputStream
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
