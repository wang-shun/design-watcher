/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.watches.PodHandler;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IEventHandler;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IPodHandler;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
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
@Component("podChangedHandler")
public class PodChangedHandler implements IPodHandler {

    private static Logger logger = Logger.getLogger(PodChangedHandler.class);

    @Resource(name = "v2PodMapper")
    private V2PodMapper podMapper;

    @Resource(name = "v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    public static Boolean checkLabels(Map<String,String> labels){
        return labels !=null && labels.containsKey("image-group-name") && labels.containsKey("application-real-name") && labels.containsKey("version-id");
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
        try {
            Integer versionId = getVersionIdInLabel(labels.get("version-id"));
            String realName = labels.get("image-group-name");
            V2ImageGroup ig = new V2ImageGroup();
            ig.setVersionId(versionId);
            ig.setRealName(realName);
            return ig;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 灰度发布中更改数据库的 deployment 和 pod 的状态为 Deleting
     * 删除集群中的 Deployment
     * @param temp
     * @param client
     */
    public void readyDeleteOld(V2Pod temp,KubernetesClient client){
        logger.info("准备执行灰度发布：");
        // 获得需要替换的 deployment
        V2ImageGroup old = imageGroupMapper.selectByPrimaryKey(temp.getReplaceOldParentId());
        logger.info("需要删除的imageGroup："+old.getId());
        old.setVersionGroupId((int) (Math.random()*1000000));
        // 获得需要替换的 pod
        if(podMapper.selectByImageGroupId(old.getId()) != null &&
                podMapper.selectByImageGroupId(old.getId()).size() > 0){
            V2Pod oldPod = podMapper.selectByImageGroupId(old.getId()).get(0);
            logger.info("需要删除的oldPod："+oldPod.getId());
            // 更改他们的状态为 删
            old.setStatus("Deleting");
            oldPod.setStatus("Deleting");
            // 设置 pod 的 ifDelete 字段为该 pod 旧
            oldPod.setIfDeleteParent(old.getId());
            //更改数据库里的状态
            imageGroupMapper.updateByPrimaryKeySelective(old);
            podMapper.updateByPrimaryKeySelective(oldPod);

            //创建一个新的 deploymnetComponent，然后把更新后的需要替换的 deployment 赋值给他
            DeploymentComponent dc = new DeploymentComponent();
            dc.setImageGroup(old);
            //在 kubernetes 集群中删除与该 DeploymentComponent 同 realName 的 deployment
            KubernetesUtil.deleteDeployment(dc,client);
        }
    }

    /**
     * 通过事件，运用回调，更新数据库中的 pod 状态，
     * 如果是灰度发布，那么就把该 pod 和 对应的 deployment 的状态置为 Deleted，删除集群中 deployment
     * @param labels
     * @param pod
     * @param client
     */
    public void resetPod(Map<String,String> labels,Pod pod, KubernetesClient client,String mark){
        V2Pod temp = this.podMapper.selectByLabels(getParentImageGroup(labels));
        logger.info("需要更新的PODid："+temp.getId());
        logger.info(temp.getRealName());
        if(mark.equals("ADDED") && (temp.getRealName() !=null && !temp.getRealName().equals(pod.getMetadata().getName()) ||
                temp.getRealName() == null)||
                (mark.equals("MODIFIED") || mark.equals("ERROR")) &&
                        (temp.getRealName() !=null && temp.getRealName().equals(pod.getMetadata().getName()) ||
                        temp.getRealName() == null)){
            logger.info("符合條件，可以更新pod！");
            temp.setStatus(pod.getStatus().getPhase());
            // pod 创建成功后，再把 pod 的名字写回到数据库，是一个回调
            temp.setRealName(pod.getMetadata().getName());
            // 更新 pod 的最近更改时间
            temp.setUpdateTime(new Date());
            // 查看是否是灰度发布，只有灰度发布这个字段才会设置，然后更改数据库，删除集群的资源
            if(temp.getReplaceOldParentId() !=null && pod.getStatus().getPhase().equals("Running")){
                this.readyDeleteOld(temp,client);
                //删除完了后，就把 pod 灰度发布的标志位置为 null
                temp.setReplaceOldParentId(null);
            }
            //更新数据库中的该 pod 信息
            podMapper.updateByPrimaryKeySelective(temp);
        }
    }

    private ContainerStatus getContainerStatus(Pod newK8sPod,int i){
        if( newK8sPod.getStatus() !=null &&
                newK8sPod.getStatus().getContainerStatuses() !=null &&
                newK8sPod.getStatus().getContainerStatuses().size()>i){
            return newK8sPod.getStatus().getContainerStatuses().get(i);
        }
        return null;
    }

    private void updateContainers(Pod newK8sPod,V2Pod temp,String type) {
        //找到对应 pod 下的所有的 container
        List<V2Container> containers = containerMapper.selectByPodId(temp);
        System.out.println("podId："+temp+"  containerSize:"+containers.size());
        if(containers !=null && containers.size() > 0) { //如果数据库中容器存在
            //遍历每一个 container
            System.out.println(type+":"+newK8sPod.getMetadata().getName()+"size:"+(containers.size() == newK8sPod.getStatus().getContainerStatuses().size()));
            logger.info(containers.get(0).getPodName());
            for (int i = 0; i < containers.size() && (type.equals("ADDED") &&
                    (containers.get(i).getPodName() != null &&
                    !containers.get(i).getPodName().equals(newK8sPod.getMetadata().getName()) || containers.get(i).getPodName() == null)
                    || (type.equals("MODIFIED") || type.equals("ERROR")) &&
                    (containers.get(i).getPodName() !=null
                            &&containers.get(i).getPodName().equals(newK8sPod.getMetadata().getName()) ||
                            containers.get(i).getPodName() == null)); i++) {
                logger.info("符合條件，可以更新container！");
                ContainerStatus ifStatus = getContainerStatus(newK8sPod,i);
                V2Container eachOne = containers.get(i);
                if(ifStatus !=null){
                    eachOne.setHostName(newK8sPod.getSpec().getNodeName()); // 添加容器所在的 node 的 name
                    eachOne.setStartDatetime(new Date()); // 添加容器的启动时间，回调
                    eachOne.setHostIp(newK8sPod.getStatus().getHostIP()); // 添加容器所在的 node 的 ip
                    eachOne.setRealName(ifStatus.getName()); // 容器的实际名字
                    eachOne.setStatus(ifStatus.getReady().toString()); // 容器的状态
                    if(ifStatus.getReady()){
                        eachOne.setContainerName(KubernetesUtil.getContainerName(newK8sPod,eachOne.getRealName()));
                    }
                    else{
                        eachOne.setContainerName(null);
                    }
                }
                eachOne.setPodName(newK8sPod.getMetadata().getName());
                containerMapper.updateByPrimaryKeySelective(eachOne); // 更新到数据库中
                if (ifStatus !=null && ifStatus.getReady().toString().equals("true")) {
                    logger.info("find container in db:" + eachOne.getId() + ":" + ifStatus.getReady().toString() + ":" + eachOne.getPodName());
                }
                V2Container newOne = containerMapper.selectByPrimaryKey(eachOne.getId());
                logger.info("change更新后的状态：" + newOne.getPodName());
            }
        }
    }

    /**
     * 完成事件到达后的后续操作,通过 pod 的打的标签和数据库中的记录关联起来
     * @param newPod
     * @param client
     */
    @Override
    public void handleEvent(Pod newPod, KubernetesClient client,String type) {

        Map<String,String> labels = newPod.getMetadata().getLabels(); //获得这个 pod 对象的标签
        // 判断这个 pod 是否有添加的那些标签，按道理说应该是都有
        // 获得该 pod 的所属的 deployment
        // 判断数据库中该 deployment 最新的一个 pod 是不是为空，即存不存在
        // 不为空就重新设置 pod 和 container
        if(this.checkLabels(labels) && this.podMapper.selectByLabels(getParentImageGroup(labels)) !=null){
            this.resetPod(labels,newPod,client,type);
            this.updateContainers(newPod,this.podMapper.selectByLabels(getParentImageGroup(labels)),type);
        }
    }
}
