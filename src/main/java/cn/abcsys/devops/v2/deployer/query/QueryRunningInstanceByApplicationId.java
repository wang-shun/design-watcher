package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryRunningInstanceByApplicationId")
public class QueryRunningInstanceByApplicationId implements IQuery{

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;


    private V2ImageGroup getImageGroup(QueryEnvIdParameter qep){
        V2ImageGroup v2ImageGroup = new V2ImageGroup();
        if (qep.getApplicationId() != null && qep.getApplicationId() >0){
            v2ImageGroup.setApplicationId(qep.getApplicationId());
        }
        v2ImageGroup.setStatus("created");
        return v2ImageGroup;
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            if (qep.getApplicationId() == null || qep.getApplicationId() == 0) throw new NullPointerException("根据应用id查询运行中的实例，输入应用id为空或者为0");
            V2ImageGroup v2ImageGroup = getImageGroup(qep);
            List<V2ImageGroup> imageGroupList = imageGroupMapper.selectByFields(v2ImageGroup,0, 9999);
            return new GridBean(1,1,imageGroupList.size(),imageGroupList,true);
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
