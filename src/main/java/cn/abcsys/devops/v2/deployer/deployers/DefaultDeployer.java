/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.deployers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 10:51
 */
@Component("defaultDeployer")
public class DefaultDeployer implements IDeployer {

    @Override
    public ResultBean exec(Object params) {
        //暂时先不实现该类型的Deployer
        ResultBean resultBean = new ResultBean();
        resultBean.setSuccess(false);
        resultBean.setMessage("传入了错误的参数，导致来到了 DefaultDeployer");
        return resultBean;
    }
}
