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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
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
public class MockRestfulQueryContainerByVersionId {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private RestTemplate template;

    @Before
    public void setUp() throws Exception {
        template = (RestTemplate)webApplicationContext.getBean("selfRestTemplate");
    }

    public static HttpEntity getHttpEntity(Object parameter){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json"));
        System.out.println(JSONObject.toJSONString(parameter));
        return new HttpEntity<String>(JSONObject.toJSONString(parameter),headers);
    }

    @Test
    public void createRawApplication() throws Exception {
        QueryEnvIdParameter queryEnvIdParameter = new QueryEnvIdParameter();
        queryEnvIdParameter.setVersionId(50000098);
        queryEnvIdParameter.setNum_per_page(10);
        queryEnvIdParameter.setPageNum(1);
        queryEnvIdParameter.setResourceType("queryContainersByVersionId");

        System.out.println(JSONObject.toJSONString(queryEnvIdParameter));
        JSONObject jo=  template.postForObject("http://127.0.0.1:9003/devops-deployer/v1.6/queryResource.do",
                getHttpEntity(queryEnvIdParameter),
                JSONObject.class);

        String param_json = JSONObject.toJSONString(queryEnvIdParameter);
    }
}
