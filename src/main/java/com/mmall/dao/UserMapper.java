package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkUserNameById(@Param("username")String username,@Param("userId")Integer userId);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    int checkEmailById(@Param("email")String email,@Param("userId")Integer userId);

    int checkPhone(String phone);

    int checkPhoneById(@Param("phone")String phone,@Param("userId")Integer userId);

    User selectUserByUsername(String username);

    int checkPassword(@Param("password")String password, @Param("userId")Integer userId);
}