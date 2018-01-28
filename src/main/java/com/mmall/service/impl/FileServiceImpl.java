package com.mmall.service.impl;

import com.mmall.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String upload(MultipartFile file, String path) {
        //获取文件扩展名
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //使用uuid生成新的文件名
        String targetFileName = UUID.randomUUID().toString() + fileExtensionName;
        //打印日志
        logger.info("开始上传文件，上传文件名：{},上传文件路径：{}，上传后的文件名：{}.",fileName,path,targetFileName);
        //判断path路径是否存在，不存在创建文件夹
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,targetFileName);
        try {
            //上传到服务
            file.transferTo(targetFile);
            //上传到ftp服务器

        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回文件名
        return null;
    }
}
