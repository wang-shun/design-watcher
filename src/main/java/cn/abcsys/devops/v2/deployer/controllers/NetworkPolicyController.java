/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.managers.DeployerManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/19 15:31
 */

@Controller
@RequestMapping("/v1.6/")
public class NetworkPolicyController {

    @Resource(name="deployerManager")
    protected DeployerManager dm;

    @RequestMapping("handleNetworkPolicy.do")
    public @ResponseBody
    ResultBean create(@RequestBody NetworkPolicyParameter networkPolicies){
        try {
            IDeployer deployer = dm.getDeployer(networkPolicies);
            return  deployer.exec(networkPolicies);
        }catch (Exception e){  //因为 exec 的异常都在 deployer 层处理了，所以只可能是 getDeployer 出现异常
            ResultBean res = new ResultBean();
            e.printStackTrace();
            res.setMessage("传入参数为空或类型错误");
            res.setSuccess(false);
            return res;
        }
    }

    @RequestMapping("checkResourceLimit.do")
    public @ResponseBody
    Map simulateResources(Integer envId, Integer projectId, String resourceCPU, String resourceMemory, Integer resourceInstance, Integer areaFlag){
        System.out.println(envId);
        System.out.println(projectId);
        System.out.println(resourceCPU);
        System.out.println(resourceMemory);
        System.out.println(resourceInstance);
        System.out.println(areaFlag);
        Map<String,Object> res = new HashMap<>();
        res.put("success",true);
        res.put("message","模拟资源限额！");
        return res;
    }

}

