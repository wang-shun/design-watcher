package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
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

@Component("queryResourceByApplicationId")
public class QueryResourceByApplicationId implements IQuery{

    private Logger logger = Logger.getLogger(QueryResourceByApplicationId.class);

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper v2ContainerMapper;

    public static Float parseMem(String maxMem){
        int indexOfGi = maxMem.indexOf("Gi");
        int indexOfMi = maxMem.indexOf("Mi");
        if (indexOfGi >= 0){ //如果为 0，会抛异常
            return Float.parseFloat(maxMem.substring(0, indexOfGi));
        }else {
            return  Float.parseFloat(maxMem.substring(0, indexOfMi))/1024;
        }
    }

    public static Float parseCpu(String maxCpu){
        int indexOfM = maxCpu.indexOf("m");
        if (indexOfM >= 0){ //如果为 0，会抛异常
            return Float.parseFloat(maxCpu.substring(0, indexOfM))/1000.0f;
        }else {
            return  Float.parseFloat(maxCpu);
        }
    }

    public static GridBean getSingleResult(Object row){
        return new GridBean(1,1,1,new ArrayList<Object>(){
            {
                add(row);
            }
        },true);
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception{
        try {
            QueryEnvIdParameter queryEnvIdParameter = (QueryEnvIdParameter)parameter;
            logger.info("查询资源总数的参数:"+ JSON.toJSONString(queryEnvIdParameter));
            //List<Integer> projectIdList = queryEnvIdParameter.getProjectIdList();
            Integer applicationId = queryEnvIdParameter.getApplicationId();
            V2Container v2ContainerParam = new V2Container();
            v2ContainerParam.setApplicationId(applicationId);
            // 把该应用的所有的容器查出来
            logger.info("查询资源总数，构造的容器对象为："+JSON.toJSONString(v2ContainerParam));
            List<V2Container> resultContainerList = v2ContainerMapper.selectByFields(v2ContainerParam,0, 999);


            Float totalCpu = new Float(0);
            Float totalMem = new Float(0);
            logger.info("查询资源总数，的容器列表："+JSON.toJSONString(resultContainerList));
            for (V2Container v2Container:resultContainerList) {
                totalCpu+=parseCpu(v2Container.getMaxCpu());
                totalMem+=parseMem(v2Container.getMaxMemory());
            }
            logger.info("查询资源总数,cpu为 "+ totalCpu+"，mem为 "+totalMem);
            ProjectTotalResource applicationTotalResource = new ProjectTotalResource();
            applicationTotalResource.setTotalCpu(totalCpu);
            applicationTotalResource.setTotalMem(totalMem);

            GridBean gridBean = getSingleResult(applicationTotalResource);
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
