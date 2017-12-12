package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名是否存在
        int resultCount = userMapper.checkUserName(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码登陆MD5
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //都不return 登陆成功 密码置空
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse register(User user) {
        //校验username email phone
        ServerResponse validResponse = this.checkUsr(user);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //插入user表  MD5加密
        user.setRole(Const.Role.CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int insertCount = userMapper.insert(user);
        if(insertCount <= 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        //str 要校验的值  |  type 要校验的类型（字段）
        if (StringUtils.isNotBlank(type)) {
            int count = 0;
            String errorMsg = null;
            if (Const.User.USERNAME.equals(type)) {
                count = userMapper.checkUserName(str);
                errorMsg = "用户名已存在";
            } else if (Const.User.EMAIL.equals(type)) {
                count = userMapper.checkEmail(str);
                errorMsg = "邮箱已存在";
            } else if (Const.User.PHONE.equals(type)) {
                count = userMapper.checkPhone(str);
                errorMsg = "手机号码已存在";
            }

            if(count > 0){
                ServerResponse.createByErrorMessage(errorMsg);
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse checkUsr(User user){
        ServerResponse response = ServerResponse.createByErrorMessage("校验失败");
        if(user != null){
            if(StringUtils.isNotBlank(user.getUsername())) {
                response = checkValid(user.getUsername(), Const.User.USERNAME);
            }
            if(StringUtils.isNotBlank(user.getEmail())) {
                response = checkValid(user.getEmail(), Const.User.EMAIL);
            }
            if(StringUtils.isNotBlank(user.getPhone())){
                response = checkValid(user.getPhone(),Const.User.PHONE);
            }
        }
        return response;
    }
}
