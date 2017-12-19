
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xianghao
 * @create 2017-10-09 下午7:42
 **/

@Service("queryManager")
public class QueryManager {



    protected Map<String, IQuery> queryMap;

    public Map<String, IQuery> getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map<String, IQuery> queryMap) {
        this.queryMap = queryMap;
    }


    public IQuery getQuery(Object obj) throws Exception{
        try {
            IQuery query = null;
            if (obj instanceof QueryParameter){
                query = queryMap.get(((QueryParameter)obj).getResourceType());
            }
            return (query == null) ? null : query;

        }catch (Exception e){
            Exception exception = new Exception("获取 IQuery 失败: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
