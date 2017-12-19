package cn.abcsys.devops.v2.deployer.delete;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDelete;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.*;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2Image;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import cn.abcsys.devops.v2.deployer.executors.kubernetes.CreateDeployment;
import com.alibaba.fastjson.JSONObject;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("deleteVersion")
public class DeleteVersion implements IDelete{

    private static Logger logger = Logger.getLogger(DeleteVersion.class);

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper v2ContainerMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper v2ImageGroupMapper;

    @Resource(name="v2ImageMapper")
    private V2ImageMapper v2ImageMapper;

    @Resource(name="v2EnvsMapper")
    private V2EnvsMapper v2EnvsMapper;

    @Resource(name="v2VolumeMountsMapper")
    private V2VolumeMountsMapper v2VolumeMountsMapper;

    @Resource(name="v2PodMapper")
    private V2PodMapper v2PodMapper;

    @Resource(name="v2ArgsMapper")
    private V2ArgsMapper v2ArgsMapper;

    @Resource(name="v2VolumesMapper")
    private V2VolumesMapper v2VolumesMapper;

    @Resource(name="v2ResourcesMapper")
    private V2ResourcesMapper v2ResourcesMapper;

    @Resource(name="v2PortsMapper")
    private V2PortsMapper v2PortsMapper;

    @Resource(name="v2ProbeMapper")
    private V2ProbeMapper v2ProbeMapper;

    @Resource(name="v2LabelsMapper")
    private V2LabelsMapper v2LabelsMapper;

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2SvcMapper")
    protected V2SvcMapper v2SvcMapper;


    public void deleteVolumeMount (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2VolumeMountsMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2VolumeMount 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2VolumeMount 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2VolumeMount 表删除完毕");

    }

    public void deleteEnv (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2EnvsMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Env 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Env 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Env 表删除完毕");

    }

    public void deleteProbe (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2ProbeMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Probe 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Probe 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Probe 表删除完毕");

    }

    public void deletePort (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2PortsMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Port 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Port 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Port 表删除完毕");

    }

    public void deleteResources (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2ResourcesMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Resources 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Resources 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Resources 表删除完毕");

    }

    public void deleteArgs (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2ArgsMapper.deleteByImageId(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Args 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Args 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Args 表删除完毕");

    }

    public void deleteImages (List<V2Image> v2ImageList) throws Exception{
        try {
            for (V2Image v2Image:v2ImageList) {
                v2ImageMapper.deleteByPrimaryKey(v2Image.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除的该实例的镜像数为 null");
        }catch (Exception e){
            logger.error("V2Image 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Image 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Image 表删除完毕");

    }

    public void deleteVolumes (List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        try {
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                v2VolumesMapper.deleteByImageGroupId(v2ImageGroup.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
        }catch (Exception e){
            logger.error("V2Volumes 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Volumes 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Volumes 表删除完毕");

    }

    public void deleteContainers (List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        try {
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                v2ContainerMapper.deleteByImageGroupId(v2ImageGroup.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
        }catch (Exception e){
            logger.error("V2Containers 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Containers 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Containers 表删除完毕");

    }

    public void deletePods (List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        try {
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                v2PodMapper.deleteByImageGroupId(v2ImageGroup.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
        }catch (Exception e){
            logger.error("V2Pods 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Pods 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Pods 表删除完毕");

    }

    public void deleteLabels (List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        try {
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                v2LabelsMapper.deleteByImageGroupId(v2ImageGroup.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
        }catch (Exception e){
            logger.error("V2Labels 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2Labels 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2Labels 表删除完毕");

    }

    public void deleteImageGroups (List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        try {
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                v2ImageGroupMapper.deleteByPrimaryKey(v2ImageGroup.getId());
            }
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
        }catch (Exception e){
            logger.error("V2ImageGroup 表删除不成功");
            e.printStackTrace();
            throw new Exception("V2ImageGroup 表删除不成功"+e.getClass()+e.getMessage());
        }
        logger.info("删除版本：V2imagegroup 表删除完毕");

    }
    public List<V2ImageGroup> getImageGroups (DeleteReleaseParameter deleteReleaseParameter) throws Exception{
        try {
            List<V2ImageGroup> imageGroupList= v2ImageGroupMapper.selectAllByVersionId(deleteReleaseParameter.getVersionId());
            return imageGroupList;
        }catch (Exception e){
            logger.error("通过 versionId 在数据库中查询所有的 imageGroup 失败");
            e.printStackTrace();
            throw new Exception("通过 versionId 在数据库中查询所有的 imageGroup 失败"+e.getMessage()+e.getClass());
        }

    }

    public List<V2Image> getImages (Integer imageGroupId) throws Exception{
        try {
            List<V2Image> imageList= v2ImageMapper.selectByImageGroupId(imageGroupId);
            return imageList;
        }catch (Exception e){
            logger.error("通过 imageGroupId 在数据库中查询所有的 image 失败");
            e.printStackTrace();
            throw new Exception("通过 imageGroupId 在数据库中查询所有的 image 失败"+e.getMessage()+e.getClass());
        }

    }

    public List<V2Image> getAllImagesByImageGroupList (List<V2ImageGroup> v2ImageGroupList) throws Exception{
        try {
            List<V2Image> imageList_result = new ArrayList<>();
            for (V2ImageGroup v2imageGroup:v2ImageGroupList) {
                List<V2Image> imageList= getImages(v2imageGroup.getId());
                imageList_result.addAll(imageList);
            }
            return imageList_result;
        }catch (NullPointerException e){
            logger.info("删除版本的实例数为 null");
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("获得 version 下所有 images 失败"+e.getMessage());
        }
    }

    public void deleteDeploymentInK8s(List<V2ImageGroup> v2ImageGroupList) throws NullPointerException, Exception{
        KubernetesClient client = null;
        try {
            client = KubernetesUtil.getClient(v2ImageGroupList.get(0).getMasterType(),
                    v2ImageGroupList.get(0).getMasterIp(), v2ImageGroupList.get(0).getMasterPort());
            for(V2ImageGroup v2ImageGroup :v2ImageGroupList){
                KubernetesUtil.deleteDeployment(v2ImageGroup.getNamespace(),
                        v2ImageGroup.getRealName() ,(KubernetesClient)client); //开始删除急群中的相关 deployment
            }
        }catch (NullPointerException e){
            logger.info("该版本的实例数为0"+ e.getMessage()+e.getClass());
        }catch (IndexOutOfBoundsException e) {
            logger.info("该版本的实例数为0"+ e.getMessage()+e.getClass());
        }catch (Exception e){
            logger.info("删除版本时连接 k8s 集群出现错误"+ e.getMessage()+e.getClass());
            throw new Exception("删除版本时连接 k8s 集群出现错误"+ e.getMessage()+e.getClass());
        }finally {
            if (client != null) client.close();
        }
    }

    @Override
    public void excuteDelete(DeleteParameter parameter) throws Exception{
        try {
            DeleteReleaseParameter qep = (DeleteReleaseParameter)parameter;
            List<V2ImageGroup> v2ImageGroupList = getImageGroups(qep);
            // 集群停止
            //deleteDeploymentInK8s(v2ImageGroupList);
            // 删除数据库的东西
            deleteImageGroups(v2ImageGroupList);
            deleteContainers(v2ImageGroupList);
            deleteLabels(v2ImageGroupList);
            deletePods(v2ImageGroupList);
            deleteVolumes(v2ImageGroupList);
            List<V2Image> v2ImageList = getAllImagesByImageGroupList(v2ImageGroupList);
            deleteImages(v2ImageList);
            deleteArgs(v2ImageList);
            deleteResources(v2ImageList);
            deletePort(v2ImageList);
            deleteProbe(v2ImageList);
            deleteEnv(v2ImageList);
            deleteVolumeMount(v2ImageList);



        }catch (NullPointerException e){
            logger.error("删除版本传入参数为空");
            NullPointerException exception = new NullPointerException("传入参数为空");
            throw exception;
        }catch (Exception e){
            logger.error("删除版本出现错误: "+e.getMessage()+e.getClass());
            e.printStackTrace();
            Exception exception = new Exception("删除版本出现错误: "+e.getMessage()+e.getClass());
            throw exception;
        }
    }
}
