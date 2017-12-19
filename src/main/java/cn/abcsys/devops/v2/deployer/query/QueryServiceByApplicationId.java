/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;
/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/13 14:51
 * @File QueryApplicationDetailsByApplicationId.java
 */
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.*;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.client.KubernetesClient;
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

@Component("queryServiceByApplicationId")
public class QueryServiceByApplicationId implements IQuery{

    @Resource(name="v2SvcMapper")
    private V2SvcMapper svcMapper;

    @Resource(name="v2SvcPortsMapper")
    private V2SvcPortsMapper svcPortsMapper;

    @Resource(name="v2SvcLabelsMapper")
    private V2SvcLabelsMapper svcLabelsMapper;


    private static Logger logger = Logger.getLogger(QueryServiceByApplicationId.class);


    /**
     * 默认一个应用下只能有一个服务
     * @param qep
     * @return
     * @throws Exception
     */
    private List<ServiceParameter> getServiceList(QueryEnvIdParameter qep) throws Exception{
        try {
            List<V2Svc> v2SvcList = svcMapper.selectAllByApplicationId(qep.getApplicationId(),
                    (qep.getPageNum()-1)*qep.getNum_per_page(),qep.getNum_per_page());
            if (v2SvcList == null || v2SvcList.size()==0){
                throw new IndexOutOfBoundsException("数据库中该应用不存在service");
            }
            KubernetesClient client = KubernetesUtil.getClient(v2SvcList.get(0).getMasterType(),
                    v2SvcList.get(0).getMasterIp(), v2SvcList.get(0).getMasterPort());
            String clusterIp = KubernetesUtil.getSvcClusterIp(v2SvcList.get(0), client);
            v2SvcList.get(0).setClusterIp(clusterIp);
            List<ServiceParameter> serviceParameterList = new ArrayList<>();
            for (V2Svc v2Svc : v2SvcList) {
                ServiceParameter serviceParameter = new ServiceParameter();
                serviceParameter.setService(v2Svc);
                serviceParameter.setPorts(svcPortsMapper.selectAllByServiceId(v2Svc.getId()));
                serviceParameter.setLabels(svcLabelsMapper.selectAllByServiceId(v2Svc.getId()));
                serviceParameterList.add(serviceParameter);
            }
            return serviceParameterList;
        }catch (IndexOutOfBoundsException e){
            logger.info("数据库中该应用不存在service");
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询数据库的服务的过程中出现未知错误"+e.getMessage()+e.getClass());
            Exception exception = new Exception("查询数据库的服务的过程中出现未知错误"+e.getMessage()+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            List<ServiceParameter> resList = getServiceList(qep);
            Integer records = svcMapper.getTotalNumByApplicationId(qep.getApplicationId());
            int total = records%qep.getNum_per_page()!=0?records/qep.getNum_per_page()+1:records/qep.getNum_per_page();
            return new GridBean(qep.getPageNum(),total,records,resList,true);
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("传入参数为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (IndexOutOfBoundsException e){
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
