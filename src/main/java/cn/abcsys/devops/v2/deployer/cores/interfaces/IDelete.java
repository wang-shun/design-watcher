package cn.abcsys.devops.v2.deployer.cores.interfaces;

import cn.abcsys.devops.v2.deployer.cores.parameter.DeleteParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;

/**
 * 所有 Mapper 接口的基类
 *
 * @author xianghao
 * @create 2017-10-09 下午7:44
 **/
public interface IDelete {
    void excuteDelete(DeleteParameter parameter) throws NullPointerException, Exception;
}
