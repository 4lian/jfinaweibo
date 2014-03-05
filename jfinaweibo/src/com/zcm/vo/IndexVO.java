package com.zcm.vo;

public class IndexVO {
	
	private String aid;
	private String title;
	private String remark;
	private String content;
	
	
	public IndexVO(){
		
	}
	
	public IndexVO(String aid){
		this.aid = aid;
	}
	
	public IndexVO(String aid,String title,String remark,String content){
		this.aid = aid;
		this.title = title;
		this.remark = remark;
		this.content = content;
	}
	
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
