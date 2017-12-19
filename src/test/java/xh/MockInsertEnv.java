/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.DeleteReleaseParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.EnvParameter;
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
public class MockInsertEnv {
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
        EnvParameter envParameter = new EnvParameter();
        envParameter.setDeployType("insertEnv");
        envParameter.setPlatfromType("kubernetes-env");
        Map<String, String> envMap = new HashMap<>();
        envMap.put("envkey","env1110test");
        envMap.put("env1110test2","env1110test2");
        envParameter.setEnvMap(envMap);
        List<Integer> versionList = new ArrayList<>();
        versionList.add(50000027);
        //versionList.add(50000015);
        envParameter.setVersionIdList(versionList);


        //parameter.setDeployments(deployments);
        String param_json = JSONObject.toJSONString(envParameter);
        mockMvc.perform(post("/v1.6/handleEnv.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
