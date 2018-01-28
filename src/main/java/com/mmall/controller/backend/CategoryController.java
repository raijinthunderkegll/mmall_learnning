package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryController {

    @Autowired
    IUserService iUserService;

    @Autowired
    ICategoryService iCategoryService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse createCategory(HttpSession session, Category category) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            if (iUserService.checkAdminRole(user)) {
                //如果有管理员权限
                return iCategoryService.createCategory(category);
            } else {
                return ServerResponse.createByErrorMessage("当前用户没有管理员权限");
            }
        } else {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateCategory(HttpSession session, Category category) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            if (iUserService.checkAdminRole(user)) {
                return iCategoryService.updateCategory(category);
            } else {
                return ServerResponse.createByErrorMessage("当前用户没有管理员权限");
            }
        } else {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
    }

    @RequestMapping(value = "/getCategory", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategory(HttpSession session, @RequestParam(value = "id", defaultValue = "0") Integer id) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            if (iUserService.checkAdminRole(user)) {
                return iCategoryService.getCategoryById(id);
            } else {
                return ServerResponse.createByErrorMessage("当前用户没有管理员权限");
            }
        } else {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
    }

    @RequestMapping(value = "/getCategoryChildren", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getChildrenCategoryById(HttpSession session, @RequestParam(value = "id", defaultValue = "0") Integer id) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            if (iUserService.checkAdminRole(user)) {
                return iCategoryService.getCategoryListByParentId(id);
            } else {
                return ServerResponse.createByErrorMessage("当前用户没有管理员权限");
            }
        } else {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
    }

    //get cat this level
    @RequestMapping(value = "/getCategorys", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategorys(HttpSession session, @RequestParam(value = "id", defaultValue = "0") Integer id) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            if (iUserService.checkAdminRole(user)) {
                return iCategoryService.getLevelCategoryList(id);
            } else {
                return ServerResponse.createByErrorMessage("当前用户没有管理员权限");
            }
        } else {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
    }

}
