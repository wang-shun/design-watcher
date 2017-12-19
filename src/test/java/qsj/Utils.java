package qsj;

import cn.abcsys.devops.v2.deployer.db.model.V2Event;
import cn.abcsys.devops.v2.deployer.db.model.V2Pod;
import io.fabric8.kubernetes.api.model.Event;
import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Utils {
    public static KubernetesClient getKubernetesClient(){
        String path = "C:/Users/qinshijun/pki/";
        String type = "https";
        String ip = "133.133.134.146";
        String port = "6443";
        String caFile = path+"ca.crt";
        String clientKeyFile = path + "apiserver-kubelet-client.key";
        String clientCaFile = path + "apiserver-kubelet-client.crt";
        return KubernetesUtil.createK8sClient(type, ip, port, caFile, clientKeyFile, clientCaFile);
    }

    public static V2Event createNewV2Event(Event event) {
        V2Event newEvent = new V2Event();
        newEvent.setApiVersion(event.getApiVersion());
        newEvent.setCount(event.getCount());
        newEvent.setFirstTimestamp(event.getFirstTimestamp());
        newEvent.setKind(event.getKind());
        newEvent.setMessage(event.getMessage());
        newEvent.setName(event.getMetadata().getName());
        newEvent.setNamespace(event.getMetadata().getNamespace());
        newEvent.setResourceVersion(event.getMetadata().getResourceVersion());
        newEvent.setSelfLink(event.getMetadata().getSelfLink());
        newEvent.setUid(event.getMetadata().getUid());
        newEvent.setReason(event.getReason());
        newEvent.setHost(event.getSource().getHost());
        newEvent.setType(event.getType());
        return newEvent;
    }

    public static V2Pod createNewV2Pod(Event event) {
        V2Pod newPod = new V2Pod();
        ObjectReference podReference = event.getInvolvedObject();
        newPod.setApiVersion(podReference.getApiVersion());
        newPod.setFilePath(podReference.getFieldPath());
        newPod.setKind(podReference.getKind());
        newPod.setRealName(podReference.getName());
        newPod.setNamespace(podReference.getNamespace());
        newPod.setUuid(podReference.getUid());
        newPod.setResourceVersion(podReference.getResourceVersion());
        return newPod;
    }

}
