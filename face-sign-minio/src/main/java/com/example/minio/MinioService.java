package com.example.minio;

import com.example.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 初始化存储桶
     */
    public void initBucket() throws ServerException, InsufficientDataException, InvalidResponseException,
            InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件到 MinIO
     * @param inputStream 文件输入流
     * @param originalFilename 原始文件名
     * @return 存储路径
     */
    public String uploadFile(InputStream inputStream, String originalFilename) throws ServerException,
            InsufficientDataException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException,
            XmlParserException, ErrorResponseException {
        String uuid = UUID.randomUUID().toString();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = uuid + fileExtension;

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .contentType("application/octet-stream")
                .build());

        return objectName;
    }

    /**
     * 获取文件的访问 URL
     * @param objectName 文件名
     * @return 文件访问 URL
     */
    public String getFileUrl(String objectName) throws ServerException, InsufficientDataException,
            InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException,
            ErrorResponseException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(2, TimeUnit.DAYS) // 设置链接有效期为 2 天
                .build());
    }

    /**
     * 删除文件
     * @param objectName 文件名
     */
    public void deleteFile(String objectName) throws ServerException, InsufficientDataException,
            InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException,
            ErrorResponseException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }
}