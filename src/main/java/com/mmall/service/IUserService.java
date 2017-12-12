package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    ServerResponse<User> login(String username, String password);
    ServerResponse register(User user);
    ServerResponse<User> checkValid(String username, String password);
    ServerResponse<User> checkUsr(User user);

}
