package com.srcskyframework.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerAdapter;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-23
 * Time: 上午9:36
 * 分组 Cache　方便　快速删除　Cache
 */
public class CacheGroupListenerFactory extends CacheEventListenerFactory {

    public CacheEventListener createCacheEventListener(final Properties properties) {
        return new CacheEventListenerAdapter() {
            public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
                super.notifyElementPut(cache, element);
                //System.out.println("notifyElementPut：" + cache.getName() + "," + element.getKey());
            }

            public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
                super.notifyElementRemoved(cache, element);
                //System.out.println("notifyElementRemoved：" + cache.getName() + "," + element.getKey());
            }
        };
    }
}
