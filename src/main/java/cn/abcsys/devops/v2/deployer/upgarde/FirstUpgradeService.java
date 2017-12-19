package cn.abcsys.devops.v2.deployer.upgarde;

import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2PodMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Labels;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("firstUpgradeService")
public class FirstUpgradeService {

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name="v2PodMapper")
    private V2PodMapper podMapper;

    @Resource(name="dbUtil")
    private DBUtil dbUtil;

    public ResultBean upgrade(){
        Long t1 = System.currentTimeMillis();
        Map<String,Object> data = new HashMap<>();
        ResultBean res = new ResultBean();
        List<V2Container> oldCs = containerMapper.selectOldContainers();
        if(oldCs !=null && oldCs.size() > 0){
            for(V2Container container :  oldCs){
                V2ImageGroup oldIg = imageGroupMapper.selectByPrimaryKey(container.getImageGroupId());
                V2Pod oldPod = podMapper.selectByPrimaryKey(container.getPodId());
                if(oldIg != null && oldPod != null){
                    List<V2Labels> newLabels= dbUtil.resetNewLabels(oldIg.getId());
                    if(KubernetesUtil.editOldRcPod(oldIg,newLabels,container,oldPod)){
                        podMapper.updateByPrimaryKeySelective(oldPod);
                        containerMapper.updateByPrimaryKeySelective(container);
                        data.put(oldPod.getRealName(),"获取，添加标签完毕！");
                    }
                    else{
                        data.put(oldIg.getRealName(),"实例获取不到对应的状态信息！");
                    }
                }
            }
        }
        Long t2 = System.currentTimeMillis();
        data.put("costSeconds",(t2-t1)/1000);
        res.setData(data);
        res.setSuccess(true);
        return res;
    }
    public V2ContainerMapper getContainerMapper() {
        return containerMapper;
    }

    public void setContainerMapper(V2ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    public V2ImageGroupMapper getImageGroupMapper() {
        return imageGroupMapper;
    }

    public void setImageGroupMapper(V2ImageGroupMapper imageGroupMapper) {
        this.imageGroupMapper = imageGroupMapper;
    }

    public V2PodMapper getPodMapper() {
        return podMapper;
    }

    public void setPodMapper(V2PodMapper podMapper) {
        this.podMapper = podMapper;
    }

    public DBUtil getDbUtil() {
        return dbUtil;
    }

    public void setDbUtil(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }
}
