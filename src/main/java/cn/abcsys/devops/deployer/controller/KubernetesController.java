package cn.abcsys.devops.deployer.controller;
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import cn.abcsys.devops.deployer.model.*;
import cn.abcsys.devops.deployer.service.InstanceDbService;
import cn.abcsys.devops.deployer.service.KubernetesService;
import com.fasterxml.jackson.databind.util.ObjectIdMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date June 14,2017
 * xyj for restful kubernetes api interface
 */
@Controller
public class KubernetesController {
    @Resource(name="kubernetesService")
    private KubernetesService kubernetesService;

    @Resource(name="instanceDbService")
    private InstanceDbService instanceDbService;

    private static Logger logger = Logger.getLogger(KubernetesController.class);

    private Boolean checkIfOperationSuccess(Map<String,Object> resMap){
        return ((Boolean) resMap.get("success")).compareTo(true) == 0;
    }

    @RequestMapping("createAndStart.do")
    public @ResponseBody
    Map createAndStart(ParamsWrapper pw){
        pw = this.instanceDbService.resetCreateRenameParams(pw);
        pw = this.instanceDbService.resetCreateEnvParams(pw);
        pw = this.instanceDbService.resetCreateVolumesParams(pw);
        pw = this.instanceDbService.resetCreatePortsParams(pw);
        //System.out.println(pw.getInstanceCore().getInstanceName());
        Map<String,Object> resMap = this.kubernetesService.create(pw);
        //System.out.println(resMap);
        if(this.checkIfOperationSuccess(resMap)){
            resMap.put("DbInfo",this.instanceDbService.insertReplicationController(pw));

            //pw = this.instanceDbService.getOriginalInstance(pw);
            pw = this.instanceDbService.resetCreatePortsParams(pw);
            this.instanceDbService.insertService(pw);
//           Map<String,Object> svcMap = this.kubernetesService.start(pw);
//            if(this.checkIfOperationSuccess(svcMap)) {
//
//                Map<String,Object> svcK= this.kubernetesService.checkServiceStatus(pw);
//                this.instanceDbService.updateNodePorts(svcK,pw);
//                resMap.put("svc",svcK);
//            }
            resMap.put("pod",this.kubernetesService.checkContainerStatus(pw));
        }
        return resMap;
    }

    @RequestMapping("start.do")
    public @ResponseBody
    Map start(ParamsWrapper pw){
        pw = this.instanceDbService.getOriginalInstance(pw);
        pw = this.instanceDbService.resetCreatePortsParams(pw);
        //System.out.println(pw.instancePortsString);
        Map<String,Object> resMap = this.kubernetesService.start(pw);
        if(this.checkIfOperationSuccess(resMap)){
            resMap.put("StartSavedInfo",this.instanceDbService.insertService(pw));
            resMap.put("message",this.kubernetesService.checkServiceStatus(pw));
        }
        return resMap;
    }

    @RequestMapping("setIP.do")
    public @ResponseBody
    Map setIP(ParamsWrapper pw){
        pw = this.instanceDbService.getOriginalInstance(pw);
        pw = this.instanceDbService.getPortsParamsByInstanceCoreId(pw);
        this.kubernetesService.stop(pw);
        Map<String,Object> resMap = this.kubernetesService.start(pw);
        if(this.checkIfOperationSuccess(resMap)){
            Map<String,Object> ports = this.kubernetesService.checkServiceStatus(pw);
            this.instanceDbService.updateService(pw,(String)ports.get("nodePorts"));
            resMap.put("message",ports);
        }
        return resMap;
    }

    @RequestMapping("setAppIP.do")
    public @ResponseBody
    Map setAppIP(ParamsWrapper pw){
        pw = this.instanceDbService.setOneApplicaitonCore(pw);
        pw = this.instanceDbService.getAllPorts(pw);
        this.kubernetesService.stopApp(pw);
        Map<String,Object> resMap = this.kubernetesService.startApp(pw);
        if(this.checkIfOperationSuccess(resMap)){
            Map<String,Object> ports = this.kubernetesService.checkAppServiceStatus(pw);
            //this.instanceDbService.updateService(pw,(String)ports.get("nodePorts"));
            resMap.put("serviceStatus",ports);
        }
        return resMap;
    }

    @RequestMapping("deleteAppSvc.do")
    public @ResponseBody
    Map deleteAppSvc(ParamsWrapper pw){
        pw = this.instanceDbService.setOneApplicaitonCore(pw);
        return this.kubernetesService.stopApp(pw);
    }

    @RequestMapping("getAppIP.do")
    public @ResponseBody
    Map getAppIP(ParamsWrapper pw){
        pw = this.instanceDbService.setOneApplicaitonCore(pw);
        return this.kubernetesService.checkAppServiceStatus(pw);
    }

//    @RequestMapping("migrate.do")
//    public @ResponseBody
//    Map migrate(ParamsWrapper pw1,String newHost){
//        ParamsWrapper oldW = this.instanceDbService.getOldParamsWrapper(pw1);
//        ParamsWrapper newW = this.instanceDbService.getNewParamsWrapper(oldW,newHost);
//        Map<String,Object> resMap = this.kubernetesService.migrate(oldW,newW);
//        if(this.checkIfOperationSuccess(resMap)){
//            resMap.put("migrateInfo",this.instanceDbService.migrate(oldW,newW));
//        }
//        return resMap;
//    }

    @RequestMapping("stop.do")
    public @ResponseBody
    Map stop(ParamsWrapper pw){
        ParamsWrapper suitablePw = this.instanceDbService.getOriginalInstance(pw);
        Map<String,Object> resMap =  this.kubernetesService.stop(suitablePw);
        if(this.checkIfOperationSuccess(resMap)){
            resMap.put("StopInfo",this.instanceDbService.deleteService(suitablePw));
            //resMap.put("message",this.kubernetesService.checkServiceStatus(suitablePw,false));
        }
        return resMap;
    }

    @RequestMapping("delete.do")
    public @ResponseBody
    Map delete(ParamsWrapper pw){
        ParamsWrapper suitablePw = this.instanceDbService.getOriginalInstance(pw);

        this.kubernetesService.stop(suitablePw);
        Map<String,Object> resMap = this.kubernetesService.delete(suitablePw);
        if(this.checkIfOperationSuccess(resMap)){
            resMap.put("DeleteInfo",this.instanceDbService.deleteRc(suitablePw));
            //resMap.put("message",this.kubernetesService.checkContainerStatus(suitablePw,false));
        }
        return resMap;
    }

//    @RequestMapping("getAllContainerInfo.do")
//    public @ResponseBody
//    Map getAllContainerInfo(InstanceCore ic,Integer page,Integer rows){
//        return instanceDbService.getAllContainerInfo(ic,page,rows);
//    }

    @RequestMapping("getAllContainerInfoNew.do")
    public @ResponseBody
    Map getAllContainerInfoNew(InstanceCore ic,Integer page,Integer rows,Integer envId){
        if(envId !=null && envId !=0){
            ic.setApplicationRuntimeId(envId);

            //ic.setApplicationRuntimeId(null);
        }
        else{
            ic.setApplicationRuntimeId(null);
        }
        return instanceDbService.getAllContainerInfoNew(ic,page,rows);
    }

    @RequestMapping("getAllImageInfo.do")
    public @ResponseBody
    Map getAllImageInfo(InstanceCore ic,Integer page,Integer rows,Integer envId){
        if(ic == null)
            ic = new InstanceCore();
        if(envId !=null && envId !=0){
            ic.setApplicationRuntimeId(envId);
            logger.info("查询环境为："+envId);
            //ic.setApplicationRuntimeId(null);
        }
        else{
            logger.info("由于没有传递envId，查询所有环境");
            ic.setApplicationRuntimeId(null);
        }
        return instanceDbService.getAllImageInfo(ic,page,rows);
    }

    @RequestMapping("getConatinerDetailInfo.do")
    public @ResponseBody
    Object getConatinerDetailInfo(String instanceRename){
        ParamsWrapper pw = instanceDbService.getConatinerDetailInfo(instanceRename);
        pw = kubernetesService.resetDynamicConatinerDetailInfo(pw);
        if(pw !=null)
            return pw;
        else{
            return new HashMap<String,String>(){
                {
                    put("message","没有对应的容器！");
                }
            };
        }
    }

    private void resetEnvId(InstanceCore ic,Integer envId){
        if(ic == null)
            ic = new InstanceCore();
        if(envId !=null && envId !=0){
            System.out.println("envId:"+envId);
            ic.setApplicationRuntimeId(envId);
        }
        else{
            ic.setApplicationRuntimeId(null);
        }
    }

    @RequestMapping("getAllNetworksInfo.do")
    public @ResponseBody
    Map getAllNetworksInfo(InstanceCore ic,Integer page,Integer rows,Integer envId){
        this.resetEnvId(ic,envId);
        return instanceDbService.getAllNetworksInfo(ic,page,rows);
    }

    @RequestMapping("getAllVolumesInfo.do")
    public @ResponseBody
    Map getAllVolumesInfo(InstanceCore ic,InstanceVolumes iv,Integer page,Integer rows,Integer envId){
        this.resetEnvId(ic,envId);
        return instanceDbService.getAllVolumesInfo(ic,iv,page,rows);
    }

    @RequestMapping("getContainersFromAppName.do")
    public @ResponseBody
    Map getContainersFromAppName(InstanceCore ic,Integer page,Integer rows,Integer envId){
        this.resetEnvId(ic,envId);
        Map<String,Object> resMap = this.instanceDbService.getContainersFromAppName(ic,page,rows);
        //resMap = this.kubernetesService.resetPodName(resMap);
        return resMap;
    }

    @RequestMapping("getContainerNetworkVolumeCounts.do")
    public @ResponseBody
    Map getContainerNetworkVolumeCounts(InstanceCore ic,Integer page,Integer envId){
        this.resetEnvId(ic,envId);
        return this.instanceDbService.getContainerNetworkVolumeCounts(ic);
    }

    @RequestMapping("checkVolumesPath.do")
    public @ResponseBody
    Map checkVolumesPath(InstanceVolumes iv,Integer envId){
        if(iv == null || iv !=null && iv.getInstanceVolumesPath() == null ){
            return new HashMap<String,Object>(){
                {
                    put("message","参数输入错误！");
                    put("success",false);
                }
            };
        }
        return this.instanceDbService.selectCountPath(iv,envId);
    }

}
