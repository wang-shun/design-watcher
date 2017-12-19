package cn.abcsys.devops.v2.deployer.cores.parameter;

/**
 * 通过 versionId 来进行该版本所有的资源的删除
 *
 * @author xianghao
 * @create 2017-10-09 下午7:35
 **/
public class DeleteReleaseParameter extends DeleteParameter{

    Integer envId;

    Integer projectId;

    Integer applicationId;

    Integer versionId;


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
}
