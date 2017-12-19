package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.db.model.V2SvcLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2SvcPorts;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/17.
 */
public class EnvParameter implements IPlatfromType,IdeployType {
    private List<Integer> versionIdList;

    private Map<String, String> envMap;

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

    public List<Integer> getVersionIdList() {
        return versionIdList;
    }

    public void setVersionIdList(List<Integer> versionIdList) {
        this.versionIdList = versionIdList;
    }

    public Map<String, String> getEnvMap() {
        return envMap;
    }

    public void setEnvMap(Map<String, String> envMap) {
        this.envMap = envMap;
    }
}
