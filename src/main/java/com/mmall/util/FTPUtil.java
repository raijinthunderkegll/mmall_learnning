package com.mmall.util;

import com.mmall.common.Const;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty(Const.FTP_IP);
    private static String ftpUser = PropertiesUtil.getProperty(Const.FTP_USER);
    private static String ftpPass = PropertiesUtil.getProperty(Const.FTP_PASSWORD);
    private static int ftpPort = 21;


    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    private boolean connectServer(String ip, int port, String user, String pwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常", e);
        }
        return isSuccess;
    }

    public boolean connectServer(){
        return connectServer(this.ip,this.port,this.user,this.pwd);
    }

    public static boolean uploadFile(File file,String remotePath) {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,ftpPort,ftpUser,ftpPass);
        logger.info("开始连接FTP服务器");
        boolean uploaded = ftpUtil.uploadFileCore(file,remotePath);
        logger.info("断开连接FTP服务器，上传结束，上传结果：{}",uploaded);
        return uploaded;
    }

    public static boolean uploadFile(File file){
        return uploadFile(file,"img");
    }

    private boolean uploadFileCore(File file,String remotePath) {
        FTPUtil ftpUtil = new FTPUtil(this.ip,this.port,this.user,this.pwd);
        boolean uploaded = true;
        FileInputStream fis = null;

        if(ftpUtil.connectServer()){
            try{
                FTPClient ftpClient = ftpUtil.getFtpClient();
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                fis = new FileInputStream(file);
                uploaded = ftpClient.storeFile(remotePath,fis);

            }catch (IOException e){
                logger.error("上传文件异常",e);
                uploaded = false;
            }finally{
                try{
                    ftpClient.disconnect();
                }catch (IOException e){
                    logger.error("关闭FTP连接发生异常",e);
                    throw new RuntimeException("关闭FTP连接发生异常",e);
                }
            }
        }

        return uploaded;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
