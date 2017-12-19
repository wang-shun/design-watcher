package cn.abcsys.devops.deployer.util;
/**
 * Copyright (2017, ) Institute of Software, Chinese Academy of Sciences
 * Copyright (2017, ) Bocloud Co,. Lmt
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xuyuanjia2017@otcaix.iscsa.ac.cn
 * @date June 2,2017
 * xyj config javaDoc
 */
public class UsefulTools {
    public static String getSimpleCurrentDateTIme(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return LocalDateTime.now().format(dtf);
    }

    public static void main(String[] args) {
        System.out.println(UsefulTools.getSimpleCurrentDateTIme());
    }
}
