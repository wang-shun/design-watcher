package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkPolicyMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryNetworkPolicyByMulti")
public class QueryNetworkPolicyByMulti implements IQuery{

    @Resource(name="v2NetworkPolicyMapper")
    private V2NetworkPolicyMapper networkPolicyMapper;

    private V2NetworkPolicy getNetwork(QueryEnvIdParameter queryEnvIdParameter){
        V2NetworkPolicy v2NetworkPolicy = new V2NetworkPolicy();
        if (queryEnvIdParameter.getEnvId() > 0){
            v2NetworkPolicy.setEnvId(queryEnvIdParameter.getEnvId());
        }
        if (queryEnvIdParameter.getNetworkName() != null && queryEnvIdParameter.getNetworkName()!=""){
            v2NetworkPolicy.setObjectName(queryEnvIdParameter.getNetworkName());
        }
        return v2NetworkPolicy;

    }
    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception{
        try {
            QueryEnvIdParameter queryEnvIdParameter = (QueryEnvIdParameter)parameter;
            Integer pageNum = queryEnvIdParameter.getPageNum(); // >0
            Integer numPerPage = queryEnvIdParameter.getNum_per_page(); // >0
            V2NetworkPolicy v2NetworkPolicy = getNetwork(queryEnvIdParameter);
            List<V2NetworkPolicy> resultList = networkPolicyMapper.selectByFields(v2NetworkPolicy, (pageNum-1)*numPerPage,
                                                                                numPerPage);
            int totalNum = networkPolicyMapper.getTotalNumByFields(v2NetworkPolicy);
            int totalPage = totalNum%numPerPage!=0?totalNum/numPerPage+1:totalNum/numPerPage;

            GridBean gridBean = new GridBean(queryEnvIdParameter.getPageNum(), totalPage, totalNum, resultList, true);
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
