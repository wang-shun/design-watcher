package cn.abcsys.devops.v2.deployer.query.mdoel;

import cn.abcsys.devops.v2.deployer.db.model.V2Container;
import cn.abcsys.devops.v2.deployer.db.model.V2ImageGroup;

/**
 * 通过环境id，项目id，应用id来获得imagegroup和container列表的返回的数据结构
 *
 * @author xianghao
 * @create 2017-10-31 下午3:13
 **/
public class ImageGroupAndContainer {
    private V2ImageGroup imageGroup;
    private V2Container container;

    public V2ImageGroup getImageGroup() {
        return imageGroup;
    }

    public void setImageGroup(V2ImageGroup imageGroup) {
        this.imageGroup = imageGroup;
    }

    public V2Container getContainer() {
        return container;
    }

    public void setContainer(V2Container container) {
        this.container = container;
    }
}
