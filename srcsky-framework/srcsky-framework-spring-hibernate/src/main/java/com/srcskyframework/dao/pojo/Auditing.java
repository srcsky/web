package com.srcskyframework.dao.pojo;

import com.srcskyframework.core.Enterprise;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

/**
 * ${FILE_NAME}
 *
 * @author Zhanggj
 *         13-6-26 下午11:18
 * @version 1.0.0
 * @see
 */
@Embeddable
@Access(AccessType.FIELD)
public class Auditing extends Enterprise {

    @Column(name = "AUDITING", length = 10)
    public Integer getAuditing() {
        return getInt("auditing");
    }

    public void setAuditing(Integer auditing) {
        set("auditing", auditing);
    }


    @Column(name = "AUDITING_DATE")
    public Date getAuditing_date() {
        return getDate("auditing_date");
    }

    public void setAuditing_date(Date auditing_date) {
        set("auditing_date", auditing_date);
    }

    @Column(name = "AUDITING_FAIL_CAUSE", length = 3000)
    public String getAuditing_fail_cause() {
        return getString("auditing_fail_cause");
    }

    public void setAuditing_fail_cause(String auditing_fail_cause) {
        set("auditing_fail_cause", auditing_fail_cause);
    }
}
