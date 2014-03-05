<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<tr id="footer">
 <td colspan="2">
 <form action="${ctx }/search" style="margin-top: 30px;">
   <span class="search"><input type="text" class="word" name="word" value="${word }"/></span>
   <span><input class="searchBtn" type="submit" value="搜 索"/></span>
   <span class="s_help">&nbsp;&nbsp;&nbsp;
       <a href="${ctx }/tags.html" title="Tag列表">Tag列表</a>&nbsp;&nbsp;
	   <a href="${ctx }/sitemap.html" title="网站地图">网站地图</a>&nbsp;&nbsp;
	   <a href="${ctx }/rss.xml" title="XML地图">XML地图</a>&nbsp;&nbsp;
	   <script src="http://s13.cnzz.com/stat.php?id=5786950&web_id=5786950" language="javascript"></script>
   </span>
 </form>
 </td>
</tr>