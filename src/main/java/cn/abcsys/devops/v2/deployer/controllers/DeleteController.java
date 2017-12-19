/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.controllers;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeleteReleaseParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.delete.DeleteService;
import cn.abcsys.devops.v2.deployer.watches.PodWatcher;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于所有的资源的删除
 *
 * @author xianghao
 * @create 2017-10-09 下午6:31
 **/

@Controller
@RequestMapping("/v1.6/")
public class DeleteController {

    @Resource(name = "DeleteService")
    protected DeleteService deleteService;

    @RequestMapping("deleteRelease.do")
    public @ResponseBody
    GridBean query(@RequestBody DeleteReleaseParameter deleteReleaseParameter){
        return deleteService.handleDelete(deleteReleaseParameter);
    }

    @RequestMapping("deleteWatch.do")
    public @ResponseBody
    Map<String, Object> query(String hostIp){
        if(AllInit.wMap.containsKey(hostIp)){
            Watch value = (Watch) AllInit.wMap.get(hostIp);
            value.close();
            value = null;
        }
        Map<String,Object> res = new HashMap<>();
        res.put("success",true);
        res.put("message","停止watch！");
        return res;
    }
}
