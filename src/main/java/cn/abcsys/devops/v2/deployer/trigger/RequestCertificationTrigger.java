package cn.abcsys.devops.v2.deployer.trigger;

import cn.abcsys.devops.v2.deployer.cores.parameter.RequestCertificationFileParameter;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
@Component("requestCertificationTrigger")
public class RequestCertificationTrigger{
    @Qualifier("selfRestTemplate")
    @Autowired
    private RestTemplate template;

    public static String baseUrl;

    public RestTemplate getTemplate() {
        return template;
    }

    public void setTemplate(RestTemplate template) {
        this.template = template;
    }

    private static Logger logger = Logger.getLogger(RequestCertificationTrigger.class);

    @Transactional(propagation = Propagation.REQUIRED)
    public JSONObject doRequest(RequestCertificationFileParameter rcfp) {
        JSONObject jo = new JSONObject();
        String res1 =  template.getForObject("http://"+rcfp.getUrl()+baseUrl+"?1="+rcfp.getApiserverKubeletClientCrt(),
                String.class);
        String res2 = template.getForObject("http://"+rcfp.getUrl()+baseUrl+"?1="+rcfp.getApiserverKubeletClientKey(),
                String.class);
        String res3 = template.getForObject("http://"+rcfp.getUrl()+baseUrl+"?1="+rcfp.getCaCrt(),
                String.class);
        jo.put("apiserverKubeletClientCrt",res1);
        jo.put("apiserverKubeletClientKey",res2);
        jo.put("caCrt",res3);
        return jo;
    }
}
