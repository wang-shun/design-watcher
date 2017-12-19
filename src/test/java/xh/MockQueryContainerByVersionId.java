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
 * 测试 NetworkPolicy 资源的 EnvId 查询功能
 *
 * @author xianghao
 * @create 2017-10-09 下午10:02
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockQueryContainerByVersionId {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void createRawApplication() throws Exception {
       //开始构建 QueryParameter
        QueryEnvIdParameter queryEnvIdParameter = new QueryEnvIdParameter();
        queryEnvIdParameter.setVersionId(6);
        queryEnvIdParameter.setNum_per_page(10);
        queryEnvIdParameter.setPageNum(1);
        //queryEnvIdParameter.setRealName("container");
        //queryEnvIdParameter.setPodName("app");
        queryEnvIdParameter.setResourceType("queryContainersByVersionId");

        System.out.println(JSONObject.toJSONString(queryEnvIdParameter));


        String param_json = JSONObject.toJSONString(queryEnvIdParameter);
        mockMvc.perform(post("/v1.6/queryResource.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
