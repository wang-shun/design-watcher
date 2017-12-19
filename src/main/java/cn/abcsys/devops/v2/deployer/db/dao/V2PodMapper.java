package cn.abcsys.devops.v2.deployer.db.dao;

import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository(value = "v2PodMapper")
public interface V2PodMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUid(String uid);

    int deleteByImageGroupId(Integer imageGroupId);

    int insert(V2Pod record);

    int insertSelective(V2Pod record);

    V2Pod selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(V2Pod record);
    int updateByPrimaryKeySelectiveStrict(V2Pod record);

    int updateByPrimaryKey(V2Pod record);

    V2Pod getMaxResourceVersion();

    List<V2Pod> selectByImageGroupId(Integer imageGroupId);

    V2Pod selectByLabels(V2ImageGroup imageGroup);
}