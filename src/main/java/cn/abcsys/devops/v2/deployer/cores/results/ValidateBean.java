/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.results;
/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/9/14 0014 17:36
 */
public class ValidateBean {
	private String service;


	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public ValidateBean(String serviceValue){
		this.service = serviceValue;
	}
}
