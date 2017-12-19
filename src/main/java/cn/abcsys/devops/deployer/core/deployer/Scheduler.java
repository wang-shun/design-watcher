/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.core.deployer;

/**
 * @author wuheng@{otcaix.iscsa.ac.cn,beyondcent.com}
 * @date   May 20, 2017
 *
 */
public interface Scheduler {
	
	/**
	 * 返回调度器的UUID
	 * 
	 * @return
	 */
	public String getUUID();
	
	/**
	 * 选取出应用运行的目标地址
	 * 
	 * @param runtime
	 * @return
	 */
	public String getHostUrl(Runtime runtime);

}
