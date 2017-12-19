/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.deployers.DefaultDeployer;

import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 10:47
 */

public class DeployerManager {

    protected Map<String, IDeployer> deployerMap;

    public Map<String, IDeployer> getDeployerMap() {
        return deployerMap;
    }

    public void setDeployerMap(Map<String, IDeployer> deployerMap) {
        this.deployerMap = deployerMap;
    }

    public IDeployer getDeployer(Object obj) {
        IDeployer deployer = null;
        if (obj instanceof IPlatfromType){
            deployer = deployerMap.get(((IPlatfromType)obj).getPlatfromType());
        }
        return (deployer == null) ? new DefaultDeployer() : deployer;
    }
}
