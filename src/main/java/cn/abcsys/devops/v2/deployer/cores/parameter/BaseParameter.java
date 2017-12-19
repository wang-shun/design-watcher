package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.results.ValidatorResultBean;

public class BaseParameter {
    //需要执行哪种策略，目前支持四种，弹性伸缩，发布（从镜像，yaml等），升级回滚，伸缩；
    protected String policyType;
    //需要执行策略的哪种操作，例如应用发布，是从镜像发布，还是从yaml文件发布，或者从镜像仓库发布
    protected String configType;
    //最终要去调用deployer 的http接口，deployer的请求接口设置和参数拼装；
    protected String triggerType;

    private ValidatorResultBean result;

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public ValidatorResultBean getResult() {
        return result;
    }

    public void setResult(ValidatorResultBean result) {
        this.result = result;
    }
}
