package com.library.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.library.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class OssUtils {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    /**
     * 上传文件到 OSS 并返回访问 URL
     * @param file 上传的文件
     * @param directory 目录（如 "avatars"）
     * @return 文件的访问 URL
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectKey = directory + "/" + UUID.randomUUID().toString() + extension;

        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // 上传到 OSS
        ossClient.putObject(ossConfig.getBucketName(), objectKey, file.getInputStream(), metadata);

        // 返回访问 URL
        return ossConfig.getBucketUrl() + "/" + objectKey;
    }

    /**
     * 删除 OSS 上的文件
     * @param fileUrl 文件的访问 URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        // 从 URL 中提取 objectKey
        String bucketUrl = ossConfig.getBucketUrl();
        if (fileUrl.startsWith(bucketUrl)) {
            String objectKey = fileUrl.substring(bucketUrl.length() + 1);
            ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
        }
    }
}
