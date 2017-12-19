package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPorts;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */
public class NetworkPolicyParameter implements IPlatfromType, IdeployType{

    private V2NetworkPolicy network;
    private List<V2NetworkLabels> labels;
    private List<V2NetworkPorts> ports;
    private String platfromType;
    private String deployType;
    private List<Integer> envIdList;

    public V2NetworkPolicy getNetwork() {
        return network;
    }

    public void setNetwork(V2NetworkPolicy network) {
        this.network = network;
    }

    public List<V2NetworkLabels> getLabels() {
        return labels;
    }

    public void setLabels(List<V2NetworkLabels> labels) {
        this.labels = labels;
    }

    public List<V2NetworkPorts> getPorts() {
        return ports;
    }

    public void setPorts(List<V2NetworkPorts> ports) {
        this.ports = ports;
    }
    public List<Integer> getEnvIdList() {
        return envIdList;
    }

    public void setEnvIdList(List<Integer> envIdList) {
        this.envIdList = envIdList;
    }

    @Override
    public String getPlatfromType() { return platfromType; }
    @Override
    public void setPlatfromType(String platfromType) { this.platfromType = platfromType; }

    @Override
    public String getDeployType() {
        return deployType;
    }
    @Override
    public void setDeployType(String deployType) {
        this.deployType = deployType;
    }


}
