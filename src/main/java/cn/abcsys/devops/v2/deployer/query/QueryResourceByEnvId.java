package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryCPUMEMParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.query.mdoel.ProjectTotalResource;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryResourceByEnvId")
public class QueryResourceByEnvId{

    private Logger logger = Logger.getLogger(QueryResourceByEnvId.class);

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper v2ContainerMapper;

    private class ResourceWithCPUMEM{
        private Float cpuSum;
        private Float memSum;
        private Integer instanceCount;

        public Float getCpuSum() {
            return cpuSum;
        }

        public void setCpuSum(Float cpuSum) {
            this.cpuSum = cpuSum;
        }

        public Float getMemSum() {
            return memSum;
        }

        public void setMemSum(Float memSum) {
            this.memSum = memSum;
        }

        public Integer getInstanceCount() {
            return instanceCount;
        }

        public void setInstanceCount(Integer instanceCount) {
            this.instanceCount = instanceCount;
        }
    }

    private class ResultResourceData {
        private ResourceWithCPUMEM projectResources;
        private ResourceWithCPUMEM envResources;
        private Boolean success;
        private String message;

        public ResourceWithCPUMEM getProjectResources() {
            return projectResources;
        }

        public void setProjectResources(ResourceWithCPUMEM projectResources) {
            this.projectResources = projectResources;
        }

        public ResourceWithCPUMEM getEnvResources() {
            return envResources;
        }

        public void setEnvResources(ResourceWithCPUMEM envResources) {
            this.envResources = envResources;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private Float parseMem(String maxMem){
        int indexOfGi = maxMem.indexOf("Gi");
        int indexOfMi = maxMem.indexOf("Mi");
        if (indexOfGi >= 0){ //如果为 0，会抛异常
            Float memNum = Float.parseFloat(maxMem.substring(0, indexOfGi));
            return memNum;
        }else {
            Float memNum = Float.parseFloat(maxMem.substring(0, indexOfMi))/1024;
            return memNum;
        }
    }

    private Float parseCpu(String maxCpu){
        int indexOfM = maxCpu.indexOf("m");
        if (indexOfM >= 0){ //如果为 0，会抛异常
            Float cpuNum = Float.parseFloat(maxCpu.substring(0, indexOfM))/1000;
            return cpuNum;
        }else {
            Float cpuNum = Float.parseFloat(maxCpu);
            return cpuNum;
        }
    }


    public Object excuteQuery(QueryCPUMEMParameter parameter) {
        try {
            QueryCPUMEMParameter queryCPUMEMParameter = (QueryCPUMEMParameter)parameter;
            logger.info("查询资源总数的参数:"+ JSON.toJSONString(queryCPUMEMParameter));
            Integer envId = queryCPUMEMParameter.getApplicationRuntimeId();
            Integer projectId = queryCPUMEMParameter.getApplicationProjectId();
            if (envId == null && projectId == null){  //不能同时为空
                logger.info("查询环境和项目的id不能同时为空");
                ResultResourceData resultResourceData = new ResultResourceData();
                resultResourceData.setSuccess(false);
                resultResourceData.setMessage("参数非法，两个projectId和RuntimeId不能同时为空!");
                return resultResourceData;
            }
            ResultResourceData resultResourceData = new ResultResourceData();
            if (envId != null && envId > 0){ //查询环境总资源
                List<V2Container> containerList = v2ContainerMapper.selectByEnvId(envId);
                if (containerList == null || containerList.size()==0){
                    ResourceWithCPUMEM resourceWithCPUMEM = new ResourceWithCPUMEM();
                    resourceWithCPUMEM.setCpuSum(Float.valueOf(0));
                    resourceWithCPUMEM.setMemSum(Float.valueOf(0));
                    resourceWithCPUMEM.setInstanceCount(0);
                    resultResourceData.setEnvResources(resourceWithCPUMEM);
                }else {
                    Float totalCpu = new Float(0);
                    Float totalMem = new Float(0);
                    logger.info("查询资源总数，环境所对应的容器列表："+JSON.toJSONString(containerList));
                    for (V2Container v2Container:containerList) {
                        totalCpu+=parseCpu(v2Container.getMaxCpu());
                        totalMem+=parseMem(v2Container.getMaxMemory());
                    }
                    logger.info("查询资源总数,cpu为 "+ totalCpu+"，mem为 "+totalMem);
                    ResourceWithCPUMEM resourceWithCPUMEM = new ResourceWithCPUMEM();
                    resourceWithCPUMEM.setCpuSum(totalCpu);
                    resourceWithCPUMEM.setMemSum(totalMem);
                    resourceWithCPUMEM.setInstanceCount(containerList.size());
                    resultResourceData.setEnvResources(resourceWithCPUMEM);
                }

            }

            if (projectId != null && projectId > 0){
                List<V2Container> containerList = v2ContainerMapper.selectByProjectId(projectId);
                if (containerList == null || containerList.size()==0){
                    ResourceWithCPUMEM resourceWithCPUMEM = new ResourceWithCPUMEM();
                    resourceWithCPUMEM.setCpuSum(Float.valueOf(0));
                    resourceWithCPUMEM.setMemSum(Float.valueOf(0));
                    resourceWithCPUMEM.setInstanceCount(0);
                    resultResourceData.setProjectResources(resourceWithCPUMEM);
                }else {
                    Float totalCpu2 = new Float(0);
                    Float totalMem2 = new Float(0);
                    logger.info("查询资源总数，项目所对应的容器列表："+JSON.toJSONString(containerList));
                    for (V2Container v2Container:containerList) {
                        totalCpu2+=parseCpu(v2Container.getMaxCpu());
                        totalMem2+=parseMem(v2Container.getMaxMemory());
                    }
                    logger.info("查询资源总数,cpu为 "+ totalCpu2+"，mem为 "+totalMem2);
                    ResourceWithCPUMEM resourceWithCPUMEM = new ResourceWithCPUMEM();
                    resourceWithCPUMEM.setCpuSum(totalCpu2);
                    resourceWithCPUMEM.setMemSum(totalMem2);
                    resourceWithCPUMEM.setInstanceCount(containerList.size());
                    resultResourceData.setProjectResources(resourceWithCPUMEM);
                }

            }

            resultResourceData.setSuccess(true);
            resultResourceData.setMessage("获取到当前环境或者项目下的CPU和memory总和 !");

            return resultResourceData;
        }catch (NullPointerException e){
            e.printStackTrace();
            ResultResourceData resultResourceData = new ResultResourceData();
            resultResourceData.setSuccess(false);
            resultResourceData.setMessage("参数非法");
            return resultResourceData;
        }catch (Exception e){
            e.printStackTrace();
            ResultResourceData resultResourceData = new ResultResourceData();
            resultResourceData.setSuccess(false);
            resultResourceData.setMessage("查询数据库发生未知错误");
            return resultResourceData;
        }
    }
}
