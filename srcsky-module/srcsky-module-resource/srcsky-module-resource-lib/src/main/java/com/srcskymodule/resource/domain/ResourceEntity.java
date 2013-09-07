package com.srcskymodule.resource.domain;


import com.srcskyframework.dao.pojo.BaseEntity;
import com.srcskyframework.helper.FileHelper;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-3-24
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "tb_base_resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResourceEntity extends BaseEntity {
    public String title;
    public String postfix;
    public Long category;
    public String hash;
    public Long length;
    //是否图片
    public Integer isimage;
    public Integer width;
    public Integer height;
    //同步状态
    public Integer synchrostatus;
    //资源来源
    public Integer origin;
    public Integer path;
    public Integer auditing;


    public ResourceEntity() {
    }

    public ResourceEntity(Map entity) {
        super(entity);
    }


    @Id
    @SequenceGenerator(name = "SEQ_BASE_RESOURCE_GENERATOR", sequenceName = "SEQ_BASE_RESOURCE", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "SEQ_BASE_RESOURCE_GENERATOR", strategy = GenerationType.AUTO)
    public Long getId() {
        return getLong("id");
    }

    public void setId(Long id) {
        set("id", id);
    }

    @Column(name = "title", length = 200)
    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    @Column(name = "postfix", length = 5)
    @Basic
    public String getPostfix() {
        return getString("postfix");
    }

    public void setPostfix(String postfix) {
        set("postfix", postfix);
    }

    /**
     * 图片类型， 可选参数 在 Constants.RESOURCE_* 中选择
     *
     * @return
     */
    @Column(name = "category", length = 20)
    @Basic
    public Long getCategory() {
        return getLong("category");
    }

    public void setCategory(Long category) {
        set("category", category);
    }


    @Column(name = "hash", length = 32)
    @Basic
    public String getHash() {
        return getString("hash");
    }

    public void setHash(String hash) {
        set("hash", hash);
    }


    @Column(name = "length", length = 10)
    @Basic
    public Long getLength() {
        return getLong("length");
    }

    public void setLength(Long length) {
        set("length", length);
    }


    public int getWidth() {
        return getInt("width");
    }

    public void setWidth(int width) {
        set("width", width);
    }

    public int getHeight() {
        return getInt("height");
    }

    public void setHeight(int height) {
        set("height", height);
    }

    public Integer getIsimage() {
        return getInt("isimage");
    }

    public void setIsimage(Integer isimage) {
        set("isimage", isimage);
    }

    public Integer getSynchrostatus() {
        return getInt("synchrostatus");
    }

    public void setSynchrostatus(Integer synchrostatus) {
        set("synchrostatus", synchrostatus);
    }

    public Integer getOrigin() {
        return getInt("origin");
    }

    public void setOrigin(Integer origin) {
        set("origin", origin);
    }

    public String getPath() {
        return getString("path");
    }

    public void setPath(String path) {
        set("path", path);
    }

    public Integer getAuditing() {
        return getInt("auditing");
    }

    public void setAuditing(Integer auditing) {
        set("auditing", auditing);
    }

    public String toString() {
        return FileHelper.generateDirectory(getLong("id")) + getString("id");
        //return FileUtility.generateDirectory(getLong("id")) + getString("id") + "." + getPostfix();
    }

    @Transient
    public String toString(int width, int height) {
        return toString() + "!" + width + "x" + height;
    }

    @Transient
    public String toString(int width) {
        return toString() + "!" + width;
    }
}
