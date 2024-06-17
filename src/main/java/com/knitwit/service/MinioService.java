package com.knitwit.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;
    
    @Transactional
    @PostConstruct
    public void init() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Transactional
    public String uploadFile(String objectName, InputStream inputStream, String contentType) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", contentType);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .headers(headers)
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить файл в MinIO.", e);
        }
    }

    public Resource getFileResource(String objectName) {
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить файловый ресурс от MinIO.", e);
        }
    }

    @Transactional
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("Не удалось удалить файл из MinIO.", e);
        }
    }
}
