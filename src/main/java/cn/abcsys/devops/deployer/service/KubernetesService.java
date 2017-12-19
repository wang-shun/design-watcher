/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.service;
import cn.abcsys.devops.deployer.kubernetes.handler.DeployerHandler;
import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.deployer.model.ParamsWrapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/6/12 0012
 * say sth.
 */
@Service("kubernetesService")
public class KubernetesService {

    @Resource(name="deployerHandler")
    private DeployerHandler kubernetesDeployerHandler;


    private static Logger logger = Logger.getLogger(KubernetesService.class);

    // wait next refactoring.
//    private Map<String,Object> setCommonMap(String[] keys,Object[] values){
//        Map<String,Object> resMap = new HashMap<String,Object>();
//        try {
//            resMap.put("InstanceName",(String)this.kubernetesDeployerHandler.create(pw));
//            resMap.put("success",true);
//            resMap.put("message","实例创建中！");
//        } catch (Exception e) {
//            resMap = exceptionHandler(resMap, e);
//        }
//        return resMap;
//    }

    public Map<String,Object> create(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("InstanceName",(String)this.kubernetesDeployerHandler.create(pw));
            resMap.put("success",true);
        } catch (Exception e) {
            e.printStackTrace();
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public ParamsWrapper commonCheckParms(ParamsWrapper pw){
        if(pw.instanceCore.getInstanceRename()!=null && pw.instanceCore.getInstanceNamespace() !=null)
            return pw;
        return null;
    }

    public Map<String,Object> start(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("ServiceName",(String)this.kubernetesDeployerHandler.start(pw));
            resMap.put("success",true);
            resMap.put("message","服务启动中！");
        } catch (Exception e) {
            e.printStackTrace();
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public Map<String,Object> startApp(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("ServiceName",(String)this.kubernetesDeployerHandler.startApplicationAndImageNameTag(pw));
            resMap.put("success",true);
            resMap.put("message","服务创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            resMap.put("success",false);
            resMap.put("message","当前应用没有设置端口，不需要创建访问！");
        }
        return resMap;
    }

    public Map<String,Object> migrate(ParamsWrapper pw1,ParamsWrapper pw2){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("MigratingInstanceName",(String)this.kubernetesDeployerHandler.migrate(pw1,pw2));
            resMap.put("namespace",pw1.instanceCore.getInstanceNamespace());
            resMap.put("success",true);
            resMap.put("message","实例迁移中！");
        } catch (Exception e) {
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public Map<String,Object> stop(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("stopStatus",this.kubernetesDeployerHandler.stop(pw));
            resMap.put("success",true);
            //resMap.put("message","服务停止中！");
        } catch (Exception e) {
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public Map<String,Object> stopApp(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            resMap.put("stopStatus",this.kubernetesDeployerHandler.stopApp(pw));
            resMap.put("success",true);
            //resMap.put("message","服务停止中！");
        } catch (Exception e) {
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    public Map<String,Object> delete(ParamsWrapper pw){
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
            this.kubernetesDeployerHandler.delete(pw);
            resMap.put("deleteStatus",true);
            resMap.put("success",true);
           // resMap.put("message","服务删除中！");
        } catch (Exception e) {
            resMap = exceptionHandler(resMap, e);
        }
        return resMap;
    }

    private Map<String,Object> exceptionHandler(Map<String, Object> resMap, Exception e) {
        logger.info("wrong request or kubernetes cluster error");
        resMap.put("success",false);
        resMap.put("message","请重新设置相关参数！");
        return resMap;
    }

    public Map<String,Object> checkContainerStatus(ParamsWrapper pw){
        Map<String,Object> status = (Map)kubernetesDeployerHandler.getContainerStatus(pw);
        while( status == null){
            status = (Map)kubernetesDeployerHandler.getContainerStatus(pw);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(status !=null){
            return status;
        }
        return new HashMap<String,Object>(){
            {
                put("success",false);
                put("message","集群环境异常！");
            }
        };
    }

    public Map<String,Object> checkServiceStatus(ParamsWrapper pw) {
        Map<String, Object> svc = (Map) kubernetesDeployerHandler.getServiceStatus(pw);
        return svc;
    }

    public Map<String,Object> checkAppServiceStatus(ParamsWrapper pw) {
        Map<String, Object> svc = (Map) kubernetesDeployerHandler.getAppServiceStatus(pw);
        return svc;
    }

    public ParamsWrapper resetDynamicConatinerDetailInfo(ParamsWrapper pw){
        //get real hostname,containerName,hostip,pod (or container)status,runtimeID(already have)
        return (ParamsWrapper) kubernetesDeployerHandler.getCurrentContainerDynamicInfo(pw);
    }

    public InstanceCore resetPodNameAndStatus(InstanceCore ic){
        ParamsWrapper pw = new ParamsWrapper();
        pw.instanceCore = ic;
        InstanceCore temp =kubernetesDeployerHandler.getPodName(pw);
        ic.setPodName(temp.getPodName());
        ic.setContainerStatus(temp.getContainerStatus());
        ic.setHostIp(temp.getHostIp());
        return ic;
    }
}
