package cn.abcsys.devops.deployer.service;
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import cn.abcsys.devops.deployer.dao.InstanceCoreMapper;
import cn.abcsys.devops.deployer.dao.InstanceEnvsMapper;
import cn.abcsys.devops.deployer.dao.InstancePortsMapper;
import cn.abcsys.devops.deployer.dao.InstanceVolumesMapper;
import cn.abcsys.devops.deployer.model.*;
import cn.abcsys.devops.deployer.util.UsefulTools;
import cn.abcsys.devops.deployer.utils.IPUtils;
import cn.abcsys.devops.v2.deployer.utils.DateUtil;
import io.fabric8.kubernetes.api.model.ServicePort;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date June 2,2017
 * xyj config javaDoc
 */
@Service("instanceDbService")
public class InstanceDbService {
    @Resource(name = "instanceCoreMapper")
    private InstanceCoreMapper instanceCoreMapper;

    @Resource(name = "instanceEnvsMapper")
    private InstanceEnvsMapper instanceEnvsMapper;

    @Resource(name = "instancePortsMapper")
    private InstancePortsMapper instancePortsMapper;

    @Resource(name = "instanceVolumesMapper")
    private InstanceVolumesMapper instanceVolumesMapper;

    @Resource(name = "kubernetesService")
    private KubernetesService ks;

    private static String[] volumeTypes = new String[]{"nfs", "ceph", "glusterfs", "local", "temporary"};

    public static int getTypeIndex(String target) {
        for (int i = 0; i < volumeTypes.length; i++) {
            if (target.equals(volumeTypes[i]))
                return i;
        }
        return -1;
    }

    private static Logger logger = Logger.getLogger(InstanceDbService.class);

    public ParamsWrapper resetCreateRenameParams(ParamsWrapper pw) {
        try {
            //System.out.println("paramsfdgsdf:"+pw.instanceCore.getInstanceNamespace());
            pw.instanceCore.setInstanceRename(pw.instanceCore.getInstanceNamespace()
                    + "-"
                    + UsefulTools.getSimpleCurrentDateTIme()
                    + "-"
                    + pw.instanceCore.getInstanceName());
            // pw.instanceCore.setInstanceTemplateNameLabels(pw.instanceCore.getInstanceRename());
            //System.out.println("params:"+ pw.instanceCore.getInstanceRename());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pw;
    }

    private String setUniqueVolumeName() {
        return "v"
                + "-"
                + UsefulTools.getSimpleCurrentDateTIme();
    }

    public ParamsWrapper resetCreateVolumesParams(ParamsWrapper pw) {
        if (pw.instanceVolumesListString == null) {
            return pw;
        }

        //local:local-name:/root:NFSFileSys
        String[] volumes = pw.instanceVolumesListString.split(";");
        List<InstanceVolumes> ivList = new ArrayList<InstanceVolumes>();
        for (String temp : volumes) {
            InstanceVolumes iv = new InstanceVolumes();
            String[] eachPath = temp.split(":");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (eachPath.length == 4 && eachPath[0].equals("local")) {
                System.out.println(temp);
                iv.setInstanceVolumesType(eachPath[0]);
                iv.setInstanceVolumesName(this.setUniqueVolumeName());
                iv.setInstanceVolumesMountPath(eachPath[2]);//+pw.instanceCore.getAppName()+"/"
                if (eachPath[3].startsWith("/")) {
                    iv.setInstanceVolumesPath(eachPath[3] + "/" + pw.instanceCore.getAppName() + "/" + pw.instanceCore.getInstanceRename() + "/" +
                            eachPath[2].replaceAll("/", "_").replaceFirst("_", ""));
                } else {
                    iv.setInstanceVolumesPath("/abcs/data/local/" + eachPath[3] + "/" + pw.instanceCore.getAppName() + "/" + pw.instanceCore.getInstanceRename() + "/" +
                            eachPath[2].replaceAll("/", "_").replaceFirst("_", ""));
                }

                ivList.add(iv);
            } else if (eachPath.length == 4 && (eachPath[0].equals("share") || eachPath[0].equals("appData"))) {
                iv.setInstanceVolumesType("local");
                iv.setInstanceVolumesName(this.setUniqueVolumeName());
                iv.setInstanceVolumesMountPath(eachPath[2]);//+pw.instanceCore.getAppName()+"/"
                iv.setInstanceVolumesPath(eachPath[3]);
                ivList.add(iv);
            }
        }
        if (ivList.size() > 0)
            pw.instanceVolumesList = ivList;
        return pw;
    }

    public ParamsWrapper resetCreateEnvParams(ParamsWrapper pw) {
        if (pw.instanceEnvsListString == null) {
            return pw;
        }
        String[] envs = pw.instanceEnvsListString.split(";");
        List<InstanceEnvs> ieList = new ArrayList<InstanceEnvs>();
        for (String kv : envs) {
            int index = kv.indexOf(":");
            if(index > 0){
                String[] detail = new String[2];
                detail[0] = kv.substring(0,index);
                if(index + 1 < kv.length()){
                    detail[1] = kv.substring(index+1,kv.length());
                }
                else{
                    detail[1] = null;
                }
                if(detail[1] !=null){
                    InstanceEnvs ie = new InstanceEnvs();
                    ie.setInstanceEnvsName(detail[0]);
                    ie.setInstanceEnvsValue(detail[1]);
                    ieList.add(ie);
                }
            }
        }
        if (ieList.size() > 0)
            pw.instanceEnvsList = ieList;
        return pw;
    }

    public ParamsWrapper resetCreatePortsParams(ParamsWrapper pw) {
        List<InstancePorts> ipList = new ArrayList<InstancePorts>();
        if (pw.instancePortsString == null || pw.instancePortsString.length() < 1) {
            pw.instancePorts = ipList;
            return pw;
        }
        String[] ports = pw.instancePortsString.split(";");
        //System.out.println(ports);

        for (String port : ports) {
            String[] values = port.split(":");
            //System.out.println(values.length);
            if (values.length == 1) {
                InstancePorts ip = new InstancePorts();
                ip.setInstancePortsPort(Integer.valueOf(values[0]));
                ipList.add(ip);
            }
        }
        if (ipList.size() > 0)
            pw.instancePorts = ipList;
        return pw;
    }

    public ParamsWrapper getPortsParamsByInstanceCoreId(ParamsWrapper pw) {
        pw.instancePorts = this.instancePortsMapper.selectByInstanceCoreId(pw.getInstanceCore().getInstanceCoreId());
        if (pw.clusterIp != null && IPUtils.invalid(pw.clusterIp)) {
            for (InstancePorts ip : pw.instancePorts) {
                ip.setInstancePortsClusterIp(pw.clusterIp);
            }
        }
        return pw;
    }

    public ParamsWrapper setOneApplicaitonCore(ParamsWrapper pw) {
        List<InstanceCore> icList = instanceCoreMapper.selectByImageNameAppName(pw.instanceCore, 0, 1);
        logger.info(icList.size());
        if (icList.size() == 1) {
            pw.instanceCore = icList.get(0);
        }
        return pw;
    }

    public ParamsWrapper getAllPorts(ParamsWrapper pw) {
        List<InstancePorts> ipList = instancePortsMapper.selectDistinctPortsByAppName(pw.instanceCore);
        if (ipList.size() > 0) {
            pw.instancePorts = ipList;
            return pw;
        }
        return null;
    }

    public Map<String, Object> insertReplicationController(ParamsWrapper pw) {
        pw.instanceCore.setInstanceCreateDatetime(new Date());
        pw.instanceCore.setInstanceCurrentStatus("created");
        pw.instanceCore.setImageName(pw.instanceCore.getInstanceImage());
        try {
            instanceCoreMapper.insertSelective(pw.instanceCore);
        } catch (Exception e) {
            e.printStackTrace();
        }
//
        Map<String, Object> resMap = new HashMap<String, Object>();
        //System.out.println(pw.instanceCore.toString());
        resMap.put("instanceCoreId", pw.instanceCore.getInstanceCoreId());
        resMap.put("instanceRename", pw.instanceCore.getInstanceRename());
        resMap.put("instanceName", pw.instanceCore.getInstanceName());

        resMap.put("InsertVolumesInfo", this.insertVolumesList(pw));

        resMap.put("InsertEnvsInfo", this.insertInstanceEnvsList(pw));

        return resMap;
    }

    public String insertVolumesList(ParamsWrapper pw) {
        StringBuilder sb = new StringBuilder();
        if (pw.getInstanceVolumesList() != null) {
            for (int i = 0; i < pw.instanceVolumesList.size(); i++) {
                InstanceVolumes iv = pw.instanceVolumesList.get(i);
                iv.setInstanceCoreId(pw.instanceCore.getInstanceCoreId());
                instanceVolumesMapper.insertSelective(iv);
                sb.append("instanceVolumesId" + i + ":" + iv.getInstanceVolumesId() + " ;");
            }
        }

        return sb.toString();
    }

    public String insertInstanceEnvsList(ParamsWrapper pw) {
        StringBuilder sb = new StringBuilder();
        if (pw.getInstanceEnvsList() != null) {
            for (int i = 0; i < pw.getInstanceEnvsList().size(); i++) {
                InstanceEnvs ie = pw.instanceEnvsList.get(i);
                ie.setInstanceCoreId(pw.instanceCore.getInstanceCoreId());
                instanceEnvsMapper.insertSelective(ie);
                sb.append("instanceEnvsId" + i + ":" + ie.getInstanceEnvsId() + " ;");
            }
        }
        return sb.toString();
    }

    public ParamsWrapper getOriginalInstance(ParamsWrapper pw) {
        InstanceCore ic = null;
        if (pw.instanceCore != null && pw.instanceCore.getInstanceRename() != null) {
            ic = instanceCoreMapper.selectByUniqueRename(pw.instanceCore.getInstanceRename());
        } else if (pw.instanceCore != null && pw.instanceCore.getInstanceCoreId() != null) {
            ic = instanceCoreMapper.selectByPrimaryKey(pw.instanceCore.getInstanceCoreId());
        }
        pw.instanceCore = ic;
        return pw;
    }

    public Map<String, Object> insertService(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstancePorts> ipList = new ArrayList<>();
        if (pw.getInstancePorts() != null) {
            for (int i = 0; i < pw.instancePorts.size(); i++) {
                InstancePorts ip = pw.instancePorts.get(i);
                ip.setInstanceCoreId(pw.instanceCore.getInstanceCoreId());
                ip.setInstancePortsCreateDatetime(new Date());
                ip.setInstancePortsPort(pw.getInstancePorts().get(i).getInstancePortsPort());
                ip.setInstancePortsCreateDatetime(new Date());
                instancePortsMapper.insertSelective(ip);
                System.out.println(ip.getInstancePortsId());
                ipList.add(ip);
                // System.out.println("sdfdf"+ pw.instancePorts.get(i).getInstanceCoreId());
                //resMap.put("insertStatus"+i,insertStatus);
            }
        }
        pw.instancePorts = ipList;
        return resMap;
    }

    public void updateService(ParamsWrapper pw, String nodePorts) {
        if (pw.getInstancePorts() != null && pw.getInstancePorts().size() == nodePorts.split(";").length) {
            String[] eachPort = nodePorts.split(";");
            for (int i = 0; i < pw.instancePorts.size(); i++) {
                InstancePorts ip = pw.instancePorts.get(i);
                ip.setInstancePortsNodePort(Integer.valueOf(eachPort[i].split(":")[0]));
                instancePortsMapper.updateByPrimaryKeySelective(pw.instancePorts.get(i));
                // System.out.println("sdfdf"+ pw.instancePorts.get(i).getInstanceCoreId());
                //resMap.put("insertStatus"+i,insertStatus);
            }
        }
    }

    public Map<String, Object> deleteService(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstancePorts> ipl = instancePortsMapper.selectByInstanceCoreId(
                this.getOriginalInstance(pw).
                        instanceCore.getInstanceCoreId());
        for (int i = 0; i < ipl.size(); i++) {
            InstancePorts ip = ipl.get(i);
            ip.setInstancePortsCurrentStatus("invalid");
            ip.setInstancePortsDeleteDatetime(new Date());
            int deleteStatus = instancePortsMapper.updateByPrimaryKeySelective(ip);
            resMap.put("instancePortsId" + i, deleteStatus);
        }
        return resMap;
    }

    public Map<String, Object> deleteRc(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        InstanceCore ic = this.getOriginalInstance(pw).instanceCore;
        ic.setInstanceCurrentStatus("invalid");
        ic.setInstanceDeleteDatetime(new Date());
        int deleteStatus = instanceCoreMapper.updateByPrimaryKeySelective(ic);
        resMap.put("instanceCoreId", deleteStatus);
        return resMap;
    }

    public Map<String, Object> updateInstanceDb(ParamsWrapper pw) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        try {
            InstanceCore instanceCore =  instanceCoreMapper.isExistInstance(pw.getInstanceCore().getComponentId());
            if (instanceCore != null) {
                instanceCore.setInstanceCreateDatetime(new Date());
                instanceCore.setInstanceCurrentStatus("created");
                instanceCore.setInstanceDeleteDatetime(null);
                instanceCore.setInstanceRename(pw.getInstanceCore().getInstanceRename());
                instanceCoreMapper.updateByPrimaryKeySelective(instanceCore);
            } else {
                pw.instanceCore.setInstanceCreateDatetime(new Date());
                pw.instanceCore.setInstanceCurrentStatus("created");
                pw.instanceCore.setImageName(pw.instanceCore.getInstanceImage());
                pw.instanceCore.setInstanceReplica(pw.instanceReplica);
                instanceCoreMapper.insertSelective(pw.instanceCore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resMap.put("instanceCoreId", pw.instanceCore.getInstanceCoreId());
        resMap.put("instanceRename", pw.instanceCore.getInstanceRename());
        resMap.put("instanceName", pw.instanceCore.getInstanceName());
        resMap.put("InsertVolumesInfo", this.insertVolumesList(pw));
        resMap.put("InsertEnvsInfo", this.insertInstanceEnvsList(pw));
        return resMap;
    }

    public List<InstanceVolumes> getInstanceVolumesListByInstanceCoreId(int instanceCoreId) {
        return this.instanceVolumesMapper.selectByInstanceCoreId(instanceCoreId);
    }

    public List<InstancePorts> getInstancePortsByInstanceCoreId(int instanceCoreId) {
        return this.instancePortsMapper.selectByInstanceCoreId(instanceCoreId);
    }

    public InstanceVolumes getInstanceVolumesListByName(String name) {
        return this.instanceVolumesMapper.selectByInstanceVolumesName(name);
    }

    public List<InstanceEnvs> getInstanceEnvsByInstanceCoreId(int instanceCoreId) {
        return this.instanceEnvsMapper.selectByInstanceCoreId(instanceCoreId);
    }

    public ParamsWrapper getOldParamsWrapper(ParamsWrapper pw) {
        ParamsWrapper oldPw = this.getOriginalInstance(pw);
        oldPw.instanceEnvsList = this.getInstanceEnvsByInstanceCoreId(oldPw.instanceCore.getInstanceCoreId());
        oldPw.instancePorts = this.getInstancePortsByInstanceCoreId(oldPw.instanceCore.getInstanceCoreId());
        oldPw.instanceVolumesList = this.getInstanceVolumesListByInstanceCoreId(oldPw.instanceCore.getInstanceCoreId());
        return oldPw;
    }

    public ParamsWrapper getNewParamsWrapper(ParamsWrapper oldPw, String host) {
        oldPw.instanceCore.setInstanceCoreId(null);
        //oldPw.instanceCore.setInstanceUuid(host);
        return oldPw;
    }

    public Map<String, Object> getAllContainerInfo(InstanceCore ic, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstanceCore> icList = instanceCoreMapper.selectByMultipleFields(ic, (page - 1) * rows, rows);
        resMap.put("records", instanceCoreMapper.selectCountByMultipleFields(ic));
        //resMap.put("records",icList.size());
        resMap.put("page", page);
        List<Map<String, Object>> rowsList = new ArrayList<>();
        for (InstanceCore tempIc : icList) {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("image", tempIc.getImageName());
            List<InstanceVolumes> ivList = this.getInstanceVolumesListByInstanceCoreId(tempIc.getInstanceCoreId());
            //List<InstanceEnvs> ieList = this.getInstanceEnvsByInstanceCoreId(tempIc.getInstanceCoreId());
            //List<InstancePorts> ipList = this.getInstancePortsByInstanceCoreId(tempIc.getInstanceCoreId());
            List<Map<String, Object>> volumeMount = new ArrayList<>();
            List<Map<String, Object>> volumeList = new ArrayList<>();
            for (InstanceVolumes iv : ivList) {
                Map<String, Object> tempVolumeMountMap = new HashMap<String, Object>();
                Map<String, Object> tempVolumeListMap = new HashMap<String, Object>();

                tempVolumeMountMap.put("MountPath", iv.getInstanceVolumesMountPath());
                tempVolumeMountMap.put("name", iv.getInstanceVolumesName());

                tempVolumeListMap.put("type", iv.getInstanceVolumesType());
                tempVolumeListMap.put("name", iv.getInstanceVolumesName());
                tempVolumeListMap.put("path", iv.getInstanceVolumesPath());
                if (iv.getInstanceVolumesType().equals("nfs")) {
                    tempVolumeListMap.put("server", iv.getInstanceVolumesServer());
                } else if (iv.getInstanceVolumesType().equals("local")) {

                }

                volumeMount.add(tempVolumeMountMap);
                volumeList.add(tempVolumeListMap);
            }
            tempMap.put("volumeMount", volumeMount);
            tempMap.put("volumeList", volumeList);

            tempMap.put("createTime", tempIc.getInstanceCreateDatetime());
            tempMap.put("containerName", tempIc.getContainerName());
            tempMap.put("instanceRename", tempIc.getInstanceRename());
            tempMap.put("containerStatus", tempIc.getContainerStatus());
            tempMap.put("network", tempIc.getContainerStatus());
            rowsList.add(tempMap);
        }
        resMap.put("rows", rowsList);
        return resMap;
    }

    public ParamsWrapper getConatinerDetailInfo(String instanceRename) {
        InstanceCore ic = this.instanceCoreMapper.selectByUniqueRename(instanceRename);
        ParamsWrapper pw = new ParamsWrapper();
        if (ic != null) {
            pw.instanceCore = ic;
            pw.instanceVolumesList = this.getInstanceVolumesListByInstanceCoreId(ic.getInstanceCoreId());
            pw.instanceEnvsList = this.getInstanceEnvsByInstanceCoreId(ic.getInstanceCoreId());
            pw.instancePorts = this.getInstancePortsByInstanceCoreId(ic.getInstanceCoreId());
            return pw;
        }
        return null;
    }

    public Map<String, Object> getAllContainerInfoNew(InstanceCore ic, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstanceCore> icList = instanceCoreMapper.selectByMultipleFields(ic, (page - 1) * rows, rows);
        resMap.put("records", instanceCoreMapper.selectCountByMultipleFields(ic));
        //resMap.put("records",icList.size());
        resMap.put("page", page);
        List<Object> rowsList = new ArrayList<>();
        for (InstanceCore tempIc : icList) {
            ParamsWrapper pw = this.getConatinerDetailInfo(tempIc.getInstanceRename());
            if (pw != null) {
                pw.instanceCore = this.ks.resetPodNameAndStatus(pw.instanceCore);
                rowsList.add(pw);
            }

        }
        resMap.put("rows", rowsList);
        return resMap;
    }

    public Map<String, Object> getAllNetworksInfo(InstanceCore ic, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        int networkTypeCount = instanceCoreMapper.selectNetworksTypeCountsByNetwork(ic);
        List<InstanceCore> pageList = instanceCoreMapper.selectNetworksByNetwork(ic, (page - 1) * rows, rows);
        resMap.put("records", networkTypeCount);
        //resMap.put("records",pageList.size());
        resMap.put("page", page);
        List<Object> rowsList = new ArrayList<>();
        for (InstanceCore temp : pageList) {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("name", temp.getInstanceNetwork());

            tempMap.put("appSize", instanceCoreMapper.selectDistinctAppNameCountByInstanceNetwork(temp));
            tempMap.put("appNames", instanceCoreMapper.selectDistinctAppNameByInstanceNetwork(temp));

            tempMap.put("containerSize", instanceCoreMapper.selectCountByInstanceNetwork(temp));
            tempMap.put("containerNames", instanceCoreMapper.selectContainerNameByInstanceNetwork(temp));
            rowsList.add(tempMap);
        }
        resMap.put("rows", rowsList);
        return resMap;
    }

    public Map<String, Object> getAllVolumesInfo(InstanceCore ic, InstanceVolumes iv, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstanceVolumes> ivList = instanceVolumesMapper.selectLikeByInstanceVolumesName(iv, (page - 1) * rows, rows, ic.getApplicationRuntimeId());

        //resMap.put("records",instanceVolumesMapper.selectLikeCountByInstanceVolumesName(name));
        //resMap.put("records",ivList.size());
        resMap.put("page", page);
        Integer volumesRecords = instanceVolumesMapper.selectLikeCountByInstanceVolumesName(iv, ic.getApplicationRuntimeId());
        System.out.println(ivList.size());
        resMap.put("records", volumesRecords);
        resMap.put("rows", ivList);
        return resMap;
    }

    public Map<String, Object> getAllImageInfo(InstanceCore ic, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<InstanceCore> icList = instanceCoreMapper.selectDistinctImageNameByImageName(ic, (page - 1) * rows, rows);
        resMap.put("records", instanceCoreMapper.selectDistinctImageNameCountByImageName(ic));
        resMap.put("page", page);
        List<Map<String, Object>> rowsList = new ArrayList<>();
        for (InstanceCore temp : icList) {
            logger.info("查询环境temp：" + temp.getApplicationRuntimeId());
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("imageName", temp.getImageName());
            //tempMap.put("imageTag",temp.getInstanceImageTag());

            Map<String, Object> appMap = new HashMap<String, Object>();
            appMap.put("count", instanceCoreMapper.selectAppCountsByImageName(temp));
            appMap.put("instanceCores", instanceCoreMapper.selectDistinctAppNameByImageName(temp));

            Map<String, Object> containerMap = new HashMap<String, Object>();
            containerMap.put("count", instanceCoreMapper.selectInstanceCountsByImageName(temp));
            containerMap.put("instanceCores", instanceCoreMapper.selectAllInfoNameByImageName(temp));

            tempMap.put("belongApps", appMap);
            tempMap.put("belongContainers", containerMap);
            rowsList.add(tempMap);
        }
        resMap.put("rows", rowsList);
        return resMap;
    }

    public Map<String, Object> getContainersFromAppName(InstanceCore ic, Integer page, Integer rows) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("records", this.instanceCoreMapper.selectCountByImageNameAppName(ic));
        List<InstanceCore> currentList = this.instanceCoreMapper.selectByImageNameAppName(ic, (page - 1) * rows, rows);
        currentList = this.setCluterIPInInstanceCore(currentList);
        resMap.put("rows", currentList);
        resMap.put("page", page);
        resMap.put("success", true);
        return resMap;
    }

    private List<InstanceCore> setCluterIPInInstanceCore(List<InstanceCore> icList) {
        for (InstanceCore ic : icList) {
            List<InstancePorts> ipList = instancePortsMapper.selectByInstanceCoreId(ic.getInstanceCoreId());
            if (ipList.size() > 0) {
                ic.setClusterIp(ipList.get(0).getInstancePortsClusterIp());
            }
        }
        return icList;
    }

    public Map<String, Object> getContainerNetworkVolumeCounts(InstanceCore ic) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        // 整理出正在运行的容器的数量
        List<InstanceCore> ics = instanceCoreMapper.selectAllContainersMultipleFields(ic);
        int number = 0;
        for (InstanceCore icA : ics) {
            InstanceCore icB = ks.resetPodNameAndStatus(icA);
            if (icB != null && icB.getContainerStatus() != null && icB.getContainerStatus().equals("1")) {
                number++;
            }
        }
        resMap.put("runningContainersCount", number);
        resMap.put("ContainersCount", instanceCoreMapper.selectCountByMultipleFields(ic));
        resMap.put("networkCount", instanceCoreMapper.selectNetworksTypeCountsByNetwork(ic));
        resMap.put("volumesCount", instanceVolumesMapper.selectLikeCountByInstanceVolumesName(new InstanceVolumes(),
                ic.getApplicationRuntimeId()));
        resMap.put("success", true);
        return resMap;
    }

    public void updateNodePorts(Map<String, Object> svcK, ParamsWrapper pw) {
        System.out.println("here:");
        System.out.println("svcK:" + svcK);
        String[] spList = ((String) svcK.get("nodePorts")).split(";");
        //System.out.println("\"here:\""+spList.size());
        if (spList.length == pw.instancePorts.size() && spList.length >= 1) {
            for (int i = 0; i < spList.length; i++) {
                String[] np = spList[i].split(":");
                //System.out.println(spList.get(i).getNodePort()+" "+pw.instancePorts.get(i).getInstancePortsId());
                pw.instancePorts.get(i).setInstancePortsNodePort(Integer.valueOf(np[0]));
                instancePortsMapper.updateByPrimaryKeySelective(pw.instancePorts.get(i));
            }
        }
    }

    public Map<String, Object> selectCountPath(InstanceVolumes iv, Integer envId) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        Integer count = this.instanceVolumesMapper.selectCountPath(iv, envId);
        logger.info("count:" + count);
        if (count == null || count != null && count == 0)
            resMap.put("ifMount", false);
        else {
            resMap.put("ifMount", true);
        }
        resMap.put("success", true);
        return resMap;
    }

    public InstanceCore selectByComponentId(@Param("componentId") Integer componentId) {
        return instanceCoreMapper.selectByComponentId(componentId);
    }
}
