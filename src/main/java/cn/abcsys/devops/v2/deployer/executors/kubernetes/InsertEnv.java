package cn.abcsys.devops.v2.deployer.executors.kubernetes;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IExector;
import cn.abcsys.devops.v2.deployer.cores.parameter.DeploymentComponent;
import cn.abcsys.devops.v2.deployer.cores.parameter.EnvParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.ImageGroupParameter;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2EnvsMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Envs;
import cn.abcsys.devops.v2.deployer.db.model.V2Image;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import com.alibaba.fastjson.JSON;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 * @Date 2017/8/25 12:36
 */
@Service("insertEnv")
public class InsertEnv implements IExector {

    @Resource(name = "dbUtil")
    protected DBUtil dbUtil;

    @Resource(name = "v2ImageGroupMapper")
    protected V2ImageGroupMapper v2ImageGroupMapper;

    @Resource(name = "v2ImageMapper")
    protected V2ImageMapper v2ImageMapper;

    @Resource(name = "v2EnvsMapper")
    protected V2EnvsMapper v2EnvsMapper;

    private static Logger logger = Logger.getLogger(InsertEnv.class);

    private ResultBean result(){
        ResultBean rb = new ResultBean();
        rb.setSuccess(true);
        rb.setMessage("插入环境变量成功");
        return rb;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResultBean handle(Object params,Object client) throws Exception{
        try {
            EnvParameter igp = (EnvParameter)params;
            logger.info(">>>>>>>>>开始插入数据库了");
            // 获得所有的 imageGroup
            List<V2ImageGroup> totalImageGroupList = new ArrayList<>();
            for (Integer versionId: igp.getVersionIdList()) {
                List<V2ImageGroup> imageGroupList = v2ImageGroupMapper.selectAllByVersionId(versionId);
                if (imageGroupList == null || imageGroupList.size()==0) continue;
                totalImageGroupList.addAll(imageGroupList);
            }
            if (totalImageGroupList == null || totalImageGroupList.size()==0){
                return result();
            }
            // 获得所有的 image
            List<V2Image> totalImageList = new ArrayList<>();
            for (V2ImageGroup v2ImageGroup:totalImageGroupList) {
                List<V2Image> imageList = v2ImageMapper.selectByImageGroupId(v2ImageGroup.getId());
                if (imageList == null || imageList.size()==0) continue;
                totalImageList.addAll(imageList);
            }
            if (totalImageList == null || totalImageList.size()==0){
                return result();
            }
            // 插入 env
            Map<String,String> failureEnvMap = new HashMap<>();
            for (V2Image v2Image:totalImageList) {
                Iterator<Map.Entry<String, String>> envIterator = igp.getEnvMap().entrySet().iterator();
                while (envIterator.hasNext()){
                    Map.Entry<String, String> entry = envIterator.next();
                    //如果 image 已经有这个 env 了，那么略过这一个环境变量
                    List<V2Envs> v2EnvsList = v2EnvsMapper.selectByEnvKey(entry.getKey(), v2Image.getId());
                    if (v2EnvsList != null && v2EnvsList.size()>0) continue;

                    V2Envs v2Envs = new V2Envs();
                    v2Envs.setImageId(v2Image.getId());
                    v2Envs.setEnvsKey(entry.getKey());
                    v2Envs.setEnvsValue(entry.getValue());
                    v2Envs.setCreateTime(new Date());
                    try {
                        v2EnvsMapper.insertSelective(v2Envs);
                        logger.info("插入环境变量到数据库中成功");
                    }catch (Exception e){
                        failureEnvMap.put(entry.getKey(), entry.getValue());
                        logger.info("插入环境变量到数据库中失败，环境变量为："+entry.getKey()+":"+entry.getValue());
                    }
                }
            }
            if (failureEnvMap.isEmpty() || failureEnvMap.size()==0){
                return result();
            }
            ResultBean rb = new ResultBean();
            rb.setSuccess(true);
            rb.setMessage("插入环境变量部分成功，不成功的环境变量有："+ JSON.toJSONString(failureEnvMap));
            return rb;
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage()+"插入环境变量 出现空指针异常，可能是数据库连接错误");
        } catch (Exception e){
            e.printStackTrace();
            NullPointerException exception = new NullPointerException("数据库插入和创建失败"+e.getMessage().getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }

    }
}
