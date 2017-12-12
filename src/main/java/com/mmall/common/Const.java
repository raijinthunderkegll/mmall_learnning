package com.mmall.common;

public class Const {

    //当前用户
    public static final String CURRENT_USER = "currentUser";

    //用户对象
    public interface User{
        String USERNAME = "username";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    //用户角色
    public interface Role{
        //管理员
        int ADMIN = 0;
        //客户
        int CUSTOMER = 1;
    }
}
