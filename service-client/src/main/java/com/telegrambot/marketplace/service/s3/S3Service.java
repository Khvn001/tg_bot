package com.telegrambot.marketplace.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    String uploadFile(String name, byte[] photo);
    List<String> uploadFiles(List<String> name, List<byte[]> photos);
    String uploadMultipartFile(String name, MultipartFile file);
    List<String> uploadMultipartFiles(List<MultipartFile> files);
    List<String> uploadMultipartFilesWithCustomNames(List<String> names, List<MultipartFile> files);
    List<String> getBucketFilesUrls();
    void deleteFiles(List<String> urls);

    void deleteFile(String url);
    String privatePathToPublicPath(String url);
}
