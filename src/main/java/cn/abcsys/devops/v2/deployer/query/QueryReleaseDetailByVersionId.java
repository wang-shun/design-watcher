/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;
/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/13 14:51
 * @File QueryApplicationDetailsByApplicationId.java
 */
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.*;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.query.mdoel.ApplicationDetails;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryReleaseDetailByVersionId")
public class QueryReleaseDetailByVersionId implements IQuery{

    @Resource(name="v2LabelsMapper")
    private V2LabelsMapper labelsMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ImageMapper")
    private V2ImageMapper imageMapper;

    @Resource(name="v2PortsMapper")
    private V2PortsMapper portsMapper;

    @Resource(name="v2ProbeMapper")
    private V2ProbeMapper probeMapper;

    @Resource(name="v2ResourcesMapper")
    private V2ResourcesMapper resourcesMapper;

    @Resource(name="v2VolumesMapper")
    private V2VolumesMapper volumesMapper;

    @Resource(name="v2VolumeMountsMapper")
    private V2VolumeMountsMapper volumeMountsMapper;

    @Resource(name="v2ArgsMapper")
    private V2ArgsMapper argsMapper;

    @Resource(name="v2EnvsMapper")
    private V2EnvsMapper envsMapper;

    private static Logger logger = Logger.getLogger(QueryReleaseDetailByVersionId.class);


    private ImageGroupParameter setDeployment(ImageGroupParameter igp,Integer versionId){
        DeploymentComponent deploymentComponent = new DeploymentComponent();

        List<V2ImageGroup> imageGroupList = imageGroupMapper.selectAllByVersionId(versionId);
        if(imageGroupList != null && imageGroupList.size() > 0){
            V2ImageGroup v2ImageGroup = imageGroupList.get(imageGroupList.size()-1); //一个版本有多个实例，取最新的一个

            deploymentComponent.setImageGroup(v2ImageGroup);

            deploymentComponent.setDeployType("createDeployment");

            List<ImageComponent> imageComponentList = new ArrayList<>();
            List<V2Image> v2ImageList = imageMapper.selectByImageGroupId(v2ImageGroup.getId());
            for (V2Image eachImage:v2ImageList) {
                ImageComponent imageComponent = new ImageComponent();

                imageComponent.setImage(eachImage);

                List<V2Resources> tmp_resource_result = resourcesMapper.selectByImageId(eachImage.getId());
                if (tmp_resource_result != null) imageComponent.setResources(tmp_resource_result.get(0));//按道理每个 image 只有一个 resource 记录，但是因为 imageId 不是主键，所以返回为List，实际上只有一条数据

                List<V2Args> tmp_args_result = argsMapper.selectByImageId(eachImage.getId());
                if (tmp_args_result != null) imageComponent.setArgs(tmp_args_result);

                List<V2VolumeMounts> tmp_volumeMounts_result = volumeMountsMapper.selectByImageId(eachImage.getId());
                if (tmp_volumeMounts_result != null) imageComponent.setVolumeMounts(tmp_volumeMounts_result);

                List<V2Probe> tmp_probe_result = probeMapper.selectByImageId(eachImage.getId());
                if (tmp_probe_result != null) imageComponent.setProbes(tmp_probe_result);

                List<V2Envs> tmp_envs_result = envsMapper.selectByImageId(eachImage.getId());
                if (tmp_envs_result != null) imageComponent.setEnvs(tmp_envs_result);

                List<V2Ports> tmp_ports_result = portsMapper.selectByImageId(eachImage.getId());
                if (tmp_ports_result != null) imageComponent.setPorts(tmp_ports_result);

                imageComponentList.add(imageComponent);
            }
            deploymentComponent.setImages(imageComponentList);

            //List<V2Labels> labelsList = labelsMapper.selectByImageGroupId(v2ImageGroup.getId());
            deploymentComponent.setLabels(labelsMapper.select3DistinctByImageGroupId(v2ImageGroup.getId()));

            deploymentComponent.setVolumes(volumesMapper.selectByImageGroupId(v2ImageGroup.getId()));

            igp.setDeployment(deploymentComponent);

            igp.setReplica(imageGroupList.size());
            igp.setProjectId(v2ImageGroup.getProjectId());
            igp.setApplicationId(v2ImageGroup.getApplicationId());
            igp.setCurrentVersionId(versionId);
            igp.setEnvId(v2ImageGroup.getEnvId());
            igp.setDeployType("createDeployment");
            igp.setPlatfromType("kubernetes-deployment");
        }
        return igp;
    }

    private void volumePathPre(String path, String split){
        path = path.split(split)[0];
    }

    private ImageGroupParameter getDetails(QueryEnvIdParameter qep){
        ImageGroupParameter parameter = new ImageGroupParameter();
        parameter = setDeployment(parameter, qep.getVersionId());
        return parameter;
    }

    public static GridBean getSingleResult(Object row){
        return new GridBean(1,1,1,new ArrayList<Object>(){
            {
                add(row);
            }
        },true);
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            //logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            ImageGroupParameter ad = this.getDetails(qep); // 获取该 version 的 imagegroup 信息
            // 开始转换成 ApplicationParameter
            ApplicationParameter applicationParameter = new ApplicationParameter();
            applicationParameter.setApiVersion(ad.getDeployment().getImageGroup().getApiVersion()); // 没有
            applicationParameter.setDeploymentName(ad.getDeployment().getImageGroup().getImageGroupName());
            applicationParameter.setEnvId(ad.getEnvId());
            applicationParameter.setId(ad.getApplicationId());

            List<ApplicationComponent> images = new ArrayList<>();
            try {
                for (ImageComponent imageComponent:ad.getDeployment().getImages()) {
                    ApplicationComponent applicationComponent = new ApplicationComponent();
                    List<Args> argsList = new ArrayList<>();
                    try {
                        for (V2Args v2Args:imageComponent.getArgs()) {
                            Args args = new Args();
                            args.setParameter(v2Args.getParameter());
                            argsList.add(args);
                        }
                    }catch (NullPointerException e){
                        logger.info("imagegroupParameter 的 getArgs 为空");
                    }
                    applicationComponent.setArgs(argsList);

                    applicationComponent.setContainerName(imageComponent.getImage().getContainerName());

                    List<EnvironmentVariables> environmentVariablesList = new ArrayList<>();
                    try {
                        for (V2Envs v2Envs:imageComponent.getEnvs()){
                            EnvironmentVariables environmentVariables = new EnvironmentVariables();
                            environmentVariables.setKey(v2Envs.getEnvsKey());
                            environmentVariables.setValue(v2Envs.getEnvsValue());
                            environmentVariablesList.add(environmentVariables);
                        }
                    }catch (NullPointerException e){
                        logger.info("imagegroupParameter 的 getEnvs 为空");
                    }
                    applicationComponent.setEnvs(environmentVariablesList);

                    applicationComponent.setId(imageComponent.getImage().getId());

                    applicationComponent.setImageType(imageComponent.getImage().getImageType()); //暂时可缺省

                    applicationComponent.setName(imageComponent.getImage().getImageName());

                    List<Ports> portsList = new ArrayList<>();
                    try {
                        for (V2Ports v2Ports:imageComponent.getPorts()){
                            Ports ports = new Ports();
                            ports.setName(v2Ports.getPortName());
                            ports.setNodePort(null); // 这里先不填
                            ports.setPort(v2Ports.getPortValue());
                            ports.setProtocol(v2Ports.getProtocol());
                            portsList.add(ports);
                        }
                    }catch (NullPointerException e){
                        logger.info("imagegroupParameter 的 getPorts 为空");
                    }
                    applicationComponent.setPorts(portsList);

                    List<Probes> probesList = new ArrayList<>();
                    try {
                        for (V2Probe v2Probe: imageComponent.getProbes()) {
                            Probes probes = new Probes();
                            probes.setDelaySeconds(v2Probe.getInitialDelaySeconds());
                            probes.setFailureThreshold(v2Probe.getFailureThreshold());
                            probes.setPath(v2Probe.getProbePath());
                            probes.setPort(v2Probe.getProbePort());
                            probes.setRequestType(v2Probe.getRequestType());
                            probes.setScheme(v2Probe.getScheme());
                            probes.setSuccessThreshold(v2Probe.getSuccessThreshold());
                            probes.setTimeoutSeconds(v2Probe.getTimeoutSeconds());
                            probes.setType(v2Probe.getProbeType());
                            probesList.add(probes);
                        }
                    }catch (NullPointerException e){
                        logger.info("imagegroupParameter 的 getProbes 为空");
                    }
                    applicationComponent.setProbes(probesList);

                    Resources resources = new Resources();
                    resources.setMaxCpu(imageComponent.getResources().getMaxCpu());
                    resources.setMaxMem(imageComponent.getResources().getMaxMem());
                    resources.setMinCpu(imageComponent.getResources().getMinCpu());
                    resources.setMinMem(imageComponent.getResources().getMinMem());
                    resources.setNetwork(imageComponent.getResources().getNetwork());
                    applicationComponent.setResources(resources);


                    applicationComponent.setSecret(imageComponent.getImage().getPullSecret());
                    applicationComponent.setStorageTime(null); //先暂缺
                    applicationComponent.setVersion(imageComponent.getImage().getImageTag());

                    List<Volumes> volumesList = new ArrayList<>();
                    try {
                        for (V2VolumeMounts v2VolumeMounts:imageComponent.getVolumeMounts()) {
                            Volumes volumes = new Volumes();
                            volumes.setMountPath(v2VolumeMounts.getMountPath());
                            volumes.setName(v2VolumeMounts.getVolumeMountName());
                            volumes.setPath(v2VolumeMounts.getMountPath());
                            volumes.setType(null); // 和同名 volume 相同
                            volumesList.add(volumes);
                        }
                    }catch (NullPointerException e){
                        logger.info("imagegroupParameter 的 getVolumeMounts 为空");
                    }
                    applicationComponent.setVolumeMounts(volumesList);
                    images.add(applicationComponent);
                }
            }catch (NullPointerException e){
                logger.info("imagegroupParameter 转化为 applicationParameter 时 images 为空");
            }
            applicationParameter.setImages(images);

            applicationParameter.setKind(ad.getDeployment().getImageGroup().getKind());

            List<Labels> labelsList = new ArrayList<>();
            try {
                for (V2Labels v2Labels:ad.getDeployment().getLabels()) {
                    Labels labels = new Labels();
                    labels.setKey(v2Labels.getLabelKey());
                    labels.setType(v2Labels.getLabelType());
                    labels.setValue(v2Labels.getLabelValue());
                    labelsList.add(labels);
                }
            }catch (NullPointerException e){
                logger.info("imagegroupParameter 转化为 applicationParameter 时 Labels 为空");
            }
            applicationParameter.setLabels(labelsList);

            applicationParameter.setMasterIp(ad.getDeployment().getImageGroup().getMasterIp());
            applicationParameter.setMasterPort(ad.getDeployment().getImageGroup().getMasterPort());
            applicationParameter.setMasterType(ad.getDeployment().getImageGroup().getMasterType());
            applicationParameter.setName(ad.getDeployment().getImageGroup().getImageGroupName());
            applicationParameter.setNamespace(ad.getDeployment().getImageGroup().getNamespace());
            applicationParameter.setPlatformType(ad.getPlatfromType());
            applicationParameter.setProjectId(ad.getDeployment().getImageGroup().getProjectId());
            applicationParameter.setRealName(null); // 不需要写
            applicationParameter.setReplaceCount(null);  //不需要写
            applicationParameter.setReplaceFactor(null);
            applicationParameter.setReplica(null);
            applicationParameter.setTargetVersionId(null);
            applicationParameter.setUrl(null);  //暂时不写
            applicationParameter.setVersionDescription(null);
            applicationParameter.setVersionId(ad.getDeployment().getImageGroup().getVersionId());
            applicationParameter.setVersionName(null);  //先不写,这个要到 application 里面去取
            applicationParameter.setVersionType("Deployment");

            List<Volumes> volumesList = new ArrayList<>();
            try{
                for (V2Volumes v2Volumes:ad.getDeployment().getVolumes()) {
                    Volumes volumes = new Volumes();
                    volumes.setType(v2Volumes.getVolumeType());
                    // 这里需要对查出来的 hostPath 做处理，type 为 hostPath 的时候，路径存的是真实路径，
                    // 类似 /abcs/data/xyjfullyrealtest-20171019185119061\xyjfullyrealtest-v1-6b721\
                    // 我们需要 /abcs/data
                    // 因此匹配应用名称，然后做字符串截断
                    String realhostPath = v2Volumes.getHostPath();
                    logger.info("volumeType:"+ v2Volumes.getVolumeType());
                    if (v2Volumes.getVolumeType().equals("hostPath") || v2Volumes.getVolumeType().equals("logPath")){
                        realhostPath = realhostPath.split(ad.getDeployment().getImageGroup().getParentName())[0];
                    }
                    if(v2Volumes.getVolumeType().equals("applicationDataPath")
                            && v2Volumes.getHostPath().contains(ad.getDeployment().getImageGroup().getParentName())){
                        realhostPath = realhostPath.split(ad.getDeployment().getImageGroup().getParentName())[0];
                    }
                    volumes.setPath(realhostPath);

                    volumes.setName(v2Volumes.getVolumeName());
                    volumes.setMountPath(realhostPath);
                    volumesList.add(volumes);
                }
            }catch (NullPointerException e){
                logger.info("imagegroupParameter 转化为 applicationParameter 时 Volumes 为空");
            }
            applicationParameter.setVolumes(volumesList);

            //logger.info(">>>>>>>>>>>>>>>>>>>>>>"+JSON.toJSONString(ad));
            return getSingleResult(applicationParameter);
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("传入参数为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
