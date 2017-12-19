package cn.abcsys.devops.v2.appstore.deployer.kubernetes.util;

import cn.abcsys.devops.deployer.model.ParamsWrapper;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeploymentConcepts {

    private static Logger logger = Logger.getLogger(DeploymentConcepts.class);
    private ParamsWrapper paramsWrapper;
    private KubernetesClient client = null;
    public DeploymentConcepts(ParamsWrapper pw) {
        this.paramsWrapper = pw;
        client = KubernetesUtil.getClient(paramsWrapper.instanceCore.getMasterType(), paramsWrapper.instanceCore.getIp(), paramsWrapper.instanceCore.getMasterPort());
    }

    public void closeClient() {
        try {
            client.close();
            client = null;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    protected FileInputStream getYaml(File yamlFile) {
        try {
            return new FileInputStream(yamlFile);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean createDeployment(String pathName) {
        boolean createDeployStatus = false;
        try {
            File file = new File(pathName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream yaml = getYaml(file);
            List<HasMetadata> list = client.load(yaml).createOrReplace();
            createDeployStatus = true;
        } catch (KubernetesClientException e) {
            logger.error("createDeployment KubernetesClientException info: " + e.getMessage());
        } catch (Exception e) {
            logger.error("createDeployment error info: " + e.getMessage());
        }
        closeClient();
        return createDeployStatus;
    }

    public ParamsWrapper getParamsWrapper() {
        return paramsWrapper;
    }

    public void setParamsWrapper(ParamsWrapper paramsWrapper) {
        this.paramsWrapper = paramsWrapper;
    }

    public String getLableStr() {
        StringBuilder lableSb = new StringBuilder();
        lableSb.append("(label-" + paramsWrapper.instanceCore.getInstanceRename());
        lableSb.append(",label-" + paramsWrapper.instanceCore.getInstanceRename() + "-master");
        lableSb.append(",label-" + paramsWrapper.instanceCore.getInstanceRename() + "-slave");
        for (int i = 0; i < this.paramsWrapper.getInstanceReplica() + 1; i++) {
            lableSb.append(",label-" + paramsWrapper.instanceCore.getInstanceRename() + "-" + i);
        }
        lableSb.append(")");
        return lableSb.toString();
    }

    public boolean deleteService() {
        return client.services().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                .withLabel("name in " + getLableStr())
                .delete();
    }

    public boolean deleteDeployment() {
        return client.extensions().deployments().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                .withLabel("name in " + getLableStr())
                .delete();
    }

    public Map<String, Object> getPodStatus() {
        Map<String, Object> resMap = new HashMap<>();
        Integer runStatus = 0;
        Integer notRunStatus = 0;
        try {
            PodList pl = client.pods().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .withLabel("name in " + getLableStr()).list();
            List<Pod> podsList = pl.getItems();
            for (Pod pod : podsList) {
                if (pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && pod.getStatus().getContainerStatuses().size() > 0) {
                    if (pod.getStatus().getContainerStatuses().get(0).getState().getRunning() != null
                            && pod.getStatus().getContainerStatuses().get(0).getReady()) {
                        runStatus ++;
                    }
                    else if (pod.getStatus().getContainerStatuses().get(0).getState().getTerminated() != null) {
                        continue;
                    }
                    else {
                        notRunStatus ++;
                    }
                } else {
                    notRunStatus ++;
                }
            }
            resMap.put("success", true);
        } catch (Exception e) {
            resMap.put("success", false);
        }
        resMap.put("runStatus", runStatus);
        resMap.put("notRunStatus", notRunStatus);
        return resMap;
    }

    public Map<String, Object> getServicePort() {
        Map<String, Object> resMap = new HashMap<>();
        try {
            Service svc = client.services()
                    .inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .withName("svc-" + this.paramsWrapper.instanceCore.getInstanceRename())
                    .get();
            if (svc != null) {
                StringBuilder sb = new StringBuilder();
                for (ServicePort sv : svc.getSpec().getPorts()) {
                    sb.append(sv.getNodePort());
                }
                resMap.put("nodePorts", sb.toString());
                resMap.put("clusterIP", svc.getSpec().getClusterIP());
                resMap.put("success", true);
                return resMap;
            }
        } catch (Exception e) {
            resMap.put("success", false);
            resMap.put("message", "当前无service！");
        }
        resMap.put("message", "当前无service！");
        resMap.put("success", false);
        return resMap;
    }

    public Map<String, List<Map<String, Object>>> getInstanceCore() {
        Map<String, List<Map<String, Object>>> resMap = new HashMap<>();
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            PodList pl = client.pods().inNamespace(this.paramsWrapper.instanceCore.getInstanceNamespace())
                    .withLabel("name in " + getLableStr()).list();
            List<Pod> podsList = pl.getItems();
            for (Pod pod : podsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", pod.getMetadata().getName());
                if (pod.getStatus() != null &&
                        pod.getStatus().getContainerStatuses() != null &&
                        pod.getStatus().getContainerStatuses().size() > 0) {
                    if (pod.getStatus().getContainerStatuses().get(0).getState().getRunning() != null
                            && pod.getStatus().getContainerStatuses().size() > 0 && pod.getStatus().getContainerStatuses().get(0).getReady()) {
                        map.put("status", "容器正在运行");
                    } else if (pod.getStatus().getContainerStatuses().get(0).getState().getTerminated() != null) {
                        map.put("status", "容器已经终止");
                    } else if (pod.getStatus().getContainerStatuses().get(0).getState().getWaiting() != null) {
                        map.put("status", "容器准备运行或停止");
                    } else {
                        map.put("status", "容器未运行");
                    }
                } else {
                    map.put("status", "容器未运行");
                }
                String objectName = "k8s_" + pod.getSpec().getContainers().get(0).getName() + "_" + pod.getMetadata().getName() + "_" + pod.getMetadata().getNamespace() + "_"
                        + pod.getMetadata().getUid() + "_0";
                map.put("objectName", objectName);
                map.put("image", pod.getSpec().getContainers().get(0).getImage() == null ? "" : pod.getSpec().getContainers().get(0).getImage());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date createTime = sdf.parse(pod.getMetadata().getCreationTimestamp());
                map.put("createTime", createTime);
                map.put("ip", pod.getStatus().getHostIP() == null ? "" : pod.getStatus().getHostIP());
                map.put("node", pod.getSpec().getNodeName() == null ? "" : pod.getSpec().getNodeName());
                resList.add(map);
            }
        } catch (Exception e) {
            resMap.put("instanceCoreList", null);
        }
        resMap.put("instanceCoreList", resList);
        return resMap;
    }

}
