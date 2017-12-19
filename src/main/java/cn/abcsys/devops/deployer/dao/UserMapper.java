/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.dao;

import cn.abcsys.devops.deployer.model.User;
import org.springframework.stereotype.Repository;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date   June 2,2017
 * a demo for controller
 */
@Repository(value="userMapper")
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}