/**
 * 宝龙电商
 * com.plocc.framework.cas
 * Authorization.java
 *
 * 2013-7-20-下午4:50
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.cas;

import java.io.Serializable;

/**
 * Authorization
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-7-20-下午4:50
 * @email zhanggj-hws@powerlong.com
 * @description 授权信息
 */
public class Authorization implements Serializable {

    // 用户ID
    private Long id;
    // 用户名
    private String username;
    // TGC 会话标识
    private String tgc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTgc() {
        return tgc;
    }

    public void setTgc(String tgc) {
        this.tgc = tgc;
    }
}
