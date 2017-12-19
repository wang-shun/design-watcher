package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.deployer.dao.InstanceCoreMapper;
import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.v2.appstore.deployer.service.DeploymentService;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.Args;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkPolicyMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2VolumesMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.print.attribute.standard.NumberUp;
import java.util.*;

/**
 * 通过版本id获得所有的容器
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryContainerNetworkVolumeCounts")
public class QueryContainerNetworkVolumeCounts implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name="v2NetworkPolicyMapper")
    private V2NetworkPolicyMapper networkPolicyMapper;

    @Resource(name="v2VolumesMapper")
    private V2VolumesMapper volumesMapper;

    @Resource(name="instanceCoreMapper")
    private InstanceCoreMapper instanceCoreMapper;

    @Resource(name = "deploymentService")
    private DeploymentService deploymentService;

    private V2Container getContainerParams(QueryEnvIdParameter qep){
        V2Container param = new V2Container();
        param.setEnvId(qep.getEnvId());
        return param;
    }


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            Map<String, Object> resultMap = new HashMap<>();

            V2Container params = this.getContainerParams(qep);
            String[] projectIdStringList = null;
            if (qep.getProjectIds() != null && !qep.getProjectIds().equals("")){
                projectIdStringList = qep.getProjectIds().split(",");
            }
            // 获取所有的运行的容器数量
            Integer runningContainerCount = containerMapper.selectLiveCountByFieldsAndProjectIds(projectIdStringList, params);
            resultMap.put("runningContainersCount", runningContainerCount);
            // 获取所有的容器数量
            Integer conttainerCount = containerMapper.selectCountByFieldsAndProjectIds(projectIdStringList, params);
            resultMap.put("ContainersCount", conttainerCount);
            // 获取所有的网络的数量
            Integer networkCount = networkPolicyMapper.getTotalNumByEnvId(qep.getEnvId());
            resultMap.put("networkCount", networkCount);
            // 获取所有的挂载的数量
            Integer volumeCount = volumesMapper.getTotalNumByEnvIdAndProjectIds(projectIdStringList, qep.getEnvId());
            resultMap.put("volumesCount", volumeCount);


            // 获取所有运行的服务容器数量
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("projectIds", qep.getProjectIds());
            paramsMap.put("envId", qep.getEnvId());
            Map<String, Object> componentMap = deploymentService.selectByComponentCoreForDeployer(paramsMap);
            List<Integer> coreIdList = null;
            Integer runStatus = 0;
            Integer notRunStatus = 0;
            if(null !=componentMap && ((Boolean) componentMap.get("success")).compareTo(true) == 0){
                coreIdList = (List)(componentMap.get("coreIdList"));
                List<InstanceCore> instanceCoreList = deploymentService.selectAllComponent(coreIdList);
                Map<String, Object> componentCountMap = deploymentService.getPodStatus(instanceCoreList);
                runStatus = Integer.valueOf(componentCountMap.get("runStatus").toString());
                notRunStatus = Integer.valueOf(componentCountMap.get("notRunStatus").toString());
            }

            resultMap.put("componentRunningCount", runStatus);
            resultMap.put("componentStopCount", notRunStatus);
            // 获取所有的服务容器数量
            resultMap.put("componentCount", runStatus + notRunStatus);
            resultMap.put("success", true);

            GridBean gridBean = new GridBean(1,1,1,  null,true);
            List<Object> objectList = new ArrayList<>();
            objectList.add(resultMap);
            gridBean.setRows(objectList);
            return gridBean;

        }catch (NullPointerException e){
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
