package cn.abcsys.devops.v2.deployer.executors.openshift;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import cn.abcsys.devops.v2.deployer.deployers.openshift.OpenshiftUtil;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:36
 */
@Service("openshift_createDeployment")
public class CreateDeployment implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    private static Logger logger = Logger.getLogger(CreateDeployment.class);

    private ResultBean result(KubernetesClient client){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        rb.setMessage("创建deployments");
        KubernetesUtil.closeClient(client);
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try {
            ImageGroupParameter igp = (ImageGroupParameter)params;
            logger.info(">>>>>>>>>开始插入数据库了");
            dbUtil.insertInstanceInfo(igp);
            for(DeploymentComponent temp :igp.getRealDeployments()){
                KubernetesClient client1 = OpenshiftUtil.getKubernetesClient("https","master.example.com","8443");
                OpenshiftUtil.createDeployment(temp,client1);
            }
            return this.result((KubernetesClient)client);
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage()+"创建新的 Deloyment 出现空指针异常，可能是数据库连接错误或者集群连接错误");
        } catch (Exception e){
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("数据库插入和创建失败"+e.getMessage().getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
