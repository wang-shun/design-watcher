/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers.databaseUtil;

import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.*;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import cn.abcsys.devops.v2.deployer.utils.BeanUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/27 10:02
 */

@Component("dbUtil")
public class DBUtil {

    private static Logger logger = Logger.getLogger(DBUtil.class);

    @Resource(name="v2NetworkLabelsMapper")
    private  V2NetworkLabelsMapper networkLabelsMapper;

    @Resource(name="v2NetworkPortsMapper")
    private  V2NetworkPortsMapper networkPortsMapper;

    @Resource(name="v2NetworkPolicyMapper")
    private  V2NetworkPolicyMapper networkPolicyMapper;

    @Resource(name="v2ImageGroupMapper")
    private  V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2LabelsMapper")
    private  V2LabelsMapper labelsMapper;

    @Resource(name="v2VolumesMapper")
    private  V2VolumesMapper volumesMapper;

    @Resource(name="v2ImageMapper")
    private  V2ImageMapper imageMapper;

    @Resource(name="v2PortsMapper")
    private  V2PortsMapper portsMapper;

    @Resource(name="v2ResourcesMapper")
    private  V2ResourcesMapper resourcesMapper;

    @Resource(name="v2EnvsMapper")
    private  V2EnvsMapper envsMapper;

    @Resource(name="v2ArgsMapper")
    private  V2ArgsMapper argsMapper;

    @Resource(name="v2VolumeMountsMapper")
    private  V2VolumeMountsMapper volumeMountsMapper;

    @Resource(name="v2ProbeMapper")
    private  V2ProbeMapper probeMapper;

    @Resource(name="v2PodMapper")
    private  V2PodMapper podMapper;

    @Resource(name="v2SvcMapper")
    private  V2SvcMapper svcMapper;

    @Resource(name="v2SvcPortsMapper")
    private  V2SvcPortsMapper svcPortsMapper;

    @Resource(name="v2SvcLabelsMapper")
    private  V2SvcLabelsMapper svcLabelsMapper;

    @Resource(name="v2ContainerMapper")
    private  V2ContainerMapper containerMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertImages(DeploymentComponent component){
        for(ImageComponent temp : component.getImages()){
            temp.getImage().setImageGroupId(component.getImageGroup().getId());
            imageMapper.insertSelective(temp.getImage());
            if(temp.getPorts() !=null && temp.getPorts().size() > 0){
                for(V2Ports v2Ports:temp.getPorts()){
                    v2Ports.setImageId(temp.getImage().getId());
                    portsMapper.insertSelective(v2Ports);
                }
            }
            if(temp.getResources() != null){
                temp.getResources().setImageId(temp.getImage().getId());
                temp.getResources().setImageGroupId(component.getImageGroup().getId());
                resourcesMapper.insertSelective(temp.getResources());
            }

            if(temp.getEnvs() !=null && temp.getEnvs().size() > 0){
                for(V2Envs env: temp.getEnvs()){
                    env.setImageId(temp.getImage().getId());
                    envsMapper.insertSelective(env);
                }
            }

            if(temp.getArgs() !=null && temp.getArgs().size() > 0){
                for(V2Args arg: temp.getArgs()){
                    arg.setImageId(temp.getImage().getId());
                    argsMapper.insertSelective(arg);
                }
            }

            if(temp.getVolumeMounts() !=null && temp.getVolumeMounts().size() > 0){
                for(V2VolumeMounts vm : temp.getVolumeMounts()){
                    vm.setImageId(temp.getImage().getId());
                    vm.setImageGroupId(component.getImageGroup().getId());
                    volumeMountsMapper.insertSelective(vm);
                }
            }

            if(temp.getProbes() !=null && temp.getProbes().size() > 0){
                for(V2Probe probe : temp.getProbes()){
                    probe.setImageId(temp.getImage().getId());
                    probeMapper.insertSelective(probe);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertImageGroup(DeploymentComponent component){
        component.getImageGroup().setStatus("created");
        component.getImageGroup().setUpdateTime(new Date());
        component.getImageGroup().setCreateTime(new Date());
        imageGroupMapper.insertSelective(component.getImageGroup());
//        for (ImageComponent image:component.getImages()){
//            V2Image temp = image.getImage();
//            temp.setImageGroupId(component.getImageGroup().getId());
//            imageMapper.insertSelective(temp);
//        }
        if(component.getVolumes() !=null && component.getVolumes().size() > 0){
            for (V2Volumes volume:component.getVolumes()) {
                volume.setImageGroupId(component.getImageGroup().getId());
                volumesMapper.insertSelective(volume);
            }
        }
        logger.info(">>>>>>>>>"+ JSON.toJSONString(component.getLabels()));
        for (V2Labels labels:component.getLabels()){
            labels.setImageGroupId(component.getImageGroup().getId());
            labelsMapper.insertSelective(labels);
        }
    }

    public void replaceOrAddLabels(String labelType,String labelKey,String labelValue,List<V2Labels> labels){
        Boolean ifExists = false;
        for(int i=0; i<labels.size(); ++i){
            V2Labels temp = labels.get(i);
            temp.setImageGroupId(null);
            temp.setId(null);
            if(temp.getLabelKey().equals(labelKey) && temp.getLabelType().equals(labelType)){
                temp.setLabelValue(labelValue);
                ifExists = true;
            }
        }
        if(!ifExists)
            labels.add(new V2Labels(labelType,labelKey,labelValue));
    }

    public String getLogPath(DeploymentComponent dc,String vName){
        if(dc.getImages() !=null && dc.getImages().size() > 0){
            for(ImageComponent temp1 : dc.getImages()){
                if(temp1.getVolumeMounts() != null && temp1.getVolumeMounts().size() > 0){
                    for(V2VolumeMounts temp2 : temp1.getVolumeMounts()){
                        if(temp2.getVolumeMountName().equals(vName)){
                            return  temp2.getMountPath()
                                    .replaceAll("/", "_").replaceFirst("_", "");
                        }
                    }
                }
            }
        }
        return null;
    }

    public void resetVolumes(DeploymentComponent temp){
        if(temp !=null && temp.getVolumes() !=null){
            for(V2Volumes eachVolume :temp.getVolumes()){
                eachVolume.setId(null);
                eachVolume.setImageGroupId(null);
                String logInsidePath = getLogPath( temp,eachVolume.getVolumeName());
                if(eachVolume.getVolumeType().equals("hostPath")){
                    if(!eachVolume.getHostPath().endsWith("/")){
                        eachVolume.setHostPath(eachVolume.getHostPath()+"/");
                    }
                    eachVolume.setHostPath(eachVolume.getHostPath().split(temp.getImageGroup().getParentName())[0]
                            + temp.getImageGroup().getParentName() +
                            "/"+temp.getImageGroup().getRealName()+"/"+logInsidePath);
                }

                if(eachVolume.getVolumeType().equals("logPath") && logInsidePath !=null){
                    eachVolume.setHostPath("/abcs/data/local/localFileSystem/"+
                            temp.getImageGroup().getParentName() +
                            "/"+temp.getImageGroup().getRealName()+"/"+
                            logInsidePath);
                }
                if(eachVolume.getVolumeType().equals("applicationDataPath")){
                    if(!eachVolume.getHostPath().endsWith("/")){
                        eachVolume.setHostPath(eachVolume.getHostPath()+"/");
                    }
                    eachVolume.setHostPath(eachVolume.getHostPath()+temp.getImageGroup().getParentName());
                }
            }
        }
    }

    public List<V2Labels>  resetNewLabels(Integer imageGroupId){
        V2ImageGroup ig = imageGroupMapper.selectByPrimaryKey(imageGroupId);
        List<V2Labels> newLabels = new ArrayList<>();
        newLabels.add(new V2Labels(imageGroupId,"imageGroupLabels","image-group-name",ig.getRealName()));
        newLabels.add(new V2Labels(imageGroupId,"imageGroupLabels","application-real-name",ig.getParentName()));
        newLabels.add(new V2Labels(imageGroupId,"imageGroupLabels","version-id","version-id-"+ig.getVersionId()));
        newLabels.add(new V2Labels(imageGroupId,"podLabels","image-group-name",ig.getRealName()));
        newLabels.add(new V2Labels(imageGroupId,"podLabels","application-real-name",ig.getParentName()));
        newLabels.add(new V2Labels(imageGroupId,"podLabels","version-id","version-id-"+ig.getVersionId()));
        for(V2Labels temp : newLabels){
            labelsMapper.insertSelective(temp);
        }
        return newLabels;
    }

    public void setSelfDeploymentComponent(DeploymentComponent temp){
        temp.getImageGroup().setId(null);
        temp.getImageGroup().setRealName(temp.getImageGroup().getImageGroupName()+"-"+
                UUID.randomUUID().toString().replaceAll("-","").toLowerCase().substring(0,5));
        temp.getImageGroup().setVersionGroupId(null);
        temp.getImageGroup().setApiVersion("extensions/v1beta1");
        temp.getImageGroup().setKind("Deployment");
        replaceOrAddLabels("imageGroupLabels","image-group-name",temp.getImageGroup().getRealName(),temp.getLabels());
        replaceOrAddLabels("imageGroupLabels","application-real-name",temp.getImageGroup().getParentName(),temp.getLabels());
        replaceOrAddLabels("imageGroupLabels","version-id","version-id-"+temp.getImageGroup().getVersionId(),temp.getLabels());
        replaceOrAddLabels("imageGroupSelectors","image-group-name",temp.getImageGroup().getRealName(),temp.getLabels());
        replaceOrAddLabels("podLabels","image-group-name",temp.getImageGroup().getRealName(),temp.getLabels());
        replaceOrAddLabels("podLabels","application-real-name",temp.getImageGroup().getParentName(),temp.getLabels());
        String value = "version-id-"+temp.getImageGroup().getVersionId();
        System.out.println(value);
        replaceOrAddLabels("podLabels","version-id",value,temp.getLabels());
        resetVolumes(temp);
    }
    /**
     * 从数据库中获得 V2IMAGEGROUP 后 创建新的 DeploymentComponent 对象，方便重新发布，
     * 然后把该 deployment 对象的很多自动设置的信息清空
     * @param dc
     */
    public void setNewDeploymentComponent(DeploymentComponent dc){
        // 更改必要的 labels
        setSelfDeploymentComponent(dc);
        for(ImageComponent imageConfig : dc.getImages()){
            //把 内部的 需要插入到数据库中自动设置的 id 都清空
            imageConfig.getImage().setImageGroupId(null);
            imageConfig.getImage().setId(null);
            if (imageConfig.getResources() != null){
                imageConfig.getResources().setImageId(null);
                imageConfig.getResources().setId(null);
                imageConfig.getResources().setImageGroupId(null);
            }
            if(imageConfig.getPorts() != null){
                for(V2Ports port : imageConfig.getPorts()){
                    port.setImageId(null);
                    port.setId(null);
                    port.setStatus("created");
                }
            }
            if(imageConfig.getArgs() !=null){
                for(V2Args argument : imageConfig.getArgs()){
                    argument.setImageId(null);
                    argument.setId(null);
                    argument.setStatus("created");
                }
            }
            if(imageConfig.getEnvs() !=null){
                for(V2Envs env: imageConfig.getEnvs()){
                    env.setImageId(null);
                    env.setId(null);
                }
            }
            if(imageConfig.getProbes() !=null){
                for(V2Probe probe : imageConfig.getProbes()){
                    probe.setImageId(null);
                    probe.setId(null);
                }
            }
            if(imageConfig.getVolumeMounts() !=null){
                for(V2VolumeMounts mount : imageConfig.getVolumeMounts()){
                    mount.setImageId(null);
                    mount.setImageGroupId(null);
                    mount.setId(null);
                }
            }
        }
    }

    /**
     * 根据数据库中的 deployment 信息，创建为新的 DeploymentComponent 对象，方便重新发布
     * @param newestGroup
     * @return
     */
    public DeploymentComponent readDeploymentConfigurations(V2ImageGroup newestGroup){
        DeploymentComponent res = new DeploymentComponent();
        res.setImageGroup(newestGroup);
        // 这里如果直接把数据库中的取出来就给他了，之后再次插入数据库就会提示重复了
        List<V2Labels> newLabels = new ArrayList<>();
        System.out.println("newestGroupId:"+newestGroup.getId());
        List<V2Labels> oldLabels = labelsMapper.selectByImageGroupId(newestGroup.getId());
        System.out.println("oldLabels:"+oldLabels.size());
        if(oldLabels !=null && oldLabels.size() > 0){
            for(V2Labels temp : oldLabels){
                newLabels.add(BeanUtils.convert(temp,V2Labels.class));
            }
            res.setLabels(newLabels);
            System.out.println("newLabels:"+newLabels.size());
        }
        //res.setLabels(BeanUtils.convert(labelsMapper.selectByImageGroupId(newestGroup.getId()), ArrayList.class));

        List<V2Volumes> newVolumes = new ArrayList<>();
        List<V2Volumes> oldVolumes= volumesMapper.selectByImageGroupId(newestGroup.getId());
        if(oldVolumes !=null && oldVolumes.size() > 0){
            for (V2Volumes tmp :oldVolumes){
                newVolumes.add(BeanUtils.convert(tmp, V2Volumes.class));
            }
            res.setVolumes(newVolumes);
        }

        List<V2Image> images = imageMapper.selectByImageGroupId(newestGroup.getId());
        List<ImageComponent> ics = new ArrayList<>();
        for(V2Image image:images){
            ImageComponent temp = new ImageComponent();
            temp.setImage(BeanUtils.convert(image, V2Image.class));

            List<V2Args> oldArgs = argsMapper.selectByImageId(image.getId());
            List<V2Args> newArgs = new ArrayList<>();
            if(oldArgs != null && oldArgs.size() > 0){
                for(V2Args argsTemp : oldArgs){
                    newArgs.add(BeanUtils.convert(argsTemp,V2Args.class));
                }
                temp.setArgs(newArgs);
            }

            List<V2Envs> oldEnvs =envsMapper.selectByImageId(image.getId());
            List<V2Envs> newEnvs = new ArrayList<>();
            if(oldEnvs != null && oldEnvs.size() > 0){
                for(V2Envs envsTemp : oldEnvs){
                    newEnvs.add(BeanUtils.convert(envsTemp,V2Envs.class));
                }
                temp.setEnvs(newEnvs);
            }

            List<V2Ports> oldPorts =portsMapper.selectByImageId(image.getId());
            List<V2Ports> newPorts = new ArrayList<>();
            if(oldPorts != null && oldPorts.size() > 0){
                for(V2Ports portTemp : oldPorts){
                    newPorts.add(BeanUtils.convert(portTemp,V2Ports.class));
                }
                temp.setPorts(newPorts);
            }

            List<V2VolumeMounts> oldVms =volumeMountsMapper.selectByImageId(image.getId());
            List<V2VolumeMounts> newVms = new ArrayList<>();
            if(oldVms != null && oldVms.size() > 0){
                for(V2VolumeMounts vmTemp : oldVms){
                    newVms.add(BeanUtils.convert(vmTemp,V2VolumeMounts.class));
                }
                temp.setVolumeMounts(newVms);
            }

            List<V2Probe> oldProbes = probeMapper.selectByImageId(image.getId());
            List<V2Probe> newProbes = new ArrayList<>();
            System.out.println("oldProbesSize;"+oldProbes.size());
            if(oldProbes != null && oldProbes.size() > 0){
                for(V2Probe tempProbe : oldProbes){
                    newProbes.add(BeanUtils.convert(tempProbe,V2Probe.class));
                }
                temp.setProbes(newProbes);
            }
            System.out.println("oldProbes;"+temp.getProbes());

            List<V2Resources> v2ResourcesList= resourcesMapper.selectByImageId(image.getId());
            if (v2ResourcesList.size() > 0)
                temp.setResources(BeanUtils.convert(v2ResourcesList.get(0), V2Resources.class)); //resource 一个 image 只有一条

            ics.add(temp);
        }
        res.setImages(ics);
        return res;
    }

    /**
     * 从数据库中获得需要灰度的 ImageGroup 列表，targetVersionId 是需要被替换的旧版本
     * @param parameter
     * @return
     */
    public List<V2ImageGroup> getReplaceImageGroups(ImageGroupParameter parameter){
        List<V2ImageGroup> neededReplacedImageGroups = imageGroupMapper.selectByVersionId(parameter.getTargetVersionId());
        //if (neededReplacedImageGroups.size() >= parameter.getReplaceCount()) throw new Exception("需要替换的实例数的个数大于该版本实际运行的实例数，请重新设置");
        if(parameter.getGreyType().equals("number") &&
                neededReplacedImageGroups.size() >= parameter.getReplaceCount()){
            return neededReplacedImageGroups.subList(0,parameter.getReplaceCount());
        }
        else if(parameter.getGreyType().equals("percentage")){
            Double startIndex = (parameter.getReplaceFactor())*neededReplacedImageGroups.size();
            return neededReplacedImageGroups.subList(0,startIndex.intValue());
        }
        return null;
    }

    /**
     * 这个函数的入口是灰度发布，然后根据传入的参数的信息来从数据库中获取需要灰度替换的 deployment 列表
     * @param parameter
     * @return
     */
    public List<DeploymentComponent> getReplaceDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> res = new ArrayList<>();
        List<V2ImageGroup> imageGroups = getReplaceImageGroups(parameter); //获得数据库中的旧的版本的 deployment，然后截取需要灰度的长度
        for(V2ImageGroup temp : imageGroups){
            res.add(readDeploymentConfigurations(temp));
        }
        return res; // 返回旧的 DeploymentComponent 对象
    }

    public void setForeignKeys(ImageGroupParameter parameter,DeploymentComponent temp ){

    }

    /**
     * 这个函数深度复制 ImageGroupParameter parameter 下的 DeploymentComponent，
     * 然后打上自己的 image-group-name，application-real-name、version-id 等标签
     * 返回 DeploymentComponent 的列表
     * @param parameter
     * @return
     */
    public List<DeploymentComponent> getNewDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> res = new ArrayList<>();
        for(int i = 0;i<parameter.getReplica();i++){
            DeploymentComponent temp = BeanUtils.convert(parameter.getDeployment(),parameter.getDeployment().getClass());
            logger.info("根据模板文件创建 realDeployment ："+JSON.toJSONString(temp));
            setSelfDeploymentComponent(temp);
            res.add(temp);
        }
        return res;
    }

    /**
     * 通过 versionId 从数据库获得该版本的 imagegroup 列表
     * 返回 DeploymentComponent 列表，但是
     * @param parameter
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<DeploymentComponent> getShrinkageDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> res = new ArrayList<>();
        List<V2ImageGroup> currentImageGroups = imageGroupMapper.selectByVersionId(parameter.getCurrentVersionId());
        if(currentImageGroups !=null && currentImageGroups.size() > 0){
            for(int i = 0;i<parameter.getShrinkageNumber();i++){ // 选取需要缩容的列表
                DeploymentComponent eachDc = new DeploymentComponent();
                eachDc.setImageGroup(currentImageGroups.get(i));
                res.add(eachDc);
            }
            return res;
        }
        return null;
    }

    /**
     * 从数据库里取的数据做深复制，方便插入
     * @param old
     * @return
     */
    public DeploymentComponent getNewDeploymentFromOld(V2ImageGroup old){
        V2ImageGroup temp = BeanUtils.convert(old,old.getClass());
        // 读取数据库中的 deployment 的配置信息
        DeploymentComponent eachDc = readDeploymentConfigurations(temp);
        // 打必要的标签，以及清空 自动产生的 id 信息
        setNewDeploymentComponent(eachDc);
        return eachDc;
    }

    /**
     * 通过 CurrentVersionId 字段获取当前数据库的该 version 的所有的 deployment
     * @param parameter
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<DeploymentComponent> getExpansionDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> res = new ArrayList<>();
        // 获取该 versionId 的所有 imageGroup
        List<V2ImageGroup> currentImageGroups = imageGroupMapper.selectAllByVersionId(parameter.getCurrentVersionId());
        if(currentImageGroups !=null && currentImageGroups.size() > 0){
            for(int i = 0;i<parameter.getExpansionNumber();i++){
                //复制最新版本的 deployment，然后进行下信息的初始化
                DeploymentComponent eachDc = getNewDeploymentFromOld(currentImageGroups.get(currentImageGroups.size()-1));
                res.add(eachDc);
            }
            return res;
        }
        return null;
    }

    /**
     * 完成 deployment 和 pod 指定下的 container 的数据库插入
     * @param dc
     * @param pod
     */
    public void insertContainers(DeploymentComponent dc,V2Pod pod){
        for(ImageComponent ic : dc.getImages()){
            V2Container container = new V2Container(pod,ic,dc);
            containerMapper.insertSelective(container);
        }
    }

    /**
     * 灰度已经做完了灰度量的校验，并且切出了需要被升级的 List<DeploymentComponent>，
     * 以及通过深度拷贝和拼接产生的新的 List<DeploymentComponent>
     * 这个函数负责将新的部分的数据完成在数据库中的存储。
     * @param newDeployments
     * @param oldDeployments
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean greyResult(List<DeploymentComponent> newDeployments,List<DeploymentComponent> oldDeployments){
        if(oldDeployments !=null){
            for( int i = 0;i<oldDeployments.size() && oldDeployments.size() == newDeployments.size();i++){
                this.insertImageGroup(newDeployments.get(i));
                this.insertImages(newDeployments.get(i));
                //添加一个 replaceOldParentId 字段即可，标记需要替换的 imageGroup 的 id，只有设置了这个字段，才会执行灰度
                V2Pod pod = new V2Pod(newDeployments.get(i),oldDeployments.get(i));
                podMapper.insertSelective(pod); //完成pod的替换id字段的填写以及整个新的 pod 的数据库插入
                this.insertContainers(newDeployments.get(i),pod);
            }
            return  new ResultBean(true,"所有的实例参数已经保存,当新实例启动成功后会替换旧实例！");
        }
        else{
            for( int i = 0;i<newDeployments.size();i++){
                this.insertImageGroup(newDeployments.get(i));
                this.insertImages(newDeployments.get(i));
                V2Pod pod = new V2Pod(newDeployments.get(i));
                podMapper.insertSelective(pod); //完成pod的替换id字段的填写以及整个新的 pod 的数据库插入
                this.insertContainers(newDeployments.get(i),pod);
            }
            return  new ResultBean(true,"所有的实例参数已经保存,用户没有选择灰度！");
        }
    }

    /**
     * 通过 versionId 来查询数据库中的该版本的所有的 V2ImageGroup，转换成 DeploymentComponent
     * @param versionId
     * @return
     */
    public List<DeploymentComponent> imageGroups2RawDeploymentComponents(Integer versionId){
        List<V2ImageGroup> currentBadGroups = imageGroupMapper.selectByVersionId(versionId);
        List<DeploymentComponent> currentBadDeployments = new ArrayList<>();
        for(V2ImageGroup temp :currentBadGroups){
            DeploymentComponent eachDeployment  =new DeploymentComponent();
            eachDeployment.setImageGroup(temp);
            currentBadDeployments.add(eachDeployment);
        }
        return currentBadDeployments;
    }

    public List<DeploymentComponent> imageGroups2RawDeploymentComponentsAll(Integer versionId){
        List<V2ImageGroup> currentBadGroups = imageGroupMapper.selectAllByVersionId(versionId);
        List<DeploymentComponent> currentBadDeployments = new ArrayList<>();
        for(V2ImageGroup temp :currentBadGroups){
            DeploymentComponent eachDeployment  =new DeploymentComponent();
            eachDeployment.setImageGroup(temp);
            currentBadDeployments.add(eachDeployment);
        }
        return currentBadDeployments;
    }

    //回滚升级，扩容当前版本时完全替换旧版本，100%替换（灰度的特殊情况）
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertRollingDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> currentDeployments = imageGroups2RawDeploymentComponents(parameter.getCurrentVersionId());
        List<DeploymentComponent> targetDeployments = imageGroups2RawDeploymentComponentsAll(parameter.getTargetVersionId());
        List<DeploymentComponent> newDeployments = new ArrayList<>();
        for(int i = 0 ;i<currentDeployments.size();i++){
            newDeployments.add(getNewDeploymentFromOld(targetDeployments.get(targetDeployments.size()-1).getImageGroup()));
        }
        parameter.setRealDeployments(newDeployments);
        return greyResult(newDeployments,currentDeployments); //插入新的 deployment 数据，所有更替操作的 删除部分 都放在 watcher 里面做
    }

    // 扩容不需要灰度发布
    //扩容当前版本
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertExpansionDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> newDeployments = getExpansionDeployments(parameter);
        for (DeploymentComponent newDeployment : newDeployments) {
            this.insertImageGroup(newDeployment);
            this.insertImages(newDeployment);
            //不需要灰度发布
            V2Pod pod = new V2Pod(newDeployment);
            podMapper.insertSelective(pod);
            this.insertContainers(newDeployment,pod);
        }
        parameter.setRealDeployments(newDeployments);
        return  new ResultBean(true,"需要增加的实例已经计划发布！");
    }

    //缩容当前版本

    /**
     * 获取缩容数量的 deployment，然后更新他们在数据库中的状态
     * @param parameter
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean deleteShrinkageDeployments(ImageGroupParameter parameter){
        List<DeploymentComponent> oldDeployments = getShrinkageDeployments(parameter); //获取需要缩容的列表
        for( int i = 0;i<oldDeployments.size() ;i++){
            //更新对应的状态deployment，pod：
            oldDeployments.get(i).getImageGroup().setStatus("Deleted");
            oldDeployments.get(i).getImageGroup().setUpdateTime(new Date());
            imageGroupMapper.updateByPrimaryKeySelective(oldDeployments.get(i).getImageGroup());// 更新 imagegroup 的状态数据

            V2Pod pod = podMapper.selectByImageGroupId(oldDeployments.get(i).getImageGroup().getId()).get(0); // 根据每个 imagegroupId 获取 pod
//            pod.setStatus("Deleting"); //更新 pod 的状态
//            pod.setUpdateTime(new Date()); //更新 pod 的状态更新时间
//            pod.setIfDeleteParent(oldDeployments.get(i).getImageGroup().getId()); //人为缩容，把 pod 所属的 imagegroupId 赋值给 Pod 的 ifDeleteParent 字段
            if(pod !=null && pod.getId() != null){
                podMapper.deleteByPrimaryKey(pod.getId());
                List<V2Container> containers = containerMapper.selectByPodId(pod);
                if(containers !=null && containers.size() > 0){
                    for(V2Container container : containers){
                        containerMapper.deleteByPrimaryKey(container.getId());
                    }
                }
            }

        }
        parameter.setRealDeployments(oldDeployments); // 把 ImageGroupParameter 的 realDeployment 赋值为旧的这些 imageGroupList
        return  new ResultBean(true,"需要删除的实例已经计划删除！");
    }

    //重新发布当前版本，一个版本不同配置的灰度
    //重新发布当前版本，不需要制定 发布替换数量
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertGreyInstanceInfoOnTheSameVersion(ImageGroupParameter parameter){
        // currentVersionId 这个字段是仅在重新发布的场景中才会设置，即需要被重新发布的版本的所有的 DeploymentComponent
        List<DeploymentComponent> currentBadDeployments = imageGroups2RawDeploymentComponents(parameter.getCurrentVersionId());
        // 进行重新发布时，数量是不需要指定的
        parameter.setReplica(currentBadDeployments.size());
        System.out.println("currentBadDeployments.size():"+currentBadDeployments.size());
        // 重新给这些 deployment 起名字
        parameter.setRealDeployments(getNewDeployments(parameter));
        // 重新发布，deployment 的名字是可以重新启的，其他的所有的配置也可以都重新指定

        // 有了需要替换的和替换的，就可以做灰度发布了
        return greyResult(parameter.getRealDeployments(),currentBadDeployments);
    }

    //灰度发布新版本
    //如果灰度不指定 targetVersionId 就相当于发布新版本

    /**
     * 完成了 parameter 的 replica 参数的更新，换成了灰度发布的目标 Deployment 的数量
     * @param parameter
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertGreyInstanceInfo(ImageGroupParameter parameter){
        if(parameter.getTargetVersionId() !=null){
            List<DeploymentComponent> oldDeployments = getReplaceDeployments(parameter); // 获取需要灰度的 deployment 列表
            parameter.setReplica(oldDeployments.size());
            parameter.setRealDeployments(getNewDeployments(parameter)); // 设置好了 replica 后，模板就有了，可以创建 deployment 列表了
            return greyResult(parameter.getRealDeployments(),oldDeployments);
        }
        else{
            parameter.setRealDeployments(getNewDeployments(parameter));
            return greyResult(parameter.getRealDeployments(),null);
        }
    }

    //可能发布新应用或者直接发布新应用版本
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertInstanceInfo(ImageGroupParameter parameter){
        if (parameter.getDeployment() == null){
            return new ResultBean(false,"数据无效！"); //如果 deployment 的 imageGroup 为空
        }
        parameter.setRealDeployments(getNewDeployments(parameter));
        logger.info(">>>>>>>>>>>>开始插入数据库的 ImageGroup");
        for (DeploymentComponent component:parameter.getRealDeployments()) {
            logger.info(">>>>>>>>>>>>开始插入数据库的 ImageGroup");
           this.insertImageGroup(component);
           this.insertImages(component);

            V2Pod pod = new V2Pod(component);
            podMapper.insertSelective(pod);
            this.insertContainers(component,pod);
        }
        return  new ResultBean(true,"所有发布新实例参数已经保存！");
    }

    //应用复制
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean copyInstanceInfo(ImageGroupParameter parameter){
//        List<ImageComponent> images;
//        try {
//            images = parameter.getDeployment().getImages();
//        }catch (NullPointerException e){
//            images = null;
//        }
//        if (images == null){
//            List<V2ImageGroup> imageGroupList = imageGroupMapper.selectAllByVersionId(parameter.getAnotherApplicationVersionId());
//            V2ImageGroup template = imageGroupList.get(0);
//            DeploymentComponent dc = readDeploymentConfigurations(template);
//            setNewDeploymentComponent(dc);
//            //DeploymentComponent deploymentComponent = new DeploymentComponent();
//            parameter.setDeployment(dc);
//            parameter.getDeployment().setImages(dc.getImages());
//            parameter.getDeployment().setVolumes(dc.getVolumes());
//            parameter.getDeployment().setLabels(dc.getLabels());
//            parameter.getDeployment().setImageGroup(dc.getImageGroup());
//            //parameter.setReplica(imageGroupList.size());
//
//        }
        return  insertInstanceInfo(parameter);
    }

    //创建网络
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertNetworkPolicy(NetworkPolicyParameter params) throws NullPointerException,Exception{
        try {
            //获得 params 的 network，labels，ports 分别检查和插入
            V2NetworkPolicy networkPolicy = ((NetworkPolicyParameter) params).getNetwork();
            List<V2NetworkLabels> labels_list = ((NetworkPolicyParameter) params).getLabels();
            List<V2NetworkPorts> ports_list = ((NetworkPolicyParameter) params).getPorts();
            List<Integer> envIdList = params.getEnvIdList();
            //1、创建数据库对象 2、检测是否有重复数据 3、插入新数据
            for (Integer envId:envIdList) {
                if (networkPolicyMapper.checkIsExist(envId,
                        networkPolicy.getObjectName()) > 0){
//                    ResultBean resultBean = new ResultBean();
//                    resultBean.setSuccess(false);
//                    resultBean.setMessage("该 NetworkPolicy 资源已经存在");
//                    return resultBean; //如果 networkpolicy 已经存在，返回
                    throw new Exception("已存在同名网络策略");
                }
            }

            logger.info("对环境"+networkPolicy.getEnvId()+"添加了一个网络："+networkPolicy.getObjectName());

            List<V2NetworkPolicy> networkPolicies= new ArrayList<>();

            for (Integer envId:envIdList){
                V2NetworkPolicy newNetworkPolicy = BeanUtils.convert(networkPolicy, networkPolicy.getClass());
                newNetworkPolicy.setEnvId(envId);
                networkPolicies.add(newNetworkPolicy);
            }
            for (V2NetworkPolicy net:networkPolicies){
                networkPolicyMapper.insertSelective(net);
                for (V2NetworkLabels label:labels_list) { //label 因为已经在 Application 做了校验，所以如果 networkpolicy 通过，他就肯定能通过
                    V2NetworkLabels newLabel = BeanUtils.convert(label,label.getClass());
                    newLabel.setNetworkPolicyId(net.getId());
                    networkLabelsMapper.insertSelective(newLabel);
                }
                for (V2NetworkPorts port:ports_list) { //label 因为已经在 Application 做了校验，所以如果 networkpolicy 通过，他就肯定能通过
                    V2NetworkPorts newPort = BeanUtils.convert(port, port.getClass());
                    newPort.setNetworkId(net.getId());
                    networkPortsMapper.insertSelective(newPort);
                }
            }

            ResultBean resultBean = new ResultBean();
            resultBean.setSuccess(true);
            resultBean.setMessage("数据库插入成功");
            return resultBean;
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("传入的参数为空");
            throw exception;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public void getNetworkPolicy(NetworkPolicyParameter params){
        params.setNetwork(networkPolicyMapper.selectByPrimaryKey(params.getNetwork().getId()));
    }

    public List<V2NetworkPolicy> getNetworkPolicyByName(NetworkPolicyParameter params){
        return networkPolicyMapper.selectByNetworkName(params.getNetwork().getObjectName());
    }

    /**
     * 删除网络时需要考虑到，后台的一个网络对应数据库中的多个网络记录，所以删除一个网络需要删除该网络的多条记录
     * @param params
     * @return
     */
    public ResultBean deleteNetworkPolicy(NetworkPolicyParameter params){
        ResultBean resultBean = new ResultBean();
        networkPolicyMapper.deleteByPrimaryKey(params.getNetwork().getId());
        networkLabelsMapper.deleteByNetworkPolicyId(params.getNetwork().getId());
        networkPortsMapper.deleteByNetworkPolicyId(params.getNetwork().getId());
        resultBean.setSuccess(true);
        resultBean.setMessage("数据库删除成功");
        return resultBean;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean insertService(ServiceParameter sp, KubernetesClient client){

        //在后台把所有的数据库中该应用下的所有 service 按照名字删除
        List<V2Svc> v2SvcList = svcMapper.selectAllByApplicationIdNotPage(sp.getService().getApplicationId());
        if (v2SvcList != null && v2SvcList.size() > 0){
            for (V2Svc v2Svc:v2SvcList) {
                try {
                    KubernetesUtil.deleteSvc(v2Svc.getNamespace(), v2Svc.getSvcName(), client);
                } catch (Exception e) {
                    logger.info("创建服务过程中，删除同名服务，后台 k8s 发生未知异常");
                }
                svcPortsMapper.deleteAllByServiceId(v2Svc.getId());
                svcLabelsMapper.deleteAllByServiceId(v2Svc.getId());
            }
            // 依据应用的名字来判定是否数据库中存在同应用名的服务，如果有，删除并重新创建
            svcMapper.deleteAllByApplicationId(sp.getService().getApplicationId());
            logger.info("创建服务，先把同应用名:"+sp.getService().getApiversion()+"下的服务删除。");
        }

        //删除干净同名服务后，开始插入新的服务
        V2Svc v2Svc = sp.getService();
        svcMapper.insertSelective(v2Svc);
        System.out.println("svcId:"+v2Svc.getId());
        for(V2SvcLabels label : sp.getLabels()){
            label.setSvcId(v2Svc.getId());
            svcLabelsMapper.insertSelective(label);
        }
        for(V2SvcPorts port : sp.getPorts()){
            port.setSvcId(v2Svc.getId());
            svcPortsMapper.insertSelective(port);
        }
        ResultBean resultBean = new ResultBean();
        resultBean.setSuccess(true);
        resultBean.setMessage("数据库插入成功");
        return resultBean;
    }

    public void getService(ServiceParameter sp){
        sp.setService(svcMapper.selectByPrimaryKey(sp.getService().getId()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean deleteService(ServiceParameter sp) throws Exception{
        ResultBean resultBean = new ResultBean();
        try {
            svcMapper.deleteByPrimaryKey(sp.getService().getId());
            svcLabelsMapper.deleteAllByServiceId(sp.getService().getId());
            svcPortsMapper.deleteAllByServiceId(sp.getService().getId());
        }catch (Exception e){
            logger.info("删除服务。删除V2Svc数据库表时发生未知错误");
            e.printStackTrace();
            throw new Exception("删除服务。删除V2Svc数据库表时发生未知错误");
        }


        resultBean.setSuccess(true);
        resultBean.setMessage("数据库删除成功");
        return resultBean;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResultBean deleteService(Integer svcId) throws Exception{
        ResultBean resultBean = new ResultBean();
        try {
            svcMapper.deleteByPrimaryKey(svcId);
            svcLabelsMapper.deleteAllByServiceId(svcId);
            svcPortsMapper.deleteAllByServiceId(svcId);
        }catch (Exception e){
            logger.info("删除服务。删除V2Svc数据库表时发生未知错误");
            e.printStackTrace();
            throw new Exception("删除服务。删除V2Svc数据库表时发生未知错误");
        }


        resultBean.setSuccess(true);
        resultBean.setMessage("数据库删除成功");
        return resultBean;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void convertV2Container(List<V2Container> containerList){
        if (containerList == null || containerList.size() == 0) return;
        for (V2Container v2Container:containerList) {
            try {
                V2ImageGroup v2ImageGroup = imageGroupMapper.selectByPrimaryKey(v2Container.getImageGroupId());
                if (v2ImageGroup == null) logger.info("V2Container 的 imageGroupId 查询不到对应的 V2ImageGroup");
                //获得 imageGroup 的 realname
                //logger.info("V2Container 的 imageGroupId 查询到了V2ImageGroup。v2Container 为"+JSONObject.toJSONString(v2Container));
                v2Container.setImageGroupRealName(v2ImageGroup.getRealName());
                //获得 application 的 realname
                v2Container.setApplicationRealName(v2ImageGroup.getParentName());
                //开始裁剪 podname 为 应用名字
                String podName = v2Container.getPodName();
//                if(podName != null){
//                    Integer projectId = v2Container.getProjectId();
//                    Integer index = podName.indexOf("-"+projectId.toString());
//                    v2Container.setPodName(podName.substring(0, index));
//                    logger.info("V2Container 转换完，结果为:"+ JSON.toJSONString(v2Container));
//                }
            }catch (Exception e){
                e.printStackTrace();
                logger.info("container 获得 imageGroup 的 realname 和 parentname，数据库查找出现未知异常，跳过");
            }

        }


    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer getRunningInstanceNumberOfTargetVersion(Integer targetVersionId) throws Exception{
        try {
            return imageGroupMapper.selectByVersionId(targetVersionId).size();

        }catch (Exception e){
            logger.info("查询需要替换的版本运行实例数出现异常");
            throw new Exception("查询需要替换的版本运行实例数出现异常");
        }

    }
}
