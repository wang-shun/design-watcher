package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryEnvIdParameter;
import cn.abcsys.devops.v2.deployer.cores.parameter.QueryParameter;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2ContainerMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2ImageGroupMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.deployers.databaseUtil.DBUtil;
import cn.abcsys.devops.v2.deployer.query.mdoel.ImageGroupAndContainer;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过版本id获得所有的容器
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryImageGroupAndContainersByMultipleId")
public class QueryImageGroupAndContainersByMultipleId implements IQuery{

    @Resource(name="v2ContainerMapper")
    private V2ContainerMapper containerMapper;

    @Resource(name="v2ImageGroupMapper")
    private V2ImageGroupMapper imageGroupMapper;

    @Resource(name = "dbUtil")
    private DBUtil dbUtil;

    private Logger logger = Logger.getLogger(QueryImageGroupAndContainersByMultipleId.class);

    private V2Container getContainerParams(QueryEnvIdParameter qep, Integer imageGroupId){
        V2Container param = new V2Container();
        if (qep.getEnvId() != 0) param.setEnvId(qep.getEnvId());
        if (qep.getProjectId() != 0) param.setProjectId(qep.getProjectId());
        if (qep.getApplicationId() != 0) param.setApplicationId(qep.getApplicationId());
        if (qep.getVersionId() != 0) param.setVersionId(qep.getVersionId());
        param.setImageGroupId(imageGroupId);
        logger.info(JSON.toJSONString(param));
        return param;
    }

    private V2ImageGroup getImageGroupParams(QueryEnvIdParameter qep){
        V2ImageGroup param = new V2ImageGroup();
        if (qep.getEnvId() != 0) param.setEnvId(qep.getEnvId());
        if (qep.getProjectId() != 0) param.setProjectId(qep.getProjectId());
        if (qep.getApplicationId() != 0) param.setApplicationId(qep.getApplicationId());
        if (qep.getVersionId() != 0) param.setVersionId(qep.getVersionId());
        param.setStatus("created");
        logger.info(JSON.toJSONString(param));
        return param;
    }


    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws Exception{
        try {
            // 完善 异常处理机制，别往外抛非功能性异常
            List<ImageGroupAndContainer> imageGroupAndContainerList = new ArrayList<>();
            QueryEnvIdParameter qep = (QueryEnvIdParameter)parameter;
            //开始先获得所有的 created 的 imagesGroup 列表
            V2ImageGroup v2ImageGroupParam = this.getImageGroupParams(qep);
            List<V2ImageGroup> v2ImageGroupList = imageGroupMapper.selectByFields(v2ImageGroupParam,(qep.getPageNum()-1)*qep.getNum_per_page(),qep.getNum_per_page());
            //查出来的 v2ImageGroupList 可能是空
            if (v2ImageGroupList == null) throw new IndexOutOfBoundsException("该应用没有对应的还在运行的 imageGroup");
            for (V2ImageGroup v2ImageGroup:v2ImageGroupList) {
                V2Container v2ContainerParam = this.getContainerParams(qep, v2ImageGroup.getId());
                List<V2Container> v2ContainerList = containerMapper.selectByFields(v2ContainerParam,(qep.getPageNum()-1)*qep.getNum_per_page(),qep.getNum_per_page());
                dbUtil.convertV2Container(v2ContainerList);
                //查出来的 v2ContainerList 不出意外应该是只有一个
                if (v2ContainerList == null || v2ContainerList.size() == 0) continue; // 如果容器列表和imageGroup匹配不上，就先不管这个 imageGroup
                ImageGroupAndContainer imageGroupAndContainer = new ImageGroupAndContainer();
                imageGroupAndContainer.setImageGroup(v2ImageGroup);
                imageGroupAndContainer.setContainer(v2ContainerList.get(0));
                imageGroupAndContainerList.add(imageGroupAndContainer);
            }
            Integer records = imageGroupMapper.selectCountByFields(v2ImageGroupParam);
            int total = records%qep.getNum_per_page()!=0?records/qep.getNum_per_page()+1:records/qep.getNum_per_page();
            return new GridBean(qep.getPageNum(),total,records,imageGroupAndContainerList,true);
        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("数据库中相应的 imageGroup 为空或者 imageGroup 对应的 container 为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (IndexOutOfBoundsException e){
            IndexOutOfBoundsException exception = new IndexOutOfBoundsException("查询的 imageGroup 或 container 在数据库中不存在");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        } catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
