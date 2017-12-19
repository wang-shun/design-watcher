/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.managers.ExecutorManager;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/5 0005 18:26
 */
@Component("kubernetesServiceDeployer")
public class KubernetesServiceDeployer implements IDeployer {

    private static Logger logger = Logger.getLogger(KubernetesServiceDeployer.class);

    @Resource(name="executorManager")
    protected ExecutorManager em;

    @Override
    public ResultBean exec(Object params) {
        ResultBean dbResult = null;
        try {
            V2Svc svc = ((ServiceParameter) params).getService();
            if (((ServiceParameter) params).getDeployType().equals("deleteService") ) {  //如果是删除服务
                dbResult = em.getExecutor(params).handle(params,null);
            }
            else{
                dbResult = em.getExecutor(params).handle(params,KubernetesUtil.getClient(svc));
            }
            return dbResult;
        }catch (IndexOutOfBoundsException e){
            logger.info(e.getMessage());
            return new ResultBean(true, e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.getMessage());
            return new ResultBean(false, e.getMessage());
        }
    }
}
