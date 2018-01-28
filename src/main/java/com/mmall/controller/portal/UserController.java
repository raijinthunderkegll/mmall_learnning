package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    //自动序列化为jason
    public ServerResponse login(String username, String password, HttpSession session) {

        ServerResponse<User> login = iUserService.login(username, password);

        if (login.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, login.getData());
        }
        return login;
    }

    /**
     * 登出
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user) {

        return iUserService.register(user);
    }

    /**
     * 校验
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str, String type) {

        return iUserService.checkValid(str, type);
    }


    /**
     * 获取session中userInfo
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_userinfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        ServerResponse response = DataUtil.getSessionInfo(Const.CURRENT_USER, session);
        return response.isSuccess() ? response : response.modifyMsg("用户未登录，无法获取当前用户信息");
    }

    //todo 重置密码  分两种情况：1。忘记密码  2。已登录

    /**
     * 忘记密码-获取重置密码问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forget_getQuestion(String username) {
        return iUserService.getQuestion(username);
    }

    /**
     * 忘记密码-校验问题答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forget_checkAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码-重置密码
     *
     * @param username
     * @param password
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forget_resetPassword(String username, String password, String forgetToken) {
        return iUserService.forgetResetPassword(username, password, forgetToken);
    }

    /**
     * 登录状态-重置密码
     *
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    /**
     * 修改用户信息
     *
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateUserInfo(HttpSession session, User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        return iUserService.updateUserInfo(user);
    }


}
