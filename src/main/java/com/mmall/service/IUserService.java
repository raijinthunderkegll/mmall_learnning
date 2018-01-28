package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    //user
    ServerResponse<User> login(String username, String password);
    ServerResponse register(User user);
    ServerResponse checkValid(String username, String type);
    ServerResponse checkValidIgnoreId(String username, String type, Integer id);
    ServerResponse checkUser(User user);
    ServerResponse getQuestion(String username);
    ServerResponse checkAnswer(String username, String question, String answer);
    ServerResponse forgetResetPassword(String username, String password, String forgetToken);
    ServerResponse resetPassword(User user, String passwordOld, String passwordNew);
    ServerResponse updateUserInfo(User user);


    //manage user
    boolean checkAdminRole(User user);
}
