package cn.abcsys.devops.v2.deployer.db.model;

import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageComponent;

import java.util.Date;

public class V2Container {
    private Integer id;

    private Integer podId;

    private Integer imageGroupId;

    private Integer versionId;

    private Integer applicationId;

    private Integer projectId;

    private Integer envId;

    private String podName;

    private String realName;

    private String containerName;

    private String imageNameTag;

    private String status;

    private String maxCpu;

    private String maxMemory;

    private String hostIp;

    private String hostName;

    private String imageGroupRealName;  // 这个字段不存在于数据库中，仅仅存在于返回值里

    private String ApplicationRealName;  // 这个字段不存在于数据库中，仅仅存在于返回值里

    private String ApplicationName;  // 这个字段不存在于数据库中，仅仅存在于返回值里

    private Date createDatetime;

    private Date startDatetime;

    public V2Container(V2Pod pod, ImageComponent ic){
        this.podId = pod.getId();
        this.status = "creating";
        if(ic.getResources() !=null){
            this.maxCpu = ic.getResources().getMaxCpu();
            this.maxMemory = ic.getResources().getMaxMem();
        }
        this.createDatetime = new Date();
        this.imageNameTag = ic.getImage().getImageName()+":"+ic.getImage().getImageTag();
    }

    public V2Container(V2Pod pod, ImageComponent ic,DeploymentComponent dc){
        this.podId = pod.getId();
        this.status = "creating";
        if(ic.getResources() !=null){
            this.maxCpu = ic.getResources().getMaxCpu();
            this.maxMemory = ic.getResources().getMaxMem();
        }
        this.createDatetime = new Date();
        this.imageNameTag = ic.getImage().getImageName()+":"+ic.getImage().getImageTag();

        this.imageGroupId = dc.getImageGroup().getId();
        this.versionId = dc.getImageGroup().getVersionId();
        this.applicationId = dc.getImageGroup().getApplicationId();
        this.projectId = dc.getImageGroup().getProjectId();
        this.envId = dc.getImageGroup().getEnvId();
    }

    public V2Container(){

    }

    public String getImageNameTag() {
        return imageNameTag;
    }

    public void setImageNameTag(String imageNameTag) {
        this.imageNameTag = imageNameTag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPodId() {
        return podId;
    }

    public void setPodId(Integer podId) {
        this.podId = podId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName == null ? null : podName.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getMaxCpu() {
        return maxCpu;
    }

    public void setMaxCpu(String maxCpu) {
        this.maxCpu = maxCpu == null ? null : maxCpu.trim();
    }

    public String getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory == null ? null : maxMemory.trim();
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp == null ? null : hostIp.trim();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName == null ? null : hostName.trim();
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Integer getImageGroupId() {
        return imageGroupId;
    }

    public void setImageGroupId(Integer imageGroupId) {
        this.imageGroupId = imageGroupId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getImageGroupRealName() {
        return imageGroupRealName;
    }

    public void setImageGroupRealName(String imageGroupRealName) {
        this.imageGroupRealName = imageGroupRealName;
    }

    public String getApplicationRealName() {
        return ApplicationRealName;
    }

    public void setApplicationRealName(String applicationRealName) {
        ApplicationRealName = applicationRealName;
    }

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationName(String applicationName) {
        ApplicationName = applicationName;
    }
}