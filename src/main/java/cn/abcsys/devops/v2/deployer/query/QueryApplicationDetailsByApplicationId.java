/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;
/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/13 14:51
 * @File QueryApplicationDetailsByApplicationId.java
 */
import cn.abcsys.devops.deployer.controller.KubernetesController;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.query.mdoel.ApplicationDetails;
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

@Component("queryApplicationDetailsByApplicationId")
public class QueryApplicationDetailsByApplicationId implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2SvcMapper")
    private V2SvcMapper svcMapper;

    private static Logger logger = Logger.getLogger(QueryApplicationDetailsByApplicationId.class);


    private V2Container getContainerParams(QueryEnvIdParameter qep,String status){
        V2Container param = new V2Container();
        param.setEnvId(qep.getEnvId());
        param.setApplicationId(qep.getApplicationId());
        param.setVersionId(qep.getVersionId());
        param.setStatus(status);
        return param;
    }

    private ApplicationDetails getDetails(QueryEnvIdParameter qep){
        ApplicationDetails ad = new ApplicationDetails();
        V2Container allParams = this.getContainerParams(qep,null);
        V2Container RunningParams = this.getContainerParams(qep,"true");// 当前数据库没有 true 的，只有 creating 和 false
        Integer all = containerMapper.selectCountByFields(allParams);
        Integer running = containerMapper.selectCountByFields(RunningParams);
        Integer versionCount = imageGroupMapper.selectVersionCountsByApplicationId(qep.getApplicationId());
        V2Svc svc = svcMapper.selectOneByApplicationId(qep.getApplicationId());
        ad.setNotRunningCount(all-running);
        ad.setRunningCount(running);
        ad.setService(svc);
        ad.setTotalCount(all);
        ad.setVersionCount(versionCount);
        return ad;
    }

    public static GridBean getSingleResult(Object row){
        return new GridBean(1,1,1,new ArrayList<Object>(){
            {
                add(row);
            }
        },true);
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            //logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            ApplicationDetails ad = this.getDetails(qep);
            //logger.info(">>>>>>>>>>>>>>>>>>>>>>"+JSON.toJSONString(ad));
            return getSingleResult(ad);
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
