package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryRunningApplicationsCountByEnvId")
public class QueryRunningApplicationsCountByEnvId implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            Integer envId = qep.getEnvId();
            Integer runningApplicationCount = 0;
            if (qep.getProjectIdList() != null && qep.getProjectIdList().size() > 0){
                runningApplicationCount = containerMapper.getRunningApplicationCountByProjectIdList(qep.getProjectIdList());
            }else {
                runningApplicationCount = containerMapper.getRunningApplicationCount(envId);
            }

            return new GridBean(1,1,runningApplicationCount,null,true);
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
