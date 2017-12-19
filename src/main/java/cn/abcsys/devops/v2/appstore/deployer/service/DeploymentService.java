package cn.abcsys.devops.v2.appstore.deployer.service;

import cn.abcsys.devops.deployer.dao.InstanceCoreMapper;
import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.deployer.model.ParamsWrapper;
import cn.abcsys.devops.v2.appstore.deployer.kubernetes.handler.DeploymentHandler;
import cn.abcsys.devops.v2.deployer.trigger.RuntimeDeployerTrigger;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("deploymentService")
public class DeploymentService {

    @Resource(name = "deploymentHandler")
    private DeploymentHandler deploymentHandler;

    @Resource(name = "instanceCoreMapper")
    private InstanceCoreMapper instanceCoreMapper;

    @Qualifier("selfRestTemplate")
    @Autowired
    private RestTemplate template;
    private static Logger logger = Logger.getLogger(DeploymentService.class);

    public Map<String, Object> create(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        String message = "";
        try {
            boolean successFlag = this.deploymentHandler.create(pw);
            resMap.put("success", successFlag);
            if (successFlag) {
                message = "组件创建成功！";
            } else {
                message = "组件创建失败！";
            }
            resMap.put("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    private Map<String, Object> exceptionHandler(Map<String, Object> resMap, Exception e) {
        logger.info("wrong request or kubernetes cluster error");
        resMap.put("success", false);
        resMap.put("message", "请重新设置相关参数！");
        return resMap;
    }

    public ParamsWrapper commonCheckParms(ParamsWrapper pw) {
        if (pw.instanceCore.getInstanceRename() != null && pw.instanceCore.getInstanceNamespace() != null)
            return pw;
        return null;
    }

    public Map<String, Object> delete(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try {
            this.deploymentHandler.delete(pw);
            resMap.put("success", true);
        } catch (Exception e) {
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public Map<String, Object> getPodStatus(ParamsWrapper pw) {
        return this.deploymentHandler.getPodStatus(pw);
    }

    public Map<String, Object> getPodStatus(List<InstanceCore> instanceCoreList){
        return this.deploymentHandler.getPodStatus(instanceCoreList);
    }

    public Map<String, Object> getServicePort(ParamsWrapper pw) {
        return this.deploymentHandler.getServicePort(pw);
    }

    public Map<String, List<Map<String, Object>>> getInstanceCore(ParamsWrapper pw) {
        return this.deploymentHandler.getInstanceCore(pw);
    }

    public Map selectByComponentCoreForDeployer(Map<String, Object> params) {
        try {
            return template.getForObject(RuntimeDeployerTrigger.appstoreUrl + "selectByComponentCoreForDeployer.do?" +
                    "projectIds={projectIds}&" +
                    "envId={envId}", Map.class, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InstanceCore> selectAllComponent(List<Integer> componentIds){
        return instanceCoreMapper.selectAllComponent(componentIds);
    }
}
