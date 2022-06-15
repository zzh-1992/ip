/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.ip;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 获取远端ip地址
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-06-15 18:09
 */
@RestController
public class Hello {

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return StringUtils.isEmpty(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    @GetMapping("/sayHello")
    public String sayHello(HttpServletRequest request) {
        String realIp = getIpAddr(request);

        String remoteHost = request.getRemoteHost();

        String xforwardedforIp = getIpAddr2(request);
        return "hello " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "" +
                " remoteHost:" + remoteHost + "" +
                " realIp:" + realIp + " " +
                "xforwardedforIp:" + xforwardedforIp;
    }

    public String getIpAddr2(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        System.out.println("x-forwarded-for:" + ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println("WL-Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            System.out.println("unknown:" + ip);
        }
        if (!StringUtils.isEmpty(ip) &&
                ip.indexOf(",") > 0) {
            String[] str = ip.split(",");
            ip = str[0];
            System.out.println("ip:" + ip);
        }
        return ip;
    }

}
