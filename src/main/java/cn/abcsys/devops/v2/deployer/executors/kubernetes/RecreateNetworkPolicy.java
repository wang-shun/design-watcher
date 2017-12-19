/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 14:13
 */
@Service("recreateNetworkPolicy")
public class RecreateNetworkPolicy implements IExector {
    private Logger logger = Logger.getLogger(RecreateNetworkPolicy.class);

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
            rb.setMessage("成功编辑networkpolicy:"+network.getMetadata().getName());
        }
        else{
            rb.setSuccess(false);
            rb.setMessage("编辑网络失败，请稍后重试！");
        }
        KubernetesUtil.closeClient(client);
        return rb;
    }

    /**
     * 传进来的参数应该是 既有id 也有发布细节
     * 现根据 id，删之前网络，成功后，证明数据库和集群都是可以连接的
     * 再创建新的网络，返回创建结果
     * @param params
     * @param client
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        logger.info(JSON.toJSONString(params));
        // 先删除以前的网络
        // dbUtil.getNetworkPolicy((NetworkPolicyParameter)params); 因为重建的内容和之前的内容除了 端口之外都相同，所以没必要重新获取
        NetworkPolicyParameter dc = this.getNetworkPolicyParameter(params);

        //获取同名的不同环境下多条记录
        List<V2NetworkPolicy> v2NetworkPolicyList = dbUtil.getNetworkPolicyByName(dc);
        for (V2NetworkPolicy v2NetworkPolicy:v2NetworkPolicyList) {
            NetworkPolicyParameter tmp = new NetworkPolicyParameter();
            tmp.setNetwork(v2NetworkPolicy);
            try {
                dbUtil.deleteNetworkPolicy(tmp);
            }catch (Exception e){
                logger.info("删除网络："+((NetworkPolicyParameter)params).getNetwork().getObjectName()+"出现了异常");
                e.printStackTrace();
                NullPointerException exception = new NullPointerException("删除网络出现异常");
                throw exception;
            }
        }
        KubernetesUtil.deleteNetwork(dc,(KubernetesClient) client);

        try {
            // 删除成功后，开始创建新的网络
            // 获取所有的环境变量List

            for (V2NetworkPolicy v2NetworkPolicy:v2NetworkPolicyList) {
                List<Integer> envIdList = new ArrayList<>();
                envIdList.clear();
                envIdList.add(v2NetworkPolicy.getEnvId());
                dc.setEnvIdList(envIdList);
                dc.getNetwork().setId(v2NetworkPolicy.getId());
                logger.info("env:"+JSON.toJSONString(envIdList)+" id:"+JSON.toJSONString(dc.getNetwork().getId()));
                ResultBean resultBean = dbUtil.insertNetworkPolicy(dc);
                logger.info(resultBean.getMessage());
            }
            NetworkPolicy network = KubernetesUtil.createNetworkPolicy(dc,(KubernetesClient) client);
            return this.result(network,(KubernetesClient) client);
        }catch (Exception e){
            logger.info("重新创建网络："+((NetworkPolicyParameter)params).getNetwork().getObjectName()+"出现了异常");
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("重新创建网络出现异常");
            throw exception;
        }
    }
}

