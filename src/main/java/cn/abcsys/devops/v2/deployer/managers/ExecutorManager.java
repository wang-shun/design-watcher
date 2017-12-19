/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.executors.DefaultExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 10:47
 */
@Service("executorManager")
public class ExecutorManager {

    protected Map<String, IExector> excutorMap ;

    public Map<String, IExector> getExcutorMap() {
        return excutorMap;
    }

    public void setExcutorMap(Map<String, IExector> excutorMap) {
        this.excutorMap = excutorMap;
    }

    public IExector getExecutor(Object params) {
        IExector exector = null;
        if(params instanceof IdeployType){
            exector = excutorMap.get(((IdeployType)params).getDeployType());
        }
        return (exector == null) ? new DefaultExecutor() : exector;
    }
}
