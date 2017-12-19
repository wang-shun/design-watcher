/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;

import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/9/6 21:40
 * 1. yaml文件放到deployer里面去；2.接口可能需要重新定义；3. 尽快出一个实例；4.new mail test submit.
 */
public class ApplicationParameter extends BaseParameter {

    //需要输入项目相关的信息
    protected Integer projectId;
    private Integer envId;
    //需要输入应用的信息
    protected Integer id; //创建时不用传，创建应用新版本传
    protected String name; //应用名，流水线
    protected String realName; //不用传 应用的名字
    protected String description;//需要保存的非格式化信息，例如oltp，olap，项目备注等；//可缺省
    protected String url; //不用传
    protected String platformType;//查看 spring-mvc.xml
    protected String catalogType;//war,database,proxy等 应用的描述信息，可缺省
    protected String moduleType;//tomcat,mysql,zookeeper 可缺省
    //需要输入版本组的信息，可能对应一个rc，rs或者deployment，甚至是自定义的结构
    protected Integer versionId;//current 创建不用传，回滚等时传就行
    protected String versionName;
    protected String versionDescription; //可缺省
    protected String versionType;//rc deployment rs等 //现在仅仅能为 Deployment
    protected Integer targetVersionId;//只能是旧版本 灰度发布时需要部分替换的数量，Application层不维护版本对应的实例数量，都从deployer中去查
    protected String greyType; //number 或者 percentage
    protected Integer replaceCount; // >0
    protected Double replaceFactor;  // 0-1
    //应用复制原来应用的版本ID，用于选择配置。
    private Integer anotherApplicationVersionId; // 应用复制时的 versionId 选择
    protected String deploymentName; //应用名+ VersionName，相当于一个 template，真实名字后面加随机数
    protected String deploymentDescription; //可缺省
    protected String namespace; //暂定 default
    protected String apiVersion; //extension....
    protected String kind; //暂定 Deployment
    protected Integer replica; // >0
    protected String masterIp;
    protected String masterType; //http https
    protected String MasterPort;
    /**
     * 剩余的都是某个镜像的对应的配置，可以有多个镜像，
     * 每个镜像可以配置自己的环境变量、卷、端口、标签（调度到指定主机上）、健康检查、资源、网络九个方面，
     * 这些数据统一封装到ApplicationComponent里面去
     */
    protected List<ApplicationComponent> images;
    //在镜像集合（deployment等）的层面设置labels，用于支持不同的配置
    protected List<Labels> labels;
    protected List<Volumes> volumes;

    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getDeploymentDescription() {
        return deploymentDescription;
    }

    public void setDeploymentDescription(String deploymentDescription) {
        this.deploymentDescription = deploymentDescription;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getReplica() {
        return replica;
    }

    public void setReplica(Integer replica) {
        this.replica = replica;
    }

    public List<ApplicationComponent> getImages() {
        return images;
    }

    public void setImages(List<ApplicationComponent> images) {
        this.images = images;
    }

    public List<Labels> getLabels() {
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
    }

    public List<Volumes> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volumes> volumes) {
        this.volumes = volumes;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    public String getMasterPort() {
        return MasterPort;
    }

    public void setMasterPort(String masterPort) {
        MasterPort = masterPort;
    }

    public Integer getTargetVersionId() {
        return targetVersionId;
    }

    public void setTargetVersionId(Integer targetVersionId) {
        this.targetVersionId = targetVersionId;
    }

    public String getGreyType() {
        return greyType;
    }

    public void setGreyType(String greyType) {
        this.greyType = greyType;
    }

    public Integer getReplaceCount() {
        return replaceCount;
    }

    public void setReplaceCount(Integer replaceCount) {
        this.replaceCount = replaceCount;
    }

    public Double getReplaceFactor() {
        return replaceFactor;
    }

    public void setReplaceFactor(Double replaceFactor) {
        this.replaceFactor = replaceFactor;
    }

    public Integer getAnotherApplicationVersionId() {
        return anotherApplicationVersionId;
    }

    public void setAnotherApplicationVersionId(Integer anotherApplicationVersionId) {
        this.anotherApplicationVersionId = anotherApplicationVersionId;
    }
}