package com.zcm.common;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.zcm.utils.FakeStaticHandler;
import com.zcm.web.admin.Admin;
import com.zcm.web.admin.AdminController;
import com.zcm.web.article.Article;
import com.zcm.web.article.ArticleController;
import com.zcm.web.category.Category;
import com.zcm.web.gbook.Gbook;
import com.zcm.web.links.Links;
import com.zcm.web.tags.Tags;
import com.zcm.web.website.Website;

/**
 * API引导式配置
 */
public class CommonConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	@Override
	public void configConstant(Constants me) {
		loadPropertyFile("a_little_config.txt");				
		me.setDevMode(getPropertyToBoolean("devMode", false));
		me.setViewType(ViewType.JSP); 	
	}
	
	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/", CommonController.class);
		me.add("/sysadmin", AdminController.class);
		me.add("/article", ArticleController.class);
	}
	
	/**
	 * 配置插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		/**配置C3p0数据库连接池插件**/
		C3p0Plugin c3p0Plugin = new C3p0Plugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password").trim());
		me.add(c3p0Plugin);
		
		/**配置ActiveRecord插件**/
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.addMapping("admin", Admin.class);
		arp.addMapping("category", Category.class);
		arp.addMapping("article", Article.class);
		arp.addMapping("links", Links.class);
		arp.addMapping("website", Website.class);
		arp.addMapping("tags", Tags.class);
		arp.addMapping("gbook", Gbook.class);
	}
	
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new FakeStaticHandler());
	}
	
	/**
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
	}
}
