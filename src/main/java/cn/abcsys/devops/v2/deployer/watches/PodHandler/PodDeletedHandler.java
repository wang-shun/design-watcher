/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches.PodHandler;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IPodHandler;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.watches.EventHandler.PodStartedEventHandler;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/12 18:31
 * @File PodDeletedHandler.java
 */
@Component("podDeletedHandler")
public class PodDeletedHandler implements IPodHandler {

    private static Logger logger = Logger.getLogger(PodDeletedHandler.class);

    @Resource(name = "v2PodMapper")
    private V2PodMapper podMapper;

    @Resource(name = "v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    public void resetPod(Pod pod){
        V2Pod temp = this.podMapper.selectByLabels(PodStartedEventHandler.getParentImageGroup(pod.getMetadata().getLabels()));
        logger.info("delete V2pod:"+temp.getId());
        resetContainers(temp,pod);
        if(temp.getRealName().equals(pod.getMetadata().getName())){
            if(temp.getIfDeleteParent() !=null){  // 是否是人为缩容
                //logger.info("人工缩容，删除对应的容器："+temp.getId());
                temp.setStatus("Deleted");
                V2ImageGroup ig = this.imageGroupMapper.selectByPrimaryKey(temp.getIfDeleteParent());
                ig.setStatus("Deleted");
                imageGroupMapper.updateByPrimaryKeySelective(ig);
                this.deleteContainers(temp);
                podMapper.updateByPrimaryKeySelectiveStrict(temp);
                return;
            }
            //logger.info("系统自动缩容，修改容器状态："+temp.getId());
            temp.setStatus("autoDeleted");  // 自动缩容
            resetContainers(temp,pod);
            podMapper.updateByPrimaryKeySelective(temp);
        }

    }

    public void deleteContainers(V2Pod temp){
        List<V2Container> containers = containerMapper.selectByPodId(temp);
        if(containers !=null && containers.size() > 0){
            for(V2Container container : containers){
                containerMapper.deleteByPrimaryKey(container.getId());
            }
        }
    }

    public void resetContainers(V2Pod temp,Pod pod){
        List<V2Container> containers = containerMapper.selectByPodId(temp);
        if(containers !=null && containers.size() > 0){
            for(int i = 0;i<containers.size()&&containers.get(i).getPodName() !=null&&
                    containers.get(i).getPodName().equals(pod.getMetadata().getName());i++){
                V2Container eachOne = containers.get(i);
                logger.info("重置pod对应的容器："+pod.getMetadata().getName()+"id:"+eachOne.getId());
                eachOne.setStartDatetime(null);
                eachOne.setHostIp("无");
                eachOne.setHostName("无");
                eachOne.setRealName("无");
                eachOne.setStatus("false");
                //eachOne.setPodName("无");
                V2Container newOne2 = containerMapper.selectByPrimaryKey(eachOne.getId());
                logger.info("delete更新前的状态："+newOne2.getPodName());
                containerMapper.updateByPrimaryKeySelectiveStrict(eachOne);

                V2Container newOne = containerMapper.selectByPrimaryKey(eachOne.getId());
                logger.info("delete更新后的状态："+newOne.getPodName());
            }
        }
    }

    @Override
    public void handleEvent(Pod pod, KubernetesClient client,String type) {
        Map<String,String> labels = pod.getMetadata().getLabels();
        if(PodChangedHandler.checkLabels(labels) && this.podMapper.selectByLabels(PodChangedHandler.getParentImageGroup(labels)) !=null){
            this.resetPod(pod);
        }
    }
}
