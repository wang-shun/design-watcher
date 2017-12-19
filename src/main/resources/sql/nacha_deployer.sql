/*
Navicat MySQL Data Transfer

Source Server         : 133.133.134.91
Source Server Version : 50638
Source Host           : 133.133.134.91:3306
Source Database       : nacha_deployer

Target Server Type    : MYSQL
Target Server Version : 50638
File Encoding         : 65001

Date: 2017-12-17 22:10:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for configuration_file
-- ----------------------------
DROP TABLE IF EXISTS `configuration_file`;
CREATE TABLE `configuration_file` (
  `file_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `file_name` varchar(30) DEFAULT NULL COMMENT '配置文件名称',
  `file_content` blob COMMENT '配置文件内容',
  `group_id` bigint(11) DEFAULT NULL COMMENT '配置组id',
  `mount_path` varchar(100) DEFAULT NULL COMMENT '挂载路径',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 1存在 0删除',
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configuration_file
-- ----------------------------

-- ----------------------------
-- Table structure for configuration_group
-- ----------------------------
DROP TABLE IF EXISTS `configuration_group`;
CREATE TABLE `configuration_group` (
  `group_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `group_name` varchar(30) DEFAULT NULL COMMENT '配置分组名称',
  `group_file_sum` int(5) DEFAULT NULL COMMENT '配置分组内配置文件数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `env_id` bigint(11) DEFAULT NULL COMMENT '环境ID',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 1存在 0删除',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of configuration_group
-- ----------------------------

-- ----------------------------
-- Table structure for instance_core
-- ----------------------------
DROP TABLE IF EXISTS `instance_core`;
CREATE TABLE `instance_core` (
  `instance_core_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_name` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_rename` varchar(100) COLLATE utf8_bin NOT NULL,
  `instance_proerties` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `instance_namespace` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_type` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_create_datetime` datetime DEFAULT NULL,
  `instance_delete_datetime` datetime DEFAULT NULL,
  `instance_current_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `instance_image` varchar(200) COLLATE utf8_bin NOT NULL,
  `instance_image_tag` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `instance_cpu` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_memory` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_request_cpu` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `instance_request_memory` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `instance_network` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_image_pull_secret` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `app_id` int(11) DEFAULT NULL,
  `app_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `cluster_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `host_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `host_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `container_status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `container_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `image_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `cmd` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `application_runtime_id` int(11) DEFAULT NULL,
  `application_tenant_id` int(11) DEFAULT NULL,
  `component_id` int(11) DEFAULT NULL,
  `master_port` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `master_type` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `instance_replica` int(11) DEFAULT NULL,
  PRIMARY KEY (`instance_core_id`),
  UNIQUE KEY `unique_name` (`instance_rename`)
) ENGINE=InnoDB AUTO_INCREMENT=1376 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of instance_core
-- ----------------------------

-- ----------------------------
-- Table structure for instance_envs
-- ----------------------------
DROP TABLE IF EXISTS `instance_envs`;
CREATE TABLE `instance_envs` (
  `instance_envs_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_core_id` int(11) NOT NULL,
  `instance_envs_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `instance_envs_value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `instance_envs_description` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `instance_envs_create_datetime` datetime DEFAULT NULL,
  `instance_envs_delete_datetime` datetime DEFAULT NULL,
  `instance_envs_current_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`instance_envs_id`),
  KEY `envs_core` (`instance_core_id`),
  CONSTRAINT `envs_core` FOREIGN KEY (`instance_core_id`) REFERENCES `instance_core` (`instance_core_id`)
) ENGINE=InnoDB AUTO_INCREMENT=572 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of instance_envs
-- ----------------------------

-- ----------------------------
-- Table structure for instance_events
-- ----------------------------
DROP TABLE IF EXISTS `instance_events`;
CREATE TABLE `instance_events` (
  `instance_events_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_core_id` int(11) DEFAULT NULL,
  `instance_events_source` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `instance_events_type` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `instance_events_datetime` datetime DEFAULT NULL,
  `instance_events_original` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `instance_events_content` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `instance_events_comment` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `instance_events_create_datetime` datetime DEFAULT NULL,
  `instance_events_delete_datetime` datetime DEFAULT NULL,
  `instance_events_current_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`instance_events_id`),
  KEY `events_core` (`instance_core_id`),
  CONSTRAINT `events_core` FOREIGN KEY (`instance_core_id`) REFERENCES `instance_core` (`instance_core_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of instance_events
-- ----------------------------

-- ----------------------------
-- Table structure for instance_ports
-- ----------------------------
DROP TABLE IF EXISTS `instance_ports`;
CREATE TABLE `instance_ports` (
  `instance_ports_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_core_id` int(11) DEFAULT NULL,
  `instance_ports_port` int(11) DEFAULT NULL,
  `instance_ports_target_port` int(11) DEFAULT NULL,
  `instance_ports_node_port` int(11) DEFAULT NULL,
  `instance_ports_cluster_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `instance_ports_description` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `instance_ports_create_datetime` datetime DEFAULT NULL,
  `instance_ports_delete_datetime` datetime DEFAULT NULL,
  `instance_ports_current_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`instance_ports_id`),
  KEY `ports_core` (`instance_core_id`),
  CONSTRAINT `ports_core` FOREIGN KEY (`instance_core_id`) REFERENCES `instance_core` (`instance_core_id`)
) ENGINE=InnoDB AUTO_INCREMENT=896 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of instance_ports
-- ----------------------------

-- ----------------------------
-- Table structure for instance_volumes
-- ----------------------------
DROP TABLE IF EXISTS `instance_volumes`;
CREATE TABLE `instance_volumes` (
  `instance_volumes_id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_core_id` int(11) DEFAULT NULL,
  `instance_volumes_type` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_volumes_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `instance_volumes_mount_path` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `instance_volumes_server` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `instance_volumes_path` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `instance_volumes_create_datetime` datetime DEFAULT NULL,
  `instance_volumes_delete_datetime` datetime DEFAULT NULL,
  `instance_volumes_current_status` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`instance_volumes_id`),
  KEY `volumes_core` (`instance_core_id`),
  CONSTRAINT `volumes_core` FOREIGN KEY (`instance_core_id`) REFERENCES `instance_core` (`instance_core_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1266 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of instance_volumes
-- ----------------------------

-- ----------------------------
-- Table structure for v2_args
-- ----------------------------
DROP TABLE IF EXISTS `v2_args`;
CREATE TABLE `v2_args` (
  `id` int(11) NOT NULL COMMENT 'id',
  `image_id` int(11) DEFAULT NULL,
  `parameter` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '参数',
  `create_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_args
-- ----------------------------

-- ----------------------------
-- Table structure for v2_container
-- ----------------------------
DROP TABLE IF EXISTS `v2_container`;
CREATE TABLE `v2_container` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pod_id` int(11) DEFAULT NULL,
  `image_group_id` int(11) DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `pod_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `container_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `image_name_tag` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `max_cpu` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `max_memory` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `host_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `host_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_datetime` datetime DEFAULT NULL,
  `start_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=974 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_container
-- ----------------------------

-- ----------------------------
-- Table structure for v2_deployment
-- ----------------------------
DROP TABLE IF EXISTS `v2_deployment`;
CREATE TABLE `v2_deployment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `resource_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_deployment
-- ----------------------------

-- ----------------------------
-- Table structure for v2_envs
-- ----------------------------
DROP TABLE IF EXISTS `v2_envs`;
CREATE TABLE `v2_envs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) DEFAULT NULL COMMENT '容器id',
  `envs_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'key',
  `envs_value` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'value',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=733 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_envs
-- ----------------------------

-- ----------------------------
-- Table structure for v2_event
-- ----------------------------
DROP TABLE IF EXISTS `v2_event`;
CREATE TABLE `v2_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `api_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `first_timestamp` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `kind` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `message` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `resource_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `self_link` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `host` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `v2_event_resource_version_index` (`resource_version`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_event
-- ----------------------------

-- ----------------------------
-- Table structure for v2_image
-- ----------------------------
DROP TABLE IF EXISTS `v2_image`;
CREATE TABLE `v2_image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_group_id` int(11) DEFAULT NULL COMMENT '实例id',
  `container_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `image_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '镜像名称',
  `image_tag` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
  `image_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `pull_secret` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '私有镜像库需要',
  `storage_time` datetime DEFAULT NULL,
  `build_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1949 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_image
-- ----------------------------

-- ----------------------------
-- Table structure for v2_image_group
-- ----------------------------
DROP TABLE IF EXISTS `v2_image_group`;
CREATE TABLE `v2_image_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version_id` int(11) DEFAULT NULL,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `version_group_id` int(11) DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `uuid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `image_group_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'default',
  `api_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `kind` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'rc,rs,deploy,pod',
  `replica` int(11) DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'rc,rs,deploy',
  `master_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_port` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50000004 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_image_group
-- ----------------------------

-- ----------------------------
-- Table structure for v2_labels
-- ----------------------------
DROP TABLE IF EXISTS `v2_labels`;
CREATE TABLE `v2_labels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_group_id` int(11) DEFAULT NULL,
  `label_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'labels,selector,pod-labels,nodeselector',
  `label_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'key',
  `label_value` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'value',
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5022 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_labels
-- ----------------------------

-- ----------------------------
-- Table structure for v2_network_labels
-- ----------------------------
DROP TABLE IF EXISTS `v2_network_labels`;
CREATE TABLE `v2_network_labels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `network_policy_id` int(11) DEFAULT NULL,
  `label_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'labels,selector,pod-labels,nodeselector',
  `label_key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'key',
  `label_value` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'value',
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_network_labels
-- ----------------------------

-- ----------------------------
-- Table structure for v2_network_policy
-- ----------------------------
DROP TABLE IF EXISTS `v2_network_policy`;
CREATE TABLE `v2_network_policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `apiversion` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `kind` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `object_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_port` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_dateime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_network_policy
-- ----------------------------

-- ----------------------------
-- Table structure for v2_network_ports
-- ----------------------------
DROP TABLE IF EXISTS `v2_network_ports`;
CREATE TABLE `v2_network_ports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `network_id` int(11) DEFAULT NULL,
  `port_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `protocol` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `port_value` int(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=739 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_network_ports
-- ----------------------------

-- ----------------------------
-- Table structure for v2_pod
-- ----------------------------
DROP TABLE IF EXISTS `v2_pod`;
CREATE TABLE `v2_pod` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `image_group_id` int(11) DEFAULT NULL,
  `file_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `kind` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `replace_pod_id` int(11) DEFAULT NULL,
  `replace_old_parent_id` int(11) DEFAULT NULL,
  `if_delete_parent` int(11) DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `api_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `resource_version` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1086 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_pod
-- ----------------------------

-- ----------------------------
-- Table structure for v2_ports
-- ----------------------------
DROP TABLE IF EXISTS `v2_ports`;
CREATE TABLE `v2_ports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) DEFAULT NULL,
  `port_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `protocol` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `port_value` int(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2151 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_ports
-- ----------------------------

-- ----------------------------
-- Table structure for v2_probe
-- ----------------------------
DROP TABLE IF EXISTS `v2_probe`;
CREATE TABLE `v2_probe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) DEFAULT NULL,
  `probe_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `request_type` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT 'liveless or readless',
  `probe_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `probe_port` int(11) DEFAULT NULL,
  `scheme` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `initial_delay_seconds` int(11) DEFAULT NULL,
  `timeout_seconds` int(11) DEFAULT NULL,
  `success_threshold` int(11) DEFAULT NULL,
  `failure_threshold` int(11) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_probe
-- ----------------------------

-- ----------------------------
-- Table structure for v2_resources
-- ----------------------------
DROP TABLE IF EXISTS `v2_resources`;
CREATE TABLE `v2_resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) DEFAULT NULL,
  `max_cpu` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `max_mem` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `min_cpu` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `min_mem` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `network` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `max_cpu_value` double(10,1) DEFAULT NULL,
  `max_mem_value` double(10,1) DEFAULT NULL,
  `min_cpu_value` double(10,1) DEFAULT NULL,
  `min_mem_value` double(10,1) DEFAULT NULL,
  `image_group_id` int(11) DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1844 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_resources
-- ----------------------------

-- ----------------------------
-- Table structure for v2_svc
-- ----------------------------
DROP TABLE IF EXISTS `v2_svc`;
CREATE TABLE `v2_svc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `apiversion` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `kind` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `svc_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `svc_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `namespace` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `cluster_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_ip` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `master_port` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_dateime` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_svc
-- ----------------------------

-- ----------------------------
-- Table structure for v2_svc_labels
-- ----------------------------
DROP TABLE IF EXISTS `v2_svc_labels`;
CREATE TABLE `v2_svc_labels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `svc_id` int(11) DEFAULT NULL,
  `label_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'labels,selector,pod-labels,nodeselector',
  `label_key` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `label_value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_svc_labels
-- ----------------------------

-- ----------------------------
-- Table structure for v2_svc_ports
-- ----------------------------
DROP TABLE IF EXISTS `v2_svc_ports`;
CREATE TABLE `v2_svc_ports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `svc_id` int(11) DEFAULT NULL,
  `port_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `protocol` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `port_value` int(11) DEFAULT NULL,
  `target_port` int(11) DEFAULT NULL,
  `node_port` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=694 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_svc_ports
-- ----------------------------

-- ----------------------------
-- Table structure for v2_volume_mounts
-- ----------------------------
DROP TABLE IF EXISTS `v2_volume_mounts`;
CREATE TABLE `v2_volume_mounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_id` int(11) DEFAULT NULL,
  `mount_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `volume_mount_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `image_group_id` int(11) DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2045 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_volume_mounts
-- ----------------------------

-- ----------------------------
-- Table structure for v2_volumes
-- ----------------------------
DROP TABLE IF EXISTS `v2_volumes`;
CREATE TABLE `v2_volumes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_group_id` int(11) DEFAULT NULL,
  `host_path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `volume_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `volume_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `version_id` int(11) DEFAULT NULL,
  `application_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `env_id` int(11) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2058 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of v2_volumes
-- ----------------------------

-- ----------------------------
-- Procedure structure for deleteDirtyInstanceCore
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteDirtyInstanceCore`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `deleteDirtyInstanceCore`(appName varchar(255))
BEGIN
	#Routine body goes here...
DELETE FROM instance_volumes WHERE instance_volumes.instance_core_id = instaceCoreId;
DELETE FROM instance_ports WHERE instance_ports.instance_core_id = instaceCoreId;
DELETE FROM instance_envs WHERE instance_envs.instance_core_id = instaceCoreId;
DELETE FROM instance_events WHERE instance_events.instance_core_id = instaceCoreId;
DELETE FROM instance_core WHERE instance_core.instance_core_id = instaceCoreId;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for deleteDirtyInstanceCoreBatch
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteDirtyInstanceCoreBatch`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `deleteDirtyInstanceCoreBatch`(appName varchar(255))
BEGIN
	#Routine body goes here...
DECLARE badId INT DEFAULT 0;
DECLARE done INT DEFAULT 0;
DECLARE cur1 CURSOR FOR SELECT instance_core.instance_core_id FROM instance_core WHERE instance_core.app_name = appName;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

OPEN cur1;

REPEAT

FETCH cur1 INTO badId;

IF NOT done THEN
	CALL deleteDirtyInstanceCore(badId);
END IF;


UNTIL done END REPEAT;

CLOSE cur1;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for strings_to_array
-- ----------------------------
DROP PROCEDURE IF EXISTS `strings_to_array`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `strings_to_array`(s_str varchar(500), s_split varchar(3))
begin  
  DECLARE i int;
	DECLARE left_str varchar(500);
	DECLARE sub_str varchar(500);
	DECLARE n int;
  set i = length(s_str) - length(replace(s_str,s_split,''));  -- 算出分隔符的总数  
  set left_str = s_str;  
  while i>0                -- 有多少个分隔符就循环多少遍  
  do   
  set sub_str = substr(left_str,1,instr(left_str,s_split)-1);            -- 得到分隔符前面的字符串  
  set left_str = substr(left_str,length(sub_str)+length(s_split)+1);     -- 得到分隔符后面的字符串  
  set n = trim(sub_str);  
  insert into list(id) values(n);  
  set i = i - 1;        
  end while;  
  -- set n = trim(left_str);  
  -- insert into list(id) values(n);  
end
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for upgradeEnvs
-- ----------------------------
DROP PROCEDURE IF EXISTS `upgradeEnvs`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `upgradeEnvs`(str varchar(500),imageId int)
begin  

DECLARE done INT DEFAULT 0;
DECLARE configEnvId int;
DECLARE configKey VARCHAR(255);
DECLARE configValue VARCHAR(255);

DECLARE cur1 CURSOR FOR select id from list;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
drop table if exists list;  
create temporary table list(id INT not NULL); 

CALL strings_to_array(str,';'); 

OPEN cur1;

REPEAT
FETCH cur1 INTO configEnvId;
IF NOT done THEN
	SET configKey = NULL;
	SET configValue = NULL;
	SELECT nacha_application.config_envs.config_envs_name,nacha_application.config_envs.config_envs_value 
	INTO configKey,configValue FROM nacha_application.config_envs WHERE nacha_application.config_envs.config_envs_self_id = configEnvId;
	if configKey IS NOT NULL AND configValue IS NOT NULL THEN
		INSERT INTO nacha_deployer.v2_envs VALUES(NULL,imageId,configKey,configValue,NOW(),NULL);
	END IF;
END IF;


UNTIL done END REPEAT;
CLOSE cur1;

DROP TEMPORARY TABLE IF EXISTS list;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for upgradeInstanceConfig
-- ----------------------------
DROP PROCEDURE IF EXISTS `upgradeInstanceConfig`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `upgradeInstanceConfig`()
BEGIN
	#Routine body goes here...

DECLARE igId INTEGER;
DECLARE newVersionId INTEGER;
DECLARE newApplicationId INTEGER;
DECLARE newProjectId INTEGER;
DECLARE newEnvId INTEGER;
DECLARE newPodId INTEGER;
DECLARE realName VARCHAR(255);
DECLARE imageGroupName VARCHAR(255);
DECLARE igNamespace VARCHAR(255) DEFAULT "default";
DECLARE apiVersion VARCHAR(255) DEFAULT "v1";
DECLARE kind VARCHAR(255) DEFAULT "rc";
DECLARE podReplica INTEGER DEFAULT 1;
DECLARE parentName VARCHAR(255);
DECLARE masterIp VARCHAR(255);
DECLARE masterType VARCHAR(255) DEFAULT "http";
DECLARE masterPort VARCHAR(255) DEFAULT "8080";
DECLARE createTime datetime;
DECLARE updateTime datetime DEFAULT NULL;
DECLARE igStatus VARCHAR(255) DEFAULT "created";

DECLARE minCpu VARCHAR(255);
DECLARE minMemeory VARCHAR(255);
DECLARE maxCpu VARCHAR(255);
DECLARE maxMemeory VARCHAR(255);

DECLARE v2ImageId INTEGER;
DECLARE newImageName VARCHAR(255);
DECLARE newImageTag VARCHAR(255);
DECLARE igExists INTEGER DEFAULT 0;

DECLARE oldConfigCoreId INTEGER;
DECLARE envsString VARCHAR(500);
DECLARE portsString VARCHAR(500);
DECLARE volumesString VARCHAR(500);

DECLARE done INT DEFAULT 0;
DECLARE cur1 CURSOR FOR SELECT instance_core_id,instance_name,instance_rename,instance_namespace,instance_create_datetime,
												instance_cpu,instance_memory,instance_request_cpu,instance_request_memory,
												app_id,app_name,ip,application_runtime_id,instance_create_datetime
												from nacha_deployer.instance_core 
												where nacha_deployer.instance_core.instance_delete_datetime IS NOT NULL and component_id is NULL;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

OPEN cur1;

REPEAT

set newVersionId = NULL;
set oldConfigCoreId = NULL;

FETCH cur1 INTO igId,imageGroupName,realName,igNamespace,createTime,maxCpu,maxMemeory,minCpu,minMemeory,newApplicationId,parentName,masterIp,newEnvId,createTime;

SELECT t.application_project_id INTO newProjectId FROM nacha_application.application_core t WHERE t.application_core_id = newApplicationId;

SELECT t.image_id,t.config_core_id INTO newVersionId,oldConfigCoreId from nacha_application.application_instance_relation t WHERE t.application_core_id = newApplicationId and t.instance_core_id = igId;
SELECT t.config_image_name,t.config_image_tag INTO newImageName,newImageTag from nacha_application.instance_image t WHERE t.config_image_id = newVersionId;
SELECT t.config_envs_ids,t.config_ports_ids,t.config_volumes_ids INTO envsString,portsString,volumesString from nacha_application.config_core t WHERE t.application_core_id = newApplicationId and t.config_core_id = oldConfigCoreId;

IF NOT done THEN
	SELECT count(*) INTO igExists FROM nacha_deployer.v2_image_group WHERE nacha_deployer.v2_image_group.id = igId;
	if igExists = 0 and newVersionId is NOT NULL and oldConfigCoreId is NOT NULL  THEN
		INSERT INTO nacha_deployer.v2_image_group VALUES(
			igId,newVersionId,newApplicationId,newProjectId,newEnvId,NULL,realName,NULL,imageGroupName,NULL,
			igNamespace,apiVersion,kind,podReplica,parentName,masterIp,masterType,masterPort,createTime,NULL,NOW(),igStatus
		);
		INSERT INTO nacha_deployer.v2_image VALUES(NULL,igId,NULL,newImageName,newImageTag,NULL,NULL,NULL,NOW(),NULL);
		select LAST_INSERT_ID() INTO v2ImageId;
		-- 环境变量
		if envsString is NOT NULL and LENGTH(envsString) > 1 THEN
			CALL upgradeEnvs(envsString,v2ImageId);

		END IF;
		-- 端口

		if portsString is NOT NULL and LENGTH(portsString) > 1 THEN
			CALL upgradePorts(portsString,v2ImageId);

		END IF;

		-- 卷
		if volumesString is NOT NULL and LENGTH(volumesString) > 1 THEN
			CALL upgradeVolumes(volumesString,v2ImageId,igId,newVersionId,newApplicationId,newProjectId,newEnvId);

		END IF;

		-- 资源
		INSERT INTO v2_resources VALUES(NULL,v2ImageId,maxCpu,maxMemeory,minCpu,minMemeory,NULL,NOW(),NULL,NULL,NULL,NULL,
		igId,newVersionId,newApplicationId,newProjectId,newEnvId,"created");

		-- STATUS
		INSERT INTO v2_pod VALUES(NULL,NULL,igId,NULL,"rc",NULL,NULL,NULL,realName,NULL,igNamespace,apiVersion,NULL,NOW(),"Running");
		select LAST_INSERT_ID() INTO newPodId;
		INSERT INTO v2_container VALUES(NULL,newPodId,igId,newVersionId,newApplicationId,newProjectId,newEnvId,NULL,NULL,NULL,CONCAT(newImageName,":",newImageTag),"old",maxCpu,maxMemeory,NULL,NULL,createTime,createTime);

	END IF;
END IF;

UNTIL done END REPEAT;
CLOSE cur1;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for upgradePorts
-- ----------------------------
DROP PROCEDURE IF EXISTS `upgradePorts`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `upgradePorts`(str varchar(500),imageId int)
begin  

DECLARE done INT DEFAULT 0;
DECLARE configPortId int;
DECLARE portsPort int;
DECLARE cur1 CURSOR FOR select id from list;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;


drop table if exists list;  
create temporary table list(id INT not NULL); 

CALL strings_to_array(str,';'); 

OPEN cur1;

REPEAT
FETCH cur1 INTO configPortId;
IF NOT done THEN
	SET portsPort = NULL;
	SELECT config_ports_port INTO portsPort FROM nacha_application.config_ports WHERE nacha_application.config_ports.config_ports_self_id = configPortId;
	if portsPort IS NOT NULL THEN
		INSERT INTO nacha_deployer.v2_ports VALUES(NULL,imageId,NULL,NULL,portsPort,NULL,"created");
	END IF;
END IF;


UNTIL done END REPEAT;
CLOSE cur1;

DROP TEMPORARY TABLE IF EXISTS list;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for upgradeVolumes
-- ----------------------------
DROP PROCEDURE IF EXISTS `upgradeVolumes`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `upgradeVolumes`(str varchar(500),imageId int,imageGroupId int,versionId int,applicationId int,projectId int,envId int)
begin  

DECLARE done INT DEFAULT 0;
DECLARE everyOne INT;
DECLARE volumePath VARCHAR(300);
DECLARE volumeMountPath VARCHAR(500);
DECLARE volumeName VARCHAR(255);
DECLARE newVName VARCHAR(255);
DECLARE newVNameIndex int;
DECLARE volumeType VARCHAR(255);
DECLARE volumeRealType VARCHAR(255);
DECLARE cur1 CURSOR FOR select id from list;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;


drop table if exists list;  
create temporary table list(id INT not NULL); 

CALL strings_to_array(str,';'); 
set newVNameIndex = 0;
OPEN cur1;

REPEAT
FETCH cur1 INTO everyOne;
IF NOT done THEN
	SELECT config_volumes_path,config_volumes_mount_path,config_volumes_name,config_volumes_type INTO 
	volumePath,volumeMountPath,volumeName,volumeType FROM nacha_application.config_volumes
	WHERE nacha_application.config_volumes.config_volumes_self_id = everyOne;
	IF volumeType = "share" THEN
		SET volumeRealType = "configFilePath";
	END IF;

	IF volumeType = "appData" THEN
		SET volumeRealType = "applicationDataPath";
	END IF;

	IF volumeType = "local" and volumeMountPath <> "localFileSystem" THEN
		SET volumeRealType = "hostPath";
	END IF;

	IF volumeType = "local" and volumeMountPath = "localFileSystem" THEN
		SET volumeRealType = "logPath";
		SET volumeMountPath = "/abcs/data/local/localFileSystem";
	END IF;

	INSERT INTO v2_volumes VALUES(NULL,imageGroupId,volumeMountPath,CONCAT("volume-",newVNameIndex),volumeRealType,NOW(),versionId,applicationId,projectId,envId,"created");
	INSERT INTO v2_volume_mounts VALUES(NULL,imageId,volumePath,CONCAT("volume-",newVNameIndex),NOW(),imageGroupId,versionId,applicationId,projectId,envId,"created");
	
	set newVNameIndex = newVNameIndex+1;
	-- SELECT res;
END IF;

UNTIL done END REPEAT;
CLOSE cur1;

DROP TEMPORARY TABLE IF EXISTS list;

END
;;
DELIMITER ;
