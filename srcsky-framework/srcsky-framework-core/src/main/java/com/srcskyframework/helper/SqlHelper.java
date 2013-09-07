package com.srcskyframework.helper;

import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Enterprise;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11-10-23
 * Time: 上午11:28
 * To change this template use File | Settings | File Templates.
 */
public class SqlHelper {
    /*Sql键值对*/
    private final static Enterprise<Class, Enterprise<String, String>> cache = new Enterprise();

    private final static String jdbcDriver = Configuration.getProperty("dbpool.driverClass").toLowerCase();
    public final static Integer DB_TYPE_ORACLE = 1;
    public final static Integer DB_TYPE_H2 = 2;
    public final static Integer DB_TYPE_MYSQL = 3;
    private static Integer db_type = 0;

    public static int getDbType() {
        if (db_type != 0) {
            return db_type;
        } else if (jdbcDriver.indexOf("oracle") != -1) {
            return db_type = DB_TYPE_ORACLE;
        } else if (jdbcDriver.indexOf("h2") != -1) {
            return db_type = DB_TYPE_H2;
        } else if (jdbcDriver.indexOf("mysql") != -1) {
            return db_type = DB_TYPE_MYSQL;
        } else {
            return 0;
        }
    }

    public static String getCurrentDate() {
        switch (getDbType()) {
            case 1:
                return "sysdate";
            case 2:
                return "now()";
        }
        return "date";
    }

    /**
     * 获取 yyyyMMdd 格式 字段
     *
     * @param field
     * @return
     */
    public static String getDateFieldBySimple(String field) {
        int dbtype = getDbType();
        if (dbtype == 1) {
            return "to_char(" + field + ",'yyyy-MM-dd')";
        } else if (dbtype == 2) {
            return "concat(year(" + field + "),'-',Month(" + field + "),'-',day(" + field + "))";
        }
        return "";
    }

/*    public static String toDate(String field, int day) {
    }*/

    /**
     * 读取Sql语句
     */
    public static synchronized String getQueryString(Class claz, String postfix) {
        Enterprise queryStrings = cache.get(claz);
        if (null == queryStrings) {
            cache.put(claz, queryStrings = new Enterprise());
        }
        if (queryStrings.isEmpty() || Configuration.devmode) {
            try {
                String content = "";
                /*if (Configuration.ConfHomeDirectory.indexOf(".jar!") != -1) {
                    content=FileUtility.readStream(claz.getResourceAsStream(claz.getSimpleName() + ".xml"));
                } else {
                    content=FileUtility.readStream(new FileInputStream(Configuration.ConfHomeDirectory+"/classes/" + claz.getName().replace(".", File.separator) + ".xml"));
                }*/
                content = FileHelper.readStream(claz.getResourceAsStream(claz.getSimpleName() + ".xml"));
                /*Document document = FileUtility.parseDocumentByString(content);
                String sqlCount = FileUtility.xpath("count(//key)", document);
                for (int i = 1; i < Integer.valueOf(sqlCount) + 1; i++) {
                    String key = FileUtility.xpath("//sql/key[" + i + "]/@name", document);
                    String body = FileUtility.xpath("//sql/key[" + i + "]/text()", document);
                    queryStrings.put(key, body);
                }*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return queryStrings.getString(postfix);
    }

    public static void main(String[] args) {

    }
}
