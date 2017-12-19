/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.delete;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDelete;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeleteParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.managers.DeleteManager;
import cn.abcsys.devops.v2.deployer.managers.QueryManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 处理查询操作的异常，封装结果给 Controller，方便返回给请求者
 *
 * @author xianghao
 * @create 2017-10-09 下午7:17
 **/
@Service("DeleteService")
public class DeleteService {

    private Logger logger = Logger.getLogger(DeleteService.class);

    @Resource(name = "deleteManager")
    protected DeleteManager deleteManager;

    public GridBean handleDelete(DeleteParameter deleteParameter) {
        try {
            IDelete delete = deleteManager.getDelete(deleteParameter);
            delete.excuteDelete(deleteParameter);
            return new GridBean(0,0,0, null, true);
        }catch (NullPointerException e){
            logger.info(e.getMessage());
            return new GridBean(0,0,0, null, false);
        }catch (Exception e){
            logger.info(e.getMessage());
            return new GridBean(0,0,0, null, false);
        }



    }
}
