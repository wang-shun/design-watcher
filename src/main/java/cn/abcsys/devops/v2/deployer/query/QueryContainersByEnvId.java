package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryContainersByEnvId")
public class QueryContainersByEnvId implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name = "dbUtil")
    private DBUtil dbUtil;

    private V2Container getContainerParams(QueryEnvIdParameter qep){
        V2Container param = new V2Container();
        param.setEnvId(qep.getEnvId());
        return param;
    }


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            V2Container params = this.getContainerParams(qep);
            List<V2Container> resList = containerMapper.selectAll(params,(qep.getPageNum()-1)*qep.getNum_per_page(),qep.getNum_per_page());
            dbUtil.convertV2Container(resList);
            Integer records = containerMapper.selectCountByFields(params);
            int total = records%qep.getNum_per_page()!=0?records/qep.getNum_per_page()+1:records/qep.getNum_per_page();
            return new GridBean(qep.getPageNum(),total,records,resList,true);
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
