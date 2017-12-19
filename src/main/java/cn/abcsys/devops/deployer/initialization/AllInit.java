/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.initialization;

import cn.abcsys.devops.v2.deployer.deployers.kubernetes.KubernetesUtil;
import cn.abcsys.devops.v2.deployer.thread.GetNodeThread;
import cn.abcsys.devops.v2.deployer.trigger.RequestCertificationTrigger;
import cn.abcsys.devops.v2.deployer.trigger.RuntimeDeployerTrigger;
import cn.abcsys.devops.v2.deployer.watches.PodWatcher;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by xyj on 2016/4/19.
 */

public class AllInit implements ServletContextListener { //实现了这个接口的会在程序服务前执行这个 contextInitialized 方法，结束时执行 contextDestroyed
    static Logger logger = Logger.getLogger(AllInit.class);
    private Log4JInit lji ;
    //public static String baseFilePath = "/Users/mr.xh/crt";  
    public static String baseFilePath = "E:";

    public static Map<String,Object> wMap = new HashMap<>();
    public static GetNodeThread gnt;

    public Log4JInit getLji() {
        return lji;
    }

    public static void getApiservers(ServletContextEvent sce){
        String prefix = sce.getServletContext().getRealPath("/");
        String file = sce.getServletContext().getInitParameter("system");
        String filePath = prefix + file;
        Properties props = new Properties();
        try {
            FileInputStream istream = new FileInputStream(filePath);
            props.load(istream);

            RequestCertificationTrigger.baseUrl = props.getProperty("v2CertificationUrl");
            RuntimeDeployerTrigger.runtimeURL = props.getProperty("v2RuntimeUrl");
            RuntimeDeployerTrigger.appstoreUrl = props.getProperty("appstoreUrl");
            if(props.getProperty("v2IntevalGetMasterSwitch") != null){
                RuntimeDeployerTrigger.intervalSwitch = props.getProperty("v2IntevalGetMasterSwitch").equals("true");
            }
            Set<Object> keys = props.keySet();//返回属性key的集合
            for (Object key : keys){
                System.out.println(key.toString());
                System.out.println(((String)props.get(key)).split(";").length == 3);
                if(key.toString().contains("apiserver") && ((String)props.get(key)).split(";").length == 3){
                    String[] res = ((String)props.get(key)).split(";");
                    setWatcher(res[0],res[1],res[2],sce);
                }
            }
            if(RuntimeDeployerTrigger.intervalSwitch){
                gnt = new GetNodeThread();
                gnt.setFlag(true);
                gnt.setSce(sce);
                gnt.start();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addWatch(Watch w,KubernetesClient client){
        if(wMap.containsKey(client.getMasterUrl().getHost())){
            //wMap.remove(client.getMasterUrl().getHost());
            return;
        }
        wMap.put(client.getMasterUrl().getHost(),w);
    }

    public static void removeWatch(KubernetesClient client){
        if(wMap.containsKey(client.getMasterUrl().getHost())){
            Watch value = (Watch) AllInit.wMap.get(client.getMasterUrl().getHost());
            wMap.remove(client.getMasterUrl().getHost());
            value = null;
        }
    }

    public static void setWatcher(String type,String ip,String port,ServletContextEvent sce){
        KubernetesClient client = KubernetesUtil.getClient(type,ip,port);
        try {
            if(client !=null && client.nodes().list().getItems() !=null){
                StringBuilder sb =  new StringBuilder("");
                for(Node temp : client.nodes().list().getItems()){
                    sb.append(temp.getMetadata().getName()+";");
                }
                System.out.println(sb.toString());
                if(!wMap.containsKey(client.getMasterUrl().getHost())){
                    System.out.println("需要添加新的watch："+client.getMasterUrl().getHost());
                    PodWatcher<Pod> wer = new PodWatcher<>();
                    wer.setClient(client);
                    wer.setSce(sce);
                    Watch w = client.pods().inNamespace("default").watch(wer);
                    addWatch(w,client);
                }
            }
        }catch (Exception e){
            logger.info("master节点信息："+type+"//"+ip+":"+port+"不可达，请管理员排查master节点信息是否正确！");
        }

    }

    public static void setWatcher(KubernetesClient client,ServletContextEvent sce){
        try {
            if(client !=null && client.nodes().list().getItems() !=null){
                StringBuilder sb =  new StringBuilder("");
                for(Node temp : client.nodes().list().getItems()){
                    sb.append(temp.getMetadata().getName()+";");
                }
                System.out.println(sb.toString());
                if(!wMap.containsKey(client.getMasterUrl().getHost())){
                    System.out.println("需要添加新的watch："+client.getMasterUrl().getHost());
                    PodWatcher<Pod> wer = new PodWatcher<>();
                    wer.setClient(client);
                    wer.setSce(sce);
                    Watch w = client.pods().inNamespace("default").watch(wer);
                    addWatch(w,client);
                }
            }
            }catch (Exception e){
                e.printStackTrace();
                logger.info("pod watch 失败："+client.getMasterUrl().getHost()+"略过！");
            }
    }

    public static  void resetWatch(KubernetesClient client,PodWatcher<Pod> self){
        removeWatch(client);
        setWatcher(client,self.getSce());
    }

    public void contextInitialized(ServletContextEvent sce) {

        String prefix = sce.getServletContext().getRealPath("/");
        System.out.println(prefix);
        String filePath = prefix + "WEB-INF/classes/config/system.properties";
        Properties props = new Properties();
        try {
            FileInputStream istream = new FileInputStream(filePath);
            props.load(istream);
        }catch (Exception e){
            logger.error("Open and load system.properties file failed");
        }
        //FileInputStream istream = new FileInputStream(filePath);
        if(props.getProperty("certificationPath") !=null && props.getProperty("certificationPath").length() > 0){
            AllInit.baseFilePath = props.getProperty("certificationPath");
        }

        logger.info("Start devops-deployer initialization jobs:");
        lji = new Log4JInit();
        lji.setLog4JPath(sce);
        getApiservers(sce);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if(gnt != null){
            gnt.setFlag(false);
            gnt = null;
        }
    }
}
