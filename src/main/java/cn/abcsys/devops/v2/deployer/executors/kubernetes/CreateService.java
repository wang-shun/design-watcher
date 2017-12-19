/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2LabelsMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcPortsMapper;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 14:13
 */
@Service("createService")
public class CreateService implements IExector {
    private Logger logger = Logger.getLogger(CreateService.class);

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2SvcMapper")
    protected V2SvcMapper v2SvcMapper;

    @Resource(name = "v2SvcPortsMapper")
    protected V2SvcPortsMapper v2SvcPortsMapper;

    @Resource(name = "v2ImageGroupMapper")
    protected V2ImageGroupMapper v2ImageGroupMapper;

    @Resource(name = "v2LabelsMapper")
    protected V2LabelsMapper v2LabelsMapper;



    private ResultBean result(io.fabric8.kubernetes.api.model.Service service, KubernetesClient client){
        ResultBean rb = new ResultBean();
        if(service !=null){
            //dao to save.
            rb.setSuccess(true);
            rb.setMessage("创建Service:"+service.getMetadata().getName());
            rb.setData(service.getSpec().getClusterIP());
        }
        else{
            rb.setSuccess(false);
            rb.setMessage("创建失败，请稍后重试！");
        }
        KubernetesUtil.closeClient(client);
        return rb;
    }

    /**
     * 是否存在非同应用的同名服务
     * @param svcName
     * @param applicationId
     * @return
     */
    private Boolean isExist(String svcName, Integer applicationId){
        List<V2Svc> svcList = v2SvcMapper.selectAllBySvcNameAndApplicationIdNotPage(svcName, applicationId);
        return svcList != null && svcList.size()>0;
    }


    /**
     * 除去当前应用的服务的所有 nodeport，是否被占用
     * @param applicationId
     * @param nodePort
     * @return
     */
    private Boolean isNodePortUsed(Integer applicationId, Integer nodePort){
        V2Svc svc = v2SvcMapper.selectOneByApplicationId(applicationId);  // 可能 svc 为空
        List<Integer> svcList;
        if (svc == null) svcList = v2SvcPortsMapper.selectAllNodeport();
        else svcList = v2SvcPortsMapper.selectAllByServiceIdExcept(svc.getId()); //可能没有
        if (svcList != null && svcList.size()>0){
            return svcList.contains(nodePort);
        }
        else {
            return false;
        }


    }

    /**
     * 如果该服务没有对应的网络，就略过
     * @param v2Svc
     */
    private void deleteServiceNetwork(V2Svc v2Svc, KubernetesClient client){
        // 获取该服务的对应的网络，名字为 [serviceName]-network
        try {
            NetworkPolicyParameter networkPolicyParameter = new NetworkPolicyParameter();
            V2NetworkPolicy v2NetworkPolicy = new V2NetworkPolicy();
            v2NetworkPolicy.setNamespace(v2Svc.getNamespace());
            // 获取该应用以前的服务的名字
            v2NetworkPolicy.setObjectName(v2Svc.getSvcName()+"-network");
            networkPolicyParameter.setNetwork(v2NetworkPolicy);
            KubernetesUtil.deleteNetwork(networkPolicyParameter, client);
        }catch (Exception e){
            logger.info("删除服务，删除对应的网络失败。可能该服务不存在对应的网络或者集群连接异常"+e.getClass());
        }


    }

    private NetworkPolicyParameter getNetworkParameter(ServiceParameter svcParameter) throws Exception{

        // 获取应用的独特的 realname 的 label
        Integer applicationId = svcParameter.getService().getApplicationId();
        if (applicationId == null || applicationId == 0) throw new Exception("服务参数没有设置应用ID");



        // 获取应用的环境id
        V2ImageGroup v2ImageGroup = new V2ImageGroup();
        v2ImageGroup.setApplicationId(applicationId);
        List<V2ImageGroup> v2ImageGroupList = v2ImageGroupMapper.selectByFields(v2ImageGroup, 0, 999);
        if (v2ImageGroupList == null || v2ImageGroupList.size() == 0) throw new Exception("服务绑定的应用在数据库中没有对应的实例记录");
        List<Integer> envList = new ArrayList<>();
        envList.add(v2ImageGroupList.get(0).getEnvId());

        // 获取端口信息
        List<V2NetworkPorts> v2NetworkPortsList = new ArrayList<>();
        for (V2SvcPorts v2SvcPort: svcParameter.getPorts()) {
            V2NetworkPorts v2NetworkPorts = new V2NetworkPorts();
            v2NetworkPorts.setPortName(v2SvcPort.getPortName());
            v2NetworkPorts.setPortValue(v2SvcPort.getPortValue());
            v2NetworkPorts.setProtocol(v2SvcPort.getProtocol());
            v2NetworkPortsList.add(v2NetworkPorts);
        }

        // 获取标签信息
        List<V2Labels> v2LabelsList = v2LabelsMapper.selectApplicationLabelByApplicationId(applicationId);
        if (v2LabelsList == null || v2LabelsList.size() == 0) throw new Exception(("服务对应的应用没有对应的应用唯一标示的label信息"));
        String applicationRealName = v2LabelsList.get(0).getLabelValue();
        List<V2NetworkLabels> v2NetworkLabelsList = new ArrayList<>();
        V2NetworkLabels v2NetworkLabels = new V2NetworkLabels();
        v2NetworkLabels.setLabelType("specPodSelector");
        v2NetworkLabels.setLabelKey("application-real-name");
        v2NetworkLabels.setLabelValue(applicationRealName);
        v2NetworkLabelsList.add(v2NetworkLabels);

        // 获取网络的基本信息
        V2NetworkPolicy v2NetworkPolicy = new V2NetworkPolicy();
        v2NetworkPolicy.setObjectName(svcParameter.getService().getSvcName()+"-network");
        v2NetworkPolicy.setEnvId(v2ImageGroupList.get(0).getEnvId());
        v2NetworkPolicy.setMasterType(svcParameter.getService().getMasterType());
        v2NetworkPolicy.setMasterPort(svcParameter.getService().getMasterPort());
        v2NetworkPolicy.setMasterIp(svcParameter.getService().getMasterIp());
        v2NetworkPolicy.setApiversion("extensions/v1beta1");
        v2NetworkPolicy.setKind("NetworkPolicy");
        v2NetworkPolicy.setNamespace("default");


        NetworkPolicyParameter dc = new NetworkPolicyParameter();
        dc.setEnvIdList(envList);
        dc.setNetwork(v2NetworkPolicy);
        dc.setPorts(v2NetworkPortsList);
        dc.setLabels(v2NetworkLabelsList);
        return dc;

    }

    /**
     * 根据 应用对应的podlabel类型的标签 键值对相等来判断是否有网络
     * @param applicationId
     * @return
     */
    private Boolean isApplicationBelongToNetwork(Integer applicationId) {
        try {
            if (v2LabelsMapper.selectNetworkLabelsByApplicationId(applicationId).size() > 0) {
                logger.info("application has its network");
                return true;
            }
            else return false;
        }catch (Exception e){
            logger.info("查询该应用是否有网络与之关联时出现异常，略过，判定为没有网络");
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        //先判断插入的服务是否和不同的应用的服务重名
        if (isExist(((ServiceParameter) params).getService().getSvcName(),((ServiceParameter) params).getService().getApplicationId())){
            logger.info("该服务名称已经被其他的应用的服务所使用，应更改服务名称重试");
            throw new Exception("该服务名称已经被其他的应用的服务所使用，应更改服务名称重试");
        }

        // 判断插入的服务 nodePort 是否被占用
        for (int i=0; i<((ServiceParameter) params).getPorts().size(); ++i){
            if (((ServiceParameter) params).getPorts().get(i).getNodePort() != null && ((ServiceParameter) params).getPorts().get(i).getNodePort()>0){
                if (isNodePortUsed(((ServiceParameter) params).getService().getApplicationId(), ((ServiceParameter) params).getPorts().get(i).getNodePort())){
                    //被占用
                    logger.info("该服务的 nodePort 已经被其他的应用服务所使用，应更改 nodePort 的值后重试");
                    throw new Exception("该服务的 nodePort 已经被其他的服务所使用，应更改 nodePort 的值后重试");
                }
            }

        }

        //插入数据库前先删除该应用下的所有的服务和对应的网络
        List<V2Svc> svcList = v2SvcMapper.selectAllByApplicationIdNotPage(((ServiceParameter) params).getService().getApplicationId());
        if (svcList != null && svcList.size() > 0){
            for (V2Svc v2Svc: svcList) {
                String name = v2Svc.getSvcName();
                dbUtil.deleteService(v2Svc.getId());
                KubernetesUtil.deleteSvc(v2Svc.getNamespace(), v2Svc.getSvcName(), (KubernetesClient) client);
                deleteServiceNetwork(v2Svc, (KubernetesClient) client);
            }
        }



        // 插入新的服务
        dbUtil.insertService((ServiceParameter) params, (KubernetesClient)client);
        logger.info("创建服务：数据库插入完成");
        logger.info("client："+client);

        io.fabric8.kubernetes.api.model.Service service = KubernetesUtil.createService((ServiceParameter) params,
                                                                                            (KubernetesClient) client);

        // 回写 clusterIP
        V2Svc currentV2Svc = v2SvcMapper.selectOneByApplicationId(((ServiceParameter) params).getService().getApplicationId());
        currentV2Svc.setClusterIp(service.getSpec().getClusterIP());
        v2SvcMapper.updateByPrimaryKeySelective(currentV2Svc);

        // 如果该应用没有关联网络，那么创建裸服务就好了，否则得创建相应的服务网络
        // 插入该新的服务的网络，此网络不录入数据库，命名为 [servicename]-network，删除时也以此名字进行删除
        // 设置新网络的 label，先找到该应用唯一的 podlabel->application-real-name
        // port 为 service 通过 nodeport 暴露出来的内部端口
        if (((ServiceParameter) params).getService().getSvcType()!=null && ((ServiceParameter) params).getService().getSvcType().equals("NodePort") &&
                isApplicationBelongToNetwork(((ServiceParameter) params).getService().getApplicationId())){
            NetworkPolicyParameter networkPolicyParameter = getNetworkParameter((ServiceParameter) params);
            NetworkPolicy network = KubernetesUtil.createNetworkPolicy(networkPolicyParameter,(KubernetesClient) client);
            if (network == null) throw new Exception("插入服务，创建对应的服务网络出现异常错误");
        }


        return this.result(service,(KubernetesClient) client);
    }
}

