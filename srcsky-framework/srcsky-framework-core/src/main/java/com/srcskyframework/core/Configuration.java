package com.srcskyframework.core;

import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午9:26
 * Email: z82422@gmail.com
 * 加载 配置信息文件      config/config.properties
 */

public class Configuration {

    // 开发模式
    public static boolean devmode = true;

    private static Enterprise config = new Enterprise();

    static {
        config = new Enterprise(getProperties("/config/_config.properties"));
        config.putAll(getProperties("/config/config.properties"));
        config.putAll(System.getProperties());
        devmode = config.getBoolean("system.devmode");
    }

    public static Enterprise get() {
        return config;
    }

    public static Properties getProperties(String filepath) {
        Properties properties = new Properties();
        InputStream inputStream = Configuration.class.getResourceAsStream(filepath);
        if (null != inputStream) {
            try {
                properties.load(inputStream);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return properties;
    }

    public static Properties getPropertiesByAbsolutePath(String filepath) {
        Properties properties = new Properties();
        try {

            FileInputStream inputStream = new FileInputStream(filepath);
            properties.load(inputStream);
            inputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }


    /**
     * 当前用户所在目录
     *
     * @return
     */
    public static String getCurrentUserSystemHomeDirectory() {
        return System.getProperty("user.home");
    }

    public static String getProperty(String property) {
        return config.getString(property);
    }

    public static void main(String[] args) {
        System.out.println("=========");
    }

    public static void initLog4j() {
        PropertyConfigurator.configure(PropertyConfigurator.class.getResource("/config/log4j/config.properties"));
    }
}
