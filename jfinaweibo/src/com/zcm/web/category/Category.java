package com.zcm.web.category;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.zcm.utils.CacheManager;

/**
 * Category model.
 */
@SuppressWarnings("serial")
public class Category extends Model<Category> {
	
	public static final Category dao = new Category();
	
	@SuppressWarnings("unchecked")
	public static List<Category> getAllCategory(){
		String key = "Category_getAllCategory";
		List<Category> cList = null;
		if(CacheManager.hasCache(key)){
			cList = (List<Category>)CacheManager.getCache(key);
		}else{
			cList = Category.dao.find("select * from category order by cid asc");
			CacheManager.putCache(key, cList);
		}
		return cList;
	}
	
}