/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;
/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/9/14 0014 15:38
 */
public class Resources {
    protected String maxCpu;
    protected String maxMem;
    protected String minCpu;
    protected String minMem;
    protected String network;

    public String getMaxCpu() {
        return maxCpu;
    }

    public void setMaxCpu(String maxCpu) {
        this.maxCpu = maxCpu;
    }

    public String getMaxMem() {
        return maxMem;
    }

    public void setMaxMem(String maxMem) {
        this.maxMem = maxMem;
    }

    public String getMinCpu() {
        return minCpu;
    }

    public void setMinCpu(String minCpu) {
        this.minCpu = minCpu;
    }

    public String getMinMem() {
        return minMem;
    }

    public void setMinMem(String minMem) {
        this.minMem = minMem;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
