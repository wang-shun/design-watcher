/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;

import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/9/28 0028 20:54
 */
public class NetworkParameter extends BaseParameter {
    protected Labels label;
    protected List<Ports> ports;

    protected List<Integer> envIdList;

    protected String masterType;
    protected String masterPort;
    protected String masterIp;

    protected String name;
    protected String platfromType;

    protected Integer id;  //创建不用填，只有重新发布网络时需要填

    public String getPlatfromType() {
        return platfromType;
    }

    public void setPlatfromType(String platfromType) {
        this.platfromType = platfromType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Labels getLabel() {
        return label;
    }

    public void setLabel(Labels label) {
        this.label = label;
    }

    public List<Ports> getPorts() {
        return ports;
    }

    public void setPorts(List<Ports> ports) {
        this.ports = ports;
    }

    public String getMasterType() {
        return masterType;
    }

    public void setMasterType(String masterType) {
        this.masterType = masterType;
    }

    public String getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(String masterPort) {
        this.masterPort = masterPort;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public List<Integer> getEnvIdList() {
        return envIdList;
    }

    public void setEnvIdList(List<Integer> envIdList) {
        this.envIdList = envIdList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
