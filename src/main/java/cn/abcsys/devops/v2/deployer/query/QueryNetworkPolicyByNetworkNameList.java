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
import java.util.logging.Logger;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryNetworkPolicyByNetworkNameList")
public class QueryNetworkPolicyByNetworkNameList implements IQuery{

    @Resource(name="v2NetworkPolicyMapper")
    private V2NetworkPolicyMapper networkPolicyMapper;

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(QueryNetworkPolicyByNetworkNameList.class);

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception{
        try {
            QueryEnvIdParameter queryEnvIdParameter = (QueryEnvIdParameter)parameter;
            List<String> networkNameList = queryEnvIdParameter.getNetworkNameList();
            Integer envId = queryEnvIdParameter.getEnvId();
            if (networkNameList == null || envId == null) throw new NullPointerException("通过网络的名字列表查询网络，传入的网络名字列表或者环境id为空");
            List<V2NetworkPolicy> resultList = networkPolicyMapper.selectByNetworkNameList(networkNameList, envId);
            if (resultList==null || resultList.size()==0) logger.info("通过网络的名字列表查询网络为空");
            int totalNum = resultList.size();
            int totalPage = 1;

            GridBean gridBean = new GridBean(1, totalPage, totalNum, resultList, true);
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
