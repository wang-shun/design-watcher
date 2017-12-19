/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.upgarde;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.managers.DeployerManager;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/11/3 0003 13:26
 */
@Controller
@RequestMapping("/v1.6/")
public class FirstUpgradeController {

    // 这里有没有必要写一个 manager？毕竟分离已经在 Controller 做了，这里明明可以直接写 KubernetesDeploymentDeployer
    @Resource(name="firstUpgradeService")
    protected FirstUpgradeService firstUpgradeService;

    @RequestMapping("upgradeInstance.do")
    public @ResponseBody
    ResultBean upgrade(){
        return firstUpgradeService.upgrade();
    }

}
