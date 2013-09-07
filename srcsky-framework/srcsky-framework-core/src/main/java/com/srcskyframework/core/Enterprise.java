package com.srcskyframework.core;

import com.srcskyframework.helper.*;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * Enterprise集成框架的 核心对象 , 很多地方都用到它
 * 该对象是 一个 数据封装模型
 * WEB 请求中的数据可以 通过 WebUtility.getInput() 转换为  Enterprise
 * Hibernate 的实体对象 也集成与 此对象
 */

public class Enterprise<A, B> extends LinkedHashMap<A, B> implements Serializable {

    private final static Logger logger = Logger.getLogger(Enterprise.class);

    private final static StringBuffer _toString = new StringBuffer("\n\t----------------[ Enterprise Result ]----------------\n\t\t    KEY\t\t|\t VALUE\n\t-----------------------------------------------------");

    protected String enterprisename;

    public Enterprise() {
    }

    public Enterprise(String name) {
        this.enterprisename = name;
    }

    public Enterprise(Map entity) {
        super(entity);
    }

    public Enterprise(Enterprise entity) {
        super(entity);
    }


    public Enterprise(String enterprisename, Enterprise entity) {
        this.enterprisename = enterprisename;
        super.putAll(entity);
    }

    /**
     * 设置主见
     *
     * @return
     */
    public Enterprise setPrimary() {
        set("primary", getLastKey());
        return this;
    }

    /**
     * 设置不需要需要验证字段
     *
     * @param fileds
     * @return
     */
    public Enterprise addNotValidField(String... fileds) {
        for (String filed : fileds) {
            set(Constants.VALIDATION_BY_DAO_UPDATE + filed, true);
        }
        return this;
    }

    public boolean isNeedValidField(String filed) {
        return !containsKey(Constants.VALIDATION_BY_DAO_UPDATE + filed);
    }


    public Enterprise getEnterprise(String key) {
        return (Enterprise) get(key);
    }

    public Enterprise setMessage(String message) {
        set("message", message);
        return this;
    }

    public String getMessage() {
        return getString("message");
    }

    public Boolean getSuccess() {
        return getBoolean("success");
    }

    public void setSuccess(Boolean success) {
        set("success", success);
    }

    public String getEnterpriseName() {
        return this.enterprisename;
    }

    public Enterprise setEnterpriseName(String name) {
        this.enterprisename = name;
        return this;
    }


    //==================================================================================================================
    //GET SET方法   Start

    public Enterprise set(Object key, Object value) {
        super.put((A) key, (B) value);
        return this;
    }

    public Long getLongId() {
        return getLong("id");
    }


    public Integer getIntId() {
        return getInt("id");
    }


    public Integer getInteger(Object key) {
        return getInt(key);
    }


    public Integer getGreaterid() {
        return getInt("greaterid");
    }

    public void setGreaterid(Integer greaterid) {
        set("greaterid", greaterid);
    }


    public Long getLessid() {
        return getLong("lessid");
    }

    public void setLessid(Long lessid) {
        set("lessid", lessid);
    }


    public Integer getInt(Object key) {
        Object value = get(key);
        if (value == null || ValidHelper.isEmpty(value.toString()) || value.toString().equals("null")) {
            return 0;
        }
        Class classType = value.getClass();
        if (classType == Integer.class) {
            return ((Integer) value).intValue();
        } else if (classType == String.class) {
            try {
                String value_temp = value.toString().trim();
                return value_temp.equalsIgnoreCase("") ? 0 : Integer.parseInt(value_temp);
            } catch (Exception ex) {
                logger.error(key + ":" + value + "，在转换为int型时异常", ex);
            }
        } else if (classType == Short.class) {
            return new Integer(((Short) value).shortValue());
        } else if (classType == Long.class) {
            return ((Long) value).intValue();
        } else if (classType == BigDecimal.class) {
            return ((BigDecimal) value).intValue();
        }
        return 0;
    }


    public double getDouble(Object key) {
        Object value = get(key);
        if (value == null) return 0;
        Class classType = value.getClass();
        if (classType == Double.class) {
            return ((Double) value).doubleValue();
        } else if (classType == Float.class) {
            try {
                return (double) ((Float) value).floatValue();
            } catch (Exception e) {
                return 0;
            }
        } else if (classType == String.class || classType == BigDecimal.class) {
            try {
                return Double.parseDouble(value.toString());
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }


    public float getFloat(Object key) {
        Object value = get(key);
        if (value == null) return 0;
        Class classType = value.getClass();
        if (classType == Float.class) {
            return ((Float) value).floatValue();
        } else if (classType == String.class || classType == BigDecimal.class) {
            try {
                return Float.parseFloat(value.toString());
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Long getLong(Object key) {
        Object value = get(key);
        if (value == null) return 0l;
        Class classType = value.getClass();
        if (classType == Long.class) {
            return ((Long) value).longValue();
        } else if (classType == String.class) {
            try {
                String value_temp = value.toString().trim();
                return value_temp.equalsIgnoreCase("") ? 0 : Long.parseLong(value_temp);
            } catch (Exception ex) {
                logger.error(key + ":" + value + "，在转换为Long型时异常", ex);
            }
        } else if (classType == Integer.class) {
            return Long.valueOf(((Integer) value));
        } else if (classType == Short.class) {
            return (long) ((Short) value).shortValue();
        } else if (classType == BigDecimal.class) {
            return ((BigDecimal) value).longValue();
        }
        return 0l;
    }


    public short getShort(Object key) {
        Object value = get(key);
        if (value == null) return 0;
        Class classType = value.getClass();
        if (classType == Short.class) {
            return ((Short) value).shortValue();
        } else if (classType == String.class) {
            try {
                return Short.parseShort(value.toString());
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }


    public boolean getBoolean(Object key) {
        Object value = get(key);
        if (value == null) return false;
        if (value.getClass().isInstance(new Boolean(true))) {
            return ((Boolean) value).booleanValue();
        } else if (value.getClass().isInstance(new String())) {
            try {
                return value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("1");
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }


    public String getString(Object key) {
        Object value = get(key);
        try {
            return value == null ? null : value.toString();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return "EException:" + ex.getMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "EException:" + ex.getMessage();
        }
    }

    //key为空时返回 defaultValue
    public String getString(Object key, String defaultValue) {
        String value = getString(key);
        return ValidHelper.isNotEmpty(value) ? value : defaultValue;
    }

    public String getStringByTrim(String key) {
        String value = getString(key);
        return value == null ? "" : value.trim();
    }


    public BigDecimal getBigDecimal(Object key) {
        Object value = get(key);
        if (value == null) {
            return new BigDecimal(0.0D);
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else {
            try {
                return new BigDecimal(value.toString());
            } catch (Exception ex) {
                return BigDecimal.ZERO;
            }
        }
    }


    public Date getDate(String key) {
        Object value = get(key);
        try {
            if (value instanceof Date) {
                return (Date) value;
            } else if (value instanceof String) {
                String dateString = ((String) value).trim();
                if (dateString.equalsIgnoreCase("") || dateString.equals("null")) {
                    return null;
                } else {
                    return DateHelper.parse((String) value);
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Set getSet(Object key) {
        Object value = get(key);
        try {
            return value == null || !(value instanceof Set) ? null : (Set) value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public <TS extends Enterprise> TS getEntity2(String key, Class<? extends TS> clazz) {
        return (TS) get(key);
    }

    public List getList(String key) {
        Object value = get(key);
        if (null == value) {
            return null;
        } else if (value instanceof List) {
            return (List) value;
        } else {
            List values = new ArrayList();
            values.add(value);
            return values;
        }
    }


    public Date getStartDate() {
        Date startDate = DateHelper.parse(DateHelper.FORMAT_SIMPLE, getString("startDate"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public Date getEndDate() {
        Date endDate = DateHelper.parse(DateHelper.FORMAT_SIMPLE, getString("endDate"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    //GET SET方法   End
    //==================================================================================================================


    // Tools Utility 方法
    //==================================================================================================================


    public List<String> getIds() {
        if (get("id") instanceof List) return getList("id");
        return (List<String>) Arrays.asList(get("id"));
    }


    //获取最后一次 set 的键(Key)
    private String getLastKey() {
        Iterator iterator = this.keySet().iterator();
        String key = null;
        while (iterator.hasNext()) {
            key = iterator.next().toString();
        }
        return key;
    }

    public String getReplaceHtml(String key) {
        return isEmpty(key) ? "" : StringHelper.replaceHtml(getString(key));
    }

    public String getFilterHtml(String key) {
        return getReplaceHtml(key);
    }

    public String getClearHtml(String key) {
        return isEmpty(key) ? "" : StringHelper.clearHtml(getString(key));
    }

    //属性 操作 辅助方法   End
    public String getStringByUtf8(String key) {
        try {
            return (isEmpty(key)) ? getString(key) : new String(getString(key).getBytes("ISO-8859-1"), "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getString(key);
    }

    public String getStringByGbk(String key) {
        try {
            return (isEmpty(key)) ? getString(key) : new String(getString(key).getBytes("ISO-8859-1"), "gbk");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getString(key);
    }

    public Object getDefaultValue(String field, Object value) {
        if (isEmpty(field)) {
            return value;
        }
        return get(field);
    }

    public String getStringByDefaultValue(String field, String value) {
        if (isEmpty(field)) {
            return value;
        }
        return getString(field);
    }

    public String substring(String key, int length, String symbol) {
        return isEmpty(key) ? "" : StringHelper.substring(getString(key), length, symbol);
    }

    public boolean isEmpty(String field) {
        return ValidHelper.isEmpty(get(field));
    }

    public boolean isNotEmpty(String field) {
        return !isEmpty(field);
    }

    public boolean isEmpty(String field, String eq) {
        return ValidHelper.isEmpty(get(field), eq);
    }

    public boolean isNumber(String field) {
        return matches(field, "^[0-9\\.]*$");
    }

    public boolean isList(String field) {
        return get(field) instanceof List;
    }

    public boolean isMap(String field) {
        return get(field) instanceof Map;
    }

    public boolean isMobile(String field) {
        return ValidHelper.isMobile(getString(field));
    }

    /**
     * 是否大于0
     *
     * @return
     */
    public boolean isGreaterThanZero(String field) {
        return getLong(field) > 0;
    }

    public boolean equals(String key, Object value) {
        return null == get(key) ? false : (get(key).toString().equalsIgnoreCase(value == null ? "" : value.toString()));
    }

    public boolean matches(String key, String regex) {
        return ValidHelper.matches(get(key), regex);
    }

    public String getURLEncoder(String key) {
        return WebHelper.getURLEncoder(getString(key));
    }

    public boolean isEmail(String key) {
        return ValidHelper.isEmail(getString(key));
    }

    public boolean isUrl(String key) {
        return ValidHelper.isUrl(getString(key));
    }

    public boolean isDate(String key) {
        return ValidHelper.isDate(get(key));
    }

    public String getURLDecoder(String key) {
        return WebHelper.getURLDecoder(getString("key"));
    }

    public void putAll(Map<? extends A, ? extends B> m) {
        if (!ValidHelper.isEmpty(m)) {
            super.putAll(m);
        }
    }

    //属性 操作 辅助方法   End
    //==================================================================================================================

    public boolean equals(Object target) {
        if (this == target) return true;
        String targetClass = target.getClass().getName();
        String selfClass = getClass().getName();
        if (target == null || !(target instanceof Enterprise)) {
            return false;
        }
        if (targetClass.indexOf(selfClass) == -1 && getClass() != target.getClass()) {
            return false;
        }
        return getLong("id").equals(((Enterprise) target).getLong("id"));
    }

    public int hashCode() {
        Map<String, Object> temp = (Map<String, Object>) this;
        int result = getInt("id");
        for (Map.Entry<String, Object> entry : temp.entrySet()) {
            if (!(null == entry.getKey()
                    || null == entry.getValue()
                    || entry.getValue() instanceof Enterprise
                    || entry.getValue() instanceof Collection
            )) {
                result = 31 * result + entry.getValue().hashCode();
            }
        }
        return result;
    }

    public String toString() {

        StringBuffer buf = new StringBuffer(_toString);

        for (Map.Entry<String, Object> entry : ((Map<String, Object>) this).entrySet()) {

            if ((null == entry.getKey())) continue;

            int fieldLength = entry.getKey().toString().length();

            buf.append("\n\t" + entry.getKey());

            for (int t = 0; t < 4 - (Math.max(1, (fieldLength / 4) + ((fieldLength % 4) > 2 ? 1 : 0))); t++) {
                buf.append("\t");
            }

            if (entry.getValue() instanceof Enterprise) {
                buf.append("|        Enterprise -> " + entry.getClass());
            } else if (entry.getValue() instanceof Collection) {
                buf.append("|        Collection	-> Collection" /*+ ((Collection) entry.getValue()).size()*/);
            } else if (entry.getValue() instanceof Set) {
                buf.append("|        Set        -> Set"/* + ((Set) entry.getValue()).size()*/);
            } else {
                buf.append("|        " + entry.getValue());
            }

        }
        return buf.append("\n\t-----------------------------------------------------").toString();
    }

    public static void main(String[] args) {




    }
}
