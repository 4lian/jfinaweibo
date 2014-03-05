package com.zcm.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.zcm.interceptor.AdminInterceptor;
import com.zcm.utils.CacheManager;
import com.zcm.utils.PageUtils;
import com.zcm.vo.ArticleVO;
import com.zcm.web.article.Article;
import com.zcm.web.category.Category;
import com.zcm.web.links.Links;
import com.zcm.web.tags.Tags;
import com.zcm.web.website.Website;

/**
 * CommonController
 */

public class CommonController extends Controller {
	
	/**
	 * 网站首页
	 */
	public void index(){
		/**获取网站配置**/
		HashMap<String,String> map = Website.getConfig();
		setAttr("config", map);
		/**导航菜单**/
		List<Category> menuList = Category.getAllCategory();
		setAttr("menuList", menuList);
		/**获取一个最新的置顶文章**/
		ArticleVO vo = this.getTopArticle();
		setAttr("top", vo);
		/**分页获取**/
		Category category = null;
		int pageNumber = this.getRequest().getAttribute("page")==null?1:Integer.parseInt(this.getRequest().getAttribute("page").toString());
		Page<Article> articleList = Article.dao.paginate(pageNumber, 15, "select *", "from article order by aid desc");
		List<ArticleVO> newarticleList = new ArrayList<ArticleVO>();
		for(Article article:articleList.getList()){
			vo = new ArticleVO();
			vo.setAddtime(article.getStr("addtime"));
			vo.setAid(article.getInt("aid"));
			vo.setCid(article.getInt("cid"));
			vo.setClicknum(article.getInt("clicknum"));
			long num = Db.queryLong("select count(1) from gbook where aid = ?  and state = 1",article.get("aid"));
			vo.setMsgnum(num);
			category = Category.dao.findFirst("select * from category where cid = ?", new Object[]{article.getInt("cid")});
			vo.setCname(category.getStr("cname"));
			vo.setCurl(category.getStr("url"));
			vo.setImgurl(article.getStr("imgurl"));
			vo.setRemark(article.getStr("remark"));
			vo.setTitle(article.getStr("title"));
			vo.setType(article.getStr("type"));
			newarticleList.add(vo);
		}
		setAttr("pageList", articleList);
		setAttr("pageNumList", PageUtils.getPages(articleList.getPageNumber(),articleList.getTotalPage()));
		setAttr("articleList", newarticleList);
		/**获取友情链接**/
		List<Links> linkList = Links.getAllLinks();
		setAttr("linkList", linkList);
		/**文章标签**/
		List<Tags> tagsList = this.getTagList(0,15);
		setAttr("tagsList", tagsList);
		render("/view/index.jsp");
	}
	
	/**
	 * 获取Tag
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Tags> getTagList(int start,int end){
		List<Tags> tagsList1 = null;
		String key = "CommonController_getTagList_"+start+"_"+end;
		if(CacheManager.hasCache(key)){
			tagsList1 = (List<Tags>)CacheManager.getCache(key);
		}else{
			tagsList1 = Tags.dao.find("select * from tags order by tid desc limit " + start + "," + end);
		}
		return tagsList1;
	}
	
	/**
	 * 获取置顶的文章
	 * @return
	 */
	private ArticleVO getTopArticle(){
		ArticleVO vo = null;
		String key = "CommonController_getTopArticle";
		if(CacheManager.hasCache(key)){
			vo = (ArticleVO)CacheManager.getCache(key);
		}else{
			/**获取一个最新的置顶文章**/
			Article top = Article.dao.findFirst("select * from article where type = ? order by aid desc", new Object[]{"1"});
			Category category = null;
			if(top!=null){
				vo = new ArticleVO();
				vo.setAddtime(top.getStr("addtime"));
				vo.setAid(top.getInt("aid"));
				vo.setCid(top.getInt("cid"));
				vo.setClicknum(top.getInt("clicknum"));
				long anum = Db.queryLong("select count(1) from gbook where aid = ? and state = 1",top.get("aid"));
				vo.setMsgnum(anum);
				category = Category.dao.findFirst("select * from category where cid = ?", new Object[]{top.getInt("cid")});
				vo.setCname(category.getStr("cname"));
				vo.setCurl(category.getStr("url"));
				vo.setImgurl(top.getStr("imgurl"));
				vo.setRemark(top.getStr("remark"));
				vo.setTitle(top.getStr("title"));
				vo.setType(top.getStr("type"));
			}
			CacheManager.putCache(key, vo);
		}
		return vo;
	}
}
