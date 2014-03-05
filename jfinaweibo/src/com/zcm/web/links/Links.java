package com.zcm.web.links;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.zcm.utils.CacheManager;

/**
 * Links model.
 */
@SuppressWarnings("serial")
public class Links extends Model<Links> {
	
	public static final Links dao = new Links();
	
	@SuppressWarnings("unchecked")
	public static List<Links> getAllLinks(){
		String key = "Links_getAllLinks";
		List<Links> cList = null;
		if(CacheManager.hasCache(key)){
			cList = (List<Links>)CacheManager.getCache(key);
		}else{
			cList = Links.dao.find("select * from links order by id asc");
			CacheManager.putCache(key, cList);
		}
		return cList;
	}
	
}