package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.v2.deployer.cores.results.ValidateBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1.6/")
public class ServerController {

    // 发布（变更）应用的统一入口；
    @RequestMapping("isDeployer.do")
    public @ResponseBody
    ValidateBean releaseApplication(){
        ValidateBean validateBean = new ValidateBean("service.deployer");
        return validateBean;
    }
}
