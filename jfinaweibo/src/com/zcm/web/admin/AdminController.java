package com.zcm.web.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zcm.interceptor.AdminInterceptor;
import com.zcm.utils.CacheManager;
import com.zcm.utils.DateUtils;
import com.zcm.utils.OperateImage;
import com.zcm.utils.ReadPropertity;
import com.zcm.utils.StringUtils;
import com.zcm.vo.AdminVO;
import com.zcm.vo.MsgVO;
import com.zcm.web.article.Article;
import com.zcm.web.category.Category;
import com.zcm.web.gbook.Gbook;
import com.zcm.web.links.Links;
import com.zcm.web.tags.Tags;
import com.zcm.web.website.Website;

/**
 * 后台管理
 * @author jiang.li
 */
public class AdminController extends Controller {
	
	/**
	 * 清理缓存
	 */
	public void clearCache() {
		CacheManager.clearAll();
		renderText("清理缓存成功");
	}
	
	/**
	 * 后台登陆
	 */
	public void index() {
		render("/sysadmin/login.jsp");
	}
	
	/**
	 * 后台登陆方法
	 */
	public void login() {
		String verifyimg = getPara("verifyimg")==null?"":getPara("verifyimg");
		String rand = getSessionAttr("rand");
		if(verifyimg.equals(rand)){
			String username = getPara("username")==null?"":getPara("username");
			String password = getPara("password")==null?"":getPara("password");
			password = StringUtils.MD5(password);
			Admin admin = Admin.dao.findFirst("select * from admin where username = ? and password = ?",username,password);
			if(admin!=null){
				AdminVO adminvo = new AdminVO();
				adminvo.setId(admin.getInt("id"));
				adminvo.setUsername(username);
				adminvo.setPassword(password);
				setSessionAttr("sysadmin", adminvo);
				removeSessionAttr("rand");
				redirect("/sysadmin/index.jsp");
			}else{
				setAttr("msg", "账号输入错误,登录失败！");
				render("/sysadmin/login.jsp");
			}
		}else{
			setAttr("msg", "验证码输入错误,登录失败！");
			render("/sysadmin/login.jsp");
		}
	}
	
	/**
	 * 后台退出
	 */
	public void logout() {
		removeSessionAttr("sysadmin");
		redirect("/sysadmin/login.jsp");
	}
	
	/**
	 * 框架头部
	 */
	@Before(AdminInterceptor.class)
	public void top() {
		render("/sysadmin/common/top.jsp");
	}
	
	/**
	 * 框架左边
	 */
	@Before(AdminInterceptor.class)
	public void left() {
		render("/sysadmin/common/left.jsp");
	}
	
	/**
	 * 框架欢迎
	 */
	@Before(AdminInterceptor.class)
	public void main() {
		render("/sysadmin/common/main.jsp");
	}
	
	/**
	 * 获取管理账号
	 */
	@Before(AdminInterceptor.class)
	public void getadmin() {
		Admin admin = Admin.dao.findFirst("select * from admin where username=?","admin");
		setAttr("admin", admin);
		render("/sysadmin/admin.jsp");
	}
	
	/**
	 * 修改管理账号密码
	 */
	@Before(AdminInterceptor.class)
	public void updateadmin() {
		String id = getPara("id")==null?"":getPara("id");
		String password = getPara("password")==null?"":getPara("password");
		if(!password.equals("")){
			Admin admin = Admin.dao.findById(id);
			password = StringUtils.MD5(password);
			admin.set("password", password);
			boolean isok = admin.update();
			if(isok){
				setAttr("msg", "密码修改成功!");
			}else{
				setAttr("msg", "密码修改失败!");
			}
		}else{
			setAttr("msg", "密码未修改!");
		}
		forwardAction("/sysadmin/getadmin");
	}
	
	/**
	 * 获取分类
	 */
	@Before(AdminInterceptor.class)
	public void listcategory() {
		List<Category> categoryList = Category.dao.find("select * from category order by cid asc");
		setAttr("categoryList", categoryList);
		render("/sysadmin/categorylist.jsp");
	}
	
	/**
	 * 删除分类
	 */
	@Before(AdminInterceptor.class)
	public void delcategory() {
		int cid = getParaToInt("cid");
		boolean isok = Db.deleteById("category", "cid", cid);
		if(isok){
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败!");
		}
		forwardAction("/sysadmin/listcategory");
	}
	
	/**
	 * 获取单个分类
	 */
	@Before(AdminInterceptor.class)
	public void getcategory() {
		int cid = getParaToInt("cid");
		Category category = Category.dao.findFirst("select * from category where cid = ?",cid);
		setAttr("category", category);
		render("/sysadmin/category.jsp");
	}
	
	/**
	 * 到分类添加页面
	 */
	@Before(AdminInterceptor.class)
	public void inputcategory() {
		render("/sysadmin/category.jsp");
	}
	
	/**
	 * 添加或者修改分类方法
	 */
	@Before(AdminInterceptor.class)
	public void updatecategory() {
		String scid = getPara("cid")==null?"":getPara("cid");
		Record c = new Record();
		c.set("cname", getPara("cname"));
		c.set("url", getPara("url"));
		c.set("title", getPara("title"));
		c.set("keywords", getPara("keywords"));
		c.set("description", getPara("description"));
		if(!scid.equals("")){
			c.set("cid", scid);
			Db.update("category", "cid", c);
			setAttr("msg", "修改成功");
		}else{
			Db.save("category", c);
			setAttr("msg", "添加成功");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listcategory");
	}
	
	/**
	 * 检查分类重复
	 */
	@Before(AdminInterceptor.class)
	public void checkCategory() {
		MsgVO msg = new MsgVO();
		String cid = getPara("cid");
		String type = getPara("type");
		String value = getPara("value");
		if(cid.equals("0")){
			List<Category> categoryList = Category.dao.find("select * from category where "+type+"=?",value);
			if(categoryList!=null && categoryList.size()>0){
				msg.setState("false");
				msg.setMsg("已经存在");
			}else{
				msg.setState("true");
				msg.setMsg("可以使用");
			}
		}else{
			List<Category> categoryList = Category.dao.find("select * from category where "+type+"=? and cid!=?",value,cid);
			if(categoryList!=null && categoryList.size()>0){
				msg.setState("false");
				msg.setMsg("已经存在");
			}else{
				msg.setState("true");
				msg.setMsg("可以使用");
			}
		}
		renderJson(msg);
	}
	
	/**
	 * 到文章添加页面
	 */
	@Before(AdminInterceptor.class)
	public void inputarticle() {
		List<Category> categoryList = Category.dao.find("select * from category order by cid asc");
		setAttr("categoryList", categoryList);
		setAttr("addtime", DateUtils.getNowTime());
		/**获取所有tags数据**/
		render("/sysadmin/article.jsp");
	}
	
	/**
	 * 获取文章
	 */
	@Before(AdminInterceptor.class)
	public void getarticle() {
		List<Category> categoryList = Category.dao.find("select * from category order by cid asc");
		setAttr("categoryList", categoryList);
		int aid = getParaToInt("aid");
		Article article = Article.dao.findFirst("select * from article where aid = ?",aid);
		setAttr("article", article);
		setAttr("addtime", article.get("addtime"));
		int cid = article.getInt("cid");
		Category category = Category.dao.findFirst("select * from category where cid = ?",cid);
		setAttr("category", category);
		
		List<Tags> tagList = Tags.dao.find("select * from tags where aid = ?",aid);
		String tags = "";
		for(Tags tag : tagList){
			tags += (tag.getStr("tname")+",");
		}
		if(tags.length()>0){
			tags = tags.substring(0,tags.length()-1);
		}
		setAttr("tags", tags);
		render("/sysadmin/article.jsp");
	}
	
	/**
	 * 分页文章
	 */
	@Before(AdminInterceptor.class)
	public void listarticle() {
		String word = getPara("word")==null?"": getPara("word");
		int pageNumber = getParaToInt("page")==null?1: getParaToInt("page");
		String where = null;
		if(word.equals("")){
			where = "from article order by aid desc";
		}else{
			where = "from article where title like '%"+word+"%' order by aid desc";
		}
		Page<Article> articleList = Article.dao.paginate(pageNumber, 15, "select *", where);
		List<Article> newarticleList = new ArrayList<Article>();
		for(Article article:articleList.getList()){
			newarticleList.add(article);
		}
		setAttr("pageList", articleList);
		setAttr("articleList", newarticleList);
		setAttr("word", word);
		List<Category> categoryList = Category.dao.find("select * from category");
		setAttr("categoryList", categoryList);
		render("/sysadmin/articlelist.jsp");
	}
	

	/**
	 * 删除文章
	 */
	@Before(AdminInterceptor.class)
	public void delarticle() throws Exception{
		int aid = getParaToInt("aid");
		boolean isok = Db.deleteById("article", "aid", aid);
		if(isok){
			Db.update("delete from tags where aid = ?",aid);
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败!");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listarticle");
	}
	
	/**
	 * 修改或者添加文章
	 */
	@Before(AdminInterceptor.class)
	public void updatearticle() throws Exception{
		String aid = getPara("aid")==null?"":getPara("aid");
		Record c = new Record();
		c.set("title", getPara("title"));
		c.set("cid", getPara("cid"));
		c.set("type", getPara("type"));
		c.set("imgurl", getPara("imgurl"));
		c.set("remark", getPara("remark"));
		c.set("content", getPara("content"));
		c.set("addtime", getPara("addtime"));
		String tags = getPara("tags")==null?"":getPara("tags");
		String[] tagArr = StringUtils.replace(tags).split(",");
		if(!aid.equals("")){
			c.set("aid", aid);
			Db.update("article", "aid", c);
			Db.update("delete from tags where aid = ?",Integer.parseInt(aid));
			Record record = null;
			for(String tag : tagArr){
				if(!"".equals(tag)){
					record = new Record();
					record.set("tname", tag);
					record.set("aid", Integer.parseInt(aid));
					record.set("cid", Integer.parseInt(getPara("cid")));
					Db.save("tags", record);
				}
			}
			setAttr("msg", "修改成功");
		}else{
			c.set("clicknum", 0);
			Db.save("article", c);
			Article record = Article.dao.findFirst("select * from article order by aid desc");
			Record r = null;
			for(String tag : tagArr){
				r = new Record();
				r.set("tname", tag);
				r.set("aid", record.getInt("aid"));
				r.set("cid", Integer.parseInt(getPara("cid")));
				Db.save("tags", r);
			}
			setAttr("msg", "添加成功");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listarticle");
	}

	
	
	
	/*** 到图片上传页面 */
	public void image() {
		render("/sysadmin/common/image.jsp");
	}

	/*** 上传图片 */
	@SuppressWarnings("deprecation")
	public void upload() {
		String fileTime = DateUtils.getNowTime(DateUtils.DATE_KEY_STR);
		String filePath = this.getRequest().getRealPath(ReadPropertity.getProperty("actorpictmp"));
		UploadFile uploadFile = this.getFile("imgFile", filePath);
		String fileName = uploadFile.getFileName();
		File sFile = new File(this.getRequest().getRealPath(ReadPropertity.getProperty("actorpictmp") + fileName));
		String newFileName = fileTime + "."+ StringUtils.getExtensionName(fileName);
		File dFile = new File(this.getRequest().getRealPath(ReadPropertity.getProperty("actorpictmp") + newFileName));
		sFile.renameTo(dFile);
		setAttr("fileName", newFileName);
		render("/sysadmin/common/cut.jsp");
	}

	/*** 剪切图片 */
	@SuppressWarnings("deprecation")
	public void cut() {
		int x = getParaToInt("x");
		int y = getParaToInt("y");
		int width = getParaToInt("width");
		int height = getParaToInt("height");
		String fileName = getPara("fileName");
		String filePath = this.getRequest().getRealPath(ReadPropertity.getProperty("actorpictmp") + fileName);
		try {
			OperateImage o = new OperateImage(x, y, width, height);
			o.setSrcpath(filePath);
			String newFilePath = this.getRequest().getRealPath(ReadPropertity.getProperty("actorpic") + fileName);
			o.setSubpath(newFilePath);
			o.cut();
			File f = new File(filePath);
			f.delete();
			setAttr("fileName", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		render("/sysadmin/common/image.jsp");
	}
	
	
	/**
	 * 网站配置分页
	 */
	@Before(AdminInterceptor.class)
	public void listwebsite() {
		int pageNumber = getParaToInt("page")==null?1: getParaToInt("page");
		StringBuffer where = new StringBuffer("from website where 1=1 ");
		where.append("order by id asc");
		Page<Website> pageList = Website.dao.paginate(pageNumber, 10, "select *", where.toString());
		setAttr("pageList", pageList);
		render("/sysadmin/websitelist.jsp");
	}
	
	/**
	 * 删除网站配置
	 */
	@Before(AdminInterceptor.class)
	public void delwebsite() {
		int id = getParaToInt("id");
		boolean isok = Db.deleteById("website", "id",id);
		if(isok){
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败!");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listwebsite");
	}
	
	/**
	 * 获取网站配置
	 */
	@Before(AdminInterceptor.class)
	public void getwebsite() {
		String id = getPara("id");
		Website website = Website.dao.findFirst("select * from website where id = ?",id);
		setAttr("website", website);
		render("/sysadmin/website.jsp");
	}
	
	/**
	 * 修改网站配置
	 */
	@Before(AdminInterceptor.class)
	public void updatewebsite() {
		String id = getPara("id")==null?"":getPara("id");
		Record c = new Record();
		c.set("code", getPara("code"));
		c.set("name", getPara("name"));
		c.set("content", getPara("content"));
		if(!id.equals("")){
			c.set("id", id);
			Db.update("website", "id", c);
			setAttr("msg", "修改成功");
		}else{
			Db.save("website", c);
			setAttr("msg", "添加成功");
		}
		CacheManager.clearAll();
		redirect("/sysadmin/listwebsite");
	}
	
	/**
	 * 到网站配置添加页面
	 */
	@Before(AdminInterceptor.class)
	public void inputwebsite() {
		render("/sysadmin/website.jsp");
	}
	
	
	/**
	 * 网站配置分页
	 */
	@Before(AdminInterceptor.class)
	public void listgbook() {
		int pageNumber = getParaToInt("page")==null?1: getParaToInt("page");
		StringBuffer where = new StringBuffer("from gbook where 1=1 ");
		where.append("order by mid desc");
		Page<Gbook> pageList = Gbook.dao.paginate(pageNumber, 15, "select *", where.toString());
		setAttr("pageList", pageList);
		render("/sysadmin/gbooklist.jsp");
	}
	
	/**
	 * 修改网站配置
	 */
	@Before(AdminInterceptor.class)
	public void updategbook() {
		Integer mid = getParaToInt("mid");
		Record c = Db.findById("gbook", "mid", mid);
		c.set("nickname", getPara("nickname"));
		c.set("email", getPara("email"));
		c.set("content", getPara("content"));
		c.set("reply", getPara("reply"));
		c.set("state", 1);
		Db.update("gbook", "mid", c);
		setAttr("msg", "修改成功");
		CacheManager.clearAll();
		redirect("/sysadmin/listgbook");
	}
	
	/**
	 * 修改网站配置
	 */
	@Before(AdminInterceptor.class)
	public void delgbook() {
		Integer mid = getParaToInt("mid");
		if(Db.deleteById("gbook", "mid", mid)){
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败");
		}
		CacheManager.clearAll();
		redirect("/sysadmin/listgbook");
	}
	
	/**
	 * 到网站配置添加页面
	 */
	@Before(AdminInterceptor.class)
	public void getgbook() {
		Integer mid = getParaToInt("mid");
		Record gbook = Db.findById("gbook", "mid", mid);
		setAttr("gbook", gbook);
		render("/sysadmin/gbook.jsp");
	}
	
	/**
	 * 友情链接分页
	 */
	@Before(AdminInterceptor.class)
	public void listlinks() {
		int pageNumber = getParaToInt("page")==null?1: getParaToInt("page");
		StringBuffer where = new StringBuffer("from links where 1=1 ");
		where.append("order by id desc");
		Page<Links> pageList = Links.dao.paginate(pageNumber, 10, "select *", where.toString());
		setAttr("pageList", pageList);
		render("/sysadmin/linkslist.jsp");
	}
	
	/**
	 * 获取友情链接
	 */
	@Before(AdminInterceptor.class)
	public void getlinks() {
		String id = getPara("id");
		Links links = Links.dao.findFirst("select * from links where id = ?",id);
		setAttr("links", links);
		render("/sysadmin/links.jsp");
	}
	
	/**
	 * 修改友情链接
	 */
	@Before(AdminInterceptor.class)
	public void updatelinks() {
		String id = getPara("id")==null?"":getPara("id");
		Record c = new Record();
		c.set("title", getPara("title"));
		c.set("url", getPara("url"));
		c.set("content", getPara("content"));
		if(!id.equals("")){
			c.set("id", id);
			Db.update("links", "id", c);
			setAttr("msg", "修改成功");
		}else{
			Db.save("links", c);
			setAttr("msg", "添加成功");
		}
		CacheManager.clearAll();
		redirect("/sysadmin/listlinks");
	}
	
	/**
	 * 到友情链接添加页面
	 */
	@Before(AdminInterceptor.class)
	public void inputlinks() {
		render("/sysadmin/links.jsp");
	}
	
	/**
	 * 删除友情链接
	 */
	@Before(AdminInterceptor.class)
	public void dellinks() {
		int fid = getParaToInt("id");
		boolean isok = Db.deleteById("links", "id", fid);
		if(isok){
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败!");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listlinks");
	}
	
	/**
	 * 标签
	 */
	@Before(AdminInterceptor.class)
	public void listtag() {
		List<Tags> tagList = Tags.dao.find("select * from tags order by tid desc");
		setAttr("taglist", tagList);
		render("/sysadmin/taglist.jsp");
	}
	
	/**
	 * 获取标签
	 */
	@Before(AdminInterceptor.class)
	public void gettag() {
		String tid = getPara("tid");
		Tags tags = Tags.dao.findFirst("select * from tags where tid = ?",tid);
		setAttr("tag", tags);
		render("/sysadmin/tag.jsp");
	}
	
	/**
	 * 修改标签
	 */
	@Before(AdminInterceptor.class)
	public void updatetag() {
		String tid = getPara("tid")==null?"":getPara("tid");
		Record c = new Record();
		c.set("tname", getPara("tname"));
		if(!tid.equals("")){
			c.set("tid", tid);
			Db.update("tags", "tid", c);
			setAttr("msg", "修改成功");
		}else{
			Db.save("tags", c);
			setAttr("msg", "添加成功");
		}
		CacheManager.clearAll();
		redirect("/sysadmin/listtag");
	}
	
	/**
	 * 到标签添加页面
	 */
	@Before(AdminInterceptor.class)
	public void inputtag() {
		render("/sysadmin/tag.jsp");
	}
	
	/**
	 * 删除标签
	 */
	@Before(AdminInterceptor.class)
	public void deltag() {
		int tid = getParaToInt("tid");
		boolean isok = Db.deleteById("tags", "tid", tid);
		if(isok){
			setAttr("msg", "删除成功");
		}else{
			setAttr("msg", "删除失败!");
		}
		CacheManager.clearAll();
		forwardAction("/sysadmin/listtag");
	}
	
}


