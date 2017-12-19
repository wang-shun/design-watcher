/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query.mdoel;

import cn.abcsys.devops.v2.deployer.db.model.V2Svc;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/13 15:07
 * @File AppplicationDetails.java
 */
public class ProjectTotalResource {

    private Integer projectId;
    private Float totalCpu;
    private Float totalMem;
    private Integer totalInstance;

    public Float getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(Float totalCpu) {
        this.totalCpu = totalCpu;
    }

    public Float getTotalMem() {
        return totalMem;
    }

    public void setTotalMem(Float totalMem) {
        this.totalMem = totalMem;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getTotalInstance() {
        return totalInstance;
    }

    public void setTotalInstance(Integer totalInstance) {
        this.totalInstance = totalInstance;
    }
}
