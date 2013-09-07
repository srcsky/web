package com.srcskyframework.dao.pojo;

import com.srcskyframework.core.Enterprise;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-6
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public class BaseEntity<K, T> extends Enterprise<K, T> {
    // 编号
    private Long id;
    // 创建人 creator
    private Long creator;
    // 创建人-管理员 creator_by_admin
    private Long creator_by_admin;
    // 创建时间
    private Date create;
    // 修改人
    private Long updator;
    // 修改人-管理员
    private Long updator_by_admin;
    // 修改时间
    private Date update;
    // 状态
    private Integer status;
    // 备注
    private String remarks;
    // 删除状态
    private Integer is_drop;


    public BaseEntity() {
    }

    public BaseEntity(Map entity) {
        super(entity);
    }


    @Column(updatable = false, insertable = false)
    public Long getId() {
        return getLong("id");
    }

    public void setId(Long id) {
        set("id", id);
    }


    @Column(name = "CREATOR", updatable = false)
    public Long getCreator() {
        return getLong("creator");
    }

    public void setCreator(Long creator) {
        set("creator", creator);
    }

    @Column(name = "CREATOR_BY_ADMIN", updatable = false)
    public Long getCreator_by_admin() {
        return getLong("creator_by_admin");
    }

    public void setCreator_by_admin(Long creator_by_admin) {
        set("creator_by_admin", creator_by_admin);
    }


    @Column(name = "`CREATE`", updatable = false)
    public Date getCreate() {
        return getDate("create");
    }

    public void setCreate(Date create) {
        set("create", create);
    }


    @Column(name = "UPDATOR")
    public Long getUpdator() {
        return getLong("updator");
    }

    public void setUpdator(Long updator) {
        set("updator", updator);
    }


    @Column(name = "UPDATOR_BY_ADMIN")
    public Long getUpdator_by_admin() {
        return getLong("updator_by_admin");
    }

    public void setUpdator_by_admin(Long updator_by_admin) {
        set("updator_by_admin", updator_by_admin);
    }


    @Column(name = "`UPDATE`")
    public Date getUpdate() {
        return getDate("update");
    }

    public void setUpdate(Date update) {
        set("update", update);
    }


    @Column(name = "STATUS", length = 10)
    public Integer getStatus() {
        return getInt("status");
    }

    public void setStatus(Integer status) {
        set("status", status);
    }


    @Column(name = "REMARKS", length = 100)
    public String getRemarks() {
        return getString("remarks");
    }

    public void setRemarks(String remarks) {
        set("remarks", remarks);
    }

    @Column(name = "is_drop", length = 2)
    public Integer getIs_drop() {
        return getInt("is_drop");
    }

    public void setIs_drop(Integer is_drop) {
        set("is_drop", is_drop);
    }
}
