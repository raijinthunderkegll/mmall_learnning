package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    public String upload(MultipartFile file,String path);

    public String upload(MultipartFile[] files,String path);
}
