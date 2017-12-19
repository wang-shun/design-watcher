package cn.abcsys.devops.v2.deployer.utils;
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date June 2,2017
 * xyj config javaDoc
 */
public class DateUtil {
    public static String getSimpleCurrentDateTIme(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return LocalDateTime.now().format(dtf);
    }

    private static String format(Date d,String timePattern){
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String formatedDate;
        if(d == null)
            formatedDate= sdf.format(new Date());
        else
            formatedDate= sdf.format(d);
        System.out.println(formatedDate);
        return formatedDate;
    }

    public static String utilDateConverter(Date d){
        String timePattern = "yyyy-MM-dd HH:mm:ss";
        return format(d,timePattern);
    }

    public static String rawUtilDateConverter(Date d){
        String timePattern = "yyyyMMddHHmmss";
        return format(d,timePattern);
    }

    public static String getRandomFilePath(HttpServletRequest request,String suffix){
        String randomName=request.getSession().getServletContext().getRealPath("/")+rawUtilDateConverter(new Date())+"."+suffix;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return randomName;
    }

    public static void delteFilePath(String filePath){
        File f = new File(filePath);
        if(f.exists()){
            f.delete();
        }
    }

    public static String writeStrToFile(String yamlString,String filePath){
        try {
            File f = new File(filePath);
            if(f.exists())
                f.delete();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            Writer os = new OutputStreamWriter(fos, "utf-8");
            os.write(yamlString);
            os.flush();
            fos.close();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
