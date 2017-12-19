package qsj;

import cn.abcsys.devops.v2.deployer.db.dao.V2DeploymentMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2EventMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Deployment;
import cn.abcsys.devops.v2.deployer.db.model.V2Event;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import static org.junit.Assert.*;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import org.apache.ibatis.jdbc.Null;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * @Author Qinshijun qinshijun2016@otcaix.iscas.ac.cn
 * @Date 2017/9/27 15:57
 */
public class WatchDeploymentTest implements Runnable{
    private KubernetesClient client;

    private ApplicationContext ac;

    private V2DeploymentMapper deploymentMapper;

    private Watch deploymentWatch;

    @Before
    public void beforeTest() throws InterruptedException {
        this.ac = new ClassPathXmlApplicationContext("config/spring-*.xml");
        this.deploymentMapper = (V2DeploymentMapper) ac.getBean("v2DeploymentMapper");
        this.client = Utils.getKubernetesClient();
    }

    @Test
    public void test() throws NullPointerException{
        V2Deployment maxDeployment = deploymentMapper.getMaxResourceVersion();
        int maxIntVersion = maxDeployment == null ? 0 : Integer.parseInt(maxDeployment.getResourceVersion());

        this.deploymentWatch = client.extensions().deployments().inNamespace("default").watch(new Watcher<Deployment>() {
            @Override
            public void eventReceived(Action action, Deployment deployment) {
                if (Integer.parseInt(deployment.getMetadata().getResourceVersion()) > maxIntVersion) {
                    System.out.println("================================================");
                    System.out.println("deployment " + action.name() + " " + deployment.toString());

                    if (action.name().equals("ADDED")) {
                        V2Deployment newDeployment = new V2Deployment();
                        newDeployment.setName(deployment.getMetadata().getName());
                        newDeployment.setUid(deployment.getMetadata().getUid());
                        newDeployment.setNamespace(deployment.getMetadata().getNamespace());
                        newDeployment.setResourceVersion(deployment.getMetadata().getResourceVersion());
                        deploymentMapper.insertSelective(newDeployment);
                    } else if (action.name().equals("DELETED")) {
                        deploymentMapper.deleteByUid(deployment.getMetadata().getUid());
                    }
                }
            }

            @Override
            public void onClose(KubernetesClientException e) {
                System.out.println("Watcher close due to " + e);
            }
        });

        try {
            Thread.sleep(600000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run () {
        test();
    }
}
