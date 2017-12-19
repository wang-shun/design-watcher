package cn.abcsys.devops.v2.deployer.cores.parameter;

import java.util.List;

/**
 * 通过资源 ID 进行资源查询
 *
 * @author xianghao
 * @create 2017-10-09 下午7:35
 **/
public class QueryEnvIdParameter extends QueryParameter{

    Integer envId;

    Integer pageNum;

    Integer num_per_page;

    Integer projectId;

    Integer applicationId;

    Integer versionId;

    Integer imageGroupId;

    Integer containerId;

    Integer networkPolicyId;

    List<Integer> projectIdList;

    String podName;

    String realName;

    String networkName;
    List<String> networkNameList;

    String projectIds;


    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getNum_per_page() {
        return num_per_page;
    }

    public void setNum_per_page(Integer num_per_page) {
        this.num_per_page = num_per_page;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getImageGroupId() {
        return imageGroupId;
    }

    public void setImageGroupId(Integer imageGroupId) {
        this.imageGroupId = imageGroupId;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

    public Integer getNetworkPolicyId() {
        return networkPolicyId;
    }

    public void setNetworkPolicyId(Integer networkPolicyId) {
        this.networkPolicyId = networkPolicyId;
    }

    public List<Integer> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<Integer> projectIdList) {
        this.projectIdList = projectIdList;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public List<String> getNetworkNameList() {
        return networkNameList;
    }

    public void setNetworkNameList(List<String> networkNameList) {
        this.networkNameList = networkNameList;
    }

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }
}
