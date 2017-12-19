package cn.abcsys.devops.v2.deployer.trigger;

import cn.abcsys.devops.v2.deployer.thread.ClusterInformation;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component("runtimeDeployerTrigger")
public class RuntimeDeployerTrigger {

    private static Logger logger = Logger.getLogger(RuntimeDeployerTrigger.class);

    public static String runtimeURL = "http://localhost:9003/devops-deployer/v1.6/";
    public static String backUrl = "cluster/getClusterMasterAndNodeIps";
    public static Boolean intervalSwitch = true;
    public static String appstoreUrl = "http://localhost:9009/devops-appstore/";

    @Qualifier("selfRestTemplate")
    @Autowired
    private RestTemplate template;

    public List<ClusterInformation> doRequest() throws Exception {
        JSONObject jo = template.getForObject(runtimeURL + backUrl, JSONObject.class);
        List<ClusterInformation> res = new ArrayList<>();
        if(jo.getBoolean("success")
                && jo.getJSONArray("data") != null
                &&jo.getJSONArray("data").size() > 0
                && jo.getJSONArray("data").getJSONObject(0) != null){
            for(int i = 0;i<jo.getJSONArray("data").size();i++){
                ClusterInformation temp = new ClusterInformation(jo.getJSONArray("data").getJSONObject(i).getString("Type"),
                        jo.getJSONArray("data").getJSONObject(i).getString("VIP"),jo.getJSONArray("data").getJSONObject(i).getString("MasterIP"));
                res.add(temp);
            }
        }
        return res;
    }
}
