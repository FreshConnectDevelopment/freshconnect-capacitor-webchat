package com.freshconnect.capacitor.webchat.cache;

import com.getcapacitor.PluginCall;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * 插件回调缓存
 */
public class PluginCallCache {

    private LoadingCache<String, PluginCall> pluginCalls;

    /**
     * 初始化配置
     */
    public void init() {
        pluginCalls = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10l, TimeUnit.MINUTES).build(
                        new CacheLoader<String, PluginCall>() {
                            public PluginCall load(String key) {
                                return null;
                            }
                        });
    }

    /**
     * 添加数据到缓存
     *
     * @param key
     * @param pluginCall
     */
    public void addPluginCallCache(String key, PluginCall pluginCall) {
        this.pluginCalls.put(key, pluginCall);
    }

    /**
     * 从缓存获取回调对象
     *
     * @param key
     * @return
     */
    public PluginCall getPluginCall(String key) {
        return this.pluginCalls.getIfPresent(key);
    }
}
