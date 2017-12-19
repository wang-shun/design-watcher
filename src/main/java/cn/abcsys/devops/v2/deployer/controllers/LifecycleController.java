/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.managers.DeployerManager;
import cn.abcsys.devops.v2.deployer.utils.ParamsUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 所有的和应用相关的入口，包括 新应用发布，灰度发布、扩容、缩容、发布新版本、发布旧版本、重复发布当前版本
 *
 * 入口进行抽象，主要是传入参数的抽象。因为 应用相关的参数 和 网络、服务参数差别很大，所以大家就不要混在一起了。各有各的参数
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 10:23
 */
@Controller
@RequestMapping("/v1.6/")
public class LifecycleController {

    // 这里有没有必要写一个 manager？毕竟分离已经在 Controller 做了，这里明明可以直接写 KubernetesDeploymentDeployer
    @Resource(name="deployerManager")
    protected DeployerManager dm;

    @RequestMapping("handleDeployer.do")
    @Transactional(propagation = Propagation.REQUIRED)
    public @ResponseBody
    ResultBean create(@RequestBody ImageGroupParameter deployments){
        IDeployer deployer = dm.getDeployer(deployments);
        return deployer.exec(deployments);
    }

}
