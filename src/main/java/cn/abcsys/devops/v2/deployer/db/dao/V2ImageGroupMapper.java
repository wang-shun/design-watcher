package cn.abcsys.devops.v2.deployer.db.dao;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "v2ImageGroupMapper")
public interface V2ImageGroupMapper{
    Integer checkIsExist(@Param("versionGroupId")Integer versionGroupId , @Param("name") String name);

    int deleteByPrimaryKey(Integer id);

    int insert(V2ImageGroup record);

    int insertSelective(V2ImageGroup record);

    V2ImageGroup selectByPrimaryKey(Integer id);
    V2ImageGroup selectByRealName(String realName);

    int updateByPrimaryKeySelective(V2ImageGroup record);

    int updateByPrimaryKey(V2ImageGroup record);

    List<V2ImageGroup> selectByVersionId(Integer versionId);
    List<V2ImageGroup> selectAllByVersionId(Integer versionId);

    List<Integer> selectRuningVersionByApplicationId(Integer appId);
    List<Integer> selectDeadVersionByApplicationId(Integer appId);

    List<Integer> selectDeadAndCreatedVersionByVersionId(Integer versionId);

    Integer selectVersionCountsByApplicationId(Integer applicationId);

    Integer getRunningDeploymentCountByVersionId(Integer versionId);

    //List<V2ImageGroup> getRunningDeploymentByApplicationId(Integer applicationId);

    List<V2ImageGroup> selectByFields(@Param("imageGroup") V2ImageGroup imageGroup, @Param("offset") Integer offset, @Param("rows")  Integer rows);
    Integer selectCountByFields(@Param("imageGroup") V2ImageGroup imageGroup);
}