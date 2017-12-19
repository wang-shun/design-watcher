/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import com.alibaba.fastjson.JSON;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.client.KubernetesClient;
import netscape.javascript.JSObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 14:13
 */
@Service("createNetworkPolicy")
public class CreateNetworkPolicy implements IExector {
    private Logger logger = Logger.getLogger(CreateNetworkPolicy.class);

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    private NetworkPolicyParameter getNetworkPolicyParameter(Object params){
        return (NetworkPolicyParameter)params;
    }

    /**
     * result 函数完成两个功能：1、分析执行结果。2、关闭 client
     * 这个函数基本上也都是一样的，也应该在下层实现
     * @param network  这个 Deployment 类是 client 的执行结果类
     * @param client
     * @return
     */
    private ResultBean result(NetworkPolicy network, KubernetesClient client){
        ResultBean rb = new ResultBean();
        if(network !=null){
            //dao to save.
            rb.setSuccess(true);
            rb.setMessage("创建networkpolicy:"+network.getMetadata().getName());
        }
        else{
            rb.setSuccess(false);
            rb.setMessage("创建失败，请稍后重试！");
        }
        KubernetesUtil.closeClient(client);
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        logger.info(JSON.toJSONString(params));
        NetworkPolicyParameter dc = this.getNetworkPolicyParameter(params);
        NetworkPolicy network = KubernetesUtil.createNetworkPolicy(dc,(KubernetesClient) client);
        ResultBean resultBean = dbUtil.insertNetworkPolicy((NetworkPolicyParameter)params);
        logger.info(resultBean.getMessage());
        return this.result(network,(KubernetesClient) client);
    }
}

