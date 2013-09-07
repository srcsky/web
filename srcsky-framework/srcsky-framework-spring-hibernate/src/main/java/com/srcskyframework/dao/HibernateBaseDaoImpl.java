/**
 * 宝龙电商
 * com.srcskyframework.dao
 * HibernateDaoImpl.java
 *
 * 2013-8-6-下午2:42
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.dao;

import com.srcskyframework.core.Constants;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.dao.pojo.BaseEntity;
import com.srcskyframework.exception.LogicException;
import com.srcskyframework.helper.DateHelper;
import com.srcskyframework.helper.Pagination;
import com.srcskyframework.helper.SqlHelper;
import com.srcskyframework.helper.ValidHelper;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * HibernateDaoImpl
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-6-下午2:42
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class HibernateBaseDaoImpl<T extends Enterprise> implements IBaseDao<T> {

    protected final Logger logger = Logger.getLogger(this.getClass());

    public final static JdbcBaseDaoImpl jdbcLogic = new JdbcBaseDaoImpl();

    @Autowired
    public SessionFactory sessionFactory;

    protected Class<T> entityClass;

    // 通过子类的泛型定义取得对象类型Class
    public HibernateBaseDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            this.entityClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
    }

    public Session getSession() {
        //事务必须是开启的(Required)，否则获取不到
        return sessionFactory.getCurrentSession();
    }

    // 执行SQL语句
    public void executeHQL(String hql, T... params) {
        Query query = getSession().createQuery(hql);
        if (null != params && 0 != params.length)
            query.setProperties(params);
        query.executeUpdate();
    }

    // 删除
    public void delete(T entity) {
        getSession().delete(entity);
    }

    public void delete(Long id) {
        delete(findById(id));
    }

    public void deleteByLogic(T entity) {
        //entity.setIs_drop(Constants.YES);
        //getSession().saveOrUpdate(entity);
    }

    public void deleteAll(Class entity) {
        executeHQL("delete from " + entity.getSimpleName());
    }


    /**
     * 修改 OR 保存
     *
     * @param entity
     */
    public T update(T entity) {
        if (entity.containsKey(Constants.VALIDATION_BY_DAO_UPDATE)) {
            Enterprise errors = validation(entity);
            if (!errors.isEmpty()) {
                LogicException exception = new LogicException("Update数据异常");
                exception.getInput().putAll(errors);
                throw exception;
            }
        }
        getSession().saveOrUpdate(entity);
        return entity;
    }

    public List<T> updateByAll(List<T> entitys) {
        for (T entity : entitys) {
            if (entity.getIntId() == 0) {
                update(entity);
            } else {
                merge(entity);
            }
        }
        return entitys;
    }

    public void merge(T entity) {
        T result = (T) getSession().merge(entity);
        entity.putAll(result);
    }

    public void refresh(T entity) {
        getSession().refresh(entity);
    }

    public void refreshForce(T entity) {
        Session session = getSession();
        session.evict(entity);
        session.refresh(session.get(entity.getClass(), entity.getLongId()));
    }

    //==================================================================================================================

    /**
     * 实体验证逻辑
     *
     * @param entity
     * @return
     */
    public Enterprise validation(T entity) {
        Enterprise<String, Object> fielderror = new Enterprise();
        return fielderror;
    }

    public T findById(Serializable id) {
        return get(entityClass, id);
    }

    /**
     * Loading 数据
     *
     * @param entity
     * @param id
     * @return
     */
    public <T> T load(Class<T> entity, Serializable id) {
        return (T) getSession().load(entity, id);
    }

    /**
     * get 数据
     *
     * @param entity
     * @param id
     * @return
     */
    public <T> T get(Class<T> entity, Serializable id) {
        return (T) getSession().get(entity, id);
    }

    public List<T> get(Class<T> entity, List<Serializable> ids) {
        Query query = getSession().createQuery("from " + entity.getSimpleName() + " t1 where t1.id in(:ids)");
        List values = new ArrayList();
        for (Object id : ids) {
            values.add(Long.valueOf(id.toString()));
        }
        query.setParameterList("ids", values);
        return query.list();
    }

    //-------------------------------------------------------------------------
    // Convenience finder methods for HQL strings
    //-------------------------------------------------------------------------


    /**
     * 数据库操作 返回单条记录
     *
     * @param params
     * @return
     */

    public T find(T params) {
        params.set(Pagination.PAGE_SIZE, 1);
        List<T> values = finds(params);
        return (isEmpty(values)) ? null : values.get(0);
    }

    public T find(StringBuffer queryString, T params) {
        List<T> values = finds(queryString, params);
        return ValidHelper.isEmpty(values) ? null : values.get(0);
    }


    public List<T> finds(T params) {
        return finds(generateSQL(params), params);
    }


    //@Cacheable(value = "com.enterprise.framework.orm.hibernate.AbstractDaoImpl", key = "'finds_'+queryString+'_'+#entity", condition = "null!=#params and #input.getBoolean('cache')")
    public List finds(StringBuffer queryString, T entity) {
        long apptime = System.currentTimeMillis();
        Query query = getSession().createQuery(queryString.toString());
        query.setCacheable(true);
        if (entity != null) {
            query.setProperties(entity);
            if (!entity.isEmpty(Pagination.PAGE_SIZE)) {
                query.setMaxResults(entity.getInt(Pagination.PAGE_SIZE));
            }
        }
        List result = query.list();
        logger.debug("执行当前SQL耗时: " + (System.currentTimeMillis() - apptime) + "MS -> " + queryString);
        return null == result ? new ArrayList() : result;
    }

    public List<T> findsParentAndNext(Class claz, Long id) {
        StringBuffer queryString = new StringBuffer("from " + claz.getSimpleName() + " t1 where ");
        queryString.append("t1.id = (select min(t2.id) from " + claz.getSimpleName() + " t2  where t2.id>" + id + ") OR t1.id = (select max(t3.id) from " + claz.getSimpleName() + " t3  where t3.id<" + id + ")");
        queryString.append(" order by t1.id desc");
        return finds(queryString, null);
    }


    //-------------------------------------------------------------------------
    // Convenience finder methods for named queries
    //-------------------------------------------------------------------------

    public Integer count(T enterprise) {
        return count(generateSQL(enterprise), enterprise);
    }

    public Integer count(StringBuffer queryResultString, T enterprise) {
        /*生成 总记录数查询 SQL*/
        String tempQueryResultString = queryResultString.toString().toLowerCase();
        int i1 = tempQueryResultString.indexOf("from " + getPojoSimpleName(getPojoClass(enterprise)).toLowerCase());
        int i2 = tempQueryResultString.indexOf("where 1=1");
        int i3 = tempQueryResultString.indexOf("order by");
        String queryCountString = new String("select count(" + getPojoSimpleName(getPojoClass(enterprise)) + ") ");
        if (i2 == -1 && i3 == -1) {
            queryCountString += queryResultString.substring(i1, queryResultString.length());
        } else if (i2 != -1 && i3 != -1) {
            queryCountString += queryResultString.substring(i1, i2) + queryResultString.substring(i2, i3);
        } else if (i2 == -1 && i3 != -1) {
            queryCountString += queryResultString.substring(i1, i3);
        } else {
            queryCountString += queryResultString.substring(i1, queryResultString.length());
        }
        /*抓取*/
        for (Object value : enterprise.keySet()) {
            if (null != value && String.valueOf(value).indexOf("fetch") != -1) {
                queryCountString = queryCountString.replace("LEFT JOIN FETCH " + getPojoSimpleName(getPojoClass(enterprise)) + "." + String.valueOf(value).substring(5) + " ", "");
            }
        }
        /**
         * 查询 总记录数 SQL
         */
        Query queryObject = getSession().createQuery(queryCountString);
        queryObject.setCacheable(true);
        queryObject.setProperties(enterprise);
        return Integer.valueOf(queryObject.uniqueResult().toString());

    }

    public Integer max(T enterprise) {
        return max(generateSQL(enterprise), enterprise);
    }

    public Integer max(StringBuffer queryResultString, T enterprise) {
        /*生成 总记录数查询 SQL*/
        String tempQueryResultString = queryResultString.toString().toLowerCase();
        int i1 = tempQueryResultString.indexOf("from " + getPojoSimpleName(getPojoClass(enterprise)).toLowerCase());
        int i2 = tempQueryResultString.indexOf("where 1=1");
        int i3 = tempQueryResultString.indexOf("order by");
        String queryMaxString = new String("select max(" + getPojoSimpleName(getPojoClass(enterprise)) + ".id) ");
        if (i2 == -1 && i3 == -1) {
            queryMaxString += queryResultString.substring(i1, queryResultString.length());
        } else if (i2 != -1 && i3 != -1) {
            queryMaxString += queryResultString.substring(i1, i2) + queryResultString.substring(i2, i3);
        } else if (i2 == -1 && i3 != -1) {
            queryMaxString += queryResultString.substring(i1, i3);
        } else {
            queryMaxString += queryResultString.substring(i1, queryResultString.length());
        }
        /**
         * 查询 记录最大ID SQL
         */
        Query queryObject = getSession().createQuery(queryMaxString);
        queryObject.setCacheable(true);
        queryObject.setProperties(enterprise);
        Object result = queryObject.uniqueResult();
        return null == result ? 0 : Integer.valueOf(result.toString());
    }

    /**
     * -------------------------------------------------------------------------
     * 分页方法Pationation
     * -------------------------------------------------------------------------
     */

    /*@Cacheable(value = "com.enterprise.framework.orm.hibernate.AbstractDaoImpl", key = "'findPager_'+#params", condition = "null!=#params and #params.getBoolean('cache')")*/
    public Pagination findPager(T params) {
        return findPager(generateSQL(params), params);
    }

    /*@Cacheable(value = "com.enterprise.framework.orm.hibernate.AbstractDaoImpl", key = "'findPager_'+queryResultString+'_'+#enterprise", condition = "null!=#params and #enterprise.getBoolean('cache')")*/
    public Pagination findPager(StringBuffer queryResultString, T enterprise) {
        long apptime = System.currentTimeMillis();
        /*构造分页数据存储对象*/
        Pagination pagination = new Pagination(enterprise);
        enterprise.set(Pagination.PAGE_RESULT_COUNT, count(queryResultString, enterprise));
        /*查询分页结果数据 SQL*/
        Query query = getSession().createQuery(queryResultString.toString());
        query.setCacheable(true);
        query.setProperties(enterprise);
        if (enterprise.getInt(Pagination.PAGE_RESULT_COUNT) != 0) {
            int i = (enterprise.getInt(Pagination.PAGE_INDEX) * enterprise.getInt(Pagination.PAGE_SIZE)) - enterprise.getInt(Pagination.PAGE_SIZE);
            query.setFirstResult(i < 0 ? 0 : i);
            query.setMaxResults(enterprise.getInt(Pagination.PAGE_SIZE));
            enterprise.set(Pagination.PAGE_RESULT, query.list());
        }
        logger.debug("执行当前SQL耗时(分页,双SQL): " + (System.currentTimeMillis() - apptime) + "MS -> " + queryResultString + "\n");
        return pagination;

    }

    public boolean isEmpty(Object object) {
        return ValidHelper.isEmpty(object);
    }

    public interface IBuilderSql {
        boolean execute(StringBuffer queryString, String simpleName);
    }

    public StringBuffer generateSQL(T params) {
        return builderSql(params, new IBuilderSql[]{});
    }

    public Class getPojoClass(T entity) {
        return entity.getClass().isAnonymousClass() ? entity.getClass().getSuperclass() : entity.getClass();
    }

    public String getPojoSimpleName(Class entity) {
        return entity.getSimpleName().toLowerCase();
    }

    public StringBuffer builderSql(T entity, IBuilderSql... builder) {
        Class pojoClass = getPojoClass(entity);
        String simpleName = getPojoSimpleName(pojoClass);
        StringBuffer queryString = new StringBuffer("from " + pojoClass.getSimpleName() + " " + simpleName + " where 1=1 ");
        if (!entity.isEmpty("id")) {
            queryString.append("and " + simpleName + ".id=:id ");
        } else if (!entity.isEmpty("greaterid")) {
            entity.set("id", entity.getInt("greaterid"));
            queryString.append("and " + simpleName + ".id > :id ");
        } else if (!entity.isEmpty("lessid")) {
            entity.set("id", entity.getInt("lessid"));
            queryString.append("and " + simpleName + ".id < :id ");
        }

        if (entity instanceof BaseEntity) {
            // 状态
            if (entity.getInt("status") > 0) {
                queryString.append(" and " + simpleName + ".status =:status ");
                if (!(entity.get("status") instanceof Integer)) {
                    entity.set("status", entity.getInt("status"));
                }
            }
            // 审核状态
            if (entity.getInt("auditing") > 0) {
                if (entity.equals("auditing", Constants.AUDITING_STATUS_SUCCESS)) {
                    queryString.append(" and " + simpleName + ".auditing in(" + Constants.AUDITING_STATUS_SUCCESS + "," + Constants.AUDITING_STATUS_SUCCESS_SECOND + ") ");
                } else {
                    queryString.append(" and " + simpleName + ".auditing =" + entity.getInt("auditing") + " ");
                }
            }
            // 过滤逻辑删除记录
            if (entity.getInt("is_drop") != 0) {
                if (entity.getInt("is_drop") > 0) {
                    queryString.append(" and (" + simpleName + ".is_drop = " + entity.getInt("is_drop") + ") ");
                } else {
                    //小于 0 则显示全部数据
                    //全部数据
                }
            } else {
                queryString.append(" and (" + simpleName + ".is_drop <> " + Constants.YES + " OR " + simpleName + ".is_drop is null) ");
            }
            // 所属用户
            if (entity.getLong("creator") > 0) {
                queryString.append("and creator=:creator");
                if (!(entity.get("creator") instanceof Long)) {
                    entity.set("creator", entity.getLong("creator"));
                }
            }
            // 查询当天创建的数据
            if (!entity.isEmpty("today")) {
                if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_ORACLE) {
                    queryString.append(" AND to_char(" + simpleName + ".create,'yyyy-MM-dd')='" + DateHelper.FORMAT_SIMPLE.format(new Date()) + "' ");
                } else if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_H2) {
                    queryString.append(" AND concat(year(" + simpleName + ".create),'-',Month(" + simpleName + ".create),'-',day(" + simpleName + ".create))='" + DateHelper.FORMAT_SIMPLE.format(new Date()).replace("-0", "-") + "' ");
                }
            }

        }
        // 抓取
        for (Object value : entity.keySet()) {
            if (null != value && String.valueOf(value).indexOf("fetch") != -1) {
                queryString.insert(queryString.indexOf("where"), "LEFT JOIN FETCH " + simpleName + "." + String.valueOf(value).substring(5) + " ");
            }
        }
        // 当 builder 返回 Flash 时 排序自动拼装，否则手动拼装不启用下边逻辑
        if (!isEmpty(builder) && builder.length > 0 && !builder[0].execute(queryString, simpleName)) {
            if (!entity.isEmpty(Pagination.PAGE_ORDER_BY) && entity.get(Pagination.PAGE_ORDER_BY) instanceof String) {
                append(queryString, "ORDER BY");
                append(queryString, entity.getString(Pagination.PAGE_ORDER_BY).replace("simpleName", simpleName));
                return queryString;
            }
        }
        append(queryString, "ORDER BY");
        append(queryString, simpleName + ".id desc");
        return queryString;
    }


    // 追加 SQL
    public StringBuffer append(StringBuffer stringBuffer, String string) {
        return stringBuffer.append(" ").append(string);
    }

    // 追加 SQL
    public StringBuffer append(StringBuffer stringBuffer, String $1, String $2, String $3) {
        return stringBuffer.append(" ").append($1).append(" ").append($2).append(".").append($3);
    }

    // 追加 时间段过滤 SQL
    public StringBuffer appendDataCondition(StringBuffer stringBuffer, String logic, String simpleName, String field, Date start, Date end) {
        if (null != start) {
            String start_by_string = DateHelper.format("yyyy-MM-dd HH:mm:ss", start);
            if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_ORACLE) {
                stringBuffer.append(" ").append(logic).append(" ").append(simpleName).append(".").append(field).append(">to_date('").append(start_by_string).append("','yyyy-mm-dd hh24:mi:ss')");
            } else {
                stringBuffer.append(" ").append(logic).append(" ").append(simpleName).append(".").append(field).append(">").append("'").append(start_by_string).append("'");
            }
        }
        if (null != end) {
            String end_by_string = DateHelper.format("yyyy-MM-dd HH:mm:ss", end);
            if (SqlHelper.getDbType() == SqlHelper.DB_TYPE_ORACLE) {
                stringBuffer.append(" ").append(logic).append(" ").append(simpleName).append(".").append(field).append("<to_date('").append(end_by_string).append("','yyyy-mm-dd hh24:mi:ss')");
            } else {
                stringBuffer.append(" ").append(logic).append(" ").append(simpleName).append(".").append(field).append("<").append("'").append(end_by_string).append("'");
            }
        }
        return stringBuffer;
    }

    // 追加 模糊匹配
    public StringBuffer appendLikeCondition(StringBuffer stringBuffer, String logic, String simpleName, String field, String value) {
        stringBuffer.append(" ").append(logic).append(" lower(").append(simpleName).append(".").append(field).append(") like '%").append(value.trim().toLowerCase()).append("%'");
        return stringBuffer;
    }

}
