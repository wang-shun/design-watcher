package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;

import java.util.List;
import java.util.Map;

public class LoadParameter implements IPlatfromType,IdeployType {


    private List<PortObject> portList;

    private Integer applicationId;

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


    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public List<PortObject> getPortList() {
        return portList;
    }

    public void setPortList(List<PortObject> portList) {
        this.portList = portList;
    }
}
