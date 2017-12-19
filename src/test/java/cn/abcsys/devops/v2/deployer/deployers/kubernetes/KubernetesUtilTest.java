package cn.abcsys.devops.v2.deployer.deployers.kubernetes;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.cores.parameter.NetworkPolicyParameter;
import cn.abcsys.devops.v2.deployer.db.inerfaces.ILabels;
import cn.abcsys.devops.v2.deployer.db.model.V2Labels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPorts;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicyPeer;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicyPort;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class KubernetesUtilTest {
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
        System.out.println("开始测试了");

    }

    @After
    public void tearDown() throws Exception {
        System.out.println("结束测试了");
    }

    @Test
    public void getLabelsByType() throws Exception {   // passed
        //1、构造一个 List<Ilabel:V2Labels> 指派多个 String 类型 2、判断结果
        List<ILabels> list = new ArrayList<>();

        //初始化所有 type 类型的label
        for (LabelType labelType:LabelType.values()) {
            V2NetworkLabels label = new V2NetworkLabels();
            label.setNetworkPolicyId(0);
            label.setId(0);
            label.setLabelKey(labelType.getValue());
            label.setLabelValue(labelType.getValue());
            label.setLabelType(labelType.getValue());
            list.add(label);
        }
        //按照所有的 type 类型来进行测试,每一个 type 应该都只通过了一条，并且获得相应的 key，value 值
        for (LabelType labelType:LabelType.values()){

            Map<String,String> result = KubernetesUtil.getLabelsByType(list, labelType.getValue());
            Assert.assertTrue(result.size() == 1);
            Assert.assertTrue(result.get(labelType.getValue()) == labelType.getValue());
        }


    }

    @Test
    public void getNetworkPolicyFrom() throws Exception {  //passed
        //1、构造一个 NetworkPolicyParameter 对象 2、判断 List<NetworkPolicyPeer> 结果
        NetworkPolicyParameter parameter = new NetworkPolicyParameter();

        //创建 labels,每个 type 创建一个
        List<V2NetworkLabels> list_label = new ArrayList<>();
        for (LabelType labelType:LabelType.values()) {
            V2NetworkLabels label = new V2NetworkLabels();
            label.setNetworkPolicyId(0);
            label.setId(0);
            label.setLabelKey(labelType.getValue());
            label.setLabelValue(labelType.getValue());
            label.setLabelType(labelType.getValue());
            list_label.add(label);
        }
        parameter.setLabels(list_label);

        //创建 deploy type
        parameter.setDeployType("createNetworkPolicy");

        //创建 plat from type
        parameter.setPlatfromType("kubernetesDeployer");

        //创建 network
        V2NetworkPolicy network = new V2NetworkPolicy();
        network.setApiversion("networking.k8s.io/v1");
        network.setApplicationId(0);
        network.setId(0);
        network.setKind("NetworkPolicy");
        network.setMasterIp("133.133.134.146");
        network.setMasterPort("8080");
        network.setMasterType("https");
        network.setObjectName("network_test");
        network.setNamespace("default");
        parameter.setNetwork(network);

        //创建 ports
        List<V2NetworkPorts> list_port = new ArrayList<>();
        V2NetworkPorts port = new V2NetworkPorts();
        port.setPortName("nanshou");
        port.setNetworkId(0);
        port.setPortValue(8080);
        port.setProtocol("TCP");
        list_port.add(port);
        parameter.setPorts(list_port);

        List<NetworkPolicyPeer> result = KubernetesUtil.getNetworkPolicyFrom(parameter);
        for (NetworkPolicyPeer peer: result) {
            //因为是测试，所以每个 type 的 label 都只有一个，所以 getMatchLabels 的 size == 1
            Assert.assertTrue(peer.getNamespaceSelector().getMatchLabels().get("ingressNamespaceSelector") == "ingressNamespaceSelector");
            //Assert.assertTrue(peer.getPodSelector().getMatchLabels().get("ingressPodSelector") == "ingressPodSelector");
        }

    }

    @Ignore
    public void getNetworkPolicyPort() throws Exception {
        //1、构造一个 NetworkPolicyParameter 对象 2、判断 List<NetworkPolicyPort> 结果
        NetworkPolicyParameter parameter = new NetworkPolicyParameter();

        //创建 labels,每个 type 创建一个
        List<V2NetworkLabels> list_label = new ArrayList<>();
        for (LabelType labelType:LabelType.values()) {
            V2NetworkLabels label = new V2NetworkLabels();
            label.setNetworkPolicyId(0);
            label.setId(0);
            label.setLabelType(labelType.getValue());
            label.setLabelKey(labelType.getValue());
            label.setLabelValue(labelType.getValue());
            list_label.add(label);
        }
        parameter.setLabels(list_label);

        //创建 deploy type
        parameter.setDeployType("createNetworkPolicy");

        //创建 plat from type
        parameter.setPlatfromType("kubernetesDeployer");

        //创建 network
        V2NetworkPolicy network = new V2NetworkPolicy();
        network.setApiversion("networking.k8s.io/v1");
        network.setApplicationId(0);
        network.setId(0);
        network.setKind("NetworkPolicy");
        network.setMasterIp("133.133.134.146");
        network.setMasterPort("8080");
        network.setMasterType("https");
        network.setObjectName("network_test");
        network.setNamespace("default");
        parameter.setNetwork(network);

        //创建 ports
        List<V2NetworkPorts> list_port = new ArrayList<>();
        V2NetworkPorts port = new V2NetworkPorts();
        port.setPortName("nanshou");
        port.setNetworkId(0);
        port.setPortValue(8080);
        port.setProtocol("TCP");
        list_port.add(port);
        parameter.setPorts(list_port);

        List<NetworkPolicyPort> result = KubernetesUtil.getNetworkPolicyPort(parameter);
//        for (NetworkPolicyPort result_port: result) {
//            //因为是测试，所以 parameter 的 port 的数量为1，所以 result 的 size == 1
//            Assert.assertTrue(result_port.getPort().intValue() == 8080);
//        }
    }

    @Ignore
    public void createNetworkPolicy() throws Exception {
        //todo 1、构造一个 NetworkPolicyParameter 和 一个KubernetesClient 2、判断 NetworkPolicy 结果
        NetworkPolicyParameter parameter = new NetworkPolicyParameter();

        //创建 labels,每个 type 创建一个
        List<V2NetworkLabels> list_label = new ArrayList<>();
        for (LabelType labelType:LabelType.values()) {
            V2NetworkLabels label = new V2NetworkLabels();
            label.setNetworkPolicyId(0);
            label.setId(0);
            label.setLabelKey(labelType.getValue());
            label.setLabelValue(labelType.getValue());
            label.setLabelType(labelType.getValue());
            list_label.add(label);
        }
        parameter.setLabels(list_label);

        //创建 deploy type
        parameter.setDeployType("createNetworkPolicy");

        //创建 plat from type
        parameter.setPlatfromType("kubernetesDeployer");

        //创建 network
        V2NetworkPolicy network = new V2NetworkPolicy();
        network.setApiversion("extensions/v1beta1");
        network.setApplicationId(0);
        network.setId(0);
        network.setKind("NetworkPolicy");
        network.setMasterIp("133.133.134.146");
        network.setMasterPort("6443");
        network.setMasterType("https");
        network.setObjectName("networktest");
        network.setNamespace("default");
        parameter.setNetwork(network);

        //创建 ports
        List<V2NetworkPorts> list_port = new ArrayList<>();
        V2NetworkPorts port = new V2NetworkPorts();
        port.setPortName("port");
        port.setNetworkId(0);
        port.setPortValue(8080);
        port.setProtocol("TCP");
        list_port.add(port);
        parameter.setPorts(list_port);

        //创建 client

        KubernetesClient client;
        String path = "/Users/mr.xh/crt";
        if(parameter.getNetwork().getMasterType().equals("https")){
            client = KubernetesUtil.createK8sClient(parameter.getNetwork().getMasterType(),
                    parameter.getNetwork().getMasterIp(),parameter.getNetwork().getMasterPort(),
                    path+"/ca.crt",path+"/apiserver-kubelet-client.key",path+"/apiserver-kubelet-client.crt");
        }
        else{
            client = KubernetesUtil.createK8sClient(parameter.getNetwork().getMasterType(),
                    parameter.getNetwork().getMasterIp(),parameter.getNetwork().getMasterPort());
        }

        // 开始测试
        Assert.assertNotNull(KubernetesUtil.createNetworkPolicy(parameter, client));
    }

}