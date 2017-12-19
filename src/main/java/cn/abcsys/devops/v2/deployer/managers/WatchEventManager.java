package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */
public class WatchEventManager {
    protected Map<String,IEventHandler> eventHandlers;

    public Map<String, IEventHandler> getEventHandlers() {
        return eventHandlers;
    }

    public void setEventHandlers(Map<String, IEventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public IEventHandler getEventHanlder(String key){
        return eventHandlers.get(key);
    }

}
