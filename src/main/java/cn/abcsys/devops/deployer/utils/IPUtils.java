/**
 * Copyright (2017, ) Institute of Sofeware, Chinese Academy of Sciences
 * 
 */
package cn.abcsys.devops.deployer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jul 10, 2017
 *
 */
public class IPUtils {

	/**
	 * IP地址
	 */
	private static Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+"
			+ "|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d"
			+ "|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
	
	public static boolean invalid(String ip) {
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
	
	public static void main(String[] args) {
		System.out.println(IPUtils.invalid("192"));
		System.out.println(IPUtils.invalid("192.168"));
		System.out.println(IPUtils.invalid("192.168.1"));
		System.out.println(IPUtils.invalid("192.168.1.1.1"));
		
		System.out.println(IPUtils.invalid("-1.192.168.1"));
		System.out.println(IPUtils.invalid("256.192.168.1"));
		System.out.println(IPUtils.invalid("0.192.168.1"));
		System.out.println(IPUtils.invalid("255.192.168.1"));
	}
}
