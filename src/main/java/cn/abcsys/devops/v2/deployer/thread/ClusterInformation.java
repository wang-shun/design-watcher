package cn.abcsys.devops.v2.deployer.thread;

public class ClusterInformation {
    private String type;
    private String vip;
    private String masterIp;

    public ClusterInformation(String type, String vip, String masterIp) {
        this.type = type;
        this.vip = vip;
        this.masterIp = masterIp;
    }

    public String getPort(){
        if(this.type == null)
            return "";
        if(this.type.equals("http")){
            return "8080";
        }
        if(this.type.equals("https")){
            return "6443";
        }
        return "";
    }

    public ClusterInformation(){

    }

    public String getRealMaster(){
        if(vip != null)
            return vip;
        if(masterIp !=null)
            return masterIp;
        return "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }
}
