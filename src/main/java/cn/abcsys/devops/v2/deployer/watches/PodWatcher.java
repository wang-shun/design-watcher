/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IPodHandler;
import cn.abcsys.devops.v2.deployer.db.dao.V2EventMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.managers.WatchPodManager;
import cn.abcsys.devops.v2.deployer.utils.SpringContextHelper;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/9 0:48
 * @File PodWatcher.java
 */
public class PodWatcher<T> implements Watcher<T> {

    private static Logger logger = Logger.getLogger(PodWatcher.class);
    private V2EventMapper eventMapper;

    private V2PodMapper podMapper;

    private KubernetesClient client;

    private WatchPodManager wpm;

    private ServletContextEvent sce;

    public WatchPodManager getWpm() {
        return wpm;
    }

    public void setWpm(WatchPodManager wpm) {
        this.wpm = wpm;
    }

    public ServletContextEvent getSce() {
        return sce;
    }

    public void setSce(ServletContextEvent sce) {
        this.sce = sce;
    }

    public V2EventMapper getEventMapper() {
        return eventMapper;
    }

    public void setEventMapper(V2EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public V2PodMapper getPodMapper() {
        return podMapper;
    }

    public void setPodMapper(V2PodMapper podMapper) {
        this.podMapper = podMapper;
    }

    public KubernetesClient getClient() {
        return client;
    }

    public void setClient(KubernetesClient client) {
        this.client = client;
    }

    private void init(){
        WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        System.out.println("context:"+springContext);
        eventMapper = (V2EventMapper )springContext.getBean("v2EventMapper");
        podMapper = (V2PodMapper )springContext.getBean("v2PodMapper");
        wpm = (WatchPodManager) springContext.getBean("watchPodManager");
    }

    /**
     * watcher 这个对象会作为回调对象传递给 kubernetesClient 对象，当KubernetesClient 检测到 event 事件时，
     * 就会执行 watcher 的 eventReceived 方法，这个传递过程应该是类似于提供一个 add 监听者 List 的接口
     * @param action
     * @param t
     */
    @Override
    public void eventReceived(Action action, T t) {
        this.init();
        Pod pod = (Pod)t;
        WatchCollections.resetContainers(pod,action.name(),client);
    }

    @Override
    public void onClose(KubernetesClientException e) {
        System.out.println("关闭watch连接："+client.getMasterUrl().getHost());
        AllInit.removeWatch(client);
    }
}
