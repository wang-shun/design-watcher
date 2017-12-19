/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.deployer.model.InstanceVolumes;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IDeployer;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryCPUMEMParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.query.QueryResourceByEnvId;
import cn.abcsys.devops.v2.deployer.query.QueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于所有的资源的查询
 *
 * @author xianghao
 * @create 2017-10-09 下午6:31
 **/

@Controller
@RequestMapping("/v1.6/")
public class QueryController {

    @Resource(name = "QueryService")
    protected QueryService queryService;

    @Resource(name = "queryResourceByEnvId")
    protected QueryResourceByEnvId queryResourceByEnvId;


    @RequestMapping("queryResource.do")
    public @ResponseBody
    GridBean query(@RequestBody QueryEnvIdParameter queryParameter){
        return queryService.handleQuery(queryParameter);
    }

    @RequestMapping("getCPUMEMCount.do")
    public @ResponseBody
    Object query(@RequestBody QueryCPUMEMParameter queryParameter){
        return queryResourceByEnvId.excuteQuery(queryParameter);
    }

    @RequestMapping("checkVolumesPath.do")
    public @ResponseBody
    Map checkVolumesPath(InstanceVolumes iv, Integer envId){
        if(iv == null || iv !=null && iv.getInstanceVolumesPath() == null ){
            return new HashMap<String,Object>(){
                {
                    put("message","参数输入错误！");
                    put("success",false);
                }
            };
        }
        return this.queryService.selectCountPath(iv,envId);
    }
}
