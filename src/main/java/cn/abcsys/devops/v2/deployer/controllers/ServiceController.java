/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.managers.DeployerManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/5 0005 18:26
 */
@Controller
@RequestMapping("/v1.6/")
public class ServiceController {

    @Resource(name="deployerManager")
    protected DeployerManager dm;

    @RequestMapping("handleServicePolicy.do")
    public @ResponseBody
    ResultBean create(@RequestBody ServiceParameter svc){
        IDeployer deployer = dm.getDeployer(svc);
        return  deployer.exec(svc);
    }

}

