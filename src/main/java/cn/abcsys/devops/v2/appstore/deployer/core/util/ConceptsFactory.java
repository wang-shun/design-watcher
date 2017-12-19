package cn.abcsys.devops.v2.appstore.deployer.core.util;

import cn.abcsys.devops.deployer.model.ParamsWrapper;
import cn.abcsys.devops.v2.appstore.deployer.kubernetes.util.DeploymentConcepts;

public class ConceptsFactory {
    public static DeploymentConcepts getKubernetesDeployerConcepts(ParamsWrapper pw) {
        return new DeploymentConcepts(pw);
    }

}
