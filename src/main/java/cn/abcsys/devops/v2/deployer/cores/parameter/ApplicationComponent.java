/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;

import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/9/14 0014 15:02
 */
public class ApplicationComponent {

    //需要指定镜像的版本
    protected Integer id; //可缺省，暂时没用到
    protected String name; //imageName
    protected String version; //imageTag
    protected String containerName; //可缺省
    protected String imageType; //可缺省
    protected String secret; //可缺省
    protected Long storageTime; //镜像创建时间，马云浩传过来
    // 创建docker容器时候需要指定的参数；
    protected List<Args> args; //可缺省，暂时没用到
    // 容器内部需要的环境变量；
    protected List<EnvironmentVariables> envs;
    // 需要挂载的卷；
    protected List<Volumes> volumeMounts;
    // 需要对外暴露的端口
    protected List<Ports> ports;
    // 需要支持的若干检查框架
    protected List<Probes> probes;
    //需要配置的资源
    protected Resources resources;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<Args> getArgs() {
        return args;
    }

    public void setArgs(List<Args> args) {
        this.args = args;
    }

    public List<EnvironmentVariables> getEnvs() {
        return envs;
    }

    public void setEnvs(List<EnvironmentVariables> envs) {
        this.envs = envs;
    }

    public List<Volumes> getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(List<Volumes> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    public List<Ports> getPorts() {
        return ports;
    }

    public void setPorts(List<Ports> ports) {
        this.ports = ports;
    }

    public List<Probes> getProbes() {
        return probes;
    }

    public void setProbes(List<Probes> probes) {
        this.probes = probes;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Long getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Long storageTime) {
        this.storageTime = storageTime;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
