package cn.abcsys.devops.v2.deployer.watches;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

/**
 * Created by Administrator on 2017/10/12.
 */
public class WatchThread extends Thread {
    private KubernetesClient client;

    private PodWatcher pw = null;
    private Watch podWatch = null;

    public KubernetesClient getClient() {
        return client;
    }

    @Override
    public void run() {
        System.out.println("first input!");
        int i = 0;
            i++;
            System.out.println(i);
            try {
                client.pods().inNamespace("default").watch(new Watcher<Pod>() {
                    @Override
                    public void eventReceived(Action action, Pod pod) {
                        System.out.println(pod.getMetadata().getLabels());
                    }

                    @Override
                    public void onClose(KubernetesClientException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("after"+i);
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    public void setClient(KubernetesClient client) {
        this.client = client;
    }
}
