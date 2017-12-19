/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches.EventHandler;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/9 21:29
 * @File PodDeletedEventHandler.java
 */
@Component("podDeletedEventHandler")
public class PodDeletedEventHandler implements IEventHandler {

    @Resource(name = "v2PodMapper")
    private V2PodMapper podMapper;

    @Resource(name = "v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    public void resetPod(Map<String,String> labels){
        V2Pod temp = this.podMapper.selectByLabels(PodStartedEventHandler.getParentImageGroup(labels));
        if(temp.getIfDeleteParent() !=null){  // 是否是人为缩容
            temp.setStatus("Deleted");
            V2ImageGroup ig = this.imageGroupMapper.selectByPrimaryKey(temp.getIfDeleteParent());
            imageGroupMapper.updateByPrimaryKeySelective(ig);
            ig.setStatus("Deleted");
            this.deleteContainers(temp);
        }
        temp.setStatus("autoDeleted");  // 自动缩容
        resetContainers(temp);
        podMapper.updateByPrimaryKeySelective(temp);
    }

    public void deleteContainers(V2Pod temp){
        List<V2Container> containers = containerMapper.selectByPodId(temp);
        if(containers !=null && containers.size() > 0){
            for(V2Container container : containers){
                containerMapper.deleteByPrimaryKey(container.getId());
            }
        }
    }

    public void resetContainers(V2Pod temp){
        List<V2Container> containers = containerMapper.selectByPodId(temp);
        if(containers !=null && containers.size() > 0){
            for(int i = 0;i<containers.size();i++){
                V2Container eachOne = containers.get(i);
                eachOne.setStartDatetime(null);
                eachOne.setHostIp("无");
                eachOne.setHostName("无");
                eachOne.setRealName("无");
                eachOne.setStatus("false");
                containerMapper.updateByPrimaryKeySelective(eachOne);
            }
        }
    }

    @Override
    public void handleEvent(Event event, KubernetesClient client) {
        Pod newPod= client.pods().inNamespace(event.getInvolvedObject().getNamespace()).
                withName(event.getInvolvedObject().getName()).get();
        Map<String,String> labels = newPod.getMetadata().getLabels();
        if(PodStartedEventHandler.checkLabels(labels) &&
                this.podMapper.selectByLabels(PodStartedEventHandler.getParentImageGroup(labels)) !=null){
            this.resetPod(labels);
        }
    }
}
