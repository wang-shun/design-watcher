/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.kubernetes.handler;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.abcsys.devops.deployer.core.util.ConceptsFactory;
import cn.abcsys.devops.deployer.kubernetes.util.DeployerConcepts;
import cn.abcsys.devops.deployer.model.InstanceCore;
import cn.abcsys.devops.deployer.model.ParamsWrapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date 2017/6/13 0014
 * say sth.
 */
@Component("deployerHandler")
public class DeployerHandler {

	private static Logger logger = Logger.getLogger(DeployerHandler.class);

	public Object create(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);

		dc.addOneContainer();
		
		dc.checkNamespace();

		dc.setSameSelectorOrLabels();

		dc.setSecret();
		dc.setVols();
		dc.setController();
		return dc.getController().getMetadata().getName();
	}

	public Object getContainerStatus(ParamsWrapper paramsWrapper){
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setSameSelectorOrLabels();
		return dc.getContainerStatus();
	}

	public Object start(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);
		dc.setSameSelectorOrLabels();

		dc.setPorts();

		dc.setService();
		return dc.getService().getMetadata().getName();
	}

	public Object startApplicationAndImageNameTag(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);
		dc.setApplicationSelectorOrLabels();

		dc.setPorts();

		dc.setAppService();
		return dc.getService().getMetadata().getName();
	}

	public Object getServiceStatus(ParamsWrapper paramsWrapper){
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		return dc.getServiceStatus();
	}

	public Object getAppServiceStatus(ParamsWrapper paramsWrapper){
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		return dc.getAppServiceStatus();
	}

	public Object migrate(ParamsWrapper paramsWrapperOld ,ParamsWrapper paramsWrapperNew) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapperOld);
		dc.setParamsWrapper(paramsWrapperOld);
		dc.deleteService();
		dc.deleteRc();

		this.create(paramsWrapperNew);
		return this.start(paramsWrapperNew);
	}

	public Object stop(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);
		dc.deleteService();
		return dc.getDeleteSvcStatus();

	}

	public Object stopApp(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);
		dc.deleteAppService();
		return dc.getDeleteSvcStatus();

	}

	public Object delete(ParamsWrapper paramsWrapper) throws Exception {
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.setParamsWrapper(paramsWrapper);
		//dc.deleteService();
		dc.deleteRc();
		return dc.getDeleteRcStatus();

	}

	public Object getCurrentContainerDynamicInfo(ParamsWrapper paramsWrapper){
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		dc.resetDynamicConatinerDetailInfo();
		return dc.getParamsWrapper();
	}

	public InstanceCore getPodName(ParamsWrapper paramsWrapper){
		DeployerConcepts dc = ConceptsFactory.getKubernetesDeployerConcepts(paramsWrapper);
		return dc.getPodName();
	}

    public static void main(String[] args) throws Exception {
    	Map<String, String> envs = new HashMap<String, String>();
    	envs.put("test", "test");
    	
    	Map<String, String> volumes = new HashMap<String, String>();
    	volumes.put("/home", "/opt/NfsFileSystem/home");
    	
    	Properties props = new Properties();
    	props.put("namespace", "default");
    	props.put("secret", "my-secret");
    	
    	DeployerHandler main = new DeployerHandler();
//    	ReplicationController rc = (ReplicationController) main.create("http://133.133.134.104:8080", "henry-tomcat",
//    			"registry.cn-hangzhou.aliyuncs.com/xuyuanjia/docker", "tomcat-centos", 
//    			"0-1", 1024, "bridge", 
//    			envs, volumes, props);
//    	Service service = (Service) main.start("http://133.133.134.104:8080", "henry-tomcat", props);
//    	System.out.println(service.getStatus());
	}
}
