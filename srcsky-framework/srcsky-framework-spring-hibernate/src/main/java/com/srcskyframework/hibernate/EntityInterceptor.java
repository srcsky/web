package com.srcskyframework.hibernate;

import com.srcskyframework.cas.Authorization;
import com.srcskyframework.core.Constants;
import com.srcskyframework.dao.pojo.BaseEntity;
import com.srcskyframework.helper.SessionHelper;
import com.srcskyframework.helper.ValidHelper;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-5-6
 * Time: 下午11:30
 * To change this template use File | Settings | File Templates.
 */
@Service
public class EntityInterceptor extends org.hibernate.EmptyInterceptor {


    /**
     * 保存
     *
     * @param entity
     * @param id
     * @param state
     * @param propertyNames
     * @param types
     * @return
     */
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        Authorization authorization = SessionHelper.getAuthorization();
        if (entity instanceof BaseEntity) {
            Date date = new Date();
            for (int i = 0; i < propertyNames.length; i++) {
                if (ValidHelper.isEmpty(state[i])) {
                    if ("create".equals(propertyNames[i]) || "update".equalsIgnoreCase(propertyNames[i]) || "auditing_date".equalsIgnoreCase(propertyNames[i])) {
                        state[i] = date;
                    } else if ("status".equalsIgnoreCase(propertyNames[i])) {
                        state[i] = Constants.STATUS_ENABLE;
                    } else if ("auditing".equals(propertyNames[i])) {
                        state[i] = Constants.AUDITING_STATUS_INIT;
                    } else if ("is_drop".equals(propertyNames[i])) {
                        state[i] = Constants.NO;
                    } else if (null != authorization && ("creator".equals(propertyNames[i]) || "updator".equals(propertyNames[i]))) {
                        state[i] = authorization.getId();
                    }
                }
            }
            return true;
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }


    /**
     * 修改
     *
     * @param entity
     * @param id
     * @param currentState
     * @param previousState
     * @param propertyNames
     * @param types
     * @return
     */
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        Authorization authorization = SessionHelper.getAuthorization();
        if (entity instanceof BaseEntity) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("update".equalsIgnoreCase(propertyNames[i])) {
                    currentState[i] = new Date();
                } else if (null != authorization && "updater".equals(propertyNames[i])) {
                    currentState[i] = authorization.getId();
                }
            }
            return true;
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
