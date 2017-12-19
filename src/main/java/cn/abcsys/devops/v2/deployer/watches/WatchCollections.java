/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches;

import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.utils.SpringContextHelper;
import cn.abcsys.devops.v2.deployer.watches.PodHandler.PodChangedHandler;
import cn.abcsys.devops.v2.deployer.watches.PodHandler.PodDeletedHandler;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/8 0008 9:42
 */
public class WatchCollections {
    private static Map<String,PodWatcher> podWatcherMap = new HashMap<>();
    private static Map<String,Watch> podWatchMap = new HashMap<>();
    private static Map<String,Thread> podWatchThreads = new HashMap<>();

    //public static WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

    public static void putPodWatchThread(KubernetesClient kc){
        String key = kc.getMasterUrl().toString();
        if(!podWatchThreads.containsKey(key)){
            WatchThread wt = new WatchThread();
            wt.setClient(kc);
            wt.start();
            podWatchThreads.put(key,wt);
        }
    }

    public static void putPodWatchIfNotExist(KubernetesClient kc){
        String key = kc.getMasterUrl().toString();
        if(!podWatchMap.containsKey(key)){
            System.out.println(key);
            PodWatcher watcher = new PodWatcher<Pod>();
            watcher.setClient(kc);
            Watch podWatch = kc.pods().inNamespace("default").watch(watcher);
            podWatcherMap.put(key,watcher);
            podWatchMap.put(key,podWatch);
        }
    }

    public static synchronized void resetContainers(Pod k8sPod,String type,KubernetesClient client){
        if(type.equals("ADDED") ||type.equals("MODIFIED") || type.equals("ERROR") ){
            PodChangedHandler changedHandler = (PodChangedHandler)SpringContextHelper.getBean("podChangedHandler");
            changedHandler.handleEvent(k8sPod,client,type);
        }
        else if(type.equals("DELETED")){
            PodDeletedHandler deletedHandler = (PodDeletedHandler)SpringContextHelper.getBean("podDeletedHandler");
            deletedHandler.handleEvent(k8sPod,client,null);
        }
    }
}
