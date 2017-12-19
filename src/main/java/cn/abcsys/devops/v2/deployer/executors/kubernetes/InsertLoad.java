package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2EnvsMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2LabelsMapper;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import com.alibaba.fastjson.JSON;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:36
 */
@Service("insertLoad")
public class InsertLoad implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2LabelsMapper")
    protected V2LabelsMapper v2LabelsMapper;

    @Resource(name = "v2ImageGroupMapper")
    protected V2ImageGroupMapper v2ImageGroupMapper;

    private static Logger logger = Logger.getLogger(InsertLoad.class);

    /**
     *
     * @param nameSpace
     * @param applicationRealName
     * @param client
     */
    private void deleteLoadNetwork(String nameSpace, String applicationRealName, KubernetesClient client){
        // 获取该服务的对应的网络，名字为 [serviceName]-network
        try {
            NetworkPolicyParameter networkPolicyParameter = new NetworkPolicyParameter();
            V2NetworkPolicy v2NetworkPolicy = new V2NetworkPolicy();
            v2NetworkPolicy.setNamespace(nameSpace);
            // 获取该应用以前的服务的名字
            v2NetworkPolicy.setObjectName(applicationRealName+"-load-network");
            networkPolicyParameter.setNetwork(v2NetworkPolicy);
            KubernetesUtil.deleteNetwork(networkPolicyParameter, client);
        }catch (Exception e){
            logger.info("删除负载，删除对应的网络失败。可能该服务不存在对应的网络或者集群连接异常"+e.getClass());
        }


    }

    private NetworkPolicyParameter getNetworkParameter(LoadParameter loadParameter, KubernetesClient client) throws Exception{

        // 获取应用的独特的 realname 的 label
        Integer applicationId = loadParameter.getApplicationId();
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
        for (PortObject port: loadParameter.getPortList()) {
            V2NetworkPorts v2NetworkPorts = new V2NetworkPorts();
            v2NetworkPorts.setPortName(port.getPortName());
            v2NetworkPorts.setPortValue(port.getPortValue());
            v2NetworkPorts.setProtocol(port.getProtocol());
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
        v2NetworkPolicy.setObjectName(applicationRealName+"-load-network");   // 负载网络的名字叫  applicationRealName-load-network
        v2NetworkPolicy.setEnvId(v2ImageGroupList.get(0).getEnvId());
        v2NetworkPolicy.setMasterType(v2ImageGroupList.get(0).getMasterType());
        v2NetworkPolicy.setMasterPort(v2ImageGroupList.get(0).getMasterPort());
        v2NetworkPolicy.setMasterIp(v2ImageGroupList.get(0).getMasterIp());
        v2NetworkPolicy.setApiversion("extensions/v1beta1");
        v2NetworkPolicy.setKind("NetworkPolicy");
        v2NetworkPolicy.setNamespace("default");

        //删除以前的负载网络
        deleteLoadNetwork(v2ImageGroupList.get(0).getNamespace(), applicationRealName, client);


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
            if (v2LabelsMapper.selectNetworkLabelsByApplicationId(applicationId).size() > 0) return true;
            else return false;
        }catch (Exception e){
            logger.info("查询该应用是否有网络与之关联时出现异常，略过，判定为没有网络");
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try {
            // 如果该应用不与已有的网络关联，那么就不创建负载网络，避免造成意外隔离
            if (!isApplicationBelongToNetwork(((LoadParameter) params).getApplicationId())){
                ResultBean rb = new ResultBean();
                rb.setSuccess(true);
                rb.setMessage("该应用没有网络与之关联，故不用创建对应的负载网络");
                return rb;
            }

            V2ImageGroup v2ImageGroup = new V2ImageGroup();
            v2ImageGroup.setApplicationId(((LoadParameter) params).getApplicationId());
            List<V2ImageGroup> v2ImageGroupList = v2ImageGroupMapper.selectByFields(v2ImageGroup, 0, 999);
            if (v2ImageGroupList == null || v2ImageGroupList.size() == 0) throw new Exception("服务绑定的应用在数据库中没有对应的实例记录");


            KubernetesClient kubernetesClient = KubernetesUtil.getClient(v2ImageGroupList.get(0).getMasterType(),
                    v2ImageGroupList.get(0).getMasterIp(), v2ImageGroupList.get(0).getMasterPort());

            NetworkPolicyParameter networkPolicyParameter = getNetworkParameter(((LoadParameter) params), kubernetesClient);
            NetworkPolicy network = KubernetesUtil.createNetworkPolicy(networkPolicyParameter,kubernetesClient);
            if (network == null) throw new Exception("插入负载，创建对应的服务网络出现异常错误");
            kubernetesClient.close();
            ResultBean rb = new ResultBean();
            rb.setSuccess(true);
            rb.setMessage("插入负载网络成功");
            return rb;
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage()+"插入负载网络 出现空指针异常，可能是数据库连接错误");
        } catch (Exception e){
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("数据库插入和创建失败"+e.getMessage().getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
