/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */
package cn.abcsys.devops.deployer.initialization;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextEvent;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by xyj on 2016/10/19 0019.
 */
public class Log4JInit {
    private static Logger logger = Logger.getLogger(Log4JInit.class);
    public void setLog4JPath(ServletContextEvent sce){
        String prefix = sce.getServletContext().getRealPath("/");
        String file = sce.getServletContext().getInitParameter("log4j");
        String filePath = prefix + file;
        Properties props = new Properties();
        try {
            FileInputStream istream = new FileInputStream(filePath);
            props.load(istream);
            istream.close();
            //toPrint(props.getProperty("log4j.appender.file.File"));
            System.out.println(props.getProperty("log4j.appender.File.File"));
            String logFile = prefix + props.getProperty("log4j.appender.File.File");//设置路径
            props.setProperty("log4j.appender.File.File",logFile);
            PropertyConfigurator.configure(props);//装入log4j配置信息
            logger.info("devops-deployer log4JFilePath:" + logFile);
        } catch (Exception e) {
            logger.error("Could not read configuration file:" + filePath );
            logger.error("Ignoring configuration file :" + filePath );
            return;
        }
    }
}
