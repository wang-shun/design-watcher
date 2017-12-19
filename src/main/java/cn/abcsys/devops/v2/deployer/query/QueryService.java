/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.deployer.model.InstanceVolumes;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2VolumesMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Volumes;
import cn.abcsys.devops.v2.deployer.executors.kubernetes.CreateNetworkPolicy;
import cn.abcsys.devops.v2.deployer.managers.QueryManager;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理查询操作的异常，封装结果给 Controller，方便返回给请求者
 *
 * @author xianghao
 * @create 2017-10-09 下午7:17
 **/
@Service("QueryService")
public class QueryService {

    private Logger logger = Logger.getLogger(QueryService.class);

    @Resource(name = "queryManager")
    protected QueryManager queryManager;

    @Resource(name = "v2VolumesMapper")
    protected V2VolumesMapper vvm;

    public GridBean handleQuery(QueryParameter queryParameter) {
        try {
            logger.info("请求参数为："+ JSON.toJSONString(queryParameter));
            logger.info("请求参数为："+ JSON.toJSONString((QueryEnvIdParameter)queryParameter));
            IQuery query = queryManager.getQuery(queryParameter);
            return query.excuteQuery(queryParameter);
        }catch (NullPointerException e){
            e.printStackTrace();
            logger.info(e.getMessage());
            GridBean gridBean = new GridBean(0,0,0, null, false);
            gridBean.setData(e.getMessage());
            return gridBean;
        }catch (IndexOutOfBoundsException e){
            logger.info(e.getMessage());
            GridBean gridBean = new GridBean(0,0,0, null, true);
            gridBean.setData(e.getMessage());
            return gridBean;
        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.getMessage());
            GridBean gridBean = new GridBean(0,0,0, null, false);
            gridBean.setData(e.getMessage());
            return gridBean; 
        }
    }

    public Map<String, Object> selectCountPath(InstanceVolumes iv, Integer envId) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        Integer count = this.vvm.selectCountPath(iv, envId);
        logger.info("count:" + count);
        if (count == null || count != null && count == 0)
            resMap.put("ifMount", false);
        else {
            resMap.put("ifMount", true);
        }
        resMap.put("success", true);
        return resMap;
    }
}
