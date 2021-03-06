/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.EnvParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.managers.ExecutorManager;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

/**
 * 对一个大类的任务进行异常的捕获和处理。然后完成该大类的所有操作共有的部分（从相同的参数中获取集群客户端）
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 11:23
 */
@Component("kubernetes-env")
public class KubernetesEnvDeployer implements IDeployer {

    private static Logger logger = Logger.getLogger(KubernetesEnvDeployer.class);

    @Resource(name="executorManager")
    protected ExecutorManager em;

    @Override
    public ResultBean exec(Object params){
        try {
            logger.info(JSONObject.toJSONString(params));
            ResultBean res= em.getExecutor(params).handle(params,null);
            return res;
        }catch (Exception e){
            //throw e;
            ResultBean dbResult = new ResultBean(false,e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
            return dbResult;
        }
    }
}
