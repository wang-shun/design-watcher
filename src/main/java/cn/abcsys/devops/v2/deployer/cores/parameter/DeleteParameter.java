
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.cores.parameter;

/**
 * 删除所有资源的参数抽象类
 *
 * @author xianghao
 * @create 2017-10-09 下午7:28
 **/
public abstract class DeleteParameter {
    String deleteType;

    public String getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(String deleteType) {
        this.deleteType = deleteType;
    }
}
