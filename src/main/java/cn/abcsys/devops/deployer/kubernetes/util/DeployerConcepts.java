/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.kubernetes.util;

import cn.abcsys.devops.deployer.model.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.*;
import io.fabric8.openshift.api.model.DoneableProject;
import org.apache.log4j.Logger;
import java.util.UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/6/12 0012
 * say sth.
 */
public class DeployerConcepts {

    private static Logger logger = Logger.getLogger(DeployerConcepts.class);
    private ParamsWrapper paramsWrapper;
    private KubernetesClient client =  null;

    private List<Container> containers = new ArrayList<Container>();

    private Map<String,String> selectorOrLabels = new HashMap<String, String>();

    private List<LocalObjectReference> imagePullSecrets = new ArrayList<LocalObjectReference>();

    private List<Volume> vols = new ArrayList<Volume>();
    private List<VolumeMount> vms = new ArrayList<VolumeMount>();

    private List<EnvVar> envVarList = new ArrayList<EnvVar>();
    private List<ServicePort> ports = new ArrayList<ServicePort>();
    private List<ContainerPort> containerPorts = new ArrayList<ContainerPort>();

    private ReplicationController controller = null;
    private Service service = null;

    private Boolean deleteSvcStatus = null;
    private Boolean deleteRcStatus = null;

    public DeployerConcepts(ParamsWrapper paramsWrapper){
        this.paramsWrapper = paramsWrapper;
        this.createK8sClient();
    }

    private void createK8sClient() {
        logger.info(paramsWrapper.instanceCore.getIp());
        io.fabric8.kubernetes.client.Config config = new io.fabric8.kubernetes.client.ConfigBuilder().
                                                        withMasterUrl("http://"+paramsWrapper.instanceCore.getIp()+":8080").build();
        try{
            client = new DefaultKubernetesClient(config);
        }catch (Exception e){
            logger.error("k8s集群连接错误："+e.getMessage(),e);
        }
    }

    public void closeClient(){
        try{
            client.close();
            client = null;
        }catch ( Exception e){
            logger.error(e.getMessage());
        }
    }

    public ParamsWrapper getParamsWrapper() {
        return paramsWrapper;
    }

    public void setParamsWrapper(ParamsWrapper paramsWrapper) {
        this.paramsWrapper = paramsWrapper;
    }

    public void addOneContainer() {
        Container container = new Container();
        container.setName(this.setNmaeWithAppNameImageVersion());
        container.setImage(paramsWrapper.instanceCore.getInstanceImage());
        container.setImagePullPolicy("IfNotPresent");
        Quantity de1 = new Quantity();
        de1.setAmount(paramsWrapper.getInstanceCore().getInstanceCpu());

        Quantity de2 = new Quantity();
        de2.setAmount(paramsWrapper.getInstanceCore().getInstanceMemory());

        Map<String, Quantity> cpu_mem = new HashMap<String, Quantity>();
        ResourceRequirements rr = new ResourceRequirements();
        cpu_mem.put("cpu", de1);
        cpu_mem.put("memory", de2);
        rr.setLimits(cpu_mem);

        if(paramsWrapper.getInstanceCore().getInstanceRequestCpu() !=null && paramsWrapper.getInstanceCore().getInstanceRequestCpu().length() > 0 &&
                paramsWrapper.getInstanceCore().getInstanceRequestMemory() !=null && paramsWrapper.getInstanceCore().getInstanceRequestMemory().length()>=4){
            Map<String, Quantity> request_cpu_mem = new HashMap<String, Quantity>();
            request_cpu_mem.put("cpu", new Quantity(paramsWrapper.getInstanceCore().getInstanceRequestCpu()));
            request_cpu_mem.put("memory", new Quantity(paramsWrapper.getInstanceCore().getInstanceRequestMemory()));
            rr.setRequests(request_cpu_mem);
        }
        container.setResources(rr);

        this.addEnvVarList();
        if (this.envVarList.size() > 0){
            logger.info(this.envVarList.get(0).getName());
            container.setEnv(this.envVarList);
        }

        this.addContainerVolumesMount();
        if (this.vms.size() > 0){
            logger.info(this.vms.get(0).getMountPath());
            container.setVolumeMounts(this.vms);
        }

        this.addContainerPortsList();
        if(this.containerPorts.size() > 0){
            container.setPorts(this.containerPorts);
        }

        if (this.paramsWrapper.instanceCore.getCmd() != null && this.paramsWrapper.instanceCore.getCmd().length() > 3) {
            List<String> tempList = new ArrayList<String>();
            tempList.add(this.paramsWrapper.instanceCore.getCmd());
            logger.info(this.paramsWrapper.instanceCore.getCmd());
            container.setCommand(tempList);
        }
        containers.add(container);
    }

    private void addContainerVolumesMount(){
        List<VolumeMount> volumeMounts = new ArrayList<VolumeMount>();
        if(paramsWrapper.instanceVolumesList !=null){
            for(InstanceVolumes iv : paramsWrapper.instanceVolumesList){
                VolumeMount volume = new VolumeMount();
                volume.setName(iv.getInstanceVolumesName());
                volume.setMountPath(iv.getInstanceVolumesMountPath());
                volume.setAdditionalProperty("key1","keysssss");
                volumeMounts.add(volume);
                logger.info("container:"+volume.toString());
            }
            this.vms = volumeMounts;
        }
    }

    private void addEnvVarList(){
        if(paramsWrapper.instanceEnvsList !=null){
            for(InstanceEnvs ie : paramsWrapper.instanceEnvsList){
                EnvVar ev = new EnvVar();
                ev.setName(ie.getInstanceEnvsName());
                ev.setValue(ie.getInstanceEnvsValue());
                this.envVarList.add(ev);
            }
        }
    }
    private void addContainerPortsList(){
        if(paramsWrapper.instancePorts !=null){
            for(InstancePorts ip : paramsWrapper.instancePorts){
                ContainerPort cp = new ContainerPort();
                cp.setContainerPort(ip.getInstancePortsPort());
                this.containerPorts.add(cp);
            }
        }
    }


    public void checkNamespace(){
        String value = paramsWrapper.instanceCore.getInstanceNamespace()== null ? "default" : paramsWrapper.instanceCore.getInstanceNamespace();
        this.paramsWrapper.instanceCore.setInstanceNamespace(value);
        //create if not exist.
        List<Namespace> nsList = client.namespaces().list().getItems();
        if(nsList !=null && nsList.size()> 0){
            Boolean ifExist = false;
            for(Namespace ns : nsList){
                if(ns.getMetadata().getName().equals(value)){
                    ifExist = true;
                    break;
                }
            }
            if(!ifExist){
                client.namespaces().createNew()
                        .withApiVersion("v1")
                        .withKind("Namespace")
                        .withNewMetadata()
                        .withName(value)
                        .endMetadata()
                        .done();
            }
        }

        logger.info(client.getNamespace());
    }

    public void setSameSelectorOrLabels(){
        this.selectorOrLabels.put("name",paramsWrapper.instanceCore.getInstanceRename());
        this.selectorOrLabels.put("appName",paramsWrapper.instanceCore.getAppName());
        //this.selectorOrLabels.put("imageNameTag",paramsWrapper.instanceCore.getInstanceImage().replaceAll("\\:","_").replaceAll("/","-"));
    }

    public void setApplicationSelectorOrLabels(){
        //this.selectorOrLabels.put("name",paramsWrapper.instanceCore.getInstanceRename());
        this.selectorOrLabels.put("appName",paramsWrapper.instanceCore.getAppName());
        //this.selectorOrLabels.put("imageNameTag",paramsWrapper.instanceCore.getInstanceImage().replaceAll("\\:","_").replaceAll("/","-"));
    }

    public void setSecret(){
        if(paramsWrapper.instanceCore.getInstanceImagePullSecret() !=null){
            LocalObjectReference lof = new LocalObjectReference();
            lof.setName(paramsWrapper.instanceCore.getInstanceImagePullSecret());
            this.imagePullSecrets.add(lof);
        }

        //logger.info(this.imagePullSecrets.toString());
    }

    public void setVols(){
        if(paramsWrapper.instanceVolumesList !=null){
            for(InstanceVolumes iv : paramsWrapper.instanceVolumesList){
                Volume vol = new Volume();
                if(iv.getInstanceVolumesType()!=null && iv.getInstanceVolumesType().equals("local")){
                    HostPathVolumeSource hp = new HostPathVolumeSource();
                    hp.setPath(iv.getInstanceVolumesPath());
                    vol.setName(iv.getInstanceVolumesName());
                    vol.setHostPath(hp);
                    vols.add(vol);
                    logger.info("rc:"+vol.toString());
                }
            }
        }
    }

    public void setPorts(){
        for(InstancePorts ip:paramsWrapper.instancePorts){
            ServicePort sp = new ServicePort();
            sp.setPort(ip.getInstancePortsPort());
            sp.setName("port"+ip.getInstancePortsPort());
            //sp.setNodePort(ip.getInstancePortsNodePort());
            ports.add(sp);

        }
    }

    public ReplicationController getController() {
        return controller;
    }

    private String setNmaeWithAppNameImageVersion(){
        StringBuilder sb = new StringBuilder("");
        if(this.paramsWrapper.instanceCore.getApplicationRuntimeId()!=null){
            sb.append("env");
            sb.append(this.paramsWrapper.instanceCore.getApplicationRuntimeId());
        }
        String res = this.paramsWrapper.instanceCore.getAppName().split("-kubernetes-")[0];
        sb.append(res);
        if(this.paramsWrapper.instanceCore.getInstanceProerties() !=null){
            sb.append(this.paramsWrapper.instanceCore.getInstanceProerties());
        }

        logger.info("container:"+sb.toString());
        return sb.toString();
    }

    public void setController() {
        logger.info(this.paramsWrapper.instanceCore.getInstanceRename());
        logger.info(this.paramsWrapper.instanceCore.getInstanceNamespace());
        this.controller = client.replicationControllers()
                .createNew()
                .withApiVersion("v1")
                .withKind("ReplicationController")
                .withNewMetadata()
                .withName(this.paramsWrapper.instanceCore.getInstanceName()+"-"+getUUID())
                .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withSelector(this.selectorOrLabels)
                .withNewTemplate()
                .withNewMetadata()
                .withName("pod-"+this.paramsWrapper.instanceCore.getInstanceRename())
                .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                .withLabels(this.selectorOrLabels)
                .endMetadata()
                .withNewSpec()
                .withContainers(this.containers)
                .withImagePullSecrets(this.imagePullSecrets)
                .withVolumes(this.vols)
                .endSpec()
                .endTemplate()
                .endSpec()
                .done();
    }

    public Service getService() {
        return service;
    }

    public void setService() {
        if(this.paramsWrapper.getClusterIp() !=null){
            this.service = client.services().createNew()
                    .withApiVersion("v1")
                    .withKind("Service")
                    .withNewMetadata()
                    .withName("svc-"+this.paramsWrapper.instanceCore.getInstanceRename())
                    .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .endMetadata()
                    .withNewSpec()
                    .withClusterIP(this.paramsWrapper.getClusterIp())
                    .withType("NodePort")
                    .withPorts(ports)
                    .withSelector(this.selectorOrLabels)
                    .endSpec()
                    .done();
        }
        else{
            this.service = client.services().createNew()
                    .withApiVersion("v1")
                    .withKind("Service")
                    .withNewMetadata()
                    .withName("svc-"+this.paramsWrapper.instanceCore.getInstanceRename())
                    .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .endMetadata()
                    .withNewSpec()
                    .withType("NodePort")
                    .withPorts(ports)
                    .withSelector(this.selectorOrLabels)
                    .endSpec()
                    .done();
        }
    }

    public void setAppService() {
        if(this.paramsWrapper.getClusterIp() !=null){
            this.service = client.services().createNew()
                    .withApiVersion("v1")
                    .withKind("Service")
                    .withNewMetadata()
                    .withName("appsvc-"+this.paramsWrapper.instanceCore.getAppName())
                    .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .endMetadata()
                    .withNewSpec()
                    .withClusterIP(this.paramsWrapper.getClusterIp())
                    .withType("NodePort")
                    .withPorts(ports)
                    .withSelector(this.selectorOrLabels)
                    .endSpec()
                    .done();
        }
        else{
            this.service = client.services().createNew()
                    .withApiVersion("v1")
                    .withKind("Service")
                    .withNewMetadata()
                    .withName("appsvc-"+this.paramsWrapper.instanceCore.getAppName())
                    .withNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .endMetadata()
                    .withNewSpec()
                    .withType("NodePort")
                    .withPorts(ports)
                    .withSelector(this.selectorOrLabels)
                    .endSpec()
                    .done();
        }
    }

    public void deleteService(){
        this.deleteSvcStatus = client.services().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                                .withName("svc-"+this.paramsWrapper.instanceCore.getInstanceRename())
                                .delete();
    }

    public void deleteAppService(){
        logger.info(this.paramsWrapper.instanceCore.getAppName());
        this.deleteSvcStatus = client.services().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                .withName("appsvc-"+this.paramsWrapper.instanceCore.getAppName())
                .delete();
    }

    public void deleteRc(){
        this.deleteRcStatus =client.replicationControllers().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                                              .withLabel("name="+paramsWrapper.instanceCore.getInstanceRename())
                                              .delete();
        logger.info("rcDeleteStatus:"+this.deleteRcStatus);
    }

    public Boolean getDeleteSvcStatus() {
        return deleteSvcStatus;
    }

    public Boolean getDeleteRcStatus() {
        return deleteRcStatus;
    }

    public void getControllerEvents(){
        EventList el = client.events().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace()).list();
        //logger.info(el.getItems().toString());
    }

    public void resetDynamicConatinerDetailInfo(){
        PodList pl=  client.pods().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace()).
                withLabel("name="+paramsWrapper.instanceCore.getInstanceRename()).list();
        if(pl == null ||
                pl !=null && pl.getItems()==null ||
                pl !=null && pl.getItems()!=null && pl.getItems().size()<=0 ){
        }
        else{
            Integer restartCount = null;
            String userName = this.paramsWrapper.instanceCore.getInstanceNamespace();
            String containerName = null;
            String podName = null;
            String uid = null;
            Pod po = pl.getItems().get(0);
            if(po.getStatus() != null &&
                    po.getStatus().getContainerStatuses() !=null &&
                    po.getStatus().getContainerStatuses().size() >0){
//                logger.info(po.getStatus().getContainerStatuses().get(0).getLastState().toString());
//                logger.info(po.getStatus().getContainerStatuses().get(0).getName());
//                logger.info(po.getStatus().getHostIP());
//                logger.info(po.toString());
                restartCount = po.getStatus().getContainerStatuses().get(0).getRestartCount();
                if(po.getStatus().getContainerStatuses().get(0).getState().getRunning() !=null
                        && po.getStatus().getContainerStatuses().size() >0 && po.getStatus().getContainerStatuses().get(0).getReady()){
                    paramsWrapper.instanceCore.setContainerStatus("容器正在运行");
                }
                else if(po.getStatus().getContainerStatuses().get(0).getState().getTerminated() !=null){
                    paramsWrapper.instanceCore.setContainerStatus("容器已经终止："+po.getStatus().getContainerStatuses().get(0).getState().getTerminated().getReason());
                }
                else if(po.getStatus().getContainerStatuses().get(0).getState().getWaiting() !=null){
                    paramsWrapper.instanceCore.setContainerStatus("容器准备运行或停止："+po.getStatus().getContainerStatuses().get(0).getState().getWaiting().getReason());
                }
                else{
                    paramsWrapper.instanceCore.setContainerStatus("容器未运行");
                }
                containerName = po.getStatus().getContainerStatuses().get(0).getName();
                String[] restulfPath = po.getMetadata().getSelfLink().split("/");
                podName = restulfPath[restulfPath.length-1];
                uid = po.getMetadata().getUid();
                //paramsWrapper.instanceCore.setContainerName();
                paramsWrapper.instanceCore.setHostIp(po.getStatus().getHostIP());

            }
            if(po.getSpec() !=null &&
                    po.getSpec().getContainers() !=null &&
                    po.getSpec().getContainers().get(0) !=null){
                paramsWrapper.instanceCore.setHostName(po.getSpec().getNodeName());
            }
            if(containerName !=null && podName!=null && userName !=null && uid !=null && restartCount !=null ){
                paramsWrapper.instanceCore.setContainerName("k8s_"+
                        containerName+"_"+
                        podName+"_"+
                        userName+"_"+
                        uid+"_"+
                        restartCount);
            }
        }
    }

    public InstanceCore getPodName(){
        InstanceCore ic = new InstanceCore();
        PodList pl = null;
        if(paramsWrapper.instanceCore !=null && paramsWrapper.instanceCore.getInstanceNamespace()!=null && paramsWrapper.instanceCore.getInstanceRename()!=null){
           try{
               pl =  client.pods().inNamespace(paramsWrapper.instanceCore.getInstanceNamespace()).withLabel("name="+paramsWrapper.instanceCore.getInstanceRename()).list();
           }catch (Exception e){
               logger.info("K8S获取不到："+paramsWrapper.instanceCore.getInstanceRename());
           }
        }
        if(pl == null ||
                pl !=null && pl.getItems()==null ||
                pl !=null && pl.getItems()!=null && pl.getItems().size()<=0 ){

            return ic;
        }
        Pod po = pl.getItems().get(0);
        ic.setPodName(po.getMetadata().getName());
        if(po.getStatus()!=null){
            ic.setHostIp(po.getStatus().getHostIP());
        }
        if(po.getStatus() != null &&
                po.getStatus().getContainerStatuses() !=null &&
                po.getStatus().getContainerStatuses().size() >0 && po.getStatus().getContainerStatuses().get(0).getReady()){
                ic.setContainerStatus("1");
        }
        else{
            ic.setContainerStatus("0");
        }
        return ic;
    }

    public Object getContainerStatus(){
        PodList pl= null;
        if(paramsWrapper.instanceCore !=null && paramsWrapper.instanceCore.getInstanceNamespace()!=null && paramsWrapper.instanceCore.getInstanceRename()!=null) {
            pl = client.pods().inNamespace(paramsWrapper.instanceCore.getInstanceNamespace()).withLabel("name=" + paramsWrapper.instanceCore.getInstanceRename()).list();
        }
        if(pl == null ||
                pl !=null && pl.getItems()==null ||
                pl !=null && pl.getItems()!=null && pl.getItems().size()<=0 ){

            return null;
        }
        Map<String,Object> resMap = new HashMap<>();
        //resMap.put("podStartTime", pl.getItems().get(0).getStatus().getStartTime());
        resMap.put("success","true");
        resMap.put("message","pod已经成功创建，容器信息请查看容器列表！");
        return resMap;
    }

    public Object getServiceStatus(){
        Map<String,Object> resMap = new HashMap<>();
        try{
            Service svc = client.services().inNamespace(paramsWrapper.instanceCore.getInstanceNamespace()).withName("svc-"+paramsWrapper.instanceCore.getInstanceRename()).get();
            //logger.info(svc.toString());
            if(svc !=null){
                StringBuilder sb = new StringBuilder();
                for(ServicePort sv : svc.getSpec().getPorts()){
                    sb.append(sv.getNodePort()+"-->"+sv.getPort()+";");
                }
                resMap.put("nodePorts",sb.toString());
                resMap.put("success",true);
                return resMap;
            }
        }catch (Exception e){
            resMap.put("message","当前无service！");
            resMap.put("success",false);

        }
        resMap.put("message","当前无service！");
        resMap.put("success",false);
       return resMap;
    }

    public Object getAppServiceStatus(){
        Map<String,Object> resMap = new HashMap<>();
        try{
            Service svc = client.services().inNamespace(paramsWrapper.instanceCore.getInstanceNamespace()).withName("appsvc-"+paramsWrapper.instanceCore.getAppName()).get();
            //logger.info(svc.toString());
            if(svc !=null){
                StringBuilder sb = new StringBuilder();
                for(ServicePort sv : svc.getSpec().getPorts()){
                    sb.append(sv.getNodePort()+"-->"+sv.getPort()+";");
                }
                resMap.put("nodePorts",sb.toString());
                resMap.put("clusterIP",svc.getSpec().getClusterIP());
                resMap.put("success",true);
                return resMap;
            }
        }catch (Exception e){
            resMap.put("success",false);
            resMap.put("message","当前无service！");
        }
        resMap.put("message","当前无service！");
        resMap.put("success",false);
        return resMap;
    }

    public static String getUUID(){
        String res = UUID.randomUUID().toString().replaceAll("-","").toLowerCase().substring(0,5);
        logger.info(res);
        return res;
    }


    public static void main(String[] args) {
//        logger.info( UUID.randomUUID().toString().replaceAll("-","").substring(0,4));
//        System.out.println("k8s_env3333;aaaaaaaaaaaaaaaaaaaa;578_default-20170731033719658-11111111111111111111-nqd9g_default_cdd5dbe3-822f-11e7-8fd7-0017fa00ec2f_0".length());
//        System.out.println("_4ac7bcf4-7bd9-11e7-8fd7-0017fa00ec2f_0".length());
//        System.out.println("k8s_");
        getUUID();
    }

}
