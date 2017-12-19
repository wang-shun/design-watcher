/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 14:13
 */
@Service("deleteService")
public class DeleteService implements IExector {
    private Logger logger = Logger.getLogger(DeleteService.class);

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2SvcMapper")
    protected V2SvcMapper v2SvcMapper;

    private ResultBean result(Integer applicationId, KubernetesClient client){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        rb.setMessage("删除application:"+applicationId+"下的Service");
        KubernetesUtil.closeClient(client);
        return rb;
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
            v2NetworkPolicy.setObjectName(v2Svc.getSvcName()+"-network");
            networkPolicyParameter.setNetwork(v2NetworkPolicy);
            KubernetesUtil.deleteNetwork(networkPolicyParameter, client);
        }catch (Exception e){
            logger.info("删除服务，删除对应的网络失败。可能该服务不存在对应的网络或者集群连接异常"+e.getClass());
        }


    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try {
            ServiceParameter sp = (ServiceParameter)params;
            //通过 application 查询到对应的 服务
            List<V2Svc> svcList = v2SvcMapper.selectAllByApplicationIdNotPage(sp.getService().getApplicationId());
            if (svcList == null || svcList.size() == 0) throw new IndexOutOfBoundsException("该应用下没有对应服务");
            client = KubernetesUtil.getClient(svcList.get(0).getMasterType(), svcList.get(0).getMasterIp(), svcList.get(0).getMasterPort());

            for (V2Svc v2Svc: svcList) {
                String name = v2Svc.getSvcName();
                dbUtil.deleteService(v2Svc.getId());
                KubernetesUtil.deleteSvc(v2Svc.getNamespace(), v2Svc.getSvcName(), (KubernetesClient) client);
                deleteServiceNetwork(v2Svc, (KubernetesClient) client);

            }
            return this.result(sp.getService().getApplicationId(),(KubernetesClient) client);
        }catch (Exception e){
            throw e;
        }

    }
}

