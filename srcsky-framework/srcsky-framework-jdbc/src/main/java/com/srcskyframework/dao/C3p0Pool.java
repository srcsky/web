package com.srcskyframework.dao;



import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:07
 * Email: z82422@gmail.com
 * C3p0Pool 链接池
 */
public class C3p0Pool {
    private static Logger logger = Logger.getLogger(C3p0Pool.class);
    /**
     * 连接池
     */
    private static Map<String, ComboPooledDataSource> pools = new Hashtable();

    static {
        if (null == System.getProperty("com.mchange.v2.c3p0.cfg.xml")) {
            System.setProperty("com.mchange.v2.c3p0.cfg.xml", new File(C3p0Pool.class.getResource("/").getFile(), "config/dbpool/c3p0-config.xml").getAbsolutePath());
        }
    }

    /**
     * 获取连接
     *
     * @param poolName
     * @return Connection
     */
    public static Connection getConnection(String poolName) {
        try {
            return getDataSource(poolName).getConnection();
        } catch (Exception ex) {
            logger.debug(ex);
            throw new RuntimeException(ex);
        }
    }

    public static ComboPooledDataSource getDataSource(String poolName) {
        try {
            ComboPooledDataSource cpds = pools.get(poolName);
            if (cpds == null) {
                synchronized (pools) {
                    cpds = pools.get(poolName);
                    /*if (SpringLocator.isInitialized()) {
                        cpds = (ComboPooledDataSource) SpringLocator.getBean(poolName);
                    } else {
                        cpds = new ComboPooledDataSource(poolName);
                    }*/
                    pools.put(poolName, cpds);
                }
            }
            return cpds;
        } catch (Exception ex) {
            logger.debug(ex);
            throw new RuntimeException(ex);
        }
    }

    public static void destroy() {
        for (ComboPooledDataSource pool : pools.values()) {
            pool.close();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50000; i++) {
            try {
                getConnection("xml").close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
