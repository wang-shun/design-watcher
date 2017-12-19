package cn.abcsys.devops.v2.deployer.controllers;


import cn.abcsys.devops.v2.deployer.cores.parameter.HaParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author Zuxingyu zuxingyu<zuxingyu@beyondcent.com>
 * @Date 2017-10-27 13:44
 */

@Controller
@RequestMapping("/v1.6/")
public class NginxHaController {
    
    
    @RequestMapping("createConsulCurl.do")
    public @ResponseBody
    ResultBean createConsulCurl (@RequestBody HaParameter haParameter) {
        ResultBean resultBean = new ResultBean();
        //拼接Url
        
        String url = "http://" + haParameter.getHostIP() + ":" + haParameter.getHostPort() + "/v1/kv/upstreams/" + haParameter.getAppName() + "/" + haParameter.getAppIP()  + ":" +  haParameter.getAppPort();
        //String url = "http://192.168.2.161:8500/v1/kv/upstreams/testApp/192.168.2.177:8080";
        String[] cmds = {"curl", "-X", "PUT", "-d","'{\"weight\":1, \"max_fails\":2, \"fail_timeout\":10}'", url};
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        Process p;
        try {
            p = pb.start();
            BufferedReader br = null;
            String line = null;
            
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (br.readLine() != null) {
                line += br.readLine();
            }
            br.close();
            
            if (line.endsWith("true")) {
                resultBean.setSuccess(true);
                resultBean.setMessage("consul添加成功");
                return resultBean;
            } else if (haParameter.getLoadBlance() == 1){
                line = null;
                url = "http://" + haParameter.getBackupHostIP() + ":" + haParameter.getBackupHostPort() + "/v1/kv/upstreams/" + haParameter.getAppName() + "/" + haParameter.getAppIP() + haParameter.getAppPort();
                String[] cmdsBackup = {"curl", "-X", "PUT", url};
                ProcessBuilder pbBackup = new ProcessBuilder(cmdsBackup);
                pbBackup.redirectErrorStream(true);
                Process pbackup;
                try {
                    pbackup = pbBackup.start();
                    BufferedReader brBackup = null;
    
                    brBackup = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while (brBackup.readLine() != null) {
                        line += br.readLine();
                    }
                    br.close();
                } catch (IOException e) {
                    resultBean.setSuccess(false);
                    resultBean.setMessage("consul添加失败");
                    return resultBean;
                }
            } else {
                resultBean.setSuccess(false);
                resultBean.setMessage("consul添加失败");
                return resultBean;
            }
        } catch (IOException e) {
            resultBean.setSuccess(false);
            resultBean.setMessage("consul添加失败");
            return resultBean;
        }
        resultBean.setSuccess(false);
        resultBean.setMessage("consul删除失败");
        return resultBean;
    }
    
    @RequestMapping("deleteConsulCurl.do")
    public @ResponseBody
    ResultBean deleteConsulCurl (@RequestBody HaParameter haParameter) {
        ResultBean resultBean = new ResultBean();
        //拼接Url
        
        String url = "http://" + haParameter.getHostIP() + ":" + haParameter.getHostPort() + "/v1/kv/upstreams/" + haParameter.getAppName() + "/" + haParameter.getAppIP() + ":" + haParameter.getAppPort();
        //String url = "http://192.168.2.161:8500/v1/kv/upstreams/testApp/192.168.2.177:8080";
        String[] cmds = {"curl", "-X", "DELETE", url};
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        Process p;
        try {
            p = pb.start();
            BufferedReader br = null;
            String line = null;
            
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (br.readLine() != null) {
                line += br.readLine();
            }
            br.close();
            
            if (line.endsWith("true")) {
                resultBean.setSuccess(true);
                resultBean.setMessage("consul删除成功");
                return resultBean;
            } else if (haParameter.getLoadBlance() == 1){
                line = null;
                url = "http://" + haParameter.getBackupHostIP() + ":" + haParameter.getBackupHostPort() + "/v1/kv/upstreams/" + haParameter.getAppName() + "/" + haParameter.getAppIP() + haParameter.getAppPort();
                String[] cmdsBackup = {"curl", "-X", "DELETE", url};
                ProcessBuilder pbBackup = new ProcessBuilder(cmdsBackup);
                pbBackup.redirectErrorStream(true);
                Process pbackup;
                try {
                    pbackup = pbBackup.start();
                    BufferedReader brBackup = null;
                    
                    brBackup = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while (brBackup.readLine() != null) {
                        line += br.readLine();
                    }
                    br.close();
                } catch (IOException e) {
                    resultBean.setSuccess(false);
                    resultBean.setMessage("consul删除失败");
                    return resultBean;
                }
            } else {
                resultBean.setSuccess(false);
                resultBean.setMessage("consul删除失败");
                return resultBean;
            }
        } catch (IOException e) {
            resultBean.setSuccess(false);
            resultBean.setMessage("consul删除失败");
            return resultBean;
        }
        resultBean.setSuccess(false);
        resultBean.setMessage("consul删除失败");
        return resultBean;
    }
}
