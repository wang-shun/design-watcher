package cn.abcsys.devops.v2.deployer.thread;

import cn.abcsys.devops.deployer.initialization.AllInit;
import cn.abcsys.devops.v2.deployer.trigger.RuntimeDeployerTrigger;
import cn.abcsys.devops.v2.deployer.utils.SpringContextHelper;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import java.util.List;

public class GetNodeThread extends  Thread {

    private static Logger logger = Logger.getLogger(GetNodeThread.class);

    private boolean flag = true;

    private RuntimeDeployerTrigger trigger = null;
    private ServletContextEvent sce;

    public ServletContextEvent getSce() {
        return sce;
    }

    public void setSce(ServletContextEvent sce) {
        this.sce = sce;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        while(flag){
            try {
                logger.info("向Runtime定时获取master的信息：");
                if(trigger == null){
                    trigger = (RuntimeDeployerTrigger) SpringContextHelper.getBean("runtimeDeployerTrigger");
                }
                List<ClusterInformation> masters = trigger.doRequest();
                for(ClusterInformation master:masters){
                    logger.info("apiserver:"+master.getType()+"://"+master.getRealMaster());
                    AllInit.setWatcher(master.getType(),master.getRealMaster(),master.getPort(),this.sce);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    }
}
