/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPorts;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtilTest;
import com.alibaba.fastjson.JSON;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/26 11:36
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockCreateNetworkPolicy {
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
        // 开始构造 NetworkPolicyParameter 的各个变量

        //构造 labels 变量
        List<V2NetworkLabels> list_label = new ArrayList<>();
        for (KubernetesUtilTest.LabelType labelType: KubernetesUtilTest.LabelType.values()) {
            V2NetworkLabels label = new V2NetworkLabels();
            label.setNetworkPolicyId(0);
            label.setId(0);
            label.setLabelKey(labelType.getValue());
            label.setLabelValue(labelType.getValue());
            label.setLabelType(labelType.getValue());
            list_label.add(label);
        }

        //创建 deploy type
        String deployType = "createNetworkPolicy";

        //创建 plat from type
        String platFromType = "kubernetes-network";

        //创建 network
        V2NetworkPolicy network = new V2NetworkPolicy();
        network.setApiversion("extensions/v1beta1");
        network.setApplicationId(0);
        network.setId(0);
        //network.setEnvId(0);
        network.setKind("NetworkPolicy");
        network.setMasterIp("192.168.2.187");
        network.setMasterPort("8080");
        network.setMasterType("http");
        network.setObjectName("network-test-xh3");
        network.setNamespace("default");

        //创建 ports
        List<V2NetworkPorts> list_port = new ArrayList<>();
        V2NetworkPorts port = new V2NetworkPorts();
        port.setPortName("port-test");
        port.setNetworkId(0);
        port.setPortValue(8080);
        port.setProtocol("TCP");
        list_port.add(port);

        List<Integer> envIdList = new ArrayList<>();
        envIdList.add(10);
        envIdList.add(11);

        NetworkPolicyParameter parameter = new NetworkPolicyParameter();
        parameter.setDeployType(deployType);
        parameter.setPlatfromType(platFromType);
        parameter.setLabels(list_label);
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
