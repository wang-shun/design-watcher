/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.interfaces;

import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;

import java.util.Map;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/8/21 0021
 * say sth.
 */
public interface IDeployer {
    /**
    * @Auther Xuyuanjia
    * @Description 执行一个原子操作，无论采用k8s或者docker swarm或者kvm，都抽象出来同样的操作；
    * @Date 0:44 2017/8/24
     * @param params
    **/
    public ResultBean exec(Object params);

}
