/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ServiceParameter;
import cn.abcsys.devops.v2.deployer.db.model.*;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtilTest;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/26 11:36
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockCreateServicePolicy {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    public enum LabelType {
        NETWORKPOLICYLABELS("networkPolicyLabels"),
        SPECPODSELECTOR("specPodSelector"),
        INGRESSPODSELECTOR("ingressPodSelector"),
        INGRESSNAMESPACESELECTOR("ingressNamespaceSelector");

        private final String value;

        private LabelType(String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }

    }

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createRawApplication() throws Exception {
        ServiceParameter serviceParameter = new ServiceParameter();
        serviceParameter.setDeployType("createService");
        serviceParameter.setPlatfromType("kubernetes-service");

        List<V2SvcLabels> v2SvcLabelsList = new ArrayList<>();
        V2SvcLabels v2SvcLabels = new V2SvcLabels();
        //v2SvcLabels.setId();
        v2SvcLabels.setLabelKey("labelkey");
        v2SvcLabels.setLabelType("");
        v2SvcLabels.setLabelValue("labelvalue");
        //v2SvcLabels.setStatus();
        //v2SvcLabels.setSvcId();
        v2SvcLabelsList.add(v2SvcLabels);
        serviceParameter.setLabels(v2SvcLabelsList);

        List<V2SvcPorts> v2SvcPortsList = new ArrayList<>();
        V2SvcPorts v2SvcPorts = new V2SvcPorts();
        //v2SvcPorts.setCreateTime();
        //v2SvcPorts.setId();
        v2SvcPorts.setNodePort(30061);
        v2SvcPorts.setPortName("portname");
        v2SvcPorts.setPortValue(80);
        v2SvcPorts.setProtocol("TCP");
        //v2SvcPorts.setStatus();
        //v2SvcPorts.setSvcId();
        v2SvcPorts.setTargetPort(9376);
        v2SvcPortsList.add(v2SvcPorts);
        serviceParameter.setPorts(v2SvcPortsList);

        V2Svc v2Svc = new V2Svc();
        v2Svc.setApiversion("v1");
        //v2Svc.setApplicationId(); 哪里有应用的概念
        // v2Svc.setClusterIp(); 暂不填
        //v2Svc.setCreateDateime(); 先不填
        //v2Svc.setId();
        v2Svc.setKind("Service");
        v2Svc.setMasterIp("133.133.134.106");
        v2Svc.setMasterPort("6443");
        v2Svc.setMasterType("https");
        v2Svc.setNamespace("default");
        //v2Svc.setStatus();
        v2Svc.setSvcName("servicexhtest");
        v2Svc.setSvcType("NodePort");
        serviceParameter.setService(v2Svc);

        String param_json = JSONObject.toJSONString(serviceParameter);
        mockMvc.perform(post("/v1.6/handleServicePolicy.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
