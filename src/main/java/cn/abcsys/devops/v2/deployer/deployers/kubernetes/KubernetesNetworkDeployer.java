/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.managers.ExecutorManager;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/3 0003 17:07
 */
@Component("kubernetesNetworkDeployer")
public class KubernetesNetworkDeployer implements IDeployer {

    private static Logger logger = Logger.getLogger(KubernetesNetworkDeployer.class);

    @Resource(name="executorManager")
    protected ExecutorManager em;

    @Override
    public ResultBean exec(Object params) {
        try {
            ResultBean res =  em.getExecutor(params).handle(params,KubernetesUtil.getClient((NetworkPolicyParameter)params));
            return res;
        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.getMessage());
            ResultBean dbResult = new ResultBean(false,e.getMessage());
            return dbResult;
        }
    }
}
