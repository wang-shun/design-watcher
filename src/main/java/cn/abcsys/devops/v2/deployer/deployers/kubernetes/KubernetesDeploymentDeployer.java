/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
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
@Component("kubernetesDeploymentDeployer")
public class KubernetesDeploymentDeployer implements IDeployer {

    private static Logger logger = Logger.getLogger(KubernetesDeploymentDeployer.class);

    @Resource(name="executorManager")
    protected ExecutorManager em;

    @Override
    public ResultBean exec(Object params){
        ResultBean res = null;
        try {
            logger.info(JSONObject.toJSONString(params));
            DeploymentComponent dc = ((ImageGroupParameter)params).getDeployment();
            //如果是扩容缩容，这里就是空。我认为其实 Client 的获取可以直接放在 executor 层去做，除非应用下的多个操作获取 Client 的方式完全相同
            if(dc !=null){
                  res = em.getExecutor(params).handle(params,KubernetesUtil.getClient(dc));
            }
            else{
                res = em.getExecutor(params).handle(params,null);
            }
            return res;
        }catch (IndexOutOfBoundsException e){
            //throw e;
            ResultBean dbResult = new ResultBean(true,e.getMessage());
            return dbResult;
        }catch (Exception e){
            //throw e;
            ResultBean dbResult = new ResultBean(false,e.getMessage());
            e.printStackTrace();
            return dbResult;
        }
    }
}
