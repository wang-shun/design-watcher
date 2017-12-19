package qsj;

public class WatchTest {
    public static void main(String[] args) throws InterruptedException{

        WatchEventTest r1 = new WatchEventTest();
        WatchPodTest r2 = new WatchPodTest();
        WatchDeploymentTest r3 = new WatchDeploymentTest();
        r1.beforeTest();
        r2.beforeTest();
        r3.beforeTest();

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        t1.start();
        t2.start();
        t3.start();
    }
}
