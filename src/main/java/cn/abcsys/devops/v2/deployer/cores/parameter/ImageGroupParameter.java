/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IPlatfromType;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IdeployType;
import cn.abcsys.devops.v2.deployer.db.model.V2Image;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Labels;

import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/6 0006 23:29
 */
public class ImageGroupParameter implements IPlatfromType,IdeployType {
    private String platfromType;
    private String deployType;

    private String handlerType;

    private Integer currentVersionId;//减
    private Integer expansionNumber;
    private Integer shrinkageNumber;
    private Integer replica;
    private Integer rollingType;

    private Integer targetVersionId;//需要替换的版本
    private String greyType;
    private Integer replaceCount;
    private Double replaceFactor;

    //应用复制原来应用的版本ID，用于选择配置。
    private Integer anotherApplicationVersionId;

    DeploymentComponent deployment;

    private Integer applicationId;
    private Integer projectId;
    private Integer envId;
    private List<DeploymentComponent> realDeployments;

    public Integer getReplica() {
        return replica;
    }

    public void setReplica(Integer replica) {
        this.replica = replica;
    }

    public DeploymentComponent getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentComponent deployment) {
        this.deployment = deployment;
    }

    @Override
    public String getPlatfromType() { return platfromType; }
    @Override
    public void setPlatfromType(String platfromType) { this.platfromType = platfromType; }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public Integer getCurrentVersionId() {
        return currentVersionId;
    }

    public void setCurrentVersionId(Integer currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public Integer getExpansionNumber() {
        return expansionNumber;
    }

    public void setExpansionNumber(Integer expansionNumber) {
        this.expansionNumber = expansionNumber;
    }

    public Integer getShrinkageNumber() {
        return shrinkageNumber;
    }

    public void setShrinkageNumber(Integer shrinkageNumber) {
        this.shrinkageNumber = shrinkageNumber;
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

    public List<DeploymentComponent> getRealDeployments() {
        return realDeployments;
    }

    public void setRealDeployments(List<DeploymentComponent> realDeployments) {
        this.realDeployments = realDeployments;
    }

    public Integer getRollingType() {
        return rollingType;
    }

    public void setRollingType(Integer rollingType) {
        this.rollingType = rollingType;
    }

    public Integer getAnotherApplicationVersionId() {
        return anotherApplicationVersionId;
    }

    public void setAnotherApplicationVersionId(Integer anotherApplicationVersionId) {
        this.anotherApplicationVersionId = anotherApplicationVersionId;
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
}
