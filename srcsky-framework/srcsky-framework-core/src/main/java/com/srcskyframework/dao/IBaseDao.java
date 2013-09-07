package com.srcskyframework.dao;

import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.Pagination;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * 数据库访问层 基础接口,  持久化操作 可通过 AbstractDaoImpl类完成所有的持久化操作
 */
public interface IBaseDao<T extends Enterprise> {

    //==================================================================================================================
    //执行SQL语句
    void executeHQL(String hql, T... params);

    //==================================================================================================================
    //删除
    void delete(T entity);

    void delete(Long id);

    void deleteByLogic(T entity);

    //==================================================================================================================
    //修改 OR  保存  单条
    T update(T entity);

    //修改 OR  保存  集合
    List<T> updateByAll(List<T> entitys);

    //合并
    void merge(T entity);

    //刷新
    void refresh(T entity);


    //==================================================================================================================
    //实体验证逻辑
    Enterprise validation(T entity);

    //==================================================================================================================
    //查询
    T findById(Serializable id);

    <T> T load(Class<T> entity, Serializable id);

    <T> T get(Class<T> entity, Serializable id);

    List<T> get(Class<T> entity, List<Serializable> ids);

    T find(T input);

    List<T> finds(T input);

    List<T> finds(StringBuffer queryString, T input);

    //相邻的 上一条 下一条记录
    List<T> findsParentAndNext(Class claz, Long id);

    Integer count(T enterprise);

    Integer max(T enterprise);

    Pagination findPager(T enterprise);

    Pagination findPager(StringBuffer queryResultString, T enterprise);
}
