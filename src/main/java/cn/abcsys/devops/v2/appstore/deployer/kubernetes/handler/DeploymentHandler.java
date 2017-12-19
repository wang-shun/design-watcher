package cn.abcsys.devops.v2.appstore.deployer.kubernetes.handler;

import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.deployer.model.ParamsWrapper;
import cn.abcsys.devops.v2.appstore.deployer.core.util.ConceptsFactory;
import cn.abcsys.devops.v2.appstore.deployer.kubernetes.util.DeploymentConcepts;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/*
* File:    ComponentController.java
* Author:  zhangqiufeng@beyondcent.com
* Modify:
* Version: 1.6
* Date:    2017年9月18日
* Description:
*/
@Component("deploymentHandler")
public class DeploymentHandler {

    private static Logger logger = Logger.getLogger(DeploymentHandler.class);

    public boolean create(ParamsWrapper pw) throws Exception {
        DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
        return dc.createDeployment(pw.getPathName());
    }

    public boolean delete(ParamsWrapper pw) throws Exception {
        DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
        dc.setParamsWrapper(pw);
        dc.deleteService();
        return dc.deleteDeployment();
    }

    public Map<String, Object> getPodStatus(ParamsWrapper pw) {
        DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
        return dc.getPodStatus();
    }

    public Map<String, Object> getPodStatus(List<InstanceCore> instanceCoreList) {
        Map<String, Object> resMap = new HashMap<>();
        Integer runStatus = 0;
        Integer notRunStatus = 0;
        for (InstanceCore instanceCore : instanceCoreList) {
            ParamsWrapper pw = new ParamsWrapper();
            pw.instanceCore = instanceCore;
            pw.instanceReplica = instanceCore.getInstanceReplica();
            DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
            Map<String, Object> statusMap = dc.getPodStatus();
            if (statusMap.get("runStatus") != null) {
                runStatus = runStatus + Integer.valueOf(statusMap.get("runStatus").toString());
            }
            if (statusMap.get("notRunStatus") != null) {
                notRunStatus = notRunStatus + Integer.valueOf(statusMap.get("notRunStatus").toString());
            }
        }
        resMap.put("runStatus",runStatus);
        resMap.put("notRunStatus",notRunStatus);
        return resMap;
    }

    public Map<String, Object> getServicePort(ParamsWrapper pw) {
        DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
        return dc.getServicePort();
    }

    public Map<String, List<Map<String, Object>>> getInstanceCore(ParamsWrapper pw) {
        DeploymentConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(pw);
        return dc.getInstanceCore();
    }
}
