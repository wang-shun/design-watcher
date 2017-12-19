/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.core;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.abcsys.devops.deployer.core.deployer.Scheduler;
import cn.abcsys.devops.deployer.model.InstanceVolumes;

/**
 * @author wuheng@{otcaix.iscsa.ac.cn,beyondcent.com}
 * @date   May 20, 2017
 * <code>Deployer<code>的作用是在Scheduler作用下
 * 实现Application与Runtime环境的关联
 */
public interface Deployer {

	/**
	 * 获取部署器的UUID
	 * 
	 * @return
	 */
	public String getUUID();
	
	/************************************************
	 * 
	 *                 核心功能
	 *
	 ************************************************/
	/**
	 * 采用Scheduler，在目标运行环境Runtime，创建应用实例Application
	 *
	 * @return
	 * @throws Exception
	 */
	public Object create(String runtimeUUID,
                         String instanceName,
                         String imageName,
                         String imageVersion,
						 List< InstanceVolumes > instanceVolumesList,
                         String cpu,
                         int memory,
                         String network,
                         Map<String, String> envs,
                         Map<String, String> volumes,
                         Properties props) throws Exception;
//	public Object create(InstanceCore instanceCore,
//						 List< InstanceVolumes > instanceVolumesList,
//						 List<InstanceEnvs> instanceEnvsList) throws Exception;
	
	
	/**
	 * 在目标运行环境Runtime，启动应用实例Application
	 * 
	 *
	 * @param props
	 * @return
	 * @throws Exception
	 */
	public Object start(String runtimeUUID, String instanceUUID,
                        Properties props) throws Exception;
	
	/**
	 * 指定目标运行环境Runtime中具体TargetHost，进行应用实例Application的迁移操作
	 * 
	 *
	 * @param targetHost
	 * @return
	 * @throws Exception
	 */
	public Object migrate(String runtimeUUID, String instanceUUID,
                          String targetHost) throws Exception;
	
	/**
	 * 停止应用实例
	 * 
	 *
	 * @return
	 * @throws Exception
	 */
	public Object stop(String runtimeUUID, String instanceUUID) throws Exception;
	
	/**
	 * 删除应用实例
	 * 
	 *
	 * @return
	 * @throws Exception
	 */
	public Object delete(String runtimeUUID, String instanceUUID) throws Exception;
	
	/**
	 * 获取当前调度器
	 * 
	 * @return
	 */
	public Scheduler getScheduler();
	
	/**
	 * 配置调度器
	 * 
	 * @param scheduler
	 * @return
	 */
	public Object setScheduler(Scheduler scheduler);

}
