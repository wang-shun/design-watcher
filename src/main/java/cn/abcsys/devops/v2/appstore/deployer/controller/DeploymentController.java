package cn.abcsys.devops.v2.appstore.deployer.controller;

import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.deployer.model.ParamsWrapper;
import cn.abcsys.devops.deployer.service.InstanceDbService;
import cn.abcsys.devops.v2.appstore.deployer.service.DeploymentService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/v1.6/appstore")
public class DeploymentController {
    @Resource(name = "deploymentService")
    private DeploymentService deploymentService;
    @Resource(name = "instanceDbService")
    private InstanceDbService instanceDbService;
    private static Logger logger = Logger.getLogger(DeploymentController.class);

    private Boolean checkIfOperationSuccess(Map<String, Object> resMap) {
        return ((Boolean) resMap.get("success")).compareTo(true) == 0;
    }

    @RequestMapping("createAndStart.do")
    @ResponseBody
    public Map createAndStart(ParamsWrapper pw) {
        pw = this.instanceDbService.resetCreateEnvParams(pw);
        pw = this.instanceDbService.resetCreateVolumesParams(pw);
        pw = this.instanceDbService.resetCreatePortsParams(pw);

        Map<String, Object> resMap = this.deploymentService.create(pw);
        if (this.checkIfOperationSuccess(resMap)) {
            resMap.put("DbInfo", this.instanceDbService.updateInstanceDb(pw));
            pw = this.instanceDbService.resetCreatePortsParams(pw);
            this.instanceDbService.insertService(pw);
        }
        return resMap;
    }

    /**
     * 删除组件--删除service，deployment
     * @param pw
     * @return
     */
    @RequestMapping("delete.do")
    @ResponseBody
    public Map delete(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<>();
        Integer componentId = pw.getInstanceCore().getComponentId();
        InstanceCore instanceCore = instanceDbService.selectByComponentId(componentId);
        pw.setInstanceCore(instanceCore);
        resMap = this.deploymentService.delete(pw);
        if (this.checkIfOperationSuccess(resMap)) {
            this.instanceDbService.deleteRc(pw);
        }
        return resMap;
    }

    @RequestMapping("getPodStatus.do")
    @ResponseBody
    public Map getPodStatus(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            Integer componentId = pw.getInstanceCore().getComponentId();
            InstanceCore instanceCore = instanceDbService.selectByComponentId(componentId);
            pw.setInstanceCore(instanceCore);
            resMap = this.deploymentService.getPodStatus(pw);
        } catch (Exception e) {
            resMap.put("success", false);
            resMap.put("message", "服务器繁忙，请稍后重试！");
        }
        return resMap;
    }

    @RequestMapping("getServicePort.do")
    @ResponseBody
    public Map getServicePort(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            Integer componentId = pw.getInstanceCore().getComponentId();
            InstanceCore instanceCore = instanceDbService.selectByComponentId(componentId);
            pw.setInstanceCore(instanceCore);
            resMap = this.deploymentService.getServicePort(pw);
        } catch (Exception e) {
            resMap.put("success", false);
            resMap.put("message", "服务器繁忙，请稍后重试！");
        }
        return resMap;
    }

    @RequestMapping("getInstanceCore.do")
    @ResponseBody
    public Map getInstanceCore(ParamsWrapper pw) {
        Map<String, List<Map<String, Object>>> resMap = new HashMap<>();
        try {
            Integer componentId = pw.getInstanceCore().getComponentId();
            InstanceCore instanceCore = instanceDbService.selectByComponentId(componentId);
            pw.setInstanceCore(instanceCore);
            resMap = this.deploymentService.getInstanceCore(pw);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return resMap;
    }
}
