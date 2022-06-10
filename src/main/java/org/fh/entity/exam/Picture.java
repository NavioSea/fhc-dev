package org.fh.entity.exam;

import java.util.List;

 /** 
 * 说明：图片管理实体类
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
public class Picture{ 
	
	private String PICTURE_ID;				//主键
	private String NAME;					//名称
	private String PARENT_ID;				//父类ID
	private String target;
	private Picture picture;
	private List<Picture> subPicture;
	private boolean hasPicture = false;
	private String treeurl;
	
	private String FILEPATH;			//路径
	public String getFFILEPATH() {
		return FILEPATH;
	}
	public void setFFILEPATH(String FILEPATH) {
		this.FILEPATH = FILEPATH;
	}
	private String CTIME;			//上传时间
	public String getFCTIME() {
		return CTIME;
	}
	public void setFCTIME(String CTIME) {
		this.CTIME = CTIME;
	}
	private String UNAME;			//上传者
	public String getFUNAME() {
		return UNAME;
	}
	public void setFUNAME(String UNAME) {
		this.UNAME = UNAME;
	}
	private String MASTER;			//所属人
	public String getFMASTER() {
		return MASTER;
	}
	public void setFMASTER(String MASTER) {
		this.MASTER = MASTER;
	}
	private String FILESIZE;			//文件大小
	public String getFFILESIZE() {
		return FILESIZE;
	}
	public void setFFILESIZE(String FILESIZE) {
		this.FILESIZE = FILESIZE;
	}
	private String REMARKS;			//备注说明
	public String getFREMARKS() {
		return REMARKS;
	}
	public void setFREMARKS(String REMARKS) {
		this.REMARKS = REMARKS;
	}

	public String getPICTURE_ID() {
		return PICTURE_ID;
	}
	public void setPICTURE_ID(String PICTURE_ID) {
		this.PICTURE_ID = PICTURE_ID;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
	public String getPARENT_ID() {
		return PARENT_ID;
	}
	public void setPARENT_ID(String PARENT_ID) {
		this.PARENT_ID = PARENT_ID;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public List<Picture> getSubPicture() {
		return subPicture;
	}
	public void setSubPicture(List<Picture> subPicture) {
		this.subPicture = subPicture;
	}
	public boolean isHasPicture() {
		return hasPicture;
	}
	public void setHasPicture(boolean hasPicture) {
		this.hasPicture = hasPicture;
	}
	public String getTreeurl() {
		return treeurl;
	}
	public void setTreeurl(String treeurl) {
		this.treeurl = treeurl;
	}
	
}
