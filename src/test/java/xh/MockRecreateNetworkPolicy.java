/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.Labels;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPorts;
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
public class MockRecreateNetworkPolicy {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createRawApplication() throws Exception {
        // 开始构造 NetworkPolicyParameter 的各个变量

        //构造 labels 变量
        List<V2NetworkLabels> labelsList = new ArrayList<>();
        V2NetworkLabels labels = new V2NetworkLabels();
        labels.setLabelKey("test-label");
        labels.setLabelType("ingressPodSelector");
        labels.setLabelValue("test-label");
        labelsList.add(labels);

        //创建 deploy type
        String deployType = "recreateNetworkPolicy";

        //创建 plat from type
        String platFromType = "kubernetes-network";

        //创建 network
        V2NetworkPolicy network = new V2NetworkPolicy();
        network.setApiversion("extensions/v1beta1");
        network.setApplicationId(0);
        network.setId(72);
        //network.setEnvId(0);
        network.setKind("NetworkPolicy");
        network.setMasterIp("133.133.134.106");
        network.setMasterPort("6443");
        network.setMasterType("https");
        network.setObjectName("test-network12");
        network.setNamespace("default");

        //创建 ports
        List<V2NetworkPorts> list_port = new ArrayList<>();
        V2NetworkPorts port = new V2NetworkPorts();
        port.setPortName("port-test");
        port.setNetworkId(0);
        port.setPortValue(8080);
        port.setProtocol("TCP");
        list_port.add(port);

        V2NetworkPorts port2 = new V2NetworkPorts();
        port2.setPortName("port-test2");
        port2.setNetworkId(0);
        port2.setPortValue(8081);
        port2.setProtocol("TCP");
        list_port.add(port2);


        List<Integer> envIdList = new ArrayList<>();
        envIdList.add(11);

        NetworkPolicyParameter parameter = new NetworkPolicyParameter();
        parameter.setDeployType(deployType);
        parameter.setPlatfromType(platFromType);
        parameter.setLabels(labelsList);
        parameter.setNetwork(network);
        parameter.setPorts(list_port);
        parameter.setEnvIdList(envIdList);

        String param_json = JSONObject.toJSONString(parameter);
        mockMvc.perform(post("/v1.6/handleNetworkPolicy.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
