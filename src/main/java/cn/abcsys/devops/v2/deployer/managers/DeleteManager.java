
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.managers;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IDelete;
import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeleteParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xianghao
 * @create 2017-10-09 下午7:42
 **/

@Service("deleteManager")
public class DeleteManager {

    protected Map<String, IDelete> deleteMap;

    public Map<String, IDelete> getDeleteMap() {
        return deleteMap;
    }

    public void setDeleteMap(Map<String, IDelete> deleteMap) {
        this.deleteMap = deleteMap;
    }


    public IDelete getDelete(Object obj) throws Exception{
        try {
            IDelete delete = null;
            if (obj instanceof DeleteParameter){
                delete = deleteMap.get(((DeleteParameter)obj).getDeleteType());
            }
            return (delete == null) ? null : delete;

        }catch (Exception e){
            Exception exception = new Exception("获取 IDelete 失败: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
