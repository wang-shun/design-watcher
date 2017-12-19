/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.service;

import cn.abcsys.devops.deployer.dao.UserMapper;
import cn.abcsys.devops.deployer.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date   June 2,2017
 * a demo for controllers
 */
@Service("userService")
public class UserService {
    @Resource(name="userMapper")
    public UserMapper userDao;

    public User selectOne(int userId) {
        return this.userDao.selectByPrimaryKey(userId);
    }

    public int insertOne(User u){
        return this.userDao.insertSelective(u);
    }
}
