package com.mmall.common;

public class Const {

    //当前用户
    public static final String CURRENT_USER = "currentUser";

    //返回前端状态satus
    public interface status {
        int SUCCESS = 0;
        int ERROR = 1;
        int NEED_LOGIN = 10;

    }

    //用户对象
    public interface User {
        String USERNAME = "username";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    //用户角色
    public interface Role {
        //管理员
        int ADMIN = 1;
        //客户
        int CUSTOMER = 0;
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"online");
        private Integer code;
        private String desc;
        private ProductStatusEnum(Integer code,String desc){
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static final String FTP_PREFIX="ftp.server.http.prefix";
    public static final String FTP_USER="ftp.user";
    public static final String FTP_PASSWORD="ftp.pass";
    public static final String FTP_IP="ftp.server.ip";

    public static final String IMG_DEFAULT_URL="http://img.happymmall.com/";

    public static final String UPLOAD_FILE_PATH="upload";


}
