package com.srcskyframework.ehcache;

import com.srcskyframework.spring.SpringLocator;
import net.sf.ehcache.Ehcache;
import org.springframework.cache.Cache;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-23
 * Time: 上午9:34
 * To change this template use File | Settings | File Templates.
 */
public class CacheManager {

    //CacheEventListener
    private static final org.springframework.cache.CacheManager cacheManager = SpringLocator.getBean(org.springframework.cache.CacheManager.class);

    /**
     * 驱逐 清除 Cache
     *
     * @param name
     * @param key
     */
    public static void evict(String name, String key) {
        Cache cache = cacheManager.getCache(name);
        if (null != cache) {
            cache.evict(key);
        }
    }

    /**
     * 按照正则 驱逐内容
     *
     * @param name
     * @param regex
     */
    public static void evictByRegex(String name, String startWith, String regex) {
        Pattern pattern = Pattern.compile(regex);
        //获取Cache 容器
        Cache container = cacheManager.getCache(name);
        if (null != container) {
            //获取缓存内容
            List<String> keys = ((Ehcache) container.getNativeCache()).getKeys();
            for (String key : keys) {
                //检测 是否 符合驱逐条件
                if (key.startsWith(startWith) && pattern.matcher(key).find()) {
                    container.evict(key);
                }
            }
        }
    }

    /**
     * 驱逐 全部　Cache 内容
     *
     * @param name
     */
    public static void evictAll(String name) {
        Cache cache = cacheManager.getCache(name);
        if (null != cache) {
            cache.clear();
        }
    }

    public static void put(String name, String key, Object value) {
        Cache container = cacheManager.getCache(name);
        container.evict(key);
        container.put(key, value);
    }
}
