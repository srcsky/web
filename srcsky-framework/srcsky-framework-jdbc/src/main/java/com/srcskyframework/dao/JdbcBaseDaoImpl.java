package com.srcskyframework.dao;

import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Constants;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.Pagination;
import com.srcskyframework.exception.LogicException;
import com.srcskyframework.helper.SqlHelper;
import com.srcskyframework.helper.ValidHelper;
import oracle.sql.CLOB;
import oracle.sql.TIMESTAMP;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JdbcDaoImpl
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-6-上午9:54
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class JdbcBaseDaoImpl<T> {
    private final static String KEY_DATABASE_DRIVER_CLASS_PATH = Configuration.getProperty("dbpool.driverClass");
    private final static Logger logger = Logger.getLogger(JdbcBaseDaoImpl.class);
    private final static String catalog = Configuration.get().getString("system.dbpool");
    private static Map cacheMappings = new HashMap();
    /*表结构*/
    private static Map<String, Enterprise> tablesStruct = new HashMap<String, Enterprise>();
    /*SQL 参数 处理*/
    private static Pattern sqlParameters = Pattern.compile("#[^#]+#");
    /*关键字*/
    private static Pattern keywords = Pattern.compile("^(create|update|size|user|table|left|right|start|online|resource|date|order|drop)$");
    /*反省实体  为 封装特定数据实体 做为类型依据*/
    private Class entityClass = Enterprise.class;

    static {
        if (null == System.getProperty("com.mchange.v2.c3p0.cfg.xml")) {
            System.setProperty("com.mchange.v2.c3p0.cfg.xml", new File(JdbcBaseDaoImpl.class.getResource("/").getFile(), "config/dbpool/c3p0-config.xml").getAbsolutePath());
        }
    }

    public JdbcBaseDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            entityClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
    }

    public String getCatalog() {
        return catalog;
    }

    /**
     * 数据库操作 基础函数
     *
     * @param callback
     * @return
     */
    public Object execute(ConnectionCallBack callback) {
        Connection connection = null;
        try {
            connection = C3p0Pool.getConnection(getCatalog());
            if (connection == null) throw new Exception("获取数据库连接异常");
            connection.setAutoCommit(false);
            Object obj = callback.execute(connection);
            connection.commit();
            return obj;
        } catch (Exception ex) {
            logger.error("AbstractDaoImpl.execute 异常:", ex);
            try {
                if (null != connection) {
                    connection.rollback();
                    logger.error("AbstractDaoImpl.execute 事务回滚成功");
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
                logger.error("AbstractDaoImpl.execute 事务回滚失败:", ex);
            }
            throw new LogicException(ex);
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception ex) {
                logger.error("关闭数据库连接异常", ex);
            }
        }
    }

    /**
     * 数据库操作 返回 Result
     *
     * @param queryString
     * @return
     */
    public Object execute(Connection connection, String queryString, Enterprise params, ResultSetCallBack callback) {
        long startTimer = System.currentTimeMillis();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {

            //Cache处理，如果有 缓存诉求 先处理缓存
            Integer cacheTimer = (null != params ? params.getInt(Pagination.PAGE_CACHE_TIMER) : 0);
            String key = queryString + (null != params ? params.toString() : "");
            if (cacheTimer > 0 && cacheMappings.get(key) != null) {
                Object[] cache = (Object[]) cacheMappings.get(key);
                if (((System.currentTimeMillis() - ((Long) cache[1]).longValue()) / 60000) > (cacheTimer)) {
                    cacheMappings.remove(key);
                }
                return cache[0];
            }
            //Cache 处理结束

            String queryStringTemp = queryString;
            Matcher matcher = sqlParameters.matcher(queryStringTemp);
            List list = new LinkedList();
            while (matcher.find()) {
                queryStringTemp = queryStringTemp.replace(matcher.group(), "?");
                list.add(matcher.group());
            }
            statement = connection.prepareStatement(queryStringTemp);
            if (!list.isEmpty() && params != null) {
                for (int i = 0; i < list.size(); i++) {
                    String field = list.get(i).toString().replace("#", "");
                    statement.setObject(i + 1, params.get(field));
                }
            }
            result = statement.executeQuery();
            Object temp = callback.execute(result);
            //Cache处理，如果有 缓存诉求 讲查询结果放进Cache
            if (cacheTimer > 0) {
                cacheMappings.put(key, new Object[]{temp, System.currentTimeMillis()});
            }
            return temp;
        } catch (Exception ex) {
            logger.error(this, ex);
        } finally {
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
            } catch (Exception ex) {
                logger.error(this, ex);
            }
            logger.debug("当前SQL耗时:" + (System.currentTimeMillis() - startTimer) + "MS,SQL=" + queryString);
        }
        return null;
    }

    public Object execute(final String queryString, final Enterprise params, final ResultSetCallBack callback) {
        final JdbcBaseDaoImpl local = this;
        return execute(new ConnectionCallBack() {
            public Object execute(Connection connection) throws Exception {
                return local.execute(connection, queryString, params, callback);
            }
        });
    }

    /**
     * 数据库操作 基础函数
     *
     * @param queryString
     */
    public void execute(Connection connection, String queryString) {
        long startTimer = System.currentTimeMillis();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(queryString);
        } catch (Exception ex) {
            logger.error(this, ex);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception ex) {
                logger.error(this, ex);
            }
            logger.debug("当前SQL耗时:" + (System.currentTimeMillis() - startTimer) + "MS,SQL=" + queryString);
        }
    }

    public void execute(final String queryString) {
        final JdbcBaseDaoImpl local = this;
        execute(new ConnectionCallBack() {
            public Object execute(final Connection connection) throws Exception {
                local.execute(connection, queryString);
                return null;
            }
        });
    }

    public void clearTable(String table) {
        execute("delete from " + table);
    }

    /**
     * 数据库操作 返回单条记录
     *
     * @param queryString
     * @return
     */
    public Enterprise find(String queryString) {
        return find(queryString, null);
    }

    public Enterprise find(final String queryString, final Enterprise params) {
        final JdbcBaseDaoImpl local = this;
        return (Enterprise) execute(new ConnectionCallBack() {
            public Object execute(final Connection connection) throws Exception {
                return local.find(connection, queryString, params);
            }
        });
    }

    public Enterprise find(Connection connection, String queryString, Enterprise params) {
        params = null != params ? params : new Enterprise().set(Pagination.PAGE_SIZE, 1);
        List list = finds(connection, queryString, params);
        return ValidHelper.isEmpty(list) ? null : (Enterprise) list.get(0);
    }

    /**
     * 数据库操作 返回map
     *
     * @param queryString
     * @return
     */
    public List<T> finds(String queryString) {
        return finds(queryString, null);
    }

    public List<T> finds(final String queryString, final Enterprise params) {
        final JdbcBaseDaoImpl local = this;

        return (List<T>) execute(new ConnectionCallBack() {
            public Object execute(final Connection connection) throws Exception {
                return local.finds(connection, queryString, params);
            }
        });
    }

    public List<T> finds(Connection connection, String queryString, Enterprise params) {
        /*限制返回记录数*/
        if (queryString.indexOf("TEMP_TABLE_RESULT_COUNT") == -1 && null != params && !params.isEmpty(Pagination.PAGE_SIZE)) {
            if (!params.isEmpty(Pagination.PAGE_INDEX)) {
                int i = (params.getInt(Pagination.PAGE_INDEX) * params.getInt(Pagination.PAGE_SIZE)) - params.getInt(Pagination.PAGE_SIZE);
                if (KEY_DATABASE_DRIVER_CLASS_PATH.toLowerCase().indexOf("oracle") != -1) {
                    queryString = "select * from ( select row_.*, rownum rownum_ from ( " + queryString + " ) row_ where rownum <= " + (i + params.getInt(Pagination.PAGE_SIZE)) + ") where rownum_ > " + i;
                } else if (KEY_DATABASE_DRIVER_CLASS_PATH.toLowerCase().indexOf("mysql") != -1) {
                    queryString = queryString + " limit " + i + "," + params.getInt(Pagination.PAGE_SIZE);
                }
            } else {
                if (KEY_DATABASE_DRIVER_CLASS_PATH.toLowerCase().indexOf("oracle") != -1) {
                    queryString = "select * from ( " + queryString + " ) where rownum <= " + params.getInt(Pagination.PAGE_SIZE);
                } else if (KEY_DATABASE_DRIVER_CLASS_PATH.toLowerCase().indexOf("mysql") != -1) {
                    queryString = queryString + " limit " + 0 + "," + params.getInt(Pagination.PAGE_SIZE);
                }
            }
        }
        List results = (List) execute(connection, queryString.toString(), params, new ResultSetCallBack() {
            public Object execute(ResultSet result) throws Exception {
                List<Enterprise> list = new ArrayList<Enterprise>();
                ResultSetMetaData metaDate = result.getMetaData();
                while (result.next()) {
                    Enterprise entity = (Enterprise) entityClass.newInstance();
                    for (int i = 1; i < metaDate.getColumnCount() + 1; i++) {
                        Object filed = metaDate.getColumnLabel(i).toLowerCase();
                        Object value = result.getObject(metaDate.getColumnLabel(i));
                        if (value instanceof CLOB) {
                            entity.set(filed, ((CLOB) value).getSubString(1, (int) ((CLOB) value).length()));
                        } else if (value instanceof TIMESTAMP) {
                            entity.put(filed, ((TIMESTAMP) value).dateValue());
                        } else {
                            entity.put(filed, value);
                        }
                    }
                    list.add(entity);
                }
                return list;
            }
        });
        return null != results ? results : Collections.emptyList();
    }


    /**
     * 分页方法
     *
     * @param queryString
     * @param input
     * @return
     */
    public Pagination findPager(final String queryString, final Enterprise input) {
        final JdbcBaseDaoImpl local = this;
        return (Pagination) execute(new ConnectionCallBack() {
            public Object execute(final Connection connection) throws Exception {
                return local.findPager(connection, queryString, input);
            }
        });
    }

    public Pagination findPager(Connection connection, final String queryString, final Enterprise input) {
        long apptime = System.currentTimeMillis();
        /*构造分页数据存储对象*/
        Pagination pagination = new Pagination(input);
        /*生成 总记录数查询 SQL*/
        String queryCountString = new String("select count(*) as COUNT from (" + queryString + ")  TEMP_TABLE_RESULT_COUNT");
        Enterprise count = find(connection, queryCountString, input);
        input.set(Pagination.PAGE_RESULT_COUNT, Integer.valueOf(count.getString("count")));
        if (input.getInt(Pagination.PAGE_RESULT_COUNT) != 0) {
            input.set(Pagination.PAGE_RESULT, finds(connection, queryString, input));
        }
        logger.debug("执行当前SQL耗时(分页,双SQL): " + (System.currentTimeMillis() - apptime) + "MS -> " + queryString + "\n");
        return pagination;
    }


    /**
     * 数据库操作 基础函数
     *
     * @param updateString
     * @return
     */
    public boolean update(final String updateString) {
        return (Boolean) execute(new ConnectionCallBack() {
            public Object execute(Connection connection) throws Exception {
                long apptime = System.currentTimeMillis();
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    boolean value = statement.executeUpdate(updateString) != 0;
                    return value;
                } finally {
                    statement.close();
                    logger.debug("执行当前SQL耗时:" + (System.currentTimeMillis() - apptime) + "MS -> " + updateString);
                }
            }
        });
    }

    public boolean update(final Enterprise entity) {
        return (Boolean) execute(new ConnectionCallBack() {
            public Object execute(Connection connection) throws Exception {
                long apptime = System.currentTimeMillis();
                PreparedStatement pstm = null;
                ResultSet result = null;
                StringBuffer updateBuffer = new StringBuffer("select ");
                Enterprise struct = getTableStruct(connection, entity.getEnterpriseName());
                try {
                    for (String key : (Set<String>) entity.keySet()) {
                        if (struct.containsKey(key.toLowerCase().replace("\"", ""))) {
                            updateBuffer.append(key.toUpperCase()).append(",");
                        }
                    }
                    if (updateBuffer.toString().matches(".*,$")) {
                        updateBuffer = updateBuffer.replace(updateBuffer.length() - 1, updateBuffer.length(), "");
                    }
                    updateBuffer.append(" from ").append(entity.getEnterpriseName());
                    if (!entity.isEmpty("where")) {
                        updateBuffer.append(" where ").append(entity.getString("where"));
                    }
                    updateBuffer.append(" for update");
                    connection.setAutoCommit(false);
                    pstm = connection.prepareStatement(updateBuffer.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    result = pstm.executeQuery();
                    while (result.next()) {
                        for (String key : (Set<String>) entity.keySet()) {
                            if (struct.containsKey(key.toLowerCase().replace("\"", ""))) {
                                Object value = result.getObject(key);
                                if (value instanceof CLOB) {
                                    CLOB clob = (CLOB) value;
                                    clob.setString(1l, entity.getString(key).toString());
                                    result.updateObject(key, clob);
                                } else {
                                    result.updateObject(key, entity.get(key));
                                }
                            }
                        }
                        result.updateRow();
                    }
                    return true;
                } finally {
                    pstm.close();
                    result.close();
                    logger.debug("执行当前对" + entity.getEnterpriseName() + "表的修改耗时:" + (System.currentTimeMillis() - apptime) + "MS -> 条件:" + entity.getString("where") + ",SQL:" + updateBuffer);
                }
            }
        });
    }

    public String save(Connection connection, Enterprise entity) throws Exception {
        long apptime = System.currentTimeMillis();
        PreparedStatement pstm = null;
        ResultSet result = null;
        StringBuffer inserBuffer = null;
        try {
            inserBuffer = new StringBuffer("insert into " + entity.getEnterpriseName() + " (");
            Enterprise struct = getTableStruct(connection, entity.getEnterpriseName());
            for (String key : (Set<String>) entity.keySet()) {
                key = key.toLowerCase().trim();
                if (struct.containsKey(key)) {
                    if (keywords.matcher(key).find()) {
                        if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_MYSQL) {
                            inserBuffer.append("`");
                            inserBuffer.append(key.toUpperCase());
                            inserBuffer.append("`");
                        } else {
                            inserBuffer.append("\"");
                            inserBuffer.append(key.toUpperCase());
                            inserBuffer.append("\"");
                        }
                    } else {
                        inserBuffer.append(key);
                    }
                    inserBuffer.append(",");
                }
            }
            if (inserBuffer.toString().matches(".*,$")) {
                inserBuffer = inserBuffer.replace(inserBuffer.length() - 1, inserBuffer.length(), "");
            }
            inserBuffer.append(") values(");
            for (String key : (Set<String>) entity.keySet()) {
                /*设置 主键生成器，列*/
                if (struct.containsKey(key.toLowerCase()) && entity.equals("primary", key)) {
                    inserBuffer.append(entity.get(key)).append(",");
                } else if (struct.containsKey(key.toLowerCase())) {
                    inserBuffer.append("?").append(",");
                }
            }
            if (inserBuffer.toString().matches(".*,$")) {
                inserBuffer = inserBuffer.replace(inserBuffer.length() - 1, inserBuffer.length(), "");
            }
            inserBuffer.append(")");

            if (!entity.isEmpty("primary")) {
                pstm = connection.prepareStatement(inserBuffer.toString(), new String[]{entity.getString("primary").toLowerCase()});
            } else {
                pstm = connection.prepareStatement(inserBuffer.toString());
            }

            int index = 1;
            for (String key : (Set<String>) entity.keySet()) {
                if (struct.containsKey(key.toLowerCase()) && !entity.equals("primary", key)) {
                    if (entity.get(key) instanceof Date) {
                        pstm.setObject(index, new java.sql.Date(entity.getDate(key).getTime()));
                    } else {
                        pstm.setObject(index, entity.get(key));
                    }
                    index = index + 1;
                }
            }
            pstm.executeUpdate();

            try {
                if (!entity.isEmpty("primary")) {
                    result = pstm.getGeneratedKeys(); //equivalent to "SELECT last_insert_id();"
                    if (result.next()) {
                        entity.set(entity.getString("primary"), result.getString(1));
                        return entity.getString(entity.getString("primary"));
                    }
                }
            } catch (Exception ex) {
            }
            return "0";
        } finally {
            if (pstm != null) {
                pstm.close();
            }
            if (result != null) {
                result.close();
            }
            logger.debug("执行当前对" + entity.getEnterpriseName() + "表的插入耗时:" + (System.currentTimeMillis() - apptime) + "MS ->" + inserBuffer);
        }
    }

    public String save(final Enterprise entity) {
        return execute(new ConnectionCallBack() {
            public Object execute(Connection connection) throws Exception {
                return save(connection, entity);
            }
        }).toString();
    }

    /**
     * 9
     * 回调函数
     */
    public interface ConnectionCallBack {
        public Object execute(Connection connection) throws Exception;
    }

    /**
     * 回调函数
     */
    public interface ResultSetCallBack {
        public Object execute(ResultSet resutl) throws Exception;
    }

    public Enterprise getTableStruct(Connection connection, final String tableName) {
        if (tablesStruct.containsKey(tableName)) {
            return tablesStruct.get(tableName);
        }
        return (Enterprise) execute(connection, "select * from " + tableName + " where 1=2", null, new ResultSetCallBack() {
            public Object execute(ResultSet result) throws Exception {
                ResultSetMetaData metaDate = result.getMetaData();
                Enterprise struct = new Enterprise();
                ResultSetMetaData metaData = result.getMetaData();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    struct.put(metaDate.getColumnLabel(i).toLowerCase(), metaDate.getColumnLabel(i).toLowerCase());
                }
                tablesStruct.put(tableName, struct);
                return struct;
            }
        });
    }

    public Enterprise getTableStruct(final String tableName) {
        final JdbcBaseDaoImpl local = this;
        return (Enterprise) execute(new ConnectionCallBack() {
            public Object execute(Connection connection) throws Exception {
                return getTableStruct(connection, tableName);
            }
        });
    }

    /**
     * 获取 Dao 引用
     *
     * @param catalog
     * @return
     */
    public static JdbcBaseDaoImpl getDao(final String catalog) {
        return new JdbcBaseDaoImpl() {
            public String getCatalog() {
                return catalog;
            }
        };
    }

    public interface IBuilderSql {
        boolean execute(StringBuffer queryString, String simpleName);
    }

    public StringBuffer builderSql(Enterprise entity, IBuilderSql... builder) {
        String simpleName = entity.getEnterpriseName().toLowerCase();
        StringBuffer queryString = new StringBuffer("from " + entity.getEnterpriseName() + " " + simpleName + " where 1=1 ");
        if (!entity.isEmpty("id")) {
            queryString.append("and " + simpleName + ".id=#id# ");
        } else if (!entity.isEmpty("greaterid")) {
            entity.set("id", entity.getInt("greaterid"));
            queryString.append("and " + simpleName + ".id >#id# ");
        } else if (!entity.isEmpty("lessid")) {
            entity.set("id", entity.getInt("lessid"));
            queryString.append("and " + simpleName + ".id < #id# ");
        }
        /*模板方法供子类调用时，返回 flase 则不执行 排序SQL拼装,返回true则正常顺序执行*/
        if (!isEmpty(builder)) {
            builder[0].execute(queryString, simpleName);
        }
        return queryString;
    }

    public boolean isEmpty(Object value) {
        return ValidHelper.isEmpty(value);
    }

    public StringBuffer append(StringBuffer stringBuffer, String string) {
        return stringBuffer.append(" ").append(string);
    }

    /**
     * 根据类名转换为 SqlMap文件 并读取SQL语句
     *
     * @param clazz
     * @param postfix
     * @return
     */
    public String getQueryString(Class clazz, String postfix) {
        return SqlHelper.getQueryString(clazz, postfix);
    }

    public String append_sql_drop_by_logic(String simpleName, Integer drop) {
        // 过滤逻辑删除记录
        if (drop != 0) {
            if (drop > 0) {
                return " and (" + simpleName + ".is_drop = " + drop + ") ";
            }
            //小于 0 则显示全部数据
            //全部数据
            return "";
        } else {
            return " and (" + simpleName + ".is_drop <> " + Constants.YES + " OR " + simpleName + ".is_drop is null) ";
        }
    }

    //  重建主键生成器
    public void replaceCreateSequence(String name, String start) {
        if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_MYSQL) // MySql无需重建 Sequence
            return;
        logger.debug("重建主键：" + name);
        // ORACLE
        String queryString = "SELECT count(*) as count FROM USER_SEQUENCES WHERE SEQUENCE_NAME='" + name.toUpperCase() + "'";
        if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_H2) {
            // H2
            queryString = "SELECT count(*) as count FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME='" + name.toUpperCase() + "'";
        }
        Enterprise result = this.find(queryString);
        if (result.getInt("count") > 0) {
            execute("DROP SEQUENCE " + name + ";");
        }
        execute("CREATE SEQUENCE " + name + " START with " + start + ";");
    }

    public Class getClassDao() {
        return this.getClass();
    }
}
