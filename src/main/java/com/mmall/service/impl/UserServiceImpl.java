package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.DataUtil;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

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
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //都不return 登陆成功 密码置空
        user.setPassword(StringUtils.EMPTY);
        user.setAnswer(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse register(User user) {
        //校验username email phone
        ServerResponse validResponse = this.checkUser(user);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //插入user表  MD5加密
        user.setRole(Const.Role.CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int insertCount = userMapper.insert(user);
        if (insertCount <= 0) {
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

            if (count > 0) {
                return ServerResponse.createByErrorMessage(errorMsg);
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse checkValidIgnoreId(String str, String type, Integer id) {
        //str 要校验的值  |  type 要校验的类型（字段）
        if (StringUtils.isNotBlank(type)) {
            int count = 0;
            String errorMsg = null;
            if (Const.User.USERNAME.equals(type)) {
                count = userMapper.checkUserNameById(str, id);
                errorMsg = "用户名已存在";
            } else if (Const.User.EMAIL.equals(type)) {
                count = userMapper.checkEmailById(str, id);
                errorMsg = "邮箱已存在";
            } else if (Const.User.PHONE.equals(type)) {
                count = userMapper.checkPhoneById(str, id);
                errorMsg = "手机号码已存在";
            }

            if (count > 0) {
                return ServerResponse.createByErrorMessage(errorMsg);
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse checkUser(User user) {
//        ServerResponse response = ServerResponse.createByErrorMessage("校验失败");
        ServerResponse response = ServerResponse.createBySuccess("校验成功");
        if (user != null) {
            Integer id;
            //如果id是空的，表示是注册的时候check；如果id非空，表示是更新的时候check，要排除当前id对应记录
            if ((id = user.getId()) != null) {
                if (response.isSuccess() && StringUtils.isNotBlank(user.getUsername())) {
                    response = checkValidIgnoreId(user.getUsername(), Const.User.USERNAME, id);
                }
                if (response.isSuccess() && StringUtils.isNotBlank(user.getEmail())) {
                    response = checkValidIgnoreId(user.getEmail(), Const.User.EMAIL, id);
                }
                if (response.isSuccess() && StringUtils.isNotBlank(user.getPhone())) {
                    response = checkValidIgnoreId(user.getPhone(), Const.User.PHONE, id);
                }
            } else {
                if (response.isSuccess() && StringUtils.isNotBlank(user.getUsername())) {
                    response = checkValid(user.getUsername(), Const.User.USERNAME);
                }
                if (response.isSuccess() && StringUtils.isNotBlank(user.getEmail())) {
                    response = checkValid(user.getEmail(), Const.User.EMAIL);
                }
                if (response.isSuccess() && StringUtils.isNotBlank(user.getPhone())) {
                    response = checkValid(user.getPhone(), Const.User.PHONE);
                }
            }

        }
        return response;
    }

    @Override
    public ServerResponse getQuestion(String username) {
        //校验用户名存在
        ServerResponse checkUserName = checkValid(username, Const.User.USERNAME);
        if (checkUserName.isSuccess()) {
            //如果不存在 isSuccess==true 校验失败
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //根据username获取user对象  username唯一
        User user = userMapper.selectUserByUsername(username);

        if (user != null && StringUtils.isNotBlank(user.getQuestion())) {
            return ServerResponse.createBySuccess(user.getQuestion());
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    @Override
    public ServerResponse checkAnswer(String username, String question, String answer) {
        User user = userMapper.selectUserByUsername(username);
        if (user != null) {
            String answerTarget = user.getAnswer();
            if (answer.equalsIgnoreCase(answerTarget)) {
                String forgetTokenStr = UUID.randomUUID().toString();
                TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetTokenStr);
                return ServerResponse.createBySuccess(forgetTokenStr);
            }
        } else {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return ServerResponse.createByErrorMessage("问题的答案不正确");
    }

    @Override
    public ServerResponse forgetResetPassword(String username, String password, String forgetToken) {
        //检查forgetToken
        if (StringUtils.isBlank(forgetToken)) {
            ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        //从缓存获取forgetToken 判断是否过期
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        //校验用户是否存在
        User user = userMapper.selectUserByUsername(username);
        if (DataUtil.isEmpty(user)) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        int updateCount = 0;
        //token匹配则修改密码
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            user.setPassword(md5Password);
            updateCount = userMapper.updateByPrimaryKeySelective(user);
        } else {
            return ServerResponse.createByErrorMessage("token匹配错误，请重新获取重置密码token");
        }

        return updateCount > 0 ? ServerResponse.createBySuccess("重置密码成功") : ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse resetPassword(User user, String passwordOld, String passwordNew) {

        if(StringUtils.isBlank(passwordNew)){
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
      //check。为了避免横向越权，校验密码的时候还要传入userId
        String password = MD5Util.MD5EncodeUtf8(passwordOld);
        Integer userId = user.getId();
        int checkCount = userMapper.checkPassword(password, userId);
        if (checkCount > 0) {
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int count = userMapper.updateByPrimaryKeySelective(user);
            if(count > 0) {
                return ServerResponse.createBySuccess("密码更新成功");
            }else{
                return ServerResponse.createByErrorMessage("密码更新失败");
            }
        } else {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
    }

    @Override
    public ServerResponse updateUserInfo(User user) {
//        //todo 判断id是否匹配
//        if(!userId.equals(user.getId())){
//            return ServerResponse.createByErrorMessage("当前登录用户不可修改其他用户信息");
//        }
        //判断username email phone是否已存在
        ServerResponse validResponse = checkUser(user);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //设置字段
        user.setPassword(null);
        user.setRole(null);
        user.setCreateTime(null);
        user.setUpdateTime(null);
        //更新
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", user);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public boolean checkAdminRole(User user) {

        return  user != null && user.getRole() == Const.Role.ADMIN;


    }
}
