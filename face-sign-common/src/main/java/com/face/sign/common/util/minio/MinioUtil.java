package com.face.sign.common.util.minio;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: minio文件服务工具类
 */
@Component
public class MinioUtil {

    Logger log = LoggerFactory.getLogger(MinioUtil.class);

    @Resource
    private MinioClient minioClient;

    /**
     * 查看存储bucket是否存在
     * @param bucketName
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("查看存储bucket是否存在出现异常",e);
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     * @param bucketName
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("创建存储bucket出现异常",e);
            return false;
        }
        return true;
    }
    /**
     * 删除存储bucket
     * @param bucketName
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("删除存储bucket出现异常",e);
            return false;
        }
        return true;
    }
    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("获取全部bucket出现异常",e);
        }
        return null;
    }

    /**
     * 文件上传
     * @param file 文件
     * @param bucketName
     * @return Boolean
     */
    public Boolean upload(MultipartFile file,String bucketName, String fileName) {
        try {
            if(!bucketExists(bucketName)) {
                makeBucket(bucketName);
            }
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(fileName)
                    .stream(file.getInputStream(),file.getSize(),-1).contentType(file.getContentType()).build();
            //文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("文件上传出现异常",e);
            return false;
        }
        return true;
    }

    /**
     * 预览图片
     * @param fileName
     * @param bucketName
     * @return
     */
    /**
     * 预览图片
     * @param fileName 文件名称
     * @param bucketName 存储桶名称
     * @return 预览链接
     */
    public String preview(String fileName, String bucketName) {
        // 检查存储桶是否存在
        if (!bucketExists(bucketName)) {
            log.error("存储桶 {} 不存在", bucketName);
            return "";
        }

        // 检查文件是否存在
        boolean fileExists = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
            for (Result<Item> result : results) {
                if (result.get().objectName().equals(fileName)) {
                    fileExists = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("检查文件是否存在时出现异常", e);
            return "";
        }

        if (!fileExists) {
            log.error("文件 {} 在存储桶 {} 中不存在", fileName, bucketName);
            return "";
        }

        // 获取预览链接
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs()
                .builder()
                .bucket(bucketName)
                .object(fileName)
                .method(Method.GET)
                .build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            log.error("获取预览链接时出现异常", e);
        }
        return "";
    }

    /**
     * 文件下载
     * @param fileName 文件名称
     * @param res response
     * @param bucketName
     * @return Boolean
     */
    public void download(String fileName, HttpServletResponse res, String bucketName) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=response.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                //设置强制下载不打开
                //res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            log.error("文件下载出现异常",e);
        }
    }

    /**
     * 查看文件对象
     * @param bucketName
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            log.error("查看文件对象出现异常",e);
            return null;
        }
        return items;
    }

    /**
     * 删除
     * @param fileName
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean remove(String fileName,String bucketName){
        try {
            minioClient.removeObject( RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            log.error("删除文件出现异常",e);
            return false;
        }
        return true;
    }

    /**
     * 批量删除文件对象（没测试）
     * @param bucketName
     * @param objects 对象名称集合
     */
    public Iterable<Result<DeleteError>> removeObjects(List<String> objects, String bucketName) {
        List<DeleteObject> dos = objects.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
        return results;
    }
}