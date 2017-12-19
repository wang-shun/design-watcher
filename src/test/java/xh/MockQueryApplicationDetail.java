/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/10/13 17:32
 * @File MockQueryApplicationDetail.java
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockQueryApplicationDetail {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testQuery() throws Exception {
        QueryEnvIdParameter param = new QueryEnvIdParameter();
        param.setResourceType("queryApplicationDetailsByApplicationId");
        param.setApplicationId(10084);

        String param_json = JSONObject.toJSONString(param);
        mockMvc.perform(post("/v1.6/queryResource.do")
                .contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();
    }
}
