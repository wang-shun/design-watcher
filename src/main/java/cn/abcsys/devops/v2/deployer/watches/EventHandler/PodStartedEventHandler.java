/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches.EventHandler;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2EventMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ContainerState;
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
 * @Date 2017/10/9 16:39
 * @File PodStartedEventHandler.java
 */
@Component("podStartedEventHandler")
public class PodStartedEventHandler implements IEventHandler {

    @Resource(name = "v2PodMapper")
    private V2PodMapper podMapper;

    @Resource(name = "v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;



    public static Boolean checkLabels(Map<String,String> labels){
        return labels.containsKey("image-group-name") && labels.containsKey("application-real-name") && labels.containsKey("version-id");
    }

    public static Integer getVersionIdInLabel(String value){
        return Integer.valueOf(value.replaceAll("version-id-",""));
    }

    /**
     * 提取传入的标签组里的关于 versionId 和 image-group-name（模板名字）的信息，然后返回一个新的 V2ImageGroup 对象
     * 这个对象等价于 pod 所在的 deployment 对象
     * @param labels
     * @return
     */
    public static V2ImageGroup getParentImageGroup(Map<String,String> labels){
        Integer versionId = getVersionIdInLabel(labels.get("version-id"));
        String realName = labels.get("image-group-name");
        V2ImageGroup ig = new V2ImageGroup();
        ig.setVersionId(versionId);
        ig.setRealName(realName);
        return ig;
    }

    /**
     * 灰度发布中更改数据库的 deployment 和 pod 的状态为 Deleting
     * 删除集群中的 Deployment
     * @param temp
     * @param client
     */
    public void readyDeleteOld(V2Pod temp,KubernetesClient client){
        // 获得需要替换的 deployment
        V2ImageGroup old = imageGroupMapper.selectByPrimaryKey(temp.getReplaceOldParentId());
        // 获得需要替换的 pod
        V2Pod oldPod = podMapper.selectByImageGroupId(old.getVersionGroupId()).get(0);
        // 更改他们的状态为 删除
        old.setStatus("Deleting");
        oldPod.setStatus("Deleting");
        // 设置 pod 的 ifDelete 字段为该 pod 旧 versionGroupId todo 这是干嘛的？
        oldPod.setIfDeleteParent(old.getVersionGroupId());
        //更改数据库里的状态
        imageGroupMapper.updateByPrimaryKeySelective(old);
        podMapper.updateByPrimaryKeySelective(oldPod);

        //创建一个新的 deploymnetComponent，然后把更新后的需要替换的 deployment 赋值给他
        DeploymentComponent dc = new DeploymentComponent();
        dc.setImageGroup(old);
        //在 kubernetes 集群中删除与该 DeploymentComponent 同 realName 的 deployment
        KubernetesUtil.deleteDeployment(dc,client);
    }

    /**
     * 通过事件，运用回调，更新数据库中的 pod 状态，
     * 如果是灰度发布，那么就把该 pod 和 对应的 deployment 的状态置为 Deleted，删除集群中 deployment
     * @param labels
     * @param event
     * @param client
     */
    public void resetPod(Map<String,String> labels,Event event, KubernetesClient client){
        //找到事件相关的 pod 的 deployment 在数据库中对应的 pod
        V2Pod temp = this.podMapper.selectByLabels(getParentImageGroup(labels));
        // 更改为 started. todo 应该是更改为 event 提供给我们的状态
        temp.setStatus("Started");
        // pod 创建成功后，再把 pod 的名字写回到数据库，是一个回调
        temp.setRealName(event.getInvolvedObject().getName());
        // 更新 pod 的最近更改时间
        temp.setUpdateTime(new Date());
        // 查看是否是灰度发布，只有灰度发布这个字段才会设置，然后更改数据库，删除集群的资源
        if(temp.getReplaceOldParentId() !=null){
            this.readyDeleteOld(temp,client);
        }
        //删除完了后，就把 pod 灰度发布的标志位置为 null
        temp.setReplaceOldParentId(null);
        //更新数据库中的该 pod 信息
        podMapper.updateByPrimaryKeySelective(temp);
    }

    /**
     *
     * @param newK8sPod 集群中的 pod
     * @param temp 数据库中的 pod
     */
    public void resetContainers(Pod newK8sPod,V2Pod temp){
        //找到对应 pod 下的所有的 container
        List<V2Container> containers = containerMapper.selectByPodId(temp);

        if(containers !=null && containers.size() > 0){ //如果数据库中容器存在
            //遍历每一个 container
            for(int i = 0;i<containers.size()&&containers.size() == newK8sPod.getStatus().getContainerStatuses().size();i++){
                ContainerStatus cs = newK8sPod.getStatus().getContainerStatuses().get(i);
                V2Container eachOne = containers.get(i);
                eachOne.setStartDatetime(new Date()); // 添加容器的启动时间，回调
                eachOne.setHostIp(newK8sPod.getStatus().getHostIP()); // 添加容器所在的 node 的 ip
                eachOne.setHostName(newK8sPod.getSpec().getNodeName()); // 添加容器所在的 node 的 name
                eachOne.setRealName(cs.getName()); // 容器的实际名字
                eachOne.setStatus(cs.getReady().toString()); // 容器的状态
                containerMapper.updateByPrimaryKeySelective(eachOne); // 更新到数据库中
            }
        }
    }

    /**
     * 完成事件到达后的后续操作
     * @param event
     * @param client
     */
    @Override
    public void handleEvent(Event event, KubernetesClient client) {
        Pod newPod= client.pods().inNamespace(event.getInvolvedObject().getNamespace()).
                withName(event.getInvolvedObject().getName()).get();
        //通过 client 获得集群中和 Event 中 pod 对象相关的 pod 对象

        Map<String,String> labels = newPod.getMetadata().getLabels(); //获得这个 pod 对象的标签

        // 判断这个 pod 是否有添加的那些标签，按道理说应该是都有
        // 获得该 pod 的所属的 deployment
        // 判断数据库中该 deployment 最新的一个 pod 是不是为空，即存不存在
        // 不为空就重新设置 pod 和 container
        if(this.checkLabels(labels) && this.podMapper.selectByLabels(getParentImageGroup(labels)) !=null){
            this.resetPod(labels,event,client);
            this.resetContainers(newPod,this.podMapper.selectByLabels(getParentImageGroup(labels)));
        }
    }
}
