/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.interfaces;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 16:51
 *  IdeployType 接口用来判断获取 executor 类型
 */
public interface IdeployType {

    public String getDeployType();

    public void setDeployType(String deployType);
}
