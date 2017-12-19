/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.EnvParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.LoadParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.PortObject;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * @Author Xianghao xianghao16@otcaix.iscas.ac.cn
 * @Date 2017/9/26 11:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockInsertLoad {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createRawApplication() throws Exception {
        // 开始构造 ImageGroupParameter 的各个变量
        LoadParameter envParameter = new LoadParameter();
        envParameter.setDeployType("insertLoad");
        envParameter.setPlatfromType("kubernetes-load");
 
        //versionList.add(50000015);
        envParameter.setApplicationId(10024);
        PortObject portObject1 = new PortObject();
        portObject1.setPortName("portname1");
        portObject1.setPortValue(8080);
        portObject1.setProtocol("TCP");
        PortObject portObject2 = new PortObject();
        portObject2.setPortName("portname2");
        portObject2.setPortValue(8081);
        portObject2.setProtocol("TCP");
        List<PortObject> portObjectList = new ArrayList<>();
        portObjectList.add(portObject1);
        portObjectList.add(portObject2);
        envParameter.setPortList(portObjectList);


        //parameter.setDeployments(deployments);
        String param_json = JSONObject.toJSONString(envParameter);
        mockMvc.perform(post("/v1.6/handleLoad.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
