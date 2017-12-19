package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
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

@Component("queryDeadVersionByApplicationId")
public class QueryDeadVersionByApplicationId implements IQuery{

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            List<Integer> resList = imageGroupMapper.selectDeadVersionByApplicationId(qep.getApplicationId());
            List<Integer> resultList = new ArrayList<>();
            for (Integer versionId:resList) {
                if (imageGroupMapper.selectDeadAndCreatedVersionByVersionId(versionId).size() == 0){
                    resultList.add(versionId);
                }
            }
            return new GridBean(1,1,1,resultList,true);
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
