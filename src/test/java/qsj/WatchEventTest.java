package qsj;

import cn.abcsys.devops.v2.deployer.db.dao.V2EventMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Event;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.client.KubernetesClient;
import static org.junit.Assert.*;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * @Author Qinshijun qinshijun2016@otcaix.iscas.ac.cn
 * @Date 2017/9/20 22:04
 */
public class WatchEventTest implements Runnable{
    private KubernetesClient client;

    private ApplicationContext ac;

    private V2EventMapper eventMapper;

    private V2PodMapper podMapper;

    private Watch eventWatch;

    @Before
    public void beforeTest() throws InterruptedException{
        this.ac = new ClassPathXmlApplicationContext("config/spring-*.xml");
        this.eventMapper = (V2EventMapper)ac.getBean("v2EventMapper");
        this.podMapper = (V2PodMapper)ac.getBean("v2PodMapper");
        this.client = Utils.getKubernetesClient();
    }

    @Test
    public void test(){
    }

    @Override
    public void run () {
        test();
    }

}
