package cn.abcsys.devops.v2.deployer.cores.interfaces;

import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;

/**
 * 所有 Mapper 接口的基类
 *
 * @author xianghao
 * @create 2017-10-09 下午7:44
 **/
public interface IQuery {
    GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception;
}
