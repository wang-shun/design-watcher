package cn.abcsys.devops.v2.deployer.cores.parameter;

/**
 * Created by Administrator on 2017/9/17.
 */
public class PortObject {
    Integer portValue;
    String portName;
    String protocol;

    public Integer getPortValue() {
        return portValue;
    }

    public void setPortValue(Integer portValue) {
        this.portValue = portValue;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}

