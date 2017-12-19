/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/29 0029 17:40
 */
@Component("queryOneContainerDetailById")
public class QueryOneContainerDetailById implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name = "dbUtil")
    private DBUtil dbUtil;

    private V2Container getContainerParams(QueryEnvIdParameter qep){
        V2Container res = containerMapper.selectByPrimaryKey(qep.getContainerId());
        V2ImageGroup ig =imageGroupMapper.selectByPrimaryKey(res.getImageGroupId());
        KubernetesClient client = KubernetesUtil.getClient(ig.getMasterType(),ig.getMasterIp(),ig.getMasterPort());
        KubernetesUtil.getContainerName(res,client);
        return res;
    }


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            V2Container res = this.getContainerParams(qep);
            List<V2Container> resList = new ArrayList<>();
            resList.add(res);
            dbUtil.convertV2Container(resList);
            return new GridBean(1,1,1,resList,true);
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
