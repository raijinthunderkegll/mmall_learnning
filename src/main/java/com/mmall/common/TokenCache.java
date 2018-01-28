package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TokenCache {

    private static final Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //guava缓存
    //initialCapacity:初始化缓存空间；maximumSize：最大缓存数 超过后调用LRU算法缓存淘汰；expireAfterAccess：有效访问时间（过期）
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(
            new CacheLoader<String, String>() {
                //默认数据加载实现。当调用get取值当时候，如果key没有对应当值，调用此方法进行加载。
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            }
    );

    /**
     * localCache插入数据
     * @param key
     * @param value
     */
    public static void setKey(String key, String value){
        localCache.put(key,value);
    }

    /**
     * localCache取值
     * @param key
     * @return
     */
    public static String getKey(String key){
        String value = null;

        try {
            value = localCache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("localCache get value by key : {"+ key + "} error",e);
        }

        return value;
    }
}
