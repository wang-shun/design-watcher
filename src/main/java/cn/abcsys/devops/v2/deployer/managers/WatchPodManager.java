package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IPodHandler;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */
public class WatchPodManager {
    protected Map<String,IPodHandler> podHandlers;

    public Map<String, IPodHandler> getPodHandlers() {
        return podHandlers;
    }

    public void setPodHandlers(Map<String, IPodHandler> podHandlers) {
        this.podHandlers = podHandlers;
    }

    public IPodHandler getPodHanlder(String key){
        return podHandlers.get(key);
    }
    
}
