package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.db.model.V2SvcLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2SvcPorts;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */
public class ServiceParameter implements IPlatfromType,IdeployType {
    private V2Svc service;
    private List<V2SvcLabels> labels;
    private List<V2SvcPorts> ports;

    private String platfromType;
    private String deployType;

    @Override
    public String getPlatfromType() {
        return platfromType;
    }

    @Override
    public void setPlatfromType(String platfromType) {
        this.platfromType = platfromType;
    }

    @Override
    public String getDeployType() {
        return deployType;
    }

    @Override
    public void setDeployType(String deployType) {
        this.deployType = deployType;
    }

    public V2Svc getService() {
        return service;
    }

    public void setService(V2Svc service) {
        this.service = service;
    }

    public List<V2SvcLabels> getLabels() {
        return labels;
    }

    public void setLabels(List<V2SvcLabels> labels) {
        this.labels = labels;
    }

    public List<V2SvcPorts> getPorts() {
        return ports;
    }

    public void setPorts(List<V2SvcPorts> ports) {
        this.ports = ports;
    }
}
