/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package xh;

import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
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
 * @Date 2017/9/26 11:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/spring-*.xml")
@WebAppConfiguration
public class MockCreateDeployment {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    public enum LabelType {
        IMAGEGROUPLABELS("imageGroupLabels"),
        IMAGEGROUPSELECTORS("imageGroupSelectors"),
        PODLABELS("podLabels"),
        NODESELECTORS("nodeSelectors");

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
        // 开始构造 ImageGroupParameter 的各个变量
        ImageGroupParameter parameter = new ImageGroupParameter();
        parameter.setReplica(1);
        //创建 deploy type
        String deployType = "createDeployment";
        parameter.setDeployType(deployType);
        //创建 plat from type
        String platfromType = "kubernetes-deployment";
        parameter.setPlatfromType(platfromType);

        //创建 List<DeploymentComponent> deployments
        DeploymentComponent component = new DeploymentComponent();

        //设置 imageGroup
        V2ImageGroup imageGroup = new V2ImageGroup();
        imageGroup.setApiVersion("extensions/v1beta1"); // for versions before 1.6.0 use extensions/v1beta1
        imageGroup.setId(10);
        imageGroup.setKind("Deployment");
        imageGroup.setMasterIp("133.133.134.106");
        imageGroup.setMasterPort("6443");
        imageGroup.setMasterType("https");
        imageGroup.setImageGroupName("bug877-7");
        imageGroup.setNamespace("default");
        imageGroup.setReplica(1);
        imageGroup.setVersionId(10);
        component.setImageGroup(imageGroup);

        //设置 List<ImageComponent>
        List<ImageComponent> imageComponents = new ArrayList<>();
        ImageComponent imageComponent = new ImageComponent();
        V2Image image = new V2Image();
        image.setContainerName("tomcat-name");
        image.setImageName("registry.cn-hangzhou.aliyuncs.com/xuyuanjia/docker");
        image.setImageTag("tomcat-centos");
        image.setId(0);
        imageComponent.setImage(image);


        //设置 List<V2Ports>
        ArrayList<V2Ports> list_v2port = new ArrayList<>();
        V2Ports v2Ports = new V2Ports();
        v2Ports.setPortValue(8080);
        v2Ports.setProtocol("TCP");
        list_v2port.add(v2Ports);
        imageComponent.setPorts(list_v2port);

        //设置 ArrayList<V2Probe>
        ArrayList<V2Probe> v2Probe_list = new ArrayList<>();
        V2Probe v2Probe1 = new V2Probe();
        v2Probe1.setProbeType("livenessProbe");
        v2Probe1.setProbePath("/");
        v2Probe1.setScheme("HTTP");
        v2Probe1.setProbePort(8080);
        v2Probe1.setFailureThreshold(5);
        v2Probe1.setSuccessThreshold(1);
        v2Probe1.setInitialDelaySeconds(60);
        v2Probe1.setTimeoutSeconds(5);
        v2Probe_list.add(v2Probe1);
        imageComponent.setProbes(v2Probe_list);

        //设置 VolumeMount
        List<V2VolumeMounts> volumeMountsList = new ArrayList<>();
        V2VolumeMounts volumeMounts = new V2VolumeMounts();
        volumeMounts.setVolumeMountName("volumename");
        volumeMounts.setMountPath("/opt");
        volumeMountsList.add(volumeMounts);
        imageComponent.setVolumeMounts(volumeMountsList);
        imageComponents.add(imageComponent);
        component.setImages(imageComponents);

        //设置List<V2Labels> 需要满足 imageGroupSelectors 和 podLabels 的 key 和 value 相同 kubernetes.io/hostname=k8snode1.develop.com
        List<V2Labels> v2Labels = new ArrayList<>();
        for (LabelType labelType: LabelType.values()) {
            if (labelType.getValue() == "nodeSelectors"){
//                V2Labels label = new V2Labels();
//                label.setImageGroupId(0);
//                label.setId(0);
//                label.setLabelKey("kubernetes.io/hostname");
//                label.setLabelValue("k8snode1.develop.com");
//                label.setLabelType(labelType.getValue());
//                v2Labels.add(label);
                continue;
            }
            V2Labels label = new V2Labels();
            label.setImageGroupId(0);
            label.setId(0);
            label.setLabelKey("mocktest");
            label.setLabelValue("mocktest");
            label.setLabelType(labelType.getValue());
            v2Labels.add(label);
        }

        component.setLabels(v2Labels);

        //设置 V2Volumes
        List<V2Volumes> volume_list = new ArrayList<>();
        V2Volumes volumes = new V2Volumes();
        volumes.setImageGroupId(0);
        volumes.setId(0);
        volumes.setVolumeName("volumename");
        volumes.setVolumeType("logPath");
        volume_list.add(volumes);
        component.setVolumes(volume_list);

        parameter.setDeployment(component);
        parameter.setReplica(1);

        //parameter.setDeployments(deployments);
        String param_json = JSONObject.toJSONString(parameter);
        mockMvc.perform(post("/v1.6/handleDeployer.do").contentType(MediaType.APPLICATION_JSON).content(param_json)).andDo(print()).andReturn();

//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().json("{'foo':'bar'}"));
    }
}
