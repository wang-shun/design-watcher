/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 */
package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ResourcesMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Image;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Resources;
import cn.abcsys.devops.v2.deployer.query.mdoel.CurrentVersionLimitationData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/11/29 0029 15:07
 */
@Component("queryLimitationDataByVersionId")
public class QueryLimitationDataByVersionId implements IQuery {

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2ImageMapper")
    private V2ImageMapper imageMapper;

    @Resource(name="v2ResourcesMapper")
    private V2ResourcesMapper resourcesMapper;

    public static Float parseMem(String maxMem){
        int indexOfGi = maxMem.indexOf("Gi");
        int indexOfMi = maxMem.indexOf("Mi");
        if (indexOfGi >= 0){ //如果为 0，会抛异常
            return Float.parseFloat(maxMem.substring(0, indexOfGi))*1024;
        }else {
            return  Float.parseFloat(maxMem.substring(0, indexOfMi));
        }
    }

    public static Float parseCpu(String maxCpu){
        int indexOfM = maxCpu.indexOf("m");
        if (indexOfM >= 0){ //如果为 0，会抛异常
            return Float.parseFloat(maxCpu.substring(0, indexOfM))/1000;
        }else {
            return  Float.parseFloat(maxCpu);
        }
    }

    private CurrentVersionLimitationData getResources(QueryEnvIdParameter qep){
        Float cpu = 0f;
        Float mem = 0f;
        CurrentVersionLimitationData res = new CurrentVersionLimitationData();
        List<V2ImageGroup> tempList = imageGroupMapper.selectByVersionId(qep.getVersionId());
        if(tempList == null || tempList.size() == 0 || tempList.get(0) == null){
            return res;
        }
        List<V2Image> images = imageMapper.selectByImageGroupId(tempList.get(0).getId());
        if(images == null || images.size() == 0 || images.get(0) == null){
            return res;
        }
        for(V2Image eachOne : images){
            List<V2Resources> resource = resourcesMapper.selectByImageId(eachOne.getId());
            if(resource == null || resource.size() == 0 || resource.get(0) == null){
                return res;
            }
            cpu+=parseCpu(resource.get(0).getMaxCpu());
            mem+=parseMem(resource.get(0).getMaxMem());
        }
        cpu = cpu*1000;
        res.setLimitCpu(cpu.intValue()+"m");
        res.setLimitMemory(mem.intValue()+"Mi");
        res.setCounts(tempList.size());
        return res;
    }

    private GridBean getResult(CurrentVersionLimitationData res){
        GridBean gb = new GridBean();
        gb.setData(res);
        gb.setSuccess(true);
        return gb;
    }

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception {
        QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
        return this.getResult(this.getResources(qep));
    }
}
