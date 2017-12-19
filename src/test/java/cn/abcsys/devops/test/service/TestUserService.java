/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.test.service;

import cn.abcsys.devops.deployer.model.User;
import cn.abcsys.devops.deployer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date   June 2,2017
 * a demo for controller
 */
public class TestUserService {
    @Resource
    private UserService us;
    private ApplicationContext ac = null;
    @Before
    public void init(){
        ac = new ClassPathXmlApplicationContext("config/spring-mybatis.xml");
        us = (UserService) ac.getBean("userService");
    }

//    @Test
//    public void selectByIdTest(){
//        User user = us.select(1);
//        System.out.println(user.toString());
//    }

    @Test
    public void insertOneUser(){
        User u = new User();
        u.setUserGender(1);
        u.setUserLevel("1");
        u.setUserLoginName("xuyuanjia");
        u.setUserLoginPassword("123456");
        u.setUserRealName("许源佳");
        System.out.println(us.insertOne(u));
    }
}
