/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.utils;

import cn.abcsys.devops.v2.deployer.cores.Lifecycle;

import java.util.Map;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/24 11:08
 */
public class ParamsUtils {

    public static String getDeployerType(Map<String, Object> params) {
        if(params == null || params.get("type") == null) {
            return "default";
        }
        return String.valueOf(params.remove("type"));
    }

    public static String toExecutorType(String type, Lifecycle lc) {
        if (type == null || lc == null) {
            return "default";
        }
        return type + "." + lc.toString();
    }

    public static void main(String[] args) {
        Lifecycle lc = Lifecycle.CREATE;
        System.out.println(lc.toString());
    }
}
