package cn.abcsys.devops.v2.deployer.deployers.openshift;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.db.inerfaces.ILabels;
import cn.abcsys.devops.v2.deployer.db.model.*;
import com.alibaba.fastjson.JSONObject;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.*;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/9/17.
 */
public class OpenshiftUtil {
    private static Logger logger = Logger.getLogger(OpenshiftUtil.class);

    public static KubernetesClient getKubernetesClient(String type,String host,String port) {
        String master = type+"://"+host+":"+port;
        try {
            io.fabric8.kubernetes.client.Config config = new io.fabric8.kubernetes.client.ConfigBuilder().withMasterUrl(master).build();
            config.setTrustCerts(true);

            config.setOauthToken("rqHrxjKOtuWW78-JZgyoVl3N9Pf4TOmp7xxn08bextY");
            return new DefaultOpenShiftClient(config);
        } catch (Exception e) {
            logger.error("获取[" + master + "]连接失败", e);
            return null;
        }
    }


    public static KubernetesClient createK8sClient(String type,String ip,String port) throws NullPointerException{
        KubernetesClient client = null;
        io.fabric8.kubernetes.client.Config config = new io.fabric8.kubernetes.client.ConfigBuilder().
                withMasterUrl(type+"://"+ip+":"+port).build();
        try{
            client = new DefaultKubernetesClient(config);
        }catch (Exception e){
            logger.error("k8s集群连接错误："+type+"://"+ip+":"+port);
            NullPointerException exception = new NullPointerException(e.getMessage()+e.getClass());
            throw exception;
        }
        return client;
    }

    public static KubernetesClient createK8sClient(String type,String ip,String port,String caFile,String clientKeyFile,String clientCaFile) {
        KubernetesClient client = null;
        io.fabric8.kubernetes.client.Config config = new io.fabric8.kubernetes.client.ConfigBuilder().
                withMasterUrl(type+"://"+ip+":"+port).withCaCertFile(caFile).
                withClientKeyFile(clientKeyFile).withClientCertFile(clientCaFile).build();
        try{
            client = new DefaultKubernetesClient(config);
        }catch (Exception e){
            logger.error("k8s集群连接错误："+type+"://"+ip+":"+port+" "+caFile);
        }
        return client;
    }

    public static KubernetesClient getClient(V2Svc svc){
        if(svc.getMasterType().equals("https")){
            logger.info("https认证文件存放路径："+AllInit.baseFilePath);
            return OpenshiftUtil.createK8sClient(svc.getMasterType(),
                    svc.getMasterIp(),svc.getMasterPort(),
                    AllInit.baseFilePath+ File.separator+svc.getMasterIp()+File.separator+"ca.crt",
                    AllInit.baseFilePath+File.separator+svc.getMasterIp()+File.separator+ "apiserver-kubelet-client.key",
                    AllInit.baseFilePath+File.separator+svc.getMasterIp()+File.separator+"apiserver-kubelet-client.crt");
        }
        return OpenshiftUtil.createK8sClient(svc.getMasterType(),
                svc.getMasterIp(),svc.getMasterPort());
    }

    /**
     * getClient 函数根据 dc 的集群 master 地址创建 http 或者 https 连接，返回连接的对象。
     * 这个函数每个 kubernetes 的 excuter 实现类都是相同的，应该可以在下层实现，精简下
     * @param
     * @return
     */
    public static KubernetesClient getClient(String type, String ip, String port){
        if(type.equals("https")){
            logger.info("https认证文件存放路径："+AllInit.baseFilePath);
            return OpenshiftUtil.createK8sClient(type, ip, port,
                    AllInit.baseFilePath+ File.separator+ip+File.separator+"ca.crt",
                    AllInit.baseFilePath+File.separator+ip+File.separator+ "apiserver-kubelet-client.key",
                    AllInit.baseFilePath+File.separator+ip+File.separator+"apiserver-kubelet-client.crt");
        }
        return OpenshiftUtil.createK8sClient(type,
                ip,port);
    }

    /**
     * getClient 函数根据 dc 的集群 master 地址创建 http 或者 https 连接，返回连接的对象。
     * 这个函数每个 kubernetes 的 excuter 实现类都是相同的，应该可以在下层实现，精简下
     * @param dc
     * @return
     */
    public static KubernetesClient getClient(NetworkPolicyParameter dc){
        if(dc.getNetwork().getMasterType().equals("https")){
            logger.info("https认证文件存放路径："+AllInit.baseFilePath);
            return OpenshiftUtil.createK8sClient(dc.getNetwork().getMasterType(),
                    dc.getNetwork().getMasterIp(),dc.getNetwork().getMasterPort(),
                    AllInit.baseFilePath+ File.separator+dc.getNetwork().getMasterIp()+File.separator+"ca.crt",
                    AllInit.baseFilePath+File.separator+dc.getNetwork().getMasterIp()+File.separator+ "apiserver-kubelet-client.key",
                    AllInit.baseFilePath+File.separator+dc.getNetwork().getMasterIp()+File.separator+"apiserver-kubelet-client.crt");
        }
        return OpenshiftUtil.createK8sClient(dc.getNetwork().getMasterType(),
                dc.getNetwork().getMasterIp(),dc.getNetwork().getMasterPort());
    }

    public static KubernetesClient getClient(DeploymentComponent dc) throws NullPointerException,Exception{
        try {
            logger.info(JSONObject.toJSONString(dc));
            String path = AllInit.baseFilePath;
            if(dc.getImageGroup().getMasterType().equals("https")){
                logger.info("创建 k8s client 时，获取证书的参数为："+dc.getImageGroup().getMasterType()+dc.getImageGroup().getMasterIp()+dc.getImageGroup().getMasterPort());
                return OpenshiftUtil.createK8sClient(dc.getImageGroup().getMasterType(),
                        dc.getImageGroup().getMasterIp(),dc.getImageGroup().getMasterPort(),
                        path+ File.separator+dc.getImageGroup().getMasterIp()+File.separator+"ca.crt",
                        path+File.separator+dc.getImageGroup().getMasterIp()+File.separator+ "apiserver-kubelet-client.key",
                        path+File.separator+dc.getImageGroup().getMasterIp()+File.separator+"apiserver-kubelet-client.crt");
            }
            else if(dc.getImageGroup().getMasterType().equals("http")){

                return OpenshiftUtil.createK8sClient(dc.getImageGroup().getMasterType(),
                        dc.getImageGroup().getMasterIp(),dc.getImageGroup().getMasterPort());
            }
            throw new Exception("集群连接协议错误（既不为 https 也不为 http）");
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("DeploymentComponent 参数部分或全为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (Exception e){
            Exception exception = new Exception(e.getMessage()+"连接 kubernetes 集群失败");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }

    public static void closeClient(KubernetesClient client ){
        try{
            client.close();
            client = null;
        }catch ( Exception e){
            logger.error("k8s client端可能已经关闭");
        }
    }

    public static Map<String,String> getLabelsByType(List labels, String type){
        Map<String,String> res = new HashMap<>();
        for(Object label: labels){
                if(label instanceof ILabels){
                    ILabels real = (ILabels)label;
                    if(real.getLabelType().equals(type)){
                        res.put(real.getLabelKey(),real.getLabelValue());
                    }
                }

            }
        return res;
    }

    public static void resetNodeSelector(List labels,String type,String key,String value){
        for(int i = 0;i<labels.size();i++){
            Object temp = labels.get(i);
            if(temp instanceof ILabels){
                ILabels real = (ILabels)temp;
                if(real.getLabelType().equals(type)){
                    System.out.println(real.getLabelKey());
                    labels.remove(real);
                    i--;
                }
            }
        }
        V2Labels temp = new V2Labels();
        temp.setLabelType(type);
        temp.setLabelValue(value);
        temp.setLabelKey(key);
        labels.add(temp);
    }

    public static List<Volume> getImageGroupVolumes(DeploymentComponent dc){
        List<Volume> volumes = new ArrayList<>();
        if (dc.getVolumes() == null) return volumes; //不一定会传入volumes
        for(V2Volumes vvl: dc.getVolumes()){
            Volume vol = new Volume();
            if(vvl.getVolumeType().equals("hostPath")||
                    vvl.getVolumeType().equals("applicationDataPath")||
                    vvl.getVolumeType().equals("configFilePath") ||
                    vvl.getVolumeType().equals("logPath")){
                HostPathVolumeSource hp = new HostPathVolumeSource();
                hp.setPath(vvl.getHostPath());
                vol.setHostPath(hp);
            }
//            else if(vvl.getVolumeType().equals("logPath")){
//                EmptyDirVolumeSource edvs = new EmptyDirVolumeSource();
//                vol.setEmptyDir(edvs);
//            }
            vol.setName(vvl.getVolumeName());
            volumes.add(vol);
        }
        return volumes;
    }

    public static List<VolumeMount> getImageVolumeMounts(ImageComponent ic){
        List<VolumeMount> volumeMounts = new ArrayList<VolumeMount>();
        if (ic.getVolumeMounts() == null) return volumeMounts;
        for(V2VolumeMounts vvm: ic.getVolumeMounts()){
            VolumeMount vmi = new VolumeMount();
            vmi.setName(vvm.getVolumeMountName());
            vmi.setMountPath(vvm.getMountPath());
            volumeMounts.add(vmi);
        }
        return volumeMounts;
    }

    public static List<EnvVar> getImageEnvironmentVariables(ImageComponent ic){
        List<EnvVar> envVarList = new ArrayList<EnvVar>();
        if (ic.getEnvs() == null) return envVarList;
        for(V2Envs ve:ic.getEnvs()){
            EnvVar ev = new EnvVar();
            if(ve.getEnvsKey() != null && ve.getEnvsValue() != null){
                ev.setName(ve.getEnvsKey());
                ev.setValue(ve.getEnvsValue());
                envVarList.add(ev);
            }
        }
        return envVarList;
    }

    public static List<ContainerPort> getImagePorts(ImageComponent ic){
        List<ContainerPort> cpl = new ArrayList<>();
        if (ic.getPorts() == null) return cpl;
        for(V2Ports vp : ic.getPorts()){
            ContainerPort cp = new ContainerPort();
            cp.setContainerPort(vp.getPortValue());
            if(vp.getPortName() != null)
                cp.setName(vp.getPortName());
            if(vp.getProtocol() != null)
                cp.setProtocol(vp.getProtocol());
            cpl.add(cp);
        }
        return cpl;
    }

    public static List<String> getImageArgs(ImageComponent ic){
        List<String> args = new ArrayList<>();
        if (ic.getArgs() == null) return args;
        for(V2Args va : ic.getArgs()){
            args.add(va.getParameter());
        }
        return args;
    }

    public static ResourceRequirements getImageResources(ImageComponent ic){
        //logger.info("创建应用，Resource _end 的设置："+ JSON.toJSONString(ic.getResources()));
        //logger.info("mincpu:"+ic.getResources().getMinCpu()+"maxcpu:"+ic.getResources().getMaxCpu()+"minmem:"+ic.getResources().getMinMem()+"maxmem:"+ic.getResources().getMaxMem());
        ResourceRequirements rr = new ResourceRequirements();
        if (ic.getResources() == null) return rr;
        Quantity de1 = new Quantity();
        de1.setAmount(ic.getResources().getMaxCpu());

        Quantity de2 = new Quantity();
        de2.setAmount(ic.getResources().getMaxMem());

        Map<String, Quantity> cpu_mem_max = new HashMap<String, Quantity>();
        cpu_mem_max.put("cpu",de1);
        cpu_mem_max.put("memory",de2);

        Quantity de3 = new Quantity();
        de3.setAmount(ic.getResources().getMinCpu());

        Quantity de4 = new Quantity();
        de4.setAmount(ic.getResources().getMinMem());

        Map<String, Quantity> cpu_mem_min = new HashMap<String, Quantity>();
        cpu_mem_min.put("cpu",de3);  // fix 了 bug
        cpu_mem_min.put("memory",de4);

        if(ic.getResources().getMaxCpu() != null && ic.getResources().getMaxMem() != null)
            rr.setLimits(cpu_mem_max);
        if(ic.getResources().getMinCpu() != null && ic.getResources().getMinMem() != null)
            rr.setRequests(cpu_mem_min);

        return rr;
    }

    public static Probe getProbeByType(ImageComponent ic,String type){
        if (ic.getProbes() == null){
            return null;
        }
        Probe probe = null;
        for(V2Probe temp : ic.getProbes()){
            if(temp.getProbeType().equals(type)){
                probe = new Probe();
                HTTPGetAction hga = new HTTPGetAction();
                hga.setPath(temp.getProbePath());
                hga.setHost("127.0.0.1");
                IntOrString ios = new IntOrString(temp.getProbePort());
                hga.setPort(ios);
                hga.setScheme(temp.getScheme());
                probe.setHttpGet(hga);
                probe.setInitialDelaySeconds(temp.getInitialDelaySeconds());
                probe.setTimeoutSeconds(temp.getTimeoutSeconds());
                probe.setSuccessThreshold(temp.getSuccessThreshold());
                probe.setFailureThreshold(temp.getFailureThreshold());
            }
        }
        return probe;
    }

    public static List<Container> getContainers(DeploymentComponent dc){
        List<Container> res = new ArrayList<>();
        for(ImageComponent ic : dc.getImages()){
            Container temp = new Container();
            temp.setName(ic.getImage().getContainerName());
            temp.setImage(ic.getImage().getImageName()+":"+ic.getImage().getImageTag());
            temp.setImagePullPolicy("IfNotPresent");
            temp.setResources(getImageResources(ic));
            temp.setArgs(getImageArgs(ic));
            temp.setEnv(getImageEnvironmentVariables(ic));
            temp.setVolumeMounts(getImageVolumeMounts(ic));
            temp.setPorts(getImagePorts(ic));
            temp.setLivenessProbe(getProbeByType(ic,"livenessProbe"));
            temp.setReadinessProbe(getProbeByType(ic,"readnessProbe"));
            res.add(temp);
        }
        return res;
    }

    /**
     * 给主机打 label
     * @param dc
     * @param client
     */
    public static void editNodeLabel(DeploymentComponent dc,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }

        Map<String,String> nodeLabels =  OpenshiftUtil.getLabelsByType(dc.getLabels(),"nodeSelectors");
        if(nodeLabels !=null && nodeLabels.size() > 0){
            String labelKey = "version-id-"+dc.getImageGroup().getVersionId();
            String labelValue = "version-id-"+dc.getImageGroup().getVersionId();
            for (String key : nodeLabels.keySet()) {
                Map<String,String> temp = new HashMap<>();
                temp.put(key,nodeLabels.get(key));
                client.nodes().withName(key).cascading(false).edit().
                        editMetadata().
                            addToLabels(labelKey,labelValue).
                        endMetadata().
                        done();
            }
            resetNodeSelector(dc.getLabels(),"nodeSelectors",labelKey,labelValue);
        }
    }

    /**
     * 创建k8s deployment
     * @param client
     * @param dc
     * @return
     * @author xh
     * @date 2017年3月17日
     */
    public static Deployment createDeployment(DeploymentComponent dc, KubernetesClient client) {
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        Deployment createDeployment = null;
        try {
            createDeployment = client.extensions().deployments().createNew()
                    .withApiVersion(dc.getImageGroup().getApiVersion())
                    .withKind(dc.getImageGroup().getKind())
                    .withNewMetadata()
                    .withName(dc.getImageGroup().getRealName())
                    .withNamespace(dc.getImageGroup().getNamespace())
                    .withLabels(getLabelsByType(dc.getLabels(),"imageGroupLabels"))
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(dc.getImageGroup().getReplica())
                    .withNewSelector()
                    .withMatchLabels(getLabelsByType(dc.getLabels(),"imageGroupSelectors"))
                    .endSelector()
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(getLabelsByType(dc.getLabels(),"podLabels"))
                    .endMetadata()
                    .withNewSpec()
                    .withVolumes(getImageGroupVolumes(dc))
                    .withNodeSelector(getLabelsByType(dc.getLabels(),"nodeSelectors"))
                    .withContainers(getContainers(dc))
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .done();
            return createDeployment;
        } catch (KubernetesClientException e) {
            logger.error("deployment name [" + dc.getImageGroup().getImageGroupName() + "]创建失败: ", e);
            return createDeployment;
        } catch (Exception e) {
            logger.error("deployment name [" + dc.getImageGroup().getImageGroupName() + "]创建失败: ", e);
            return createDeployment;
        }

    }



    public static List<NetworkPolicyPeer> getNetworkPolicyFrom(NetworkPolicyParameter npp){
        List<NetworkPolicyPeer> res = new ArrayList<>();

        NetworkPolicyPeer from = new NetworkPolicyPeer();
        LabelSelector pod = new LabelSelector();
        pod.setMatchLabels(getLabelsByType(npp.getLabels(),"ingressPodSelector"));

        LabelSelector ns = new LabelSelector();
        ns.setMatchLabels(getLabelsByType(npp.getLabels(),"ingressNamespaceSelector"));
        from.setPodSelector(pod);
        //from.setNamespaceSelector(ns);   // 不能同时既有 podselector 又有 namespaceselector，两者只能取其一。否则会报错

        res.add(from);
        return res;
    }

    public static List<NetworkPolicyPort> getNetworkPolicyPort(NetworkPolicyParameter npp){
        List<NetworkPolicyPort> res = new ArrayList<>();
        for(V2NetworkPorts port: npp.getPorts()){
            NetworkPolicyPort ePort = new NetworkPolicyPort();
            IntOrString ios = new IntOrString(port.getPortValue());
            ePort.setPort(ios);
            ePort.setProtocol(port.getProtocol());
            res.add(ePort);
        }
        return res;
    }

    public static NetworkPolicy createNetworkPolicy(NetworkPolicyParameter npp,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        return client.extensions().networkPolicies().createNew()
                                                    .withApiVersion(npp.getNetwork().getApiversion())
                                                    .withKind(npp.getNetwork().getKind())
                                                    .withNewMetadata()
                                                        .withName(npp.getNetwork().getObjectName())
                                                        .withNamespace(npp.getNetwork().getNamespace())
                                                        .withLabels(getLabelsByType(npp.getLabels(),"networkPolicyLabels"))
                                                    .endMetadata()
                                                    .withNewSpec()
                                                        .withNewPodSelector()
                                                            .withMatchLabels(getLabelsByType(npp.getLabels(),"specPodSelector"))
                                                        .endPodSelector()
                                                        .addNewIngress()
                                                            .withFrom(getNetworkPolicyFrom(npp))
                                                            .withPorts(getNetworkPolicyPort(npp))
                                                        .endIngress()
                                                    .endSpec()
                                                    .done();
    }

    public static List<ServicePort> getServicePorts(ServiceParameter sp){
        List<ServicePort> spList = new ArrayList<>();
        for(V2SvcPorts port: sp.getPorts()){
            ServicePort service = new ServicePort();
            service.setName(port.getPortName());
            service.setNodePort(port.getNodePort());
            service.setPort(port.getPortValue());
            IntOrString ios = new IntOrString(port.getTargetPort());
            service.setTargetPort(ios);
            spList.add(service);
        }
        return spList;
    }

    public static Service createService(ServiceParameter sp,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        return client.services().createNew()
                                .withApiVersion(sp.getService().getApiversion())
                                .withKind(sp.getService().getKind())
                                .withNewMetadata()
                                    .withName(sp.getService().getSvcName())
                                    .withNamespace(sp.getService().getNamespace())
                                    .withLabels(getLabelsByType(sp.getLabels(),"serviceLabels"))
                                .endMetadata()
                                .withNewSpec()
                                    .withType(sp.getService().getSvcType())
                                    .withClusterIP(sp.getService().getClusterIp())
                                    .withPorts(getServicePorts(sp))
                                    .withSelector(getLabelsByType(sp.getLabels(),"serviceSelector"))
                                .endSpec()
                                .done();
    }

    /**
     * 根据 deployment 的真实名字来直接删除 deployment
     * @param dc
     * @param client
     */
    public static void deleteDeployment(DeploymentComponent dc,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        try {
            client.extensions().deployments().inNamespace(dc.getImageGroup().getNamespace())
                    .withLabel("image-group-name="+dc.getImageGroup().getRealName())
                    .delete();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            client.replicationControllers().inNamespace(dc.getImageGroup().getNamespace())
                    .withLabel("name="+dc.getImageGroup().getRealName())
                    .delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("已经发起删除deployment的请求（用于灰度发布，缩容，回滚升级）:"+dc.getImageGroup().getRealName());
    }

    public static void deleteDeployment(String namespace, String realname,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        try {
            client.extensions().deployments().inNamespace(namespace)
                    .withLabel("image-group-name="+realname)
                    .delete();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            client.replicationControllers().inNamespace(namespace)
                    .withLabel("name="+realname)
                    .delete();
        }catch (Exception e){
            e.printStackTrace();
        }

        logger.info("已经发起删除deployment的请求（用于删除应用或者版本）:"+realname);
    }

    public static void deleteNetwork(NetworkPolicyParameter npp,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        client.extensions().networkPolicies().inNamespace(npp.getNetwork().getNamespace())
                .withName(npp.getNetwork().getObjectName())
                .delete();
        logger.info("已经发起删除network-policy的请求（用于灰度发布，缩容，回滚升级）:"+npp.getNetwork().getObjectName());
    }

    public static void deleteSvc(ServiceParameter sp,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        client.services().inNamespace(sp.getService().getNamespace())
                .withLabel("service-name="+sp.getService().getSvcName())
                .delete();
        logger.info("已经发起删除Service的请求（用于灰度发布，缩容，回滚升级）:"+sp.getService().getSvcName());
    }

    public static void deleteSvc(String namespace, String serviceName, KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        client.services().inNamespace(namespace)
                .withLabel("service-name="+serviceName)
                .delete();
        logger.info("已经发起删除Service的请求（用于灰度发布，缩容，回滚升级）:"+serviceName);
    }

    public static String getSvcClusterIp(V2Svc sp,KubernetesClient client){
        if(client == null){
            throw new NullPointerException("k8s集群连接失败！");
        }
        logger.info("查询服务的CluesterIp:"+sp.getSvcName());
        try {
            return client.services().inNamespace(sp.getNamespace()).withName(sp.getSvcName()).get().getSpec().getClusterIP();

        }catch (NullPointerException e){
            logger.info("k8s 集群没有对应的service资源");
            return "";
        }
    }

    public static void getContainerName(V2Container container,KubernetesClient client){
        if(client ==null || container == null)
            return;
        if(container.getPodName() != null && container.getPodName().length() > 1){
            Pod po =  client.pods().inNamespace("default").withName(container.getPodName()).get();
            if(po != null && po.getStatus() != null && po.getStatus().getContainerStatuses() !=null && po.getStatus().getContainerStatuses().size() >0 &&
                    po.getSpec() != null && po.getSpec().getContainers() !=null && po.getSpec().getContainers().size() > 0 &&
                    po.getStatus().getContainerStatuses().size() == po.getSpec().getContainers().size() ){
                Integer restartCount = null;
                String userName = "default";
                String containerName = null;
                String podName = null;
                String uid = null;
                for(int i = 0 ;i< po.getStatus().getContainerStatuses().size();i++){
                    if(po.getSpec().getContainers().get(i).getName().equals(container.getRealName())){
                        restartCount = po.getStatus().getContainerStatuses().get(i).getRestartCount();
                        containerName = po.getStatus().getContainerStatuses().get(i).getName();
                        String[] restulfPath = po.getMetadata().getSelfLink().split("/");
                        podName = restulfPath[restulfPath.length-1];
                        uid = po.getMetadata().getUid();
                        container.setHostIp(po.getStatus().getHostIP());
                        container.setHostName(po.getSpec().getNodeName());
                        if(containerName !=null && podName!=null && userName !=null && uid !=null && restartCount !=null ){
                            container.setRealName("k8s_"+
                                    containerName+"_"+
                                    podName+"_"+
                                    userName+"_"+
                                    uid+"_"+
                                    restartCount);
                        }
                    }
                }
            }
        }
    }

    public static String getContainerName(Pod po,String realName) {
        if (po != null && po.getStatus() != null && po.getStatus().getContainerStatuses() != null && po.getStatus().getContainerStatuses().size() > 0 &&
                po.getSpec() != null && po.getSpec().getContainers() != null && po.getSpec().getContainers().size() > 0 &&
                po.getStatus().getContainerStatuses().size() == po.getSpec().getContainers().size()) {
            Integer restartCount = null;
            String userName = "default";
            String containerName = null;
            String podName = null;
            String uid = null;
            for (int i = 0; i < po.getStatus().getContainerStatuses().size(); i++) {
                if (po.getSpec().getContainers().get(i).getName().equals(realName)) {
                    restartCount = po.getStatus().getContainerStatuses().get(i).getRestartCount();
                    containerName = po.getStatus().getContainerStatuses().get(i).getName();
                    String[] restulfPath = po.getMetadata().getSelfLink().split("/");
                    podName = restulfPath[restulfPath.length - 1];
                    uid = po.getMetadata().getUid();
                    if (containerName != null && podName != null && uid != null && restartCount != null) {
                        return "k8s_" +
                                containerName + "_" +
                                podName + "_" +
                                userName + "_" +
                                uid + "_" +
                                restartCount;
                    }
                }
            }
        }
        return null;
    }

    public static Boolean editOldRcPod(V2ImageGroup ig,List<V2Labels> labels,V2Container container,V2Pod pod){
        KubernetesClient client = getClient(ig.getMasterType(),ig.getMasterIp(),ig.getMasterPort());
        Map<String,String> kvs = new HashMap<>();
        String podName = null;
        String containerConfigName = null;
        String containerRealName = null;
        String hostIp = null ;
        String hostName = null;
        String containerStatus = null;
        for(V2Labels temp : labels){
            kvs.put(temp.getLabelKey(),temp.getLabelValue());
        }
        if(client == null)
            return false;
        try {
            ReplicationController oldRc = client.replicationControllers().inNamespace(ig.getNamespace())
                    .withLabel("name="+ig.getRealName()).list().getItems().get(0);
            Pod oldPod = client.pods().inNamespace(ig.getNamespace())
                    .withLabel("name="+ig.getRealName()).list().getItems().get(0);
            if(oldRc != null && oldPod != null){
                podName = oldPod.getMetadata().getName();

                if ( oldPod.getStatus() != null && oldPod.getStatus().getContainerStatuses() != null && oldPod.getStatus().getContainerStatuses().size() > 0 &&
                        oldPod.getSpec() != null && oldPod.getSpec().getContainers() != null && oldPod.getSpec().getContainers().size() > 0 &&
                        oldPod.getStatus().getContainerStatuses().size() == oldPod.getSpec().getContainers().size()) {
                    containerConfigName = oldPod.getStatus().getContainerStatuses().get(0).getName();
                    containerRealName = getContainerName(oldPod,containerConfigName);
                    hostIp = oldPod.getStatus().getHostIP();
                    hostName = oldPod.getSpec().getNodeName();
                    containerStatus = oldPod.getStatus().getContainerStatuses().get(0).getReady().toString();
                }
                container.setContainerName(containerRealName);
                container.setRealName(containerConfigName);
                container.setPodName(podName);
                container.setHostName(hostName);
                container.setHostIp(hostIp);
                container.setStatus(containerStatus);
                pod.setRealName(podName);
                client.replicationControllers().inNamespace(ig.getNamespace()).withName(oldRc.getMetadata().getName()).cascading(false).edit().
                        editMetadata().
                        addToLabels(kvs).
                        endMetadata()
                        .editSpec()
                        .editTemplate().
                        editMetadata()
                        .addToLabels(kvs)
                        .endMetadata()
                        .endTemplate()
                        .endSpec().
                        done();
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }
}
