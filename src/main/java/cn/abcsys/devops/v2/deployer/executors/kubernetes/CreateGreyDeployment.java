package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
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
@Service("createGreyDeployment")
public class CreateGreyDeployment implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    Logger logger = Logger.getLogger(CreateGreyDeployment.class);

    private ResultBean result(KubernetesClient client, ImageGroupParameter parameter){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        try {
            if (parameter.getDeployment().getImageGroup().getVersionId() != null &&
                    parameter.getDeployment().getImageGroup().getVersionId() > 0)
                rb.setMessage("发布旧版本成功!");
            else rb.setMessage("发布新版本成功!");
        }catch (Exception e){
            rb.setMessage("发布版本成功!");
        }

        KubernetesUtil.closeClient(client);
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try{
            //开始进行灰度发
            ImageGroupParameter igp = (ImageGroupParameter)params;
            //对 targetVersionId 进行校验，必须确保替换数小于实际运行数
            logger.info("graytype value :"+igp.getGreyType()+"; replaceCount value:"+igp.getReplaceCount());
            if (igp.getGreyType()!=null && igp.getGreyType().equals("number")){
                logger.info("1st judge");
                if(igp.getReplaceCount() > dbUtil.getRunningInstanceNumberOfTargetVersion(igp.getTargetVersionId())) {
                    logger.info("2nd judge");
                    throw new Exception("需要替换的实例数的个数大于该版本实际运行的实例数，请重新设置");
                }

            }

            dbUtil.insertGreyInstanceInfo(igp); // 完成了 jgp 的参数的基于灰度的更新
            for(DeploymentComponent temp :igp.getRealDeployments()){
                KubernetesUtil.createDeployment(temp,(KubernetesClient)client);
            }
            return this.result((KubernetesClient)client, (ImageGroupParameter)params);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }
}
