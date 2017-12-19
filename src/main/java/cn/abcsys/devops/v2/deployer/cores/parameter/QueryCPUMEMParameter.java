package cn.abcsys.devops.v2.deployer.cores.parameter;

import java.util.List;

/**
 * 通过资源 ID 进行资源查询
 *
 * @author xianghao
 * @create 2017-10-09 下午7:35
 **/
public class QueryCPUMEMParameter extends QueryParameter{
    Integer applicationRuntimeId;
    Integer applicationProjectId;


    public Integer getApplicationRuntimeId() {
        return applicationRuntimeId;
    }

    public void setApplicationRuntimeId(Integer applicationRuntimeId) {
        this.applicationRuntimeId = applicationRuntimeId;
    }

    public Integer getApplicationProjectId() {
        return applicationProjectId;
    }

    public void setApplicationProjectId(Integer applicationProjectId) {
        this.applicationProjectId = applicationProjectId;
    }
}
