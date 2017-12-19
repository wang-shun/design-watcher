package cn.abcsys.devops.v2.deployer.cores.parameter;

public class HaParameter {
    private Integer comId;
    private String virtualIP;
    private String hostName;
    private String hostIP;
    private String hostPort;
    private String backupHostIP;
    private String backupHostPort;
    private Integer loadBlance;
    private String appIP;
    private String appPort;
    private String appName;
    
    public Integer getComId () {
        return comId;
    }
    
    public void setComId (Integer comId) {
        this.comId = comId;
    }
    
    public String getVirtualIP () {
        return virtualIP;
    }
    
    public void setVirtualIP (String virtualIP) {
        this.virtualIP = virtualIP;
    }
    
    public String getHostName () {
        return hostName;
    }
    
    public void setHostName (String hostName) {
        this.hostName = hostName;
    }
    
    public String getHostIP () {
        return hostIP;
    }
    
    public void setHostIP (String hostIP) {
        this.hostIP = hostIP;
    }
    
    public String getHostPort () {
        return hostPort;
    }
    
    public void setHostPort (String hostPort) {
        this.hostPort = hostPort;
    }
    
    public String getBackupHostIP () {
        return backupHostIP;
    }
    
    public void setBackupHostIP (String backupHostIP) {
        this.backupHostIP = backupHostIP;
    }
    
    public String getBackupHostPort () {
        return backupHostPort;
    }
    
    public void setBackupHostPort (String backupHostPort) {
        this.backupHostPort = backupHostPort;
    }
    
    public Integer getLoadBlance () {
        return loadBlance;
    }
    
    public void setLoadBlance (Integer loadBlance) {
        this.loadBlance = loadBlance;
    }
    
    public String getAppIP () {
        return appIP;
    }
    
    public void setAppIP (String appIP) {
        this.appIP = appIP;
    }
    
    public String getAppPort () {
        return appPort;
    }
    
    public void setAppPort (String appPort) {
        this.appPort = appPort;
    }
    
    public String getAppName () {
        return appName;
    }
    
    public void setAppName (String appName) {
        this.appName = appName;
    }
}
