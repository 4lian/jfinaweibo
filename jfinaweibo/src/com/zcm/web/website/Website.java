package com.zcm.web.website;

import java.util.HashMap;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.zcm.utils.CacheManager;

/**
 * Website model.
 */
@SuppressWarnings("serial")
public class Website extends Model<Website> {
	
	public static final Website dao = new Website();
	
	/**
	 * 获取网站配置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String,String> getConfig(){
		String key = "Website_HashMap";
		HashMap<String,String> map = null;
		if(CacheManager.hasCache(key)){
			map = (HashMap<String,String>)CacheManager.getCache(key);
		}else{
			map = new HashMap<String,String>();
	    	String sql = "select * from website";
	    	List<Website> websiteList = Website.dao.find(sql);
			for(Website web:websiteList){
				map.put(web.getStr("code"), web.getStr("content"));
			}
			CacheManager.putCache(key, map);
		}
		return map;
	}
}