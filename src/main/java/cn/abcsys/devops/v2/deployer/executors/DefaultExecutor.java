/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.executors;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:27
 */
@Service("defaultExecutor")
public class DefaultExecutor implements IExector {
    @Override
    public ResultBean handle(Object params,Object client) {
        return null;
    }
}
