package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.LoadParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.PortObject;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2LabelsMapper;
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
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:36
 */
@Service("deleteLoad")
public class DeleteLoad implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2LabelsMapper")
    protected V2LabelsMapper v2LabelsMapper;

    @Resource(name = "v2ImageGroupMapper")
    protected V2ImageGroupMapper v2ImageGroupMapper;

    private static Logger logger = Logger.getLogger(DeleteLoad.class);

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


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try {
            V2ImageGroup v2ImageGroup = new V2ImageGroup();
            v2ImageGroup.setApplicationId(((LoadParameter) params).getApplicationId());
            List<V2ImageGroup> v2ImageGroupList = v2ImageGroupMapper.selectByFields(v2ImageGroup, 0, 999);
            if (v2ImageGroupList == null || v2ImageGroupList.size() == 0) throw new Exception("负载绑定的应用在数据库中没有对应的实例记录");


            KubernetesClient kubernetesClient = KubernetesUtil.getClient(v2ImageGroupList.get(0).getMasterType(),
                    v2ImageGroupList.get(0).getMasterIp(), v2ImageGroupList.get(0).getMasterPort());

            List<V2Labels> v2LabelsList = v2LabelsMapper.selectApplicationLabelByApplicationId(((LoadParameter) params).getApplicationId());
            if (v2LabelsList == null || v2LabelsList.size() == 0) throw new Exception(("服务对应的应用没有对应的应用唯一标示的label信息"));
            String applicationRealName = v2LabelsList.get(0).getLabelValue();

            deleteLoadNetwork(v2ImageGroupList.get(0).getNamespace(), applicationRealName, kubernetesClient);
            kubernetesClient.close();
            ResultBean rb = new ResultBean();
            rb.setSuccess(true);
            rb.setMessage("删除负载网络成功");
            return rb;
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage()+"删除负载网络 出现空指针异常，可能是数据库连接错误");
        } catch (Exception e){
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("数据库插入和创建失败"+e.getMessage().getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
