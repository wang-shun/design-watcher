package cn.abcsys.devops.v2.deployer.cores.parameter;

public class RequestCertificationFileParameter extends BaseParameter {
    private String apiserverKubeletClientCrt = "/etc/kubernetes/pki/apiserver-kubelet-client.crt";
    private String apiserverKubeletClientKey = "/etc/kubernetes/pki/apiserver-kubelet-client.key";
    private String caCrt="/etc/kubernetes/pki/ca.crt";
    private String url;

    public String getApiserverKubeletClientCrt() {
        return apiserverKubeletClientCrt;
    }

    public void setApiserverKubeletClientCrt(String apiserverKubeletClientCrt) {
        this.apiserverKubeletClientCrt = apiserverKubeletClientCrt;
    }

    public String getApiserverKubeletClientKey() {
        return apiserverKubeletClientKey;
    }

    public void setApiserverKubeletClientKey(String apiserverKubeletClientKey) {
        this.apiserverKubeletClientKey = apiserverKubeletClientKey;
    }

    public String getCaCrt() {
        return caCrt;
    }

    public void setCaCrt(String caCrt) {
        this.caCrt = caCrt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
