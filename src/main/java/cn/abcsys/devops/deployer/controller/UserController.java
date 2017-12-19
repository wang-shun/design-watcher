/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.controller;

import cn.abcsys.devops.deployer.model.User;
import cn.abcsys.devops.deployer.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date   June 2,2017
 * a demo for controllers
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Resource(name="userService")
    private UserService us;
    private static Logger logger = Logger.getLogger(UserController.class);

    //http://localhost:8080/user/insertOneUser.do?userLoginName=xuyuanjia&userLoginPassword=123456
    @RequestMapping("insertOneUser.do")
    public @ResponseBody Map insert(User u){
        System.out.println(u.getUserLoginName());
        Map resMap = new HashMap<String,Object>();
        int status = us.insertOne(u);
        resMap.put("status",status);
        logger.info("return status:"+status);
        return resMap;
    }
}
