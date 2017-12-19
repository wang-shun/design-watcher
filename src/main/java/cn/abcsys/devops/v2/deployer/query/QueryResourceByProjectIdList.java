package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkPolicyMapper;
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

@Component("queryResourceByProjectIdList")
public class QueryResourceByProjectIdList implements IQuery{

    private Logger logger = Logger.getLogger(QueryResourceByProjectIdList.class);

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper v2ContainerMapper;

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

    public static GridBean getSingleResult(List row){
        return new GridBean(1,1,1, row,true);
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception{
        try {
            QueryEnvIdParameter queryEnvIdParameter = (QueryEnvIdParameter)parameter;
            logger.info("查询资源总数的参数:"+ JSON.toJSONString(queryEnvIdParameter));
            List<Integer> projectIdList = queryEnvIdParameter.getProjectIdList();
            if (projectIdList == null || projectIdList.size()==0) throw new NullPointerException("传入的项目列表为空");
            // List<V2Container> resultContainerList = new ArrayList<>();
            List<ProjectTotalResource> projectTotalResourceList = new ArrayList<>();
            // 每个项目单独弄一个列表存
            for (Integer projectId:projectIdList) {
                List<V2Container> containerList = v2ContainerMapper.selectByProjectId(projectId);
                if (containerList == null || containerList.size()==0){
                    ProjectTotalResource projectTotalResource = new ProjectTotalResource();
                    // 设置 cpu 和 mem
                    projectTotalResource.setTotalCpu(Float.valueOf(0));
                    projectTotalResource.setTotalMem(Float.valueOf(0));
                    // 设置 容器实例数
                    projectTotalResource.setTotalInstance(0);
                    // 设置 项目id
                    projectTotalResource.setProjectId(projectId);
                    projectTotalResourceList.add(projectTotalResource);
                    continue;
                }
                Float totalCpu = new Float(0);
                Float totalMem = new Float(0);
                logger.info("查询资源总数，项目所对应的容器列表："+JSON.toJSONString(containerList));
                for (V2Container v2Container:containerList) {
                    totalCpu+=parseCpu(v2Container.getMaxCpu());
                    totalMem+=parseMem(v2Container.getMaxMemory());
                }
                logger.info("查询资源总数,cpu为 "+ totalCpu+"，mem为 "+totalMem);
                ProjectTotalResource projectTotalResource = new ProjectTotalResource();
                // 设置 cpu 和 mem
                projectTotalResource.setTotalCpu(totalCpu);
                projectTotalResource.setTotalMem(totalMem);
                // 设置 容器实例数
                projectTotalResource.setTotalInstance(containerList.size());
                // 设置 项目id
                projectTotalResource.setProjectId(projectId);
                projectTotalResourceList.add(projectTotalResource);
            }

            GridBean gridBean = getSingleResult(projectTotalResourceList);
            return gridBean;
        }catch (NullPointerException e){
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("传入参数为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
