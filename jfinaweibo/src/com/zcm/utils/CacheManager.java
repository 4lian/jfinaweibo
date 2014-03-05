package com.zcm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
  * 文件名：CacheManager.java 管理缓存
  *
  */
@SuppressWarnings("all")
public class CacheManager {

	private static HashMap cacheMap = new HashMap();

	/**
	 * 单实例构造方法
	 */
	private CacheManager() {
		super();
	}

	/**
	 * 得到缓存。同步静态方法
	 * 
	 * @param key
	 * @return
	 */
	public synchronized static Object getCache(String key) {
		return cacheMap.get(key);
	}

	/**
	 * 判断是否存在一个缓存
	 * 
	 * @param key
	 * @return
	 */
	public synchronized static boolean hasCache(String key) {
		return cacheMap.containsKey(key);
	}

	/**
	 * 清除所有缓存
	 */
	public synchronized static void clearAll() {
		cacheMap.clear();
	}

	/**
	 * 清除指定的缓存
	 * 
	 * @param key
	 */
	public synchronized static void clearOnly(String key) {
		cacheMap.remove(key);
	}

	/**
	 * 载入缓存
	 * 
	 * @param key
	 * @param obj
	 */
	public synchronized static void putCache(String key, Object obj) {
		cacheMap.put(key, obj);
	}
}