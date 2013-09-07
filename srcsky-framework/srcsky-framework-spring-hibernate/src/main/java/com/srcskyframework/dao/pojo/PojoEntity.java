package com.srcskyframework.dao.pojo;

import javax.persistence.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-6
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */

@MappedSuperclass
public class PojoEntity<K, T> extends BaseEntity<K, T> {
    public PojoEntity() {
    }

    public PojoEntity(Map entity) {
        super(entity);
    }

    @Id
    @SequenceGenerator(name = "SEQ_BASE_GENERATOR", sequenceName = "SEQ_BASE", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "SEQ_BASE_GENERATOR", strategy = GenerationType.AUTO)
    public Long getId() {
        return getLong("id");
    }

    public void setId(Long id) {
        set("id", id);
    }

}
