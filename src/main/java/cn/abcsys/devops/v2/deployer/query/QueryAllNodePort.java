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
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcLabelsMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2SvcPortsMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Svc;
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

@Component("queryAllNodePort")
public class QueryAllNodePort implements IQuery{

    @Resource(name="v2SvcPortsMapper")
    private V2SvcPortsMapper svcPortsMapper;

    private static Logger logger = Logger.getLogger(QueryAllNodePort.class);


    /**
     * nodeport 的占用和具体的环境、项目、应用无关，只要调用这个接口，那么都会返回所有的已经使用的 NodePort 列表
     * @param parameter
     * @return
     * @throws Exception
     */
    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            List<Integer> resList = svcPortsMapper.selectAllNodeport();
            if (resList == null || resList.size() == 0) throw new IndexOutOfBoundsException("没有正在使用的节点端口列表");
            List<Integer> realRes =new ArrayList<>();
            for(int i = 0;i< resList.size();i++){
                if(resList.get(i) != null){
                    realRes.add(resList.get(i));
                }
            }
            Integer records = realRes.size();
            return new GridBean(1,1,records,realRes,true);
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("传入参数为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (IndexOutOfBoundsException e){
            logger.info(e.getMessage());
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
