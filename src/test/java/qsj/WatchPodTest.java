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
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * @Author Qinshijun qinshijun2016@otcaix.iscas.ac.cn
 * @Date 2017/9/27 15:57
 */
public class WatchPodTest implements Runnable{
    private KubernetesClient client;

    private ApplicationContext ac;

    private V2PodMapper podMapper;

    private V2DeploymentMapper deploymentMapper;

    private Watch podWatch;

    @Before
    public void beforeTest() throws InterruptedException {
        this.ac = new ClassPathXmlApplicationContext("config/spring-*.xml");
        this.podMapper = (V2PodMapper)ac.getBean("v2PodMapper");
        this.deploymentMapper = (V2DeploymentMapper)ac.getBean("v2DeploymentMapper");
        this.client = Utils.getKubernetesClient();
    }

    @Test
    public void test() throws NullPointerException {
        V2Pod maxPod = podMapper.getMaxResourceVersion();
        int maxIntVersion = maxPod == null ? 0 : Integer.parseInt(maxPod.getResourceVersion());
        this.podWatch = client.pods().inNamespace("default").watch(new Watcher<Pod>() {
            @Override
            public void eventReceived(Action action, Pod pod) {
                if (Integer.parseInt(pod.getMetadata().getResourceVersion()) > maxIntVersion) {
                    System.out.println("================================================");
                    System.out.println("pod " + action.name() + " " + pod.toString());

                    if (action.name().equals("ADDED")) {
//                        String podName = pod.getMetadata().getName();
//                        String deploymentName = podName.substring(0, podName.length() - 17);
//                        int deploymentId = deploymentMapper.selectByName(deploymentName).getId();

                        V2Pod newPod = new V2Pod();
                        newPod.setRealName(pod.getMetadata().getName());
                        newPod.setUuid(pod.getMetadata().getUid());
                        newPod.setResourceVersion(pod.getMetadata().getResourceVersion());
//                        newPod.setDeploymentId(deploymentId);

                        podMapper.insertSelective(newPod);
                    } else if (action.name().equals("DELETED")) {
                        podMapper.deleteByUid(pod.getMetadata().getUid());
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
