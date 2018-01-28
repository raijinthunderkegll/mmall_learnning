package common;

import com.mmall.common.ServerResponse;
import com.mmall.controller.portal.UserController;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.DataUtil;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Component
public class MainTest {

//    @Autowired
//    static UserMapper userMapper;

    public static void main(String[] args) {

//        boolean bool = DataUtil.isAllFieldNull(new TestBean());
//        System.out.println(bool);

//        Integer i = 0;
//        System.out.println(i == 0);

        ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/applicationContext.xml");
        UserMapper userMapper = (UserMapper) context.getBean("userMapper");


//        int count = userMapper.checkUserNameById("admin",3);
//        System.out.println(count);



        IUserService userService = (IUserService) context.getBean("iUserService");
        User user = new User();
        user.setId(100);
        userService.checkUser(user);


        System.out.println(MD5Util.MD5EncodeUtf8("900330"));
    }
}
