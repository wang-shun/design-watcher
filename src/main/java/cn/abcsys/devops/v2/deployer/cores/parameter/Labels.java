/*
 *  @Author Xuyuanjia xuyuanjia2017@otcaix.iscas.ac.cn
 *  @Date ${DATE} ${TIME}
 */

package cn.abcsys.devops.v2.deployer.cores.parameter;

public class Labels {
    protected String key;
    protected String value;
    protected String type;

    public Labels(){

    }

    public Labels(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
