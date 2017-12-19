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
public class ApplicationDetails {

    private Integer versionCount;
    private Integer runningCount;
    private Integer totalCount;
    private Integer notRunningCount;
    private V2Svc service;

    public Integer getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(Integer versionCount) {
        this.versionCount = versionCount;
    }

    public Integer getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(Integer runningCount) {
        this.runningCount = runningCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNotRunningCount() {
        return notRunningCount;
    }

    public void setNotRunningCount(Integer notRunningCount) {
        this.notRunningCount = notRunningCount;
    }

    public V2Svc getService() {
        return service;
    }

    public void setService(V2Svc service) {
        this.service = service;
    }
}
