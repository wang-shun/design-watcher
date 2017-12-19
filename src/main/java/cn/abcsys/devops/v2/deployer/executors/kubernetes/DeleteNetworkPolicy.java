/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/6 0006 0:03
 */
@Service("deleteNetworkPolicy")
public class DeleteNetworkPolicy implements IExector {
    private Logger logger = Logger.getLogger(DeleteNetworkPolicy.class);

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    private NetworkPolicyParameter getNetworkPolicyParameter(Object params){
        return (NetworkPolicyParameter)params;
    }

    /**
     * result 函数完成两个功能：1、分析执行结果。2、关闭 client
     * 这个函数基本上也都是一样的，也应该在下层实现
     * @param client
     * @return
     */
    private ResultBean result(String name,KubernetesClient client){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        rb.setMessage("删除networkpolicy:"+name);
        KubernetesUtil.closeClient(client);
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) {
        dbUtil.getNetworkPolicy((NetworkPolicyParameter)params);
        NetworkPolicyParameter dc = this.getNetworkPolicyParameter(params);
        String name = dc.getNetwork().getObjectName();
        //获取同名的不同环境下多条记录
        List<V2NetworkPolicy> v2NetworkPolicyList = dbUtil.getNetworkPolicyByName(dc);
        for (V2NetworkPolicy v2NetworkPolicy:v2NetworkPolicyList) {
            dc.setNetwork(v2NetworkPolicy);
            dbUtil.deleteNetworkPolicy(dc);
        }
        KubernetesUtil.deleteNetwork(dc,(KubernetesClient) client);
        return this.result(name,(KubernetesClient) client);
    }
}

