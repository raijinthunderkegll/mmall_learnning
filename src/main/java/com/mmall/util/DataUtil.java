package com.mmall.util;

import com.mmall.common.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataUtil {

//    private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);

    /**
     * 获取session值
     * @param key
     * @param session
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> getSessionInfo(String key, HttpSession session){
        T data = (T)session.getAttribute(key);
        if(data == null){
            return ServerResponse.createByError();
        }
        ServerResponse<T> response = ServerResponse.createBySuccess(data);
        return response;
    }

    public static boolean isEmpty(Object object){
        if(object == null){
            return true;
        }
        return isAllFieldNull(object);
    }

    public static boolean isNotEmpty(Object object){
        return !isNotEmpty(object);
    }

    public static boolean isAllFieldNull(Object object) {
        if (object == null) {
//            logger.error("获取对象属性出错，对象object为null");
            throw new RuntimeException("获取对象属性出错，对象object为null");
        }
        boolean flag = true;
        try {
            Class targetClass = object.getClass();
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Object value = field.get(object);
                if(value instanceof Number){
                    //校验为0
                    flag =  (Integer)value == 0;
                }else if(value instanceof Boolean) {
                    //校验为false
                    flag = (Boolean)value;
                }else {
                    //校验为空
                    flag = value != null;
                }
                if(!flag){
                    return flag;
                }
            }
        } catch (IllegalAccessException e) {
//            logger.error("获取对象属性出错，对象object ：{}", object, e);
            throw new RuntimeException("获取对象属性出错，对象object ：" + object, e);
        }
        return flag;
    }
}
