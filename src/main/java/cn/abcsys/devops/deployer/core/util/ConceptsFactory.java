package cn.abcsys.devops.deployer.core.util;/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import cn.abcsys.devops.deployer.kubernetes.util.DeployerConcepts;
import cn.abcsys.devops.deployer.model.ParamsWrapper;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/6/13 0013
 * say sth.
 */
public class ConceptsFactory {
    public static DeployerConcepts getKubernetesDeployerConcepts(ParamsWrapper paramsWrapper){
        return new DeployerConcepts(paramsWrapper);
    }

}
