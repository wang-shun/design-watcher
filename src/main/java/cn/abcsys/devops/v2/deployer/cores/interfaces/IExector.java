/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.interfaces;

import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;

import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 11:58
 */
public interface IExector {

    public ResultBean handle(Object params,Object client) throws Exception;
}
