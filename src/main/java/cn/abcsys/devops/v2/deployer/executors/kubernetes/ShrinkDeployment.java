package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:36
 */
@Service("shrinkDeployment")
public class ShrinkDeployment implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    private ResultBean result(KubernetesClient client){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        rb.setMessage("缩容deployments");
        KubernetesUtil.closeClient(client);
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        ImageGroupParameter igp = (ImageGroupParameter)params;
        dbUtil.deleteShrinkageDeployments(igp);
        client = KubernetesUtil.getClient(igp.getRealDeployments().get(0));
        for(DeploymentComponent temp :igp.getRealDeployments()){
            KubernetesUtil.deleteDeployment(temp,(KubernetesClient)client); //开始删除急群中的相关 deployment
        }
        return this.result((KubernetesClient)client);
    }
}
