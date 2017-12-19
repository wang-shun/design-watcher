package cn.abcsys.devops.test.kubernetes;/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import cn.abcsys.devops.deployer.core.util.ConceptsFactory;
import cn.abcsys.devops.deployer.kubernetes.handler.DeployerHandler;
import cn.abcsys.devops.deployer.kubernetes.util.DeployerConcepts;
import cn.abcsys.devops.deployer.model.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/6/13 0013
 * say sth.
 */
public class TestDeployerHandler {

    @Resource
    DeployerHandler dh;

    private ApplicationContext ac = null;

    @Before
    public void init(){
        ac = new ClassPathXmlApplicationContext("config/spring-mybatis.xml");
        dh =(DeployerHandler)ac.getBean("deployerHandler");
    }

    @Test
    public void actionTest(){
        Thread.currentThread().getContextClassLoader().getResource("");
        try {
//            ReplicationController rc = (ReplicationController)dh.create(this.getTestParamsWrapper());
//            System.out.println(rc.toString());

            DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(this.getTestParamsWrapper());
            dc.checkNamespace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ParamsWrapper getTestParamsWrapper(){
        InstanceCore ic = new InstanceCore();
        ic.setInstanceName("tomcat");
        ic.setInstanceRename("xuyuanjia-201706131204777-tomcat");
        ic.setIp("http://40.125.203.167:8080");
        ic.setInstanceNamespace("default");
        ic.setInstanceType("kubernetes");
        ic.setInstanceImage("registry.cn-hangzhou.aliyuncs.com/xuyuanjia/docker:tomcat-centos");
        ic.setInstanceCpu("1");
        ic.setInstanceMemory("300Mi");
        ic.setInstanceNetwork("bridge");
        ic.setInstanceImagePullSecret("my-secret");
        //ic.setInstanceTemplateNameLabels("name=label-xuyuanjia-201706131204777-tomcat");

        List<InstanceVolumes> ivl = new ArrayList<InstanceVolumes>();

        List<InstanceEnvs> iel = new ArrayList<InstanceEnvs>();

        List<InstancePorts> ipl = new ArrayList<InstancePorts>();
        InstancePorts ip = new InstancePorts();
        ip.setInstancePortsPort(8080);
        ipl.add(ip);

        ParamsWrapper pw = new ParamsWrapper();
        pw.instanceCore = ic;
        pw.instanceVolumesList = ivl;
        pw.instanceEnvsList = iel;
        pw.instancePorts = ipl;

        return pw;
    }
}
